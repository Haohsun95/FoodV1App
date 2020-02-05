package com.example.asus.foodv1app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
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

public class OCommentActivity extends AppCompatActivity {

    ListView listView_OComment;
    String member_no_text;
    SharedPreferences sharedPreferences;
    final String ShowMyCommentURL = "http://140.136.155.55/F-ShowSpecificMemberCommentNew.php";
    private ActionBar toolbar;

    ArrayList O_Rating_no = new ArrayList();
    ArrayList O_Rating_score = new ArrayList();
    ArrayList O_Rating_detail = new ArrayList();
    ArrayList O_Rating_date = new ArrayList();
    ArrayList O_Store_no = new ArrayList();
    ArrayList O_Member_no = new ArrayList();
    ArrayList O_Store_name = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocomment);

        toolbar = getSupportActionBar();
        toolbar.setTitle("評論管理");

        listView_OComment = findViewById(R.id.listView_OComment);
        listView_OComment.setDivider(null);

        //取出sharedPreferences儲存之會員編號
        sharedPreferences = getSharedPreferences("DATA",0);
        member_no_text = sharedPreferences.getString("mInfoNo","");
        System.out.println("執行前"+member_no_text);
        ShowOComment();

    }

    private void ShowOComment(){

        O_Rating_no.clear();
        O_Rating_score.clear();
        O_Rating_detail.clear();
        O_Rating_date.clear();
        O_Store_no.clear();
        O_Member_no.clear();
        O_Store_name.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowMyCommentURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("ratings");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        O_Rating_no.add(jsonChild.getString("rating_no"));
                        O_Rating_score.add(jsonChild.getString("rating_score"));
                        O_Rating_detail.add(jsonChild.getString("rating_detail"));
                        O_Rating_date.add(jsonChild.getString("rating_date"));
                        O_Store_no.add(jsonChild.getString("store_no"));
                        O_Member_no.add(jsonChild.getString("member_no"));
                        O_Store_name.add(jsonChild.getString("store_name"));
                    }
                    listView_OComment.setAdapter(new Adapter(getApplicationContext()));
                }
                catch (JSONException e)
                {
                    Toasty.info(OCommentActivity.this,"目前會員無任何評論", Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(OCommentActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                System.out.println("執行中"+member_no_text);
                map.put("member_no",member_no_text);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //Adapter
    private class Adapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        TextView oc_tv_store_name, oc_tv_feedBack, oc_tv_feedBackTime;
        RatingBar oc_ratingBar_detail;

        public Adapter(Context applicationContext)
        {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return O_Rating_no.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.o_comment, null);
            oc_ratingBar_detail = (RatingBar) viewGroup.findViewById(R.id.oc_ratingBar_detail);
            oc_tv_store_name = (TextView) viewGroup.findViewById(R.id.oc_tv_store_name);
            oc_tv_feedBack = (TextView) viewGroup.findViewById(R.id.oc_tv_feedBack);
            oc_tv_feedBackTime = (TextView) viewGroup.findViewById(R.id.oc_tv_feedBackTime);

            float rating_num;
            rating_num = Float.parseFloat(O_Rating_score.get(position).toString());

            oc_ratingBar_detail.setRating(rating_num);
            oc_tv_store_name.setText(O_Store_name.get(position).toString());
            oc_tv_feedBack.setText(O_Rating_detail.get(position).toString());
            oc_tv_feedBackTime.setText(O_Rating_date.get(position).toString());

            return viewGroup;
        }
    }
}
