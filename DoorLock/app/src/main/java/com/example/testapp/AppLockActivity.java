package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppLockActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private TextView countView;
    private Button timerBtn;

    long nowTime;
    long lockTime;
    long leftTime;

    int leftSec;
    int leftMin;
    int leftHour;

    int lockAttempt;

    //파이어베이스 연결
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        countView = (TextView) findViewById(R.id.timerView);
        timerBtn = (Button) findViewById(R.id.timerBtn);

        nowTime = System.currentTimeMillis();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lockTime = snapshot.child("lockTime").getValue(Long.class);
                lockAttempt = snapshot.child("attempt").getValue(Integer.class);
                lockAttempt = (int)Math.floor(lockAttempt / 3);
                lockTime = lockTime + (lockAttempt * 1 * 60 * 1000);
                leftTime = lockTime - nowTime;

                if(leftTime < 0) {
                    countView.setText("로그인 제한 시간 종료");
                    timerBtn.setText("로그인 가능");
                }
                else {
                    leftSec = (int)Math.round((double)(leftTime / 1000));
                    timerBtn.setEnabled(false);
                    timerBtn.setText("로그인 불가");

                    countDownTimer();
                    countDownTimer.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void onclick_login_back(View v) {
        Intent intent_login_back = new Intent(getApplicationContext(), PassWordActivity.class);
        startActivity(intent_login_back);
    }

    public void countDownTimer() {
        countDownTimer = new CountDownTimer(leftTime, 1000) {
            @Override
            public void onTick(long l) {
                countView.setText("로그인 제한 시간이 " + leftSec + "초 남았습니다.");
                leftSec--;
            }

            @Override
            public void onFinish() {
                countView.setText("로그인 제한 시간 종료");
                timerBtn.setText("로그인 가능");
                timerBtn.setEnabled(true);

                databaseReference.child("appLock").setValue(0);
                databaseReference.child("lockTime").setValue(0);
            }
        };
    }

}

