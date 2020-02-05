package com.example.asus.foodv1app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    public static CategoryFragment newInstance(){
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    public CategoryFragment() {
        // Required empty public constructor
    }

    com.jaredrummler.materialspinner.MaterialSpinner chooseCategory;
    final String ShowStoreEventURL = "http://140.136.155.55/F-ShowAllStoreEventNew.php";
    final String ShowCategoryURL = "http://140.136.155.55/F-ShowCategoryItemNew.php";
    final String ShowThumbsUpURL = "http://140.136.155.55/F-ShowThumbsupsNew.php";
    String category_text_2, combine01;
    ListView listViewCategory;
    //Store - ArrayList
    ArrayList Event_no = new ArrayList();
    ArrayList Event_title = new ArrayList();
    ArrayList Event_detail = new ArrayList();
    ArrayList Event_sd = new ArrayList();
    ArrayList Event_ed = new ArrayList();
    ArrayList Store_no = new ArrayList();
    ArrayList Store_name = new ArrayList();
    //Store - ArrayList
    ArrayList C_Store_no = new ArrayList();
    ArrayList C_Store_name = new ArrayList();
    ArrayList C_Store_category = new ArrayList();
    ArrayList C_Store_phone = new ArrayList();
    ArrayList C_Store_address = new ArrayList();
    ArrayList C_Store_longitude = new ArrayList();
    ArrayList C_Store_latitude = new ArrayList();
    ArrayList C_Store_intro = new ArrayList();
    ArrayList C_Member_no = new ArrayList();
    //
    ArrayList T_Like_no = new ArrayList();
    ArrayList T_Like_time = new ArrayList();
    ArrayList T_Store_no = new ArrayList();
    ArrayList T_Member_no = new ArrayList();
    ArrayList T_Store_name = new ArrayList();
    ArrayList T_Store_address = new ArrayList();
    ArrayList T_Store_category = new ArrayList();
    ArrayList T_Store_intro = new ArrayList();
    ArrayList T_Member_username = new ArrayList();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category,container,false);

        chooseCategory = rootView.findViewById(R.id.chooseCategory);
        listViewCategory = rootView.findViewById(R.id.listViewCategory);
        listViewCategory.setDivider(null);
        //顯示所有店家之促銷活動清單
        ShowEvent();

        chooseCategory.setItems("請選擇食物類別","動態消息","異國料理","燒烤類","火鍋類","簡餐類");
        chooseCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                category_text_2 = String.valueOf(item);
                if (category_text_2.matches("動態消息")){
                    ShowThumbsUp();
                }
                else if (category_text_2.matches("異國料理")) {
                    ShowCategory1();
                }
                else if (category_text_2.matches("燒烤類")) {
                    ShowCategory2();
                }
                else if (category_text_2.matches("火鍋類")) {
                    ShowCategory3();
                }
                else if (category_text_2.matches("簡餐類")) {
                    ShowCategory4();
                }
                else {
                    ShowEvent();
                }
            }
        });

        return rootView;
    }
    //最差的方法 - 排行榜
    private void ShowThumbsUp(){

        T_Like_no.clear();
        T_Like_time.clear();
        T_Store_no.clear();
        T_Member_no.clear();
        T_Store_name.clear();
        T_Store_address.clear();
        T_Store_category.clear();
        T_Store_intro.clear();
        T_Member_username.clear();

        StringRequest stringRequest = new StringRequest(ShowThumbsUpURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("thumbsups");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        T_Like_no.add(jsonChild.getString("like_no"));
                        T_Like_time.add(jsonChild.getString("like_time"));
                        T_Store_no.add(jsonChild.getString("store_no"));
                        T_Member_no.add(jsonChild.getString("member_no"));
                        T_Store_name.add(jsonChild.getString("store_name"));
                        T_Store_address.add(jsonChild.getString("store_address"));
                        T_Store_category.add(jsonChild.getString("store_category"));
                        T_Store_intro.add(jsonChild.getString("store_intro"));
                        T_Member_username.add(jsonChild.getString("member_username"));
                    }
                    listViewCategory.setAdapter(new Adapter_show_ThumbsUp(getActivity().getApplicationContext()));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);

    }
    //最差的方法 - 促銷活動類
    private void ShowEvent(){
        Event_no.clear();
        Event_title.clear();
        Event_detail.clear();
        Event_sd.clear();
        Event_ed.clear();
        Store_no.clear();
        Store_name.clear();

        StringRequest stringRequest = new StringRequest(ShowStoreEventURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("events");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        Event_no.add(jsonChild.getString("event_no"));
                        Event_title.add(jsonChild.getString("event_title"));
                        Event_detail.add(jsonChild.getString("event_detail"));
                        Event_sd.add(jsonChild.getString("event_sd"));
                        Event_ed.add(jsonChild.getString("event_ed"));
                        Store_no.add(jsonChild.getString("store_no"));
                        Store_name.add(jsonChild.getString("store_name"));
                    }
                    listViewCategory.setAdapter(new Adapter_show_AllEvent(getActivity().getApplicationContext()));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //最差的方法 - 異國料理類
   private void ShowCategory1(){
        C_Store_no.clear();
        C_Store_name.clear();
        C_Store_category.clear();
        C_Store_phone.clear();
        C_Store_address.clear();
        C_Store_longitude.clear();
        C_Store_latitude.clear();
        C_Store_intro.clear();
        C_Member_no.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowCategoryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("stores");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        C_Store_no.add(jsonChild.getString("store_no"));
                        C_Store_name.add(jsonChild.getString("store_name"));
                        C_Store_category.add(jsonChild.getString("store_category"));
                        C_Store_phone.add(jsonChild.getString("store_phone"));
                        C_Store_address.add(jsonChild.getString("store_address"));
                        C_Store_longitude.add(jsonChild.getString("store_longitude"));
                        C_Store_latitude.add(jsonChild.getString("store_latitude"));
                        C_Store_intro.add(jsonChild.getString("store_intro"));
                        C_Member_no.add(jsonChild.getString("member_no"));
                    }
                    listViewCategory.setAdapter(new Adapter_show_category(getActivity().getApplicationContext()));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_category","異國料理");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //最差的方法 - 燒烤類
    private void ShowCategory2(){
        C_Store_no.clear();
        C_Store_name.clear();
        C_Store_category.clear();
        C_Store_phone.clear();
        C_Store_address.clear();
        C_Store_longitude.clear();
        C_Store_latitude.clear();
        C_Store_intro.clear();
        C_Member_no.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowCategoryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("stores");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        C_Store_no.add(jsonChild.getString("store_no"));
                        C_Store_name.add(jsonChild.getString("store_name"));
                        C_Store_category.add(jsonChild.getString("store_category"));
                        C_Store_phone.add(jsonChild.getString("store_phone"));
                        C_Store_address.add(jsonChild.getString("store_address"));
                        C_Store_longitude.add(jsonChild.getString("store_longitude"));
                        C_Store_latitude.add(jsonChild.getString("store_latitude"));
                        C_Store_intro.add(jsonChild.getString("store_intro"));
                        C_Member_no.add(jsonChild.getString("member_no"));
                    }
                    listViewCategory.setAdapter(new Adapter_show_category(getActivity().getApplicationContext()));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_category","燒烤類");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //最差的方法 - 火鍋類
    private void ShowCategory3(){
        C_Store_no.clear();
        C_Store_name.clear();
        C_Store_category.clear();
        C_Store_phone.clear();
        C_Store_address.clear();
        C_Store_longitude.clear();
        C_Store_latitude.clear();
        C_Store_intro.clear();
        C_Member_no.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowCategoryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("stores");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        C_Store_no.add(jsonChild.getString("store_no"));
                        C_Store_name.add(jsonChild.getString("store_name"));
                        C_Store_category.add(jsonChild.getString("store_category"));
                        C_Store_phone.add(jsonChild.getString("store_phone"));
                        C_Store_address.add(jsonChild.getString("store_address"));
                        C_Store_longitude.add(jsonChild.getString("store_longitude"));
                        C_Store_latitude.add(jsonChild.getString("store_latitude"));
                        C_Store_intro.add(jsonChild.getString("store_intro"));
                        C_Member_no.add(jsonChild.getString("member_no"));
                    }
                    listViewCategory.setAdapter(new Adapter_show_category(getActivity().getApplicationContext()));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_category","火鍋類");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //最差的方法 - 簡餐類
    private void ShowCategory4(){
        C_Store_no.clear();
        C_Store_name.clear();
        C_Store_category.clear();
        C_Store_phone.clear();
        C_Store_address.clear();
        C_Store_longitude.clear();
        C_Store_latitude.clear();
        C_Store_intro.clear();
        C_Member_no.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowCategoryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("stores");
                    for (int i = 0 ; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        C_Store_no.add(jsonChild.getString("store_no"));
                        C_Store_name.add(jsonChild.getString("store_name"));
                        C_Store_category.add(jsonChild.getString("store_category"));
                        C_Store_phone.add(jsonChild.getString("store_phone"));
                        C_Store_address.add(jsonChild.getString("store_address"));
                        C_Store_longitude.add(jsonChild.getString("store_longitude"));
                        C_Store_latitude.add(jsonChild.getString("store_latitude"));
                        C_Store_intro.add(jsonChild.getString("store_intro"));
                        C_Member_no.add(jsonChild.getString("member_no"));
                    }
                    listViewCategory.setAdapter(new Adapter_show_category(getActivity().getApplicationContext()));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("store_category","簡餐類");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //Adapter_show_category
    private class Adapter_show_category extends BaseAdapter{

        Context ctx;
        LayoutInflater layoutInflater;
        TextView tvC_StoreName, tvC_StoreAddress, tvC_StoreIntro;
        ImageView category_image;

        public Adapter_show_category(Context applicationContext){
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return C_Store_no.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.category_item, null);
            tvC_StoreName = (TextView) viewGroup.findViewById(R.id.tvC_StoreName);
            tvC_StoreAddress = (TextView) viewGroup.findViewById(R.id.tvC_StoreAddress);
            tvC_StoreIntro = (TextView) viewGroup.findViewById(R.id.tvC_StoreIntro);
            category_image = (ImageView) viewGroup.findViewById(R.id.category_image);

            tvC_StoreName.setText(C_Store_name.get(position).toString());
            tvC_StoreAddress.setText(C_Store_address.get(position).toString());
            tvC_StoreIntro.setText(C_Store_intro.get(position).toString());

            if (C_Store_category.get(position).toString().matches("異國料理"))
            {
                category_image.setImageResource(R.drawable.usfood);
            }
            else if(C_Store_category.get(position).toString().matches("火鍋類"))
            {
                category_image.setImageResource(R.drawable.hotpot);
            }
            else if (C_Store_category.get(position).toString().matches("燒烤類"))
            {
                category_image.setImageResource(R.drawable.bbq);
            }
            else
            {
                category_image.setImageResource(R.drawable.dessert);
            }

            listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("store_title",C_Store_name.get(position).toString());
                    startActivity(intent);
                }
            });

            return viewGroup;
        }
    }
    //Adapter_show_event
    private class Adapter_show_AllEvent extends BaseAdapter{

        Context ctx;
        LayoutInflater layoutInflater;
        TextView tv_category_event_store_name, tv_category_event_title, tv_category_event_detail, tv_category_event_sd, tv_category_event_ed;

        public Adapter_show_AllEvent(Context applicationContext){
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return Event_no.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.category_event, null);
            tv_category_event_store_name = (TextView) viewGroup.findViewById(R.id.tv_category_event_store_name);
            tv_category_event_title = (TextView) viewGroup.findViewById(R.id.tv_category_event_title);
            tv_category_event_detail = (TextView) viewGroup.findViewById(R.id.tv_category_event_detail);
            tv_category_event_sd = (TextView) viewGroup.findViewById(R.id. tv_category_event_sd);
            tv_category_event_ed = (TextView) viewGroup.findViewById(R.id.tv_category_event_ed);

            tv_category_event_store_name.setText(Store_name.get(position).toString());
            tv_category_event_title.setText(Event_title.get(position).toString());
            tv_category_event_detail.setText(Event_detail.get(position).toString());
            tv_category_event_sd.setText(Event_sd.get(position).toString());
            tv_category_event_ed.setText(Event_ed.get(position).toString());

            listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("store_title",Store_name.get(position).toString());
                    startActivity(intent);
                }
            });

            return viewGroup;
        }
    }
    //Adapter_show_ThumbsUp
    private class Adapter_show_ThumbsUp extends BaseAdapter{

        Context ctx;
        LayoutInflater layoutInflater;
        TextView tv_greatUser, tvG_StoreName, tvG_StoreAddress, tvG_StoreIntro;
        ImageView G_category_image;

        public Adapter_show_ThumbsUp(Context applicationContext){
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return T_Like_no.size();
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
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.greatitem, null);
            G_category_image = (ImageView) viewGroup.findViewById(R.id.G_category_image);
            tv_greatUser = (TextView) viewGroup.findViewById(R.id.tv_greatUser);
            tvG_StoreName = (TextView) viewGroup.findViewById(R.id.tvG_StoreName);
            tvG_StoreAddress = (TextView) viewGroup.findViewById(R.id.tvG_StoreAddress);
            tvG_StoreIntro = (TextView) viewGroup.findViewById(R.id.tvG_StoreIntro);

            combine01 = T_Member_username.get(position).toString() +" 於 "+ T_Like_time.get(position).toString() + " 對 " + T_Store_name.get(position).toString()+" 按讚";

            tv_greatUser.setText(combine01);
            tvG_StoreName.setText(T_Store_name.get(position).toString());
            tvG_StoreAddress.setText(T_Store_address.get(position).toString());
            tvG_StoreIntro.setText(T_Store_intro.get(position).toString());

            if (T_Store_category.get(position).toString().matches("異國料理"))
            {
                G_category_image.setImageResource(R.drawable.usfood);
            }
            else if(T_Store_category.get(position).toString().matches("火鍋類"))
            {
                G_category_image.setImageResource(R.drawable.hotpot);
            }
            else if (T_Store_category.get(position).toString().matches("燒烤類"))
            {
                G_category_image.setImageResource(R.drawable.bbq);
            }
            else
            {
                G_category_image.setImageResource(R.drawable.dessert);
            }

            listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("store_title",T_Store_name.get(position).toString());
                    startActivity(intent);
                }
            });

            return viewGroup;
        }
    }

}
