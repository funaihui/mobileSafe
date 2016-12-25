package com.customview.xiaohui.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.customview.xiaohui.mobilesafe.utils.EncryptTools;
import com.customview.xiaohui.mobilesafe.utils.MyConstants;
import com.customview.xiaohui.mobilesafe.utils.SPUtil;

import java.util.List;

public class LocationService extends Service {
    private LocationListener listener;
    private LocationManager locationManager;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.i("Wizardev", "locationManageronCreate: ");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                StringBuilder builder = new StringBuilder();
                float accuracy = location.getAccuracy();//精度
                double altitude = location.getAltitude();//海拔高度
                double latitude = location.getLatitude();//经度
                double longitude = location.getLongitude();//纬度
                builder.append("经度："+accuracy+"\n海拔高度："+altitude+"\n经度："+latitude+"\n纬度："+longitude);
                String s = SPUtil.getString(getApplicationContext(), MyConstants.SAVENUM, "");
                String saveNum = EncryptTools.deciphery(MyConstants.SEED,s);SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(saveNum,"",builder.toString(),null,null);
                stopSelf();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        List<String> allProviders = locationManager.getAllProviders();
        Log.i("Wizardev", "allProviders: "+allProviders);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"没开启权限",Toast.LENGTH_SHORT).show();
            return;
        }
        Criteria c = new Criteria();
        c.setCostAllowed(true);
        c.setSpeedAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(c, true);
        locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
        locationManager = null;
    }
}
