package com.example.asus.foodv1app;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    EditText et_Profile_Account ,et_Profile_Update_Name, et_Profile_Update_Password;
    Button btn_Update_Profile;
    SharedPreferences sharedPreferences;
    final String ShowMemberProfileURL = "http://140.136.155.55/F-MemberProfileNew.php";
    final String UpdateMemberProfileURL = "http://140.136.155.55/F-UpdateProfileNew.php";
    String member_profile_account ,member_password_text, member_username_text;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = getSupportActionBar();
        toolbar.setTitle("編輯會員檔案");

        et_Profile_Account = (EditText) findViewById(R.id.et_Profile_Account);
        et_Profile_Update_Name = (EditText) findViewById(R.id.et_Profile_Update_Name);
        et_Profile_Update_Password = (EditText) findViewById(R.id.et_Profile_Update_Password);
        btn_Update_Profile = (Button) findViewById(R.id.btn_Update_Profile);
        //取出sharedPreferences儲存之會員帳號
        sharedPreferences = getSharedPreferences("DATA",0);
        et_Profile_Account.setText(sharedPreferences.getString("mInfoAccount",""));
        member_profile_account = sharedPreferences.getString("mInfoAccount","");
        et_Profile_Account.setKeyListener(null);
        //顯示會員基本資料
        ShowMemberProfile();
        //更新會員基本資料
        btn_Update_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile();
            }
        });
    }
    //顯示會員基本資料
    private void ShowMemberProfile(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowMemberProfileURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse5 = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse5.optJSONArray("members");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        member_password_text = jsonArray.getJSONObject(i).getString("member_password");
                        member_username_text = jsonArray.getJSONObject(i).getString("member_username");
                    }
                    et_Profile_Update_Name.setText(member_username_text);
                    et_Profile_Update_Password.setText(member_password_text);
                }
                catch (JSONException e)
                {
                    Toasty.error(ProfileActivity.this,"No Data",Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ProfileActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("member_account", member_profile_account);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //修改會員基本資料
    private void UpdateProfile(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UpdateMemberProfileURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        btn_Update_Profile.setEnabled(false);
                        Toasty.success(ProfileActivity.this,"Success",Toast.LENGTH_SHORT,true).show();
                        finish();
                    }
                    else {
                        Toasty.warning(ProfileActivity.this,"Update Failed",Toast.LENGTH_SHORT,true).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ProfileActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("member_account", member_profile_account);
                map.put("member_username", et_Profile_Update_Name.getText().toString());
                map.put("member_password", et_Profile_Update_Password.getText().toString());
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}

