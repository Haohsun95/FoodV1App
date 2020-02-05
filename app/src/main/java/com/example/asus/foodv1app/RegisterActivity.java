package com.example.asus.foodv1app;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class RegisterActivity extends AppCompatActivity {

    Button btn_register;
    EditText etUsername, etAccount, etPassword, etPasswordCheck;
    String register_Username, register_Account, register_Password, member_category;
    com.jaredrummler.materialspinner.MaterialSpinner registerMemberCategory;
    final String Register_URL = "http://140.136.155.55/F-RegisterNew.php";
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = (Button) findViewById(R.id.btn_register);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etAccount = (EditText) findViewById(R.id.etAccount);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordCheck = (EditText) findViewById(R.id.etPasswordCheck);
        registerMemberCategory = (com.jaredrummler.materialspinner.MaterialSpinner) findViewById(R.id.registerMemberCategory);
        registerMemberCategory.setItems("請選擇會員類別","一般會員","商家會員");

        registerMemberCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                member_category = String.valueOf(item);
            }
        });
        //點擊註冊會員
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberRegister();
            }
        });
    }
    //新增註冊會員之資料至資料庫
    private void memberRegister(){
        register_Username = etUsername.getText().toString();
        register_Account = etAccount.getText().toString();
        register_Password = etPassword.getText().toString();

        flag = (etPassword.getText().toString()).compareTo(etPasswordCheck.getText().toString());

        if(register_Username.matches("")||register_Account.matches("")
                ||register_Password.matches("") ||member_category.matches("請選擇會員類別") ||flag != 0)
        {
            Toasty.warning(RegisterActivity.this,"輸入資料不完整",Toast.LENGTH_SHORT,true).show();
        }
        else
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Register_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success){
                            btn_register.setEnabled(false);
                            Toasty.success(RegisterActivity.this,"Success",Toast.LENGTH_SHORT,true).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toasty.warning(RegisterActivity.this,"Register Failed",Toast.LENGTH_SHORT,true).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toasty.error(RegisterActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put("member_account",register_Account);
                    map.put("member_password",register_Password);
                    map.put("member_username",register_Username);
                    map.put("member_category",member_category);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

    }
}
