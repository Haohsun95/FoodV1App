package com.example.asus.foodv1app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class SCommentActivity extends AppCompatActivity {

    ListView listView_SComment;
    String store_no_text;
    final String ShowStoreCommentURL = "http://140.136.155.55/F-ShowSpecificStoreCommentNew.php";
    private ActionBar toolbar;

    ArrayList S_Rating_no = new ArrayList();
    ArrayList S_Rating_score = new ArrayList();
    ArrayList S_Rating_detail = new ArrayList();
    ArrayList S_Rating_date = new ArrayList();
    ArrayList S_Store_no = new ArrayList();
    ArrayList S_Member_no = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scomment);

        toolbar = getSupportActionBar();
        toolbar.setTitle("評論管理");

        listView_SComment = findViewById(R.id.listView_SComment);
        listView_SComment.setDivider(null);

        //從MemberFragment傳遞到此頁面店家編號
        Intent intent = getIntent();
        store_no_text = intent.getStringExtra("store_no");

        ShowSComment();

    }

    private void ShowSComment(){

        S_Rating_no.clear();
        S_Rating_score.clear();
        S_Rating_detail.clear();
        S_Rating_date.clear();
        S_Store_no.clear();
        S_Member_no.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowStoreCommentURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("ratings");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        S_Rating_no.add(jsonChild.getString("rating_no"));
                        S_Rating_score.add(jsonChild.getString("rating_score"));
                        S_Rating_detail.add(jsonChild.getString("rating_detail"));
                        S_Rating_date.add(jsonChild.getString("rating_date"));
                        S_Store_no.add(jsonChild.getString("store_no"));
                        S_Member_no.add(jsonChild.getString("member_no"));
                    }

                    listView_SComment.setAdapter(new Adapter(getApplicationContext()));
                }
                catch (JSONException e)
                {
                    Toasty.info(SCommentActivity.this,"目前店家無任何評論", Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(SCommentActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
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
    private class Adapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        TextView sc_tv_feedBack, sc_tv_feedBackTime;
        RatingBar sc_ratingBar_detail;

        public Adapter(Context applicationContext)
        {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return S_Rating_no.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.s_comment, null);
            sc_ratingBar_detail = (RatingBar) viewGroup.findViewById(R.id.sc_ratingBar_detail);
            sc_tv_feedBack = (TextView) viewGroup.findViewById(R.id.sc_tv_feedBack);
            sc_tv_feedBackTime = (TextView) viewGroup.findViewById(R.id.sc_tv_feedBackTime);

            float rating_num;
            rating_num = Float.parseFloat(S_Rating_score.get(position).toString());

            sc_ratingBar_detail.setRating(rating_num);
            sc_tv_feedBack.setText(S_Rating_detail.get(position).toString());
            sc_tv_feedBackTime.setText(S_Rating_date.get(position).toString());

            return viewGroup;
        }
    }
}
