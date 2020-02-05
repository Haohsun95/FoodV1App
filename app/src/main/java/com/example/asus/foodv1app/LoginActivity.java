package com.example.asus.foodv1app;


import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    TextView tvRegister;
    EditText et_login_account, et_login_password;
    String member_account, member_password;
    final String Login_URL = "http://140.136.155.55/F-LoginNew.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        et_login_account = (EditText) findViewById(R.id.et_login_account);
        et_login_password = (EditText) findViewById(R.id.et_login_password);

        //點擊登入會員
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        //轉至註冊頁面
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_r = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent_r);
            }
        });
    }
    //到資料庫核對帳號密碼
    private void login(){
        member_account = et_login_account.getText().toString();
        member_password = et_login_password.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Login_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success)
                    {
                        btn_login.setEnabled(false);
                        Toasty.success(LoginActivity.this,"Successful Connection",Toast.LENGTH_SHORT,true).show();
                        //若登入成功，將會員編號，帳號，會員類別傳至MainActivity
                        String Full_member_no = jsonResponse.getString("member_no");
                        String Full_member_account = jsonResponse.getString("member_account");
                        String Full_member_category = jsonResponse.getString("member_category");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("member_no",Full_member_no);
                        intent.putExtra("member_account",Full_member_account);
                        intent.putExtra("member_category",Full_member_category);
                        LoginActivity.this.startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toasty.warning(LoginActivity.this,"登入帳號或密碼錯誤",Toast.LENGTH_SHORT,true).show();
                    }

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(LoginActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("member_account",member_account);
                map.put("member_password",member_password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
