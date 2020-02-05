package com.example.asus.foodv1app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class Maps extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    private SupportMapFragment mSupportMapFragment;
    private boolean mbIsZoomFirst = true;

    private Polyline mPolylineRoute;

    Location mLastLocation; // 最新定位信息
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private Marker position; // Marker对象，添加定位位置用到

    // 循环刷新定位信息延时
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();


        // 設定Google Map的Info Window。
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.map_info_window, null);
                TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
                txtTitle.setText(marker.getTitle());
                TextView txtSnippet = (TextView) v.findViewById(R.id.txtSnippet);
                txtSnippet.setText(marker.getSnippet());
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        // 設定Info Window的OnClickListener。
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        });

        // 建立Polyline，並且先將它隱藏。
        PolylineOptions polylineOpt = new PolylineOptions().width(15).color(Color.BLUE);
        ArrayList<LatLng> listLatLng = new ArrayList<LatLng>();
        listLatLng.add(new LatLng(25.0336110, 121.5650000));
        listLatLng.add(new LatLng(25.037, 121.5650000));
        listLatLng.add(new LatLng(25.037, 121.5630000));
        polylineOpt.addAll(listLatLng);
        mPolylineRoute = mMap.addPolyline(polylineOpt);
        mPolylineRoute.setVisible(false);
    }
    /**

     * 如果取得了权限,显示地图定位层

     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(Maps.this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect(); // 停止定位监听
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect(); // 开始监听定位
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) { // 定位成功时的回调


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // 更新位置
        updateLocation();
        // 持续监听
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void updateLocation() {

        if(mLastLocation == null)
            return;

        LatLng latLng = null;

        CameraPosition.Builder cameraPositionBuilder = new CameraPosition.Builder().target(latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        cameraPositionBuilder.zoom(17);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPositionBuilder.build());
        // 将视角切换到定位到的位置
        if (mMap != null) {
            if (position != null) {
                position.remove();
            }
            mMap.animateCamera(cameraUpdate);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic)));
            markerOptions.position(latLng);
            markerOptions.flat(true);
            // 添加定位Marker
            position = mMap.addMarker(markerOptions);
        }
    }
    // 循环刷新定位
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
        // 位置变化监听
        mLastLocation = location;
        // 更新位置
        updateLocation();
    }
}
