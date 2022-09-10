package com.example.testapp;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

//웹뷰
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

//파이어베이스
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class SubActivity_Camera extends AppCompatActivity {

    //메뉴버튼을 위한 레이아웃
    private DrawerLayout mDrawerLayout;

    //웹뷰
    private WebView webView;
    private String url;

    //파이어베이스
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_camera);

        //파이어베이스
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //불러오기
                url = snapshot.child("JSON").child("ESP32CAM").getValue(String.class);
                
                //웹뷰
                webView = (WebView)findViewById(R.id.camera_webview);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
                webView.setWebChromeClient(new WebChromeClient());
                webView.setWebViewClient(new WebViewClientClass());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        
        //---------------------------------------ksy------------------------------
        //툴바 메뉴
        Toolbar toolbar = (Toolbar) findViewById(R.id.cam_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_camera);

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
        //---------------------------------------ksy------------------------------

    }


    //웹뷰
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    //웹뷰
    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    //---------------------------------------ksy------------------------------
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
    //---------------------------------------ksy------------------------------
}
