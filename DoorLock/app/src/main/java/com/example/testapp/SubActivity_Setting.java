package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubActivity_Setting extends AppCompatActivity implements Button.OnClickListener {

    //메뉴버튼을 위한 레이아웃
    private DrawerLayout mDrawerLayout;

    //파이어베이스 연결
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();


    //현재 비밀번호
    String nowPW = "";

    //입력한 비밀번호
    EditText editPW1;
    EditText editPW2;
    EditText editPW3;

    //커스텀 키패드 버튼 구성
    Button[] btn = new Button[12];
    Integer[] R_id_btn = {R.id.set_num0, R.id.set_num1, R.id.set_num2, R.id.set_num3,
            R.id.set_num4, R.id.set_num5, R.id.set_num6, R.id.set_num7, R.id.set_num8,
            R.id.set_num9, R.id.set_ast, R.id.set_numsign};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_setting);

        //툴바 메뉴
        Toolbar toolbar = (Toolbar) findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_setting);

        //메뉴 버튼 이미지
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.dehaze);

        //네비게이션 메뉴 설정
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.doorlock_control){
                    Intent intent1 = new Intent(getApplicationContext(), SubActivity_Doorlock.class);
                    startActivity(intent1);
                }
                else if(id == R.id.camera_control){
                    Intent intent2 = new Intent(getApplicationContext(), SubActivity_Camera.class);
                    startActivity(intent2);
                }
                else if(id == R.id.setting){
                    Intent intent3 = new Intent(getApplicationContext(), SubActivity_Setting.class);
                    startActivity(intent3);
                }

                return true;
            }
        });

        //DB에 저장되어있는 비밀번호 읽기
        databaseReference.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nowPW = String.valueOf(snapshot.getValue(Integer.class)); //문자열로 변환
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //비밀번호 입력 창 아이디
        editPW1 = findViewById(R.id.pw1);
        editPW2 = findViewById(R.id.pw2);
        editPW3 = findViewById(R.id.pw3);

        editPW1.setTag("pw1");
        editPW2.setTag("pw2");
        editPW3.setTag("pw3");

        //비밀번호 입력 창 키보드 숨기기
        editPW1.setTextIsSelectable(true);
        editPW1.setShowSoftInputOnFocus(false);
        editPW2.setTextIsSelectable(true);
        editPW2.setShowSoftInputOnFocus(false);
        editPW3.setTextIsSelectable(true);
        editPW3.setShowSoftInputOnFocus(false);

        //버튼 아이디 읽기
        for(int i = 0; i < 12; i++) {
            btn[i] = (Button) findViewById(R_id_btn[i]);
        }

        for(int i = 0; i < 12; i++) {
            btn[i].setOnClickListener(this);
        }

    }

    //왼쪽 상단 메뉴버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //버튼 입력
    public void onClick(View v) {
        //입력된 버튼의 id
        Button inputBtn = (Button)findViewById(v.getId());

        //선택한 버튼의 글자
        String s = inputBtn.getText().toString();

        //선택한 에딧텍스트

        if(editPW1.isFocused()) {
            textChange(editPW1, s);
        }
        else if(editPW2.isFocused()) {
            textChange(editPW2, s);
        }
        else if(editPW3.isFocused()) {
            textChange(editPW3, s);
        }
    }

    //추가 버튼
    public void onclick_cancle(View v) {    //전체 삭제
        editPW1.setText("");
        editPW2.setText("");
        editPW3.setText("");
    }
    public void onclick_complete(View v) {  //입력된 비밀번호를 확인 후 비밀번호 변경 동작을 완료
        String pw1 = editPW1.getText().toString();
        String pw2 = editPW2.getText().toString();
        String pw3 = editPW3.getText().toString();

        boolean passwordCheck1 = nowPW.equals(pw1);
        boolean passwordCheck2 = pw2.equals(pw3);

        if(!passwordCheck1) {
            Toast t = Toast.makeText(getApplicationContext(), "현재 비밀번호가 다릅니다.", Toast.LENGTH_SHORT);
            t.show();
        }
        if(!passwordCheck2) {
            Toast t = Toast.makeText(getApplicationContext(), "새 비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT);
            t.show();
        }

        if(passwordCheck1 && passwordCheck2){
            databaseReference.child("password").setValue(Integer.parseInt(pw2)); //정수로 변환
            databaseReference.child("flag").setValue(1);
            finish();
        }
    }
    public void onclick_erase(View v) { //커서 위치에서 한 글자 지우는 동작

        //선택한 에딧텍스트
        if(editPW1.isFocused()) {
            textErase(editPW1);
        }
        else if(editPW2.isFocused()) {
            textErase(editPW2);
        }
        else if(editPW3.isFocused()) {
            textErase(editPW3);
        }

    }

    //글자 입력 동작
    public void textChange(EditText et, String input) {
        int selection = et.getSelectionStart();
        String data = et.getText().toString();

        if(data.length() == 4) {
            return;
        }

        data = data.substring(0, selection) + input + data.substring(selection, data.length());

        et.setText(data);
        et.setSelection(selection + 1);
    }

    //backspace 동작
    public void textErase(EditText et){
        int selection = et.getSelectionStart();
        String data = et.getText().toString();

        if(selection == 0){
            return;
        }

        data = data.substring(0, selection - 1) + data.substring(selection, data.length());

        et.setText(data);
        et.setSelection(selection-1);
    }

}