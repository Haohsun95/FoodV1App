package com.example.asus.foodv1app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class UpdateStoreActivity extends AppCompatActivity {

    EditText et_StoreUpdateName, et_StoreUpdateCategory, et_StoreUpdatePhone, et_StoreUpdateAddress, et_StoreUpdateIntro;
    String storeNameText, storePhoneText, storeCategoryText, storeAddressText, storeIntroText, storeNoText, memberNo;
    String updatePlace, updateStoreLatitude, updateStoreLongitude;
    CheckBox checkboxUpdateStoreMap;
    Button btnUpdateStoreProfile;
    final String ShowStoreProfileURL = "http://140.136.155.55/F-ShowSpecificStoreProfileNew.php";
    final String UpdateStoreProfile_URL = "http://140.136.155.55/F-UpdateSpecificStoreProfileNew.php";
    SharedPreferences sharedPreferences;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_store);

        toolbar = getSupportActionBar();
        toolbar.setTitle("修改店家資訊");

        et_StoreUpdateName = findViewById(R.id.et_StoreUpdateName);
        et_StoreUpdateCategory = findViewById(R.id.et_StoreUpdateCategory);
        et_StoreUpdatePhone = findViewById(R.id.et_StoreUpdatePhone);
        et_StoreUpdateAddress = findViewById(R.id.et_StoreUpdateAddress);
        et_StoreUpdateIntro = findViewById(R.id.et_StoreUpdateIntro);
        checkboxUpdateStoreMap = findViewById(R.id.checkboxUpdateStoreMap);
        btnUpdateStoreProfile = findViewById(R.id.btnUpdateStoreProfile);
        //取出sharedPreferences儲存之會員編號
        sharedPreferences = getSharedPreferences("DATA",0);
        memberNo = sharedPreferences.getString("mInfoNo","");

        //從MemberFragment傳遞到此頁面店家編號
        Intent intent = getIntent();
        storeNoText = intent.getStringExtra("store_no");

        //找經緯度
        checkboxUpdateStoreMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    getLatLongFromPlaceUpdate();
                }
            }
        });
        //店家資料
        ShowStoreProfile();
        //點擊更新店家資料
        btnUpdateStoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStoreData();
            }
        });
    }

    //地址查詢經緯度
    private void getLatLongFromPlaceUpdate() {
        try
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            updatePlace = et_StoreUpdateAddress.getText().toString();
            addresses = geocoder.getFromLocationName(updatePlace,5);
            if (addresses == null ||addresses.size() == 0 )
            {
                Toasty.warning(UpdateStoreActivity.this,"無法取得地址資訊",Toast.LENGTH_SHORT,true).show();
            }
            else
            {
                Address location = addresses.get(0);
                updateStoreLatitude = Double.toString(location.getLatitude());
                updateStoreLongitude = Double.toString(location.getLongitude());
                Toasty.info(UpdateStoreActivity.this,"緯度："+ updateStoreLatitude + "經度：" + updateStoreLongitude,Toast.LENGTH_SHORT,true).show();
            }
        }
        catch (Exception ex){
            Toasty.error(UpdateStoreActivity.this,"Error",Toast.LENGTH_SHORT,true).show();
        }
    }
    //顯示店家資料
    private void ShowStoreProfile(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowStoreProfileURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse5 = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse5.optJSONArray("stores");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        storeNameText = jsonArray.getJSONObject(i).getString("store_name");
                        storeCategoryText= jsonArray.getJSONObject(i).getString("store_category");
                        storePhoneText= jsonArray.getJSONObject(i).getString("store_phone");
                        storeAddressText = jsonArray.getJSONObject(i).getString("store_address");
                        storeIntroText = jsonArray.getJSONObject(i).getString("store_intro");
                    }
                    et_StoreUpdateName.setText(storeNameText);
                    et_StoreUpdateCategory.setText(storeCategoryText);
                    et_StoreUpdatePhone.setText(storePhoneText);
                    et_StoreUpdateAddress.setText(storeAddressText);
                    et_StoreUpdateIntro.setText(storeIntroText);
                }
                catch (JSONException e)
                {
                    Toasty.error(UpdateStoreActivity.this,"No Data", Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(UpdateStoreActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_no", storeNoText);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //更新店家資料至資料庫
    private void UpdateStoreData(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UpdateStoreProfile_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success){
                        btnUpdateStoreProfile.setEnabled(false);
                        Toasty.success(UpdateStoreActivity.this,"Success",Toast.LENGTH_SHORT,true).show();
                        finish();
                    } else {
                        Toasty.warning(UpdateStoreActivity.this,"修改店家失敗",Toast.LENGTH_SHORT,true).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(UpdateStoreActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("store_no",storeNoText);
                map.put("store_name",et_StoreUpdateName.getText().toString());
                map.put("store_category",et_StoreUpdateCategory.getText().toString());
                map.put("store_phone",et_StoreUpdatePhone.getText().toString());
                map.put("store_address",et_StoreUpdateAddress.getText().toString());
                map.put("store_latitude",updateStoreLatitude);
                map.put("store_longitude",updateStoreLongitude);
                map.put("store_intro",et_StoreUpdateIntro.getText().toString());
                map.put("member_no",memberNo);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);


    }

}
