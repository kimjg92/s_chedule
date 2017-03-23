package com.shinhan.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static android.os.Build.VERSION_CODES.M;
import static com.shinhan.myapplication.R.id.location;
import static java.lang.Thread.sleep;

public class GoogleMapExam extends AppCompatActivity {

    SupportMapFragment mapFragment;
    GoogleMap map;
    int index = 0;
    class MyMarker{
        String name;
        LatLng latLng;
        MyMarker(String name, LatLng latLng){
            this.name = name;
            this.latLng = latLng;
        }
    }

    MyMarker []  markers =  {
            new MyMarker ("워터루역", new LatLng(51.503219,-0.112369)),
            new MyMarker ("런던아이", new LatLng(51.503304,-0.119586)),
            new MyMarker ("콜로세움", new LatLng(41.890210,12.492231)),
            new MyMarker ("신한은행", new LatLng(37.662299,126.770709)),
            new MyMarker ("홍대", new LatLng(37.550838,126.921831)),
            new MyMarker ("우리집", new LatLng(37.509783,126.924862)),
            new MyMarker ("학교", new LatLng(37.503609,126.957026)),
            new MyMarker ("용남고", new LatLng(36.294633,127.244964)),
            new MyMarker ("홍콩", new LatLng(22.431081,114.103112)),
            new MyMarker ("코타키나발루", new LatLng(5.994602,116.082189))

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_exam);

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap; // 비동기방식으로 구글 지도 객체 획득
                PolylineOptions rectOptions = new PolylineOptions();
                rectOptions.color(Color.RED);
                for(int i = 0 ; i < markers.length ; i++){
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(markers[i].latLng);
                    marker.title(markers[i].name);
                    map.addMarker(marker);
                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
                            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),15));
                            return false;
                        }
                    });
                    rectOptions.add(markers[i].latLng);
                }
                Polyline polyline = map.addPolyline(rectOptions);
            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"GPS 권한 필요합니다.",Toast.LENGTH_SHORT).show();
            } else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"GPS 권한 승인!",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"GPS 권한 거부!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GPSListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {//위치 변경시 작동하는 함수
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            TextView textView = (TextView)findViewById(R.id.location);
            textView.setText("내위치 : "+latitude +" , "+longitude);
            Toast.makeText(GoogleMapExam.this, "위도 : "+latitude + " / 경도 : "+longitude,Toast.LENGTH_SHORT).show();

            LatLng curPoint = new LatLng(latitude,longitude);
            if(map != null){
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void startLocationService(View view){

        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {

            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null){
                TextView textView = (TextView) findViewById(R.id.location);
                textView.setText("내 위치 : " + location.getLatitude() + " / " + location.getLongitude());
                Toast.makeText(this, "Last Know Location - 위도 : " + location.getLatitude() +
                    " / 경도 : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng curPoint = new LatLng(location.getLatitude(),location.getLongitude());
                if(map != null){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
                }
            }
            GPSListener gpsListener = new GPSListener();
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0 , gpsListener);


        }
    }

    public void onNextButtonClick (View v){
        if(map != null){
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers[index++%10].latLng,15));
            map.animateCamera(CameraUpdateFactory.zoomTo(map.getMinZoomLevel()));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(markers[index++%10].latLng,15));
        }
    }

    public void onWorldMapButtonClick(View v) {
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.moveCamera(CameraUpdateFactory.zoomTo(map.getMinZoomLevel()));
        }
    }
}
