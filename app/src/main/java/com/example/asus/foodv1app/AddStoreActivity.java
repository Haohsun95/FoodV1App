package com.example.asus.foodv1app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class AddStoreActivity extends AppCompatActivity {

    EditText et_addStore_Name, et_addStore_Phone, et_addStore_address, et_addStore_intro;
    CheckBox checkbox_add_store_map;
    String place, store_name, store_category, store_phone, store_address, store_latitude, store_longitude, store_intro, member_no;
    Button btn_add_store;
    com.jaredrummler.materialspinner.MaterialSpinner add_store_category;
    final String AddStore_URL = "http://140.136.155.55/F-AddStoreNew.php";
    SharedPreferences sharedPreferences;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);

        toolbar = getSupportActionBar();
        toolbar.setTitle("登錄店家");

        et_addStore_Name = (EditText) findViewById(R.id.et_addStore_Name);
        et_addStore_Phone = (EditText) findViewById(R.id.et_addStore_Phone);
        et_addStore_address = (EditText) findViewById(R.id.et_addStore_address);
        et_addStore_intro = (EditText) findViewById(R.id.et_addStore_intro);
        add_store_category = (com.jaredrummler.materialspinner.MaterialSpinner) findViewById(R.id.add_store_category);
        checkbox_add_store_map = (CheckBox) findViewById(R.id.checkbox_add_store_map);
        btn_add_store = (Button) findViewById(R.id.btn_add_store);

        sharedPreferences = getSharedPreferences("DATA",0);
        member_no = sharedPreferences.getString("mInfoNo","");

        add_store_category.setItems("請選擇店家類別","異國料理","燒烤類","火鍋類","簡餐類");
        add_store_category.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                store_category = String.valueOf(item);
            }
        });
        //180527編輯：找經緯度
        checkbox_add_store_map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    getLatLongFromPlace();
                }
            }
        });
        //180527編輯：新增店家資料
        btn_add_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddStoreData();
            }
        });
    }
    //180527 地址查詢經緯度
    private void getLatLongFromPlace() {
        try
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;
            place = et_addStore_address.getText().toString();
            addresses = geocoder.getFromLocationName(place,5);
            if (addresses == null ||addresses.size() == 0 )
            {
                Toasty.warning(AddStoreActivity.this,"無法取得地址資訊",Toast.LENGTH_SHORT,true).show();
            }
            else
            {
                Address location = addresses.get(0);
                store_latitude = Double.toString(location.getLatitude());
                store_longitude = Double.toString(location.getLongitude());
                Toasty.info(AddStoreActivity.this,"緯度："+ store_latitude + "經度：" + store_longitude,Toast.LENGTH_SHORT,true).show();
            }
        }
        catch (Exception ex){
            Toasty.error(AddStoreActivity.this,"Error",Toast.LENGTH_SHORT,true).show();
        }
    }
    //180527 新增店家資訊至資料庫
    private void AddStoreData(){
        store_name = et_addStore_Name.getText().toString();
        store_phone = et_addStore_Phone.getText().toString();
        store_address = et_addStore_address.getText().toString();
        store_intro = et_addStore_intro.getText().toString();

        if(store_name.matches("")||store_phone.matches("") || store_category.matches("請選擇店家類別")
                ||store_address.matches("") ||store_intro.matches("") || store_latitude.matches("") || store_longitude.matches(""))
        {
            Toasty.warning(AddStoreActivity.this,"登錄店家資訊欄位不可空白",Toast.LENGTH_SHORT,true).show();
        }
        else
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AddStore_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success){
                            btn_add_store.setEnabled(false);
                            Toasty.success(AddStoreActivity.this,"Success",Toast.LENGTH_SHORT,true).show();
                            finish();
                        } else {
                            Toasty.warning(AddStoreActivity.this,"登錄商店失敗",Toast.LENGTH_SHORT,true).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toasty.error(AddStoreActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put("store_name",store_name);
                    map.put("store_category",store_category);
                    map.put("store_phone",store_phone);
                    map.put("store_address",store_address);
                    map.put("store_latitude",store_latitude);
                    map.put("store_longitude",store_longitude);
                    map.put("store_intro",store_intro);
                    map.put("member_no",member_no);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
