package com.example.asus.foodv1app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    public static FavoriteFragment newInstance(){
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    public FavoriteFragment() {
        // Required empty public constructor
    }

    ListView favorite_ListView;
    SharedPreferences sharedPreferences;
    String F_member_no_text;
    final String ShowFavoriteURL = "http://140.136.155.55/F-ShowFavoriteNew.php";
    //ArrayList
    ArrayList F_Favorite_no = new ArrayList();
    ArrayList F_Member_no = new ArrayList();
    ArrayList F_Store_no = new ArrayList();
    ArrayList F_Store_name = new ArrayList();
    ArrayList F_Store_address = new ArrayList();
    ArrayList F_Store_intro = new ArrayList();
    ArrayList F_Store_category = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite,container,false);

        favorite_ListView = rootView.findViewById(R.id.favorite_ListView);
        favorite_ListView.setDivider(null);

        //取出sharedPreferences儲存之會員編號
        sharedPreferences = this.getActivity().getSharedPreferences("DATA",0);
        F_member_no_text = sharedPreferences.getString("mInfoNo","");
        //顯示收藏
        showFavorite();

        return rootView;
    }
    //從資料庫抓取會員收藏店家資料
    private void showFavorite(){
        F_Favorite_no.clear();
        F_Member_no.clear();
        F_Store_no.clear();
        F_Store_name.clear();
        F_Store_address.clear();
        F_Store_intro.clear();
        F_Store_category.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ShowFavoriteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.optJSONArray("favorites");
                    for (int i = 0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonChild = jsonArray.getJSONObject(i);
                        F_Favorite_no.add(jsonChild.getString("favorite_no"));
                        F_Member_no.add(jsonChild.getString("member_no"));
                        F_Store_no.add(jsonChild.getString("store_no"));
                        F_Store_name.add(jsonChild.getString("store_name"));
                        F_Store_address.add(jsonChild.getString("store_address"));
                        F_Store_intro.add(jsonChild.getString("store_intro"));
                        F_Store_category.add(jsonChild.getString("store_category"));
                    }
                    favorite_ListView.setAdapter(new Adapter_show_Favorite(getActivity().getApplicationContext()));
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("member_no",F_member_no_text);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //Adapter
    private class Adapter_show_Favorite extends BaseAdapter {

        Context ctx;
        LayoutInflater layoutInflater;
        TextView tvF_StoreName, tvF_StoreAddress, tvF_StoreIntro;
        ImageView favorite_image;
        String category;

        public Adapter_show_Favorite(Context applicationContext){
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return F_Favorite_no.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.favoriteitem, null);
            tvF_StoreName = (TextView) viewGroup.findViewById(R.id.tvF_StoreName);
            tvF_StoreAddress = (TextView) viewGroup.findViewById(R.id.tvF_StoreAddress);
            tvF_StoreIntro = (TextView) viewGroup.findViewById(R.id.tvF_StoreIntro);
            favorite_image = (ImageView) viewGroup.findViewById(R.id.favorite_image);

            tvF_StoreName.setText(F_Store_name.get(position).toString());
            tvF_StoreAddress.setText(F_Store_address.get(position).toString());
            tvF_StoreIntro.setText(F_Store_intro.get(position).toString());

            if (F_Store_category.get(position).toString().matches("異國料理"))
            {
                favorite_image.setImageResource(R.drawable.usfood);
            }
            else if(F_Store_category.get(position).toString().matches("火鍋類"))
            {
                favorite_image.setImageResource(R.drawable.hotpot);
            }
            else if (F_Store_category.get(position).toString().matches("燒烤類"))
            {
                favorite_image.setImageResource(R.drawable.bbq);
            }
            else
            {
                favorite_image.setImageResource(R.drawable.dessert);
            }

            favorite_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("store_title",F_Store_name.get(position).toString());
                    startActivity(intent);
                }
            });

            return viewGroup;
        }
    }

}
