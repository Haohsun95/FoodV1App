package com.example.asus.foodv1app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ManageEventActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String member_no_text, store_no_text, event_no_text;
    GridView gridViewShowAllEvent;
    FloatingActionButton fab_addEvent;
    final String ShowStoreEventURL = "http://140.136.155.55/F-ShowSpecificStoreEventNew.php";
    final String DeleteSpecificStoreEventURL = "http://140.136.155.55/F-DeleteSpecificStoreEventNew.php";
    private ActionBar toolbar;
    //Event Array
    ArrayList Event_no = new ArrayList();
    ArrayList Event_title = new ArrayList();
    ArrayList Event_detail = new ArrayList();
    ArrayList Event_sd = new ArrayList();
    ArrayList Event_ed = new ArrayList();
    ArrayList Store_no = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        toolbar = getSupportActionBar();
        toolbar.setTitle("促銷活動管理");

        fab_addEvent = (FloatingActionButton) findViewById(R.id.fab_addEvent);
        gridViewShowAllEvent = (GridView) findViewById(R.id.gridViewShowAllEvent);

        //取回sharedPreferences之會員編號
        sharedPreferences = getSharedPreferences("DATA",0);
        member_no_text = sharedPreferences.getString("mInfoNo","");
        //從MemberFragment傳遞到此頁面店家編號
        Intent intent = getIntent();
        store_no_text = intent.getStringExtra("store_no");
        //顯示listView
        ShowAllEvent();

        fab_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageEventActivity.this, AddEventActivity.class);
                intent.putExtra("store_no",store_no_text);
                ManageEventActivity.this.startActivity(intent);
            }
        });
    }
    // 顯示店家促銷活動
    private void ShowAllEvent(){
        Event_no.clear();
        Event_title.clear();
        Event_detail.clear();
        Event_sd.clear();
        Event_ed.clear();
        Store_no.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowStoreEventURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("events");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        Event_no.add(jsonChild.getString("event_no"));
                        Event_title.add(jsonChild.getString("event_title"));
                        Event_detail.add(jsonChild.getString("event_detail"));
                        Event_sd.add(jsonChild.getString("event_sd"));
                        Event_ed.add(jsonChild.getString("event_ed"));
                        Store_no.add(jsonChild.getString("store_no"));
                    }
                    gridViewShowAllEvent.setAdapter(new Adapter(getApplicationContext()));
                }
                catch (JSONException e)
                {
                    Toasty.info(ManageEventActivity.this,"目前店家無促銷活動",Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ManageEventActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_no",store_no_text);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //Adapter
    private class Adapter extends BaseAdapter{
        Context ctx;
        LayoutInflater layoutInflater;
        TextView tv_event_title, tv_event_detail, tv_event_sd, tv_event_ed;

        public Adapter(Context applicationContext)
        {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Event_title.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.event, null);
            tv_event_title = (TextView) viewGroup.findViewById(R.id.tv_event_title);
            tv_event_detail = (TextView) viewGroup.findViewById(R.id.tv_event_detail);
            tv_event_sd = (TextView) viewGroup.findViewById(R.id.tv_event_sd);
            tv_event_ed = (TextView) viewGroup.findViewById(R.id.tv_event_ed);

            tv_event_title.setText(Event_title.get(position).toString());
            tv_event_detail.setText(Event_detail.get(position).toString());
            tv_event_sd.setText(Event_sd.get(position).toString());
            tv_event_ed.setText(Event_ed.get(position).toString());

            gridViewShowAllEvent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(ManageEventActivity.this)
                            .setTitle("刪除活動")
                            .setMessage("確定刪除"+ Event_title.get(position).toString()+"嗎？")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    event_no_text = Event_no.get(position).toString();
                                    deleteEvent();
                                }
                            }).show();

                    return false;
                }
            });

            return viewGroup;
        }
    }
    //從資料庫刪除活動
    private void deleteEvent(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DeleteSpecificStoreEventURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success){
                        Toasty.success(ManageEventActivity.this,"Success",Toast.LENGTH_SHORT,true).show();
                        ShowAllEvent();

                    } else {
                        Toasty.warning(ManageEventActivity.this,"刪除促銷活動失敗",Toast.LENGTH_SHORT,true).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ManageEventActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("event_no",event_no_text);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
