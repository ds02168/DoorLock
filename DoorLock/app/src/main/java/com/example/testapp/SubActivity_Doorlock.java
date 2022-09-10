package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


//태형
public class SubActivity_Doorlock extends AppCompatActivity {

    /*
     * -------------------V1.0-------------------
     * 1. 메인화면과 합치기 위하여 프래그먼트로 작성
     * 2. 하단과 상단의 뷰들은 확인을 위해 임시로 생성
     * 3. 서버 클라이언트간 Log의 Tag는 "Server"로 통일
     * -------------------V1.1-------------------
     * 1. 하나의 화면으로 구성되므로 도어 프래그먼트 삭제
     * 2. 도어 프래그먼트의 기능을 SubAcitivty_DoorLock로 이전
     * -------------------V1.2-------------------
     * 1. JSON통신을 위한 클래스 생성과 외부라이브러리 Import
     * 2. (Volley,Json)
     * -------------------V1.3-------------------
     * 1. Volley를 이용한 HTTP통신을 위해 AppHelper(큐)정의, onCreate에서 인스턴스받기
     * 2. Json을 주고 받기 위해 DoorLock(데이터), ResponseDoor(통신정보)정의
     * -------------------V1.4-------------------
     * 1. 서버 상단 주소입력창으로 수정
     * 2. 안드로이드 -> PC로 서버 이전
     * 3. UI수정
     * -------------------V1.5-------------------
     * 1. 서버 서비스(서버구축시 필요로 남김),클라이언트 쓰레드,핸들러 삭제
     * 2. 안드로이드 외부의 NODE.JS서버 호출
     * -------------------V1.6-------------------
     * 1. 도어락 조작 Post방식 Request추가(서버 미구현으로 동작x)
     * 2. 조작전 서버로 부터 도어락 상태 받아오도록 수정
     * -------------------V1.7-------------------
     * 1. 메인화면과 합침 2021.10.27
     * 2. 2개의 이미지버튼을 1개의 이미지와 버튼으로 수정
     * 3. 하단 화면에 상태 출력
     * -------------------V1.8-------------------
     * 1. 파이어베이스 연결
     * 2. UI수정(기존의 서버 연결 부분 삭제, 텍스트 뷰 변경)
     * 3. 도어락 클래스를 DTO구조로 수정
     * -------------------V1.9-------------------
     * 1. 리얼타임 데이터베이스 데이터 송,수신
     * 2. Volley RequestQueue(AppHelper.class) 삭제
     * -------------------V2.0-------------------
     * 1. 날짜 출력 추가
     * 2. 온라인/오프라인 화면 분할
     * -------------------V2.1-------------------
     * 1. UI 텍스트 수정
     * 2. 네트워크 연결상태 확인 추가(와이파이, 모바일네트워크 둘다 OK!)
     * -------------------V2.2-------------------
     * 1. 네비게이션 추가
     * -------------------V2.3-------------------
     * 1. 스크롤뷰 삭제
     * 2. 알림(포그라운드) 추가
     * -------------------V2.4-------------------
     * 1. Server.class, DoorResponse.class 삭제
     * 2. 도어락 제어 상수는 아두이노와 맞춤
     * */

    //문이 열림, 닫힘, 잠김 상황을 상수로 표현
    public static final int DOOR_OPEN = 1; //열린 상태
    public static final int DOOR_CLOSE = 0; //잠긴 상태


    //알람을 위한 채널
    private static String CHANNEL_ID = "FRONT_CHANNEL";
    private static String CHANNEL_NAME = "FRONT_CHANNEL";

    int openflag = 2; //도어락 상태
    TextView door_navi; //서버 연결 상태 확인
    Button doorlock_control; //조작 버튼
    ImageView doorlock_status; //상태 이미지
    String[] buttons = {"문 잠금", "문 열기", "연결중...."}; //버튼 글자 저장
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //날짜 포맷
    ConnectivityManager manager; //네트워크 연결상태 확인하는 매니저
    NotificationManager alam; //알람을 출력하는 메니저


    //파이어베이스 데이터베이스 연결
    private FirebaseDatabase database;

    //데이터베이스내에 도어 위치까지 레퍼런스(커서)
    private DatabaseReference databaseReference;

    //데이트베이스 연결상태 확인 레퍼런스
    private DatabaseReference connectedRef;

    //메뉴버튼을 위한 레이아웃 - ksy
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_doorlock);

        //---------------------------------------ksy------------------------------
        //툴바 메뉴
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //도어락 제어페이지 드로어레이아웃
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_doorlock);

        //메뉴 버튼 이미지
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.dehaze);

        //네비게이션 메뉴 버튼 동작 설정
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.doorlock_control) {
                    Intent intent1 = new Intent(getApplicationContext(), SubActivity_Doorlock.class);
                    startActivity(intent1);
                } else if (id == R.id.camera_control) {
                    Intent intent2 = new Intent(getApplicationContext(), SubActivity_Camera.class);
                    startActivity(intent2);
                } else if (id == R.id.setting) {
                    Intent intent3 = new Intent(getApplicationContext(), SubActivity_Setting.class);
                    startActivity(intent3);
                }

                return true;
            }
        });
        //---------------------------------------ksy------------------------------

        //문을 조작하는 버튼과 상태를 보여주는 이미지
        doorlock_control = findViewById(R.id.doorlock_control);
        doorlock_status = findViewById(R.id.doorlock_status);


        //도어락 상태 확인
        door_navi = findViewById(R.id.door_navi);

        //문 조작 버튼
        doorlock_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 상태에 따라 다르게 요청을 보냄
                if (doorlock_control.getText().toString() == buttons[0]) {
                    String message = "문을 잠그시겠습니까?";
                    requestDialog(v, message, DOOR_CLOSE);
                } else if (doorlock_control.getText().toString() == buttons[1]) {
                    String message = "문을 여시겠습니까?";
                    requestDialog(v, message, DOOR_OPEN);
                }
            }
        });

        //초기화 함수
        initSettings();

        Intent intent = getIntent();
        processIntent(intent);
    }


    //왼쪽 상단 메뉴버튼 - ksy
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //초기화
    public void initSettings() {
        //파이어베이스 데이터베이스객체 가져오기
        database = FirebaseDatabase.getInstance();
        //데이터베이스의 루트를 Door로
        databaseReference = database.getReference();
        //연결상태 확인
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        //연결 상태 확인을 위한 네트워크 메니저
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //알람 출력을 위한 노티피케이션 메니저
        alam = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //파이어 베이스에서 데이터 읽기(리스너)
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                openflag = snapshot.child("openflag").getValue(Integer.class);
                processRead(); //레이아웃 수정

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                disconnected(); //연결 해제시
            }
        });

        //연결 상태 확인
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    processRead(); //레이아웃 수정
                    Log.d("openflag", "connected");
                } else {
                    disconnected(); //연결 해제시
                    Log.d("openflag", "disconnected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                disconnected(); //연결 해제시
            }
        });

        //네트워크 콜백 리스너
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        manager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) { //네트워크가 연결 되었을 때
                super.onAvailable(network);
                database.goOnline(); //데이터베이스 연결 설정
            }

            @Override
            public void onLost(@NonNull Network network) { //네트워크 연결이 끊겼을 때
                super.onLost(network);
                database.goOffline(); //데이터베이스 연결 끊기
            }
        });

    }


    //액태비티가 이미 만들어져 있을때
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }


    //첫 화면에서 받아온 인텐트 처리하는 함수
    private void processIntent(Intent intent) {
        if (intent != null) {
            Snackbar.make(this.getWindow().getDecorView(), "환영합니다. 도어락 제어는 신중히 선택해 주세요.", Snackbar.LENGTH_LONG).show();

        }
    }


    //파이어베이스 부터 받아온 데이터를 기반으로 레이아웃 수정
    public void processRead() {
        Date now = new Date(); //현재시간 구하기
        //문의 상태에 따라 화면을 수정
        Log.d("openflag", "문상태 : "+openflag);
        doorlock_control.setEnabled(true);
        switch (openflag) {
            case DOOR_OPEN: //잠금 해제
                println("[" + format.format(now) + "] 잠금이 해제되었습니다!");
                door_navi.setText("도어락 상태 : 열림");
                doorlock_status.setImageResource(R.drawable.door_open);
                doorlock_control.setText(buttons[0]);
                break;
            case DOOR_CLOSE: //잠금 설정
                println("[" + format.format(now) + "] 잠금이 설정되었습니다!");
                door_navi.setText("도어락 상태 : 잠김");
                doorlock_status.setImageResource(R.drawable.door_lock);
                doorlock_control.setText(buttons[1]);
                break;
            default:
                break;
        }

    }

    //다이얼로그 출력
    public void requestDialog(View v, String message, int door) {
        String title = "확인 메시지";
        String titleButtonYes = "예";
        String titleButtonNo = "아니오";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(titleButtonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                writeDoor(door);
            }
        });
        builder.setNegativeButton(titleButtonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //리얼타임 데이터베이스에 쓰기
    public void writeDoor(int door) {
        databaseReference.child("openflag").setValue(door);
    }

    //접속이 끊어졌을때
    public void disconnected() {
        doorlock_control.setEnabled(false);
        doorlock_status.setImageResource(R.drawable.main_page_image);
        doorlock_control.setText(buttons[2]);
        door_navi.setText("연결을 확인중입니다...");
    }

    //알람 메시지 출력
    public void println(String data) {
        NotificationCompat.Builder builder = null;

        //채널 생성(API 29기준 다름)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (alam.getNotificationChannel(CHANNEL_ID) == null) {
                alam.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                ));
            }
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        //알람 메시지 구성
        builder.setContentTitle("간단 알림");
        builder.setContentText(data);
        builder.setSmallIcon(android.R.drawable.ic_menu_view);

        //알림
        Notification noti = builder.build();
        alam.notify(1, noti);
    }
}