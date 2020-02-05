package com.example.asus.foodv1app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddEventActivity extends AppCompatActivity {

    TextView tv_startDay, tv_endDay;
    EditText et_addStore_event_title, et_addStore_event;
    String event_title, event_detail, startDay, endDay, store_no, store_no_text;
    Button btn_add_store_event;
    CalendarView calendarView;
    String start_date, end_date;
    com.skydoves.elasticviews.ElasticLayout el_pick_startDay, el_pick_endDay;
    final String AddStoreEventURL = "http://140.136.155.55/F-AddEventNew.php";
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        toolbar = getSupportActionBar();
        toolbar.setTitle("加入促銷活動");

        tv_startDay = (TextView) findViewById(R.id.tv_startDay);
        tv_endDay = (TextView) findViewById(R.id.tv_endDay);
        et_addStore_event_title = (EditText) findViewById(R.id.et_addStore_event_title);
        et_addStore_event = (EditText) findViewById(R.id.et_addStore_event);
        btn_add_store_event = (Button) findViewById(R.id.btn_add_store_event);
        el_pick_startDay = (com.skydoves.elasticviews.ElasticLayout) findViewById(R.id.el_pick_startDay);
        el_pick_endDay = (com.skydoves.elasticviews.ElasticLayout) findViewById(R.id.el_pick_endDay);

        //傳遞到此頁面店家編號
        Intent intent = getIntent();
        store_no_text = intent.getStringExtra("store_no");

        //點擊選擇活動開始日期
        el_pick_startDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStartDate();
            }
        });
        //點擊選擇活動結束日期
        el_pick_endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEndDate();
            }
        });
        //點擊新增活動
        btn_add_store_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEvent();
            }
        });

    }
    //選擇活動開始日期
    private void pickStartDate(){
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.pick_sdatedialog, null);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddEventActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        alertBuilder.setView(dialogView).setTitle("活動開始日期");

        calendarView = (CalendarView) dialogView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                start_date = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                tv_startDay.setText(start_date);
            }
        });
        alertBuilder.setPositiveButton("加入日期", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    //選擇活動結束結束
    private void pickEndDate(){
        LayoutInflater layoutInflater = getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.pick_sdatedialog, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddEventActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        alertBuilder.setView(dialogView).setTitle("活動截止日期");
        calendarView = (CalendarView) dialogView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                end_date = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
                tv_endDay.setText(end_date);
            }
        });
        alertBuilder.setPositiveButton("加入日期", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
    //新增促銷活動至資料庫
    private void AddEvent(){
        event_title = et_addStore_event_title.getText().toString();
        event_detail = et_addStore_event.getText().toString();
        startDay = start_date;
        endDay = end_date;
        store_no = store_no_text;

        if(event_title.matches("")||event_detail.matches("") ||
                startDay.matches("") || endDay.matches(""))
        {
            Toasty.warning(AddEventActivity.this,"輸入活動資料不完整",Toast.LENGTH_SHORT,true).show();
        }
        else
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AddStoreEventURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success){
                            btn_add_store_event.setEnabled(false);
                            Toasty.success(AddEventActivity.this,"Success",Toast.LENGTH_SHORT,true).show();
                            finish();
                        } else {
                            Toasty.warning(AddEventActivity.this,"加入促銷活動失敗",Toast.LENGTH_SHORT,true).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toasty.error(AddEventActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put("event_title",event_title);
                    map.put("event_detail",event_detail);
                    map.put("event_sd",startDay);
                    map.put("event_ed",endDay);
                    map.put("store_no",store_no);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
