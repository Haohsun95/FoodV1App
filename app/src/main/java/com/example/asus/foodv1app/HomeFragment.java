package com.example.asus.foodv1app;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment01;
    final String showAllStoreDataURL = "http://140.136.155.55/F-ShowAllStoreDataNew.php";

    GoogleMap mGoogleMap;
    String store_name;
    Marker Mmarker;
    private Marker position;
    Location mLastLocation; // 最新定位訊息
    LocationRequest mLocationRequest;
    com.github.clans.fab.FloatingActionButton fab_HF3dMap, fab_HF_NORMAL, fab_HF_SATELLITE, fab_HF_TERRAIN, fab_HF_HYBRID;
    //private Marker position; // Marker對象，添加定位位置用到
    // 循環刷新定位訊息延遲
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    Double Latitude02 = 0.00;
    Double Longitude02 = 0.00;
    final ArrayList<HashMap<String, String>> location = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        fab_HF3dMap = rootView.findViewById(R.id.fab_HF3dMap);
        fab_HF_NORMAL = rootView.findViewById(R.id.fab_HF_NORMAL);
        fab_HF_SATELLITE = rootView.findViewById(R.id.fab_HF_SATELLITE);
        fab_HF_TERRAIN = rootView.findViewById(R.id.fab_HF_TERRAIN);
        fab_HF_HYBRID = rootView.findViewById(R.id.fab_HF_HYBRID);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Test();

        fab_HF3dMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 設定地圖的俯視角度，並且放大到一定的等級，讓3D建築物出現。
                CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(mGoogleMap.getCameraPosition().target)
                                .tilt(60)
                                .zoom(18)
                                .build());
                mGoogleMap.animateCamera(camUpdate);
            }
        });
        fab_HF_NORMAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        fab_HF_SATELLITE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        fab_HF_TERRAIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        fab_HF_HYBRID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        return rootView;
    }

    public void Test() {

        mapFragment01 = SupportMapFragment.newInstance();
        mapFragment01.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                enableMyLocation();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, showAllStoreDataURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = jsonResponse.optJSONArray("stores");
                            int i;
                            for (i = 0; i < jsonArray.length(); i++) {
                                HashMap hashMap = new HashMap<String, String>();
                                JSONObject jsonChild = jsonArray.getJSONObject(i);
                                hashMap.put("store_name", jsonChild.getString("store_name"));
                                hashMap.put("store_category", jsonChild.getString("store_category"));
                                hashMap.put("store_phone", jsonChild.getString("store_phone"));
                                hashMap.put("store_address", jsonChild.getString("store_address"));
                                hashMap.put("store_longitude", jsonChild.getString("store_longitude"));
                                hashMap.put("store_latitude", jsonChild.getString("store_latitude"));
                                hashMap.put("store_intro", jsonChild.getString("store_intro"));
                                hashMap.put("store_no", jsonChild.getString("store_no"));
                                hashMap.put("member_no", jsonChild.getString("member_no"));

                                System.out.println("----捞資料-----" + jsonChild.getString("store_name"));
                                location.add(hashMap);
                            }

                            for (int j = 0; j < location.size(); j++) {
                                System.out.println("執行迴圈");
                                Latitude02 = Double.parseDouble(location.get(j).get("store_latitude"));
                                Longitude02 = Double.parseDouble(location.get(j).get("store_longitude"));
                                store_name = location.get(j).get("store_name");
                                String store_category = location.get(j).get("store_category");
                                if (store_category.equals("火鍋類")) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(Latitude02, Longitude02))
                                            .title(store_name)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.hotpot1))
                                            .alpha(0.7f);
                                    Mmarker = mGoogleMap.addMarker(markerOptions);
                                    Mmarker.showInfoWindow();
                                } else if (store_category.equals("簡餐類")) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(Latitude02, Longitude02))
                                            .title(store_name)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dessert1))
                                            .alpha(0.7f);
                                    Mmarker = mGoogleMap.addMarker(markerOptions);
                                    Mmarker.showInfoWindow();
                                } else if (store_category.equals("燒烤類")) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(Latitude02, Longitude02))
                                            .title(store_name)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bbq1))
                                            .alpha(0.7f);
                                    Mmarker = mGoogleMap.addMarker(markerOptions);
                                    Mmarker.showInfoWindow();
                                } else if (store_category.equals("異國料理")) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(Latitude02, Longitude02))
                                            .title(store_name)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.usfood1))
                                            .alpha(0.7f);
                                    Mmarker = mGoogleMap.addMarker(markerOptions);
                                    Mmarker.showInfoWindow();
                                }

                                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        Mmarker = marker;
                                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                                        intent.putExtra("store_title", marker.getTitle());
                                        startActivity(intent);
                                        return false;
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            Toasty.error(getActivity(), "error", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(getActivity(), "Connection Failed", Toast.LENGTH_SHORT, true).show();
                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                requestQueue.add(stringRequest);

                System.out.println("執行前" + location.size());
            }
        });
        getChildFragmentManager().beginTransaction().replace(R.id.flMapContainer, mapFragment01).commit();
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mGoogleMap != null) {
            // Access to the location has been granted to the app.
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect(); // 停止定位監聽
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect(); // 開始監聽定位
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) { // 定位成功時的回調

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // 更新位置
        updateLocation();
        // 持續監聽
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void updateLocation() {

        if (mLastLocation == null)
            return;

        LatLng latLng = null;

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder().target(latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        cameraPositionBuilder.zoom(17);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPositionBuilder.build());
        // 将视角切换到定位到的位置
        if (mGoogleMap != null) {
            if (position != null) {
                position.remove();
            }
            mGoogleMap.animateCamera(cameraUpdate);
            MarkerOptions markerOptions = new MarkerOptions();
            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic)));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person3));
            markerOptions.position(latLng);
            markerOptions.flat(true);
            // 添加定位Marker
            position = mGoogleMap.addMarker(markerOptions);
        }
    }

    // 循環刷新定位
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // 位置變化監聽
        mLastLocation = location;
        // 更新位置
        updateLocation();
    }



}