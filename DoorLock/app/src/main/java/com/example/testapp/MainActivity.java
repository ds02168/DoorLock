package com.example.testapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{
    private DrawerLayout mDrawerLayout;
    private Context context = this;

    Button doorlock_btn;
    Button camera_btn;
    Button setting_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //툴바 메뉴
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.dehaze);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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


        //메인 화면의 버튼
        doorlock_btn = (Button)findViewById(R.id.doorlock_btn);
        doorlock_btn.setOnClickListener(this);
        camera_btn = (Button)findViewById(R.id.camera_btn);
        camera_btn.setOnClickListener(this);
        setting_btn = (Button)findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(this);

    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.doorlock_btn:
                Intent intent1 = new Intent(MainActivity.this, SubActivity_Doorlock.class);
                startActivity(intent1);
                break;
            case R.id.camera_btn:
                Intent intent2 = new Intent(MainActivity.this, SubActivity_Camera.class);
                startActivity(intent2);
                break;
            case R.id.setting_btn:
                Intent intent3 = new Intent(MainActivity.this, SubActivity_Setting.class);
                startActivity(intent3);
                break;

        }
    }
}