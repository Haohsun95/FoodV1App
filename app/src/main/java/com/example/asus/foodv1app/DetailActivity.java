package com.example.asus.foodv1app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Rating;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class DetailActivity extends AppCompatActivity {

    TextView tv_detail_store_name, tv_detail_store_category, tv_detail_store_phone, tv_detail_store_address, tv_detail_store_intro;
    String store_no_text, store_name_text, store_category_text, store_phone_text, store_address_text, store_intro_text, store_name_getData;
    String store_no_addFavorite, member_no_addFavorite, member_no_addRating , store_no_addRating;
    String store_no_thumbsUp, member_no_thumbsUp;
    String addRatingScore, addRatingFeedBack;
    com.github.clans.fab.FloatingActionButton fab_addComment, fab_addFavorite, fab_addGreat;
    SharedPreferences sharedPreferences;
    RatingBar ratingBar;
    EditText et_feedBack;
    String ratingScore;
    //ListView listView_rating;
    RecyclerView rv_eventList, rv_ratingList;
    private ActionBar toolbar;

    final String ShowStoreDetailURL = "http://140.136.155.55/F-ShowSpecificStoreDetailNew.php";
    final String AddStoreFavoriteURL = "http://140.136.155.55/F-AddFavoriteNew.php";
    final String AddRating_URL = "http://140.136.155.55/F-AddRatingNew.php";
    final String ShowStoreRatingURL = "http://140.136.155.55/F-ShowRatingNew.php";
    final String AddThumbsUpURL = "http://140.136.155.55/F-AddThumbsUpNew.php";
    final String ShowStoreEventURL = "http://140.136.155.55/F-ShowStoreDetailEventNew.php";

    ArrayList Rating_no = new ArrayList();
    ArrayList Rating_score = new ArrayList();
    ArrayList Rating_detail = new ArrayList();
    ArrayList Rating_date = new ArrayList();
    ArrayList R_Store_no = new ArrayList();
    ArrayList R_Member_no = new ArrayList();
    ArrayList R_Store_name = new ArrayList();

    ArrayList D_Event_no = new ArrayList();
    ArrayList D_Event_title = new ArrayList();
    ArrayList D_Event_detail = new ArrayList();
    ArrayList D_Event_sd = new ArrayList();
    ArrayList D_Event_ed = new ArrayList();
    ArrayList D_Store_no = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = getSupportActionBar();
        toolbar.setTitle("美食主頁");

        tv_detail_store_name = findViewById(R.id.tv_detail_store_name);
        tv_detail_store_category = findViewById(R.id.tv_detail_store_category);
        tv_detail_store_phone = findViewById(R.id.tv_detail_store_phone);
        tv_detail_store_address = findViewById(R.id.tv_detail_store_address);
        tv_detail_store_intro = findViewById(R.id.tv_detail_store_intro);
        fab_addComment = findViewById(R.id.fab_addComment);
        fab_addFavorite = findViewById(R.id.fab_addFavorite);
        fab_addGreat = findViewById(R.id.fab_addGreat);
        //listView_rating = findViewById(R.id.listView_rating);
        rv_eventList = findViewById(R.id.rv_eventList);
        rv_ratingList = findViewById(R.id.rv_ratingList);

        //listView_rating.setDivider(null);

        Intent intent = getIntent();
        intent.getStringExtra("store_title");
        store_name_getData = intent.getStringExtra("store_title");

        //取出sharedPreferences儲存之會員編號
        sharedPreferences = getSharedPreferences("DATA",0);
        member_no_addFavorite = sharedPreferences.getString("mInfoNo","");
        member_no_addRating = sharedPreferences.getString("mInfoNo","");
        member_no_thumbsUp = sharedPreferences.getString("mInfoNo","");

        StoreData();
        ShowDetailEvent();
        ShowRating();

        fab_addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.rating, null);
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alertBuilder.setView(dialogView).setTitle("提供回饋");

                ratingBar = dialogView.findViewById(R.id.ratingBar);
                et_feedBack = dialogView.findViewById(R.id.et_feedBack);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ratingScore = String.valueOf(rating);
                    }
                });

                alertBuilder.setPositiveButton("傳送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddRating();
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        fab_addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFavorite();
            }
        });

        fab_addGreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddThumbsUp();
            }
        });

    }
    //顯示店家基本資訊
    private void StoreData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowStoreDetailURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse5 = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse5.optJSONArray("stores");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        store_no_text = jsonArray.getJSONObject(i).getString("store_no");
                        store_name_text = jsonArray.getJSONObject(i).getString("store_name");
                        store_category_text = jsonArray.getJSONObject(i).getString("store_category");
                        store_phone_text = jsonArray.getJSONObject(i).getString("store_phone");
                        store_address_text = jsonArray.getJSONObject(i).getString("store_address");
                        store_intro_text = jsonArray.getJSONObject(i).getString("store_intro");
                    }
                    store_no_addFavorite = store_no_text;
                    store_no_addRating = store_no_text;
                    store_no_thumbsUp = store_no_text;
                    tv_detail_store_name.setText(store_name_text);
                    tv_detail_store_category.setText(store_category_text);
                    tv_detail_store_phone.setText(store_phone_text);
                    tv_detail_store_address.setText(store_address_text);
                    tv_detail_store_intro.setText(store_intro_text);

                }
                catch (JSONException e)
                {
                    Toasty.error(DetailActivity.this,"No Data",Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(DetailActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_name", store_name_getData);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //新增美食收藏至資料庫
    private void AddFavorite(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AddStoreFavoriteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        Toasty.success(DetailActivity.this,"收藏店家成功",Toast.LENGTH_SHORT,true).show();
                    }
                    else {
                        Toasty.warning(DetailActivity.this,"加入收藏失敗",Toast.LENGTH_SHORT,true).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(DetailActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_no", store_no_addFavorite);
                map.put("member_no", member_no_addFavorite);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //新增評論至資料庫
    private void AddRating(){
        addRatingScore = ratingScore;
        addRatingFeedBack = et_feedBack.getText().toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        final String strDate = simpleDateFormat.format(curDate);

        if (addRatingScore.matches("")||addRatingFeedBack.matches("")) {
            Toasty.warning(DetailActivity.this,"意見回饋不可空白",Toast.LENGTH_SHORT,true).show();
        }
        else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AddRating_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success){
                            ShowRating();
                            Toasty.success(DetailActivity.this,"感謝您的回饋",Toast.LENGTH_SHORT,true).show();
                        } else {
                            Toasty.warning(DetailActivity.this,"回饋失敗",Toast.LENGTH_SHORT,true).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toasty.error(DetailActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put("rating_score",addRatingScore);
                    map.put("rating_detail",addRatingFeedBack);
                    map.put("rating_date",strDate);
                    map.put("store_no",store_no_addRating);
                    map.put("member_no",member_no_addRating);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

    }
    //顯示其店家之所有評論
    private void ShowRating(){

        System.out.println("執行時--------------"+store_name_getData);

        Rating_no.clear();
        Rating_score.clear();
        Rating_detail.clear();
        Rating_date.clear();
        R_Store_no.clear();
        R_Member_no.clear();
        R_Store_name.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowStoreRatingURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse5 = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse5.optJSONArray("ratings");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        Rating_no.add(jsonChild.getString("rating_no"));
                        Rating_score.add(jsonChild.getString("rating_score"));
                        Rating_detail.add(jsonChild.getString("rating_detail"));
                        Rating_date.add(jsonChild.getString("rating_date"));
                        R_Store_no.add(jsonChild.getString("store_no"));
                        R_Member_no.add(jsonChild.getString("member_no"));
                        R_Store_name.add(jsonChild.getString("store_name"));
                    }
                    //listView Adapter
                    //listView_rating.setAdapter(new Adapter(getApplicationContext()));
                    ListAdapter2 adapter = new ListAdapter2(getApplicationContext());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rv_ratingList.setLayoutManager(layoutManager);
                    rv_ratingList.setAdapter(adapter);
                }
                catch (JSONException e)
                {
                    Toasty.error(DetailActivity.this,"No Data",Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(DetailActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                System.out.println("執行中--------------"+store_name_getData);
                map.put("store_name", store_name_getData);
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
        TextView tv_feedBack, tv_feedBackTime;
        RatingBar ratingBar_detail;

        public Adapter(Context applicationContext)
        {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Rating_no.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.showrating, null);
            tv_feedBack = (TextView) viewGroup.findViewById(R.id.tv_feedBack);
            tv_feedBackTime = (TextView) viewGroup.findViewById(R.id.tv_feedBackTime);
            ratingBar_detail = (RatingBar) viewGroup.findViewById(R.id.ratingBar_detail);

            float rating_num;
            rating_num = Float.parseFloat(Rating_score.get(position).toString());

            ratingBar_detail.setRating(rating_num);
            tv_feedBack.setText(Rating_detail.get(position).toString());
            tv_feedBackTime.setText(Rating_date.get(position).toString());

            return viewGroup;
        }
    }
    //新增按讚至資料庫
    private void AddThumbsUp(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        final String strDate = simpleDateFormat.format(curDate);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AddThumbsUpURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        Toasty.success(DetailActivity.this,"按讚成功",Toast.LENGTH_SHORT,true).show();
                    }
                    else {
                        Toasty.warning(DetailActivity.this,"按讚失敗",Toast.LENGTH_SHORT,true).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(DetailActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("like_time",strDate);
                map.put("store_no", store_no_thumbsUp);
                map.put("member_no", member_no_thumbsUp);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void ShowDetailEvent(){
        D_Event_no.clear();
        D_Event_title.clear();
        D_Event_detail.clear();
        D_Event_sd.clear();
        D_Event_ed.clear();
        D_Store_no.clear();

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
                        D_Event_no.add(jsonChild.getString("event_no"));
                        D_Event_title.add(jsonChild.getString("event_title"));
                        D_Event_detail.add(jsonChild.getString("event_detail"));
                        D_Event_sd.add(jsonChild.getString("event_sd"));
                        D_Event_ed.add(jsonChild.getString("event_ed"));
                        D_Store_no.add(jsonChild.getString("store_no"));
                    }

                    ListAdapter adapter = new ListAdapter(getApplicationContext());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rv_eventList.setLayoutManager(layoutManager);
                    rv_eventList.setAdapter(adapter);

                }
                catch (JSONException e)
                {
                    Toasty.info(DetailActivity.this,"目前無促銷活動",Toast.LENGTH_SHORT,true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(DetailActivity.this,"Connection Failed",Toast.LENGTH_SHORT,true).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_name",store_name_getData);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

        Context ctx;

        public ListAdapter(Context context){
            this.ctx = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_detail_event_title, tv_detail_event_detail, tv_detail_event_sd, tv_detail_event_ed;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_detail_event_title = (TextView)itemView.findViewById(R.id.tv_detail_event_title);
                tv_detail_event_detail = (TextView)itemView.findViewById(R.id.tv_detail_event_detail);
                tv_detail_event_sd = (TextView)itemView.findViewById(R.id.tv_detail_event_sd);
                tv_detail_event_ed = (TextView)itemView.findViewById(R.id.tv_detail_event_ed);
            }
        }
        @NonNull
        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detailevent,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {

            holder.tv_detail_event_title.setText(D_Event_title.get(position).toString());
            holder.tv_detail_event_detail.setText(D_Event_detail.get(position).toString());
            holder.tv_detail_event_sd.setText(D_Event_sd.get(position).toString());
            holder.tv_detail_event_ed.setText(D_Event_ed.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return D_Event_no.size();
        }


    }

    public class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.ViewHolder>{

        Context ctx;
        public ListAdapter2 (Context context){
            this.ctx = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv_feedBack, tv_feedBackTime;
            RatingBar ratingBar_detail;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_feedBack = (TextView) itemView.findViewById(R.id.tv_feedBack);
                tv_feedBackTime = (TextView) itemView.findViewById(R.id.tv_feedBackTime);
                ratingBar_detail = (RatingBar) itemView.findViewById(R.id.ratingBar_detail);
            }
        }

        @NonNull
        @Override
        public ListAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showrating,parent,false);
            ListAdapter2.ViewHolder viewHolder = new ListAdapter2.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ListAdapter2.ViewHolder holder, int position) {
            float rating_num;
            rating_num = Float.parseFloat(Rating_score.get(position).toString());

            holder.ratingBar_detail.setRating(rating_num);
            holder.tv_feedBack.setText(Rating_detail.get(position).toString());
            holder.tv_feedBackTime.setText(Rating_date.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return Rating_no.size();
        }
    }
}
