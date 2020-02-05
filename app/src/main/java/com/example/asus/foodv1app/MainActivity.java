package com.example.asus.foodv1app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String mInfo_no,mInfo_account, mInfo_category;
    private SharedPreferences settings;
    private ActionBar toolbar;
    private static final int REQUEST_LOCATION_PERMISSION =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();
        toolbar.setTitle("美食地圖");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation1);

        final Intent intent = getIntent();
        mInfo_no = intent.getStringExtra("member_no");
        mInfo_account = intent.getStringExtra("member_account");
        mInfo_category = intent.getStringExtra("member_category");
        saveData();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                android.support.v4.app.Fragment selectedFragment = null;
                switch (item.getItemId())
                {
                    case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance();
                        toolbar.setTitle("美食地圖");
                        break;
                    case R.id.navigation_favorite:
                        selectedFragment = FavoriteFragment.newInstance();
                        toolbar.setTitle("美食收藏");
                        break;
                    case R.id.navigation_category:
                        selectedFragment = CategoryFragment.newInstance();
                        toolbar.setTitle("美食媒合");
                        break;
                    case R.id.navigation_member:
                        selectedFragment = MemberFragment.newInstance();
                        toolbar.setTitle("會員設定");
                        break;
                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, selectedFragment);
                fragmentTransaction.commit();
                return true;
            }
        });
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, HomeFragment.newInstance());
        fragmentTransaction.commit();

    }
    //2018.05.07
    public void saveData(){
        settings = getSharedPreferences("DATA",0);
        settings.edit().putString("mInfoNo",mInfo_no).putString("mInfoAccount",mInfo_account).putString("mInfoCategory",mInfo_category).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST,Menu.NONE,"會員登出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getGroupId() == Menu.NONE) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            new AlertDialog.Builder(MainActivity.this).setTitle("確定退出？")
                    .setMessage("確定退出頁面嗎")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        return true;
    }

}
