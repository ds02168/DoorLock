package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class PassWordActivity extends AppCompatActivity implements Button.OnClickListener {

    //파이어베이스 연결
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    //기본 정보
    String default_pw = "0000";
    String pw_result = "";
    int passcodeNum = 0;
    int appLockState = 0;
    long nowTime;
    long lockTime;
    int lockAttempt;

    TextView[] passcode = new TextView[4];

    Button[] btn = new Button[12];
    Integer[] R_id_btn = {R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4,
            R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9, R.id.ast, R.id.numsign};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        //DB에 저장된 값 읽기
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                default_pw = String.valueOf(snapshot.child("password").getValue(Integer.class)); //문자열로 변환
                appLockState = snapshot.child("appLock").getValue(Integer.class);
                lockAttempt = snapshot.child("attempt").getValue(Integer.class);

                if(appLockState == 1) {
                    //앱이 잠긴 상태임
                    lockAttempt = snapshot.child("attempt").getValue(Integer.class);
                    lockTime = snapshot.child("lockTime").getValue(Long.class);
                    nowTime = System.currentTimeMillis();
                    lockAttempt = (int)Math.floor(lockAttempt / 3);

                    //잠긴 시점에서 시도횟수가 3배수에 따라 잠금 시간 증가
                    lockTime = lockTime + (lockAttempt * 1 * 60 * 1000);
                    if(nowTime < lockTime) {
                        //앱 잠금 시간이 남아있음, 잠금 화면으로 이동
                        appLockPage();
                    }
                    else {  //앱 잠금 시간이 지났음, DB에 앱 잠금 상태 데이터 초기화
                        databaseReference.child("appLock").setValue(0);
                        databaseReference.child("lockTime").setValue(0);
                        Toast t = Toast.makeText(getApplicationContext(), "시간 지남", Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        passcode[0] = findViewById(R.id.passcode1);
        passcode[1] = findViewById(R.id.passcode2);
        passcode[2] = findViewById(R.id.passcode3);
        passcode[3] = findViewById(R.id.passcode4);

        for(int i = 0; i < 12; i++) {
            btn[i] = (Button) findViewById(R_id_btn[i]);
        }

        for(int i = 0; i < 12; i++) {
            btn[i].setOnClickListener(this);
        }

    }

    public void onClick(View v) {
        Button inputBtn = (Button)findViewById(v.getId());

        String s = inputBtn.getText().toString();

        passcode[passcodeNum++].setText(s);

        if(passcodeNum == 4) {
            for(int i = 0; i < 4; i++) {    //입력된 비밀번호 합치기
                pw_result += passcode[i].getText().toString();
            }
            if(pw_result.equals(default_pw)){   //로그인 성공
                loginSucceed();
            }
            else {  //로그인 실패
                loginFail();
                Toast t = Toast.makeText(getApplicationContext(), "비밀번호 오류", Toast.LENGTH_SHORT);
                t.show();
            }
            passcodeNum = 0;
            pw_result = "";
            for(int i = 0; i < 4; i++) {
                passcode[i].setText("");
            }
        }
    }

    //로그인 실패
    public void loginFail() {
        DatabaseReference attemptRef = databaseReference.child("attempt");
        
        databaseReference.child("attempt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int failNum = snapshot.getValue(Integer.class);
                failNum++;
                databaseReference.child("attempt").setValue(failNum);

                int failCnt = failNum % 3;

                if(failCnt == 0 && failNum != 0) {
                    databaseReference.child("appLock").setValue(1);

                    long newLockTime = System.currentTimeMillis();
                    databaseReference.child("lockTime").setValue(newLockTime);

                    appLockPage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //로그인 성공
    public void loginSucceed() {
        databaseReference.child("attempt").setValue(0);
        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent1);
        finish();
    }

    //앱 잠금 화면 출력
    public void appLockPage() {
        Intent intent_applock = new Intent(getApplicationContext(), AppLockActivity.class);
        startActivity(intent_applock);
        finish();
    }
}