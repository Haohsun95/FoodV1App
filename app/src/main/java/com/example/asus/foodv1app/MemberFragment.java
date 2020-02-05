package com.example.asus.foodv1app;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    public static MemberFragment newInstance(){
        MemberFragment fragment = new MemberFragment();
        return fragment;
    }

    public MemberFragment() {
        // Required empty public constructor
    }
    SharedPreferences sharedPreferences;
    String Member_Store_no, member_num1, member_no_store;
    final String showMemberStoreNumberURL = "http://140.136.155.55/F-StoreProfileNew.php";
    com.skydoves.elasticviews.ElasticLayout el_addStore, el_updateStore, el_memberProfile, el_manageEvent, el_manageComment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_member,container,false);

        el_addStore = rootView.findViewById(R.id.el_addStore);
        el_updateStore = rootView.findViewById(R.id.el_updateStore);
        el_memberProfile = rootView.findViewById(R.id.el_memberProfile);
        el_manageEvent =rootView.findViewById(R.id.el_manageEvent);
        el_manageComment =rootView.findViewById(R.id.el_manageComment);
        //取出sharedPreferences儲存之會員編號
        sharedPreferences = this.getActivity().getSharedPreferences("DATA",0);
        member_num1 = sharedPreferences.getString("mInfoNo","");


        showMemberStoreNumber();

        //店家新增店家檔案 (18.06.06 已完成)
        el_addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("mInfoCategory","").matches("商家會員")) {
                    if (Member_Store_no == null) {
                        Intent intent = new Intent(getActivity(),AddStoreActivity.class);
                        startActivity(intent);
                    } else {
                        Toasty.info(getActivity(),"已登錄店家資料檔",Toast.LENGTH_SHORT,true).show();
                    } } else {
                    Toasty.warning(getActivity(),"一般會員身分無權限使用",Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        //店家修改店家檔案 (18.06.08 已完成)
        el_updateStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("mInfoCategory","").matches("商家會員")) {
                    if (Member_Store_no == null) {
                        Toasty.info(getActivity(),"請先登錄店家資料檔",Toast.LENGTH_SHORT,true).show();
                    } else {
                        Intent intent = new Intent(getActivity(),UpdateStoreActivity.class);
                        intent.putExtra("store_no", Member_Store_no);
                        startActivity(intent);
                    } } else {
                    Toasty.warning(getActivity(),"一般會員身分無權限使用",Toast.LENGTH_SHORT,true).show();
                }
            }
        });

        //編輯會員檔案 (18.06.06 已完成)
        el_memberProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        //管理促銷活動 (18.06.07 已完成)
        el_manageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getString("mInfoCategory","").matches("商家會員")) {
                    if (Member_Store_no == null) {
                        Toasty.info(getActivity(),"請先登錄店家資料檔",Toast.LENGTH_SHORT,true).show();
                    } else {
                        Intent intent = new Intent(getActivity(),ManageEventActivity.class);
                        intent.putExtra("store_no", Member_Store_no);
                        startActivity(intent);
                    } } else {
                    Toasty.warning(getActivity(),"一般會員身分無權限使用",Toast.LENGTH_SHORT,true).show();
                }
            }
        });
        //管理評論
        el_manageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若是商家會員，進入SCommentActivity
                if (sharedPreferences.getString("mInfoCategory","").matches("商家會員")){
                    if (Member_Store_no == null) {
                        Toasty.info(getActivity(),"請先登錄店家資料檔",Toast.LENGTH_SHORT,true).show();
                    }else {
                        Intent intent = new Intent(getActivity(), SCommentActivity.class);
                        intent.putExtra("store_no", Member_Store_no);
                        startActivity(intent);
                    }
                } else {
                    //一般會員進入OCommentActivity
                    Intent intent = new Intent(getActivity(),OCommentActivity.class);
                    startActivity(intent);
                }

            }
        });

        return rootView;
    }
    //查詢店家會員之店家編號
    private void showMemberStoreNumber(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, showMemberStoreNumberURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("stores");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        Member_Store_no = jsonArray.getJSONObject(i).getString("store_no");
                    }
                }
                catch (JSONException e) {
                    member_no_store = "0";
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getActivity(),"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("member_no", member_num1);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
