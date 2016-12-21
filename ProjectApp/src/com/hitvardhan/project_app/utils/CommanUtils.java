package com.hitvardhan.project_app.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitvardhan.project_app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.hitvardhan.project_app.R.id.map;

/**
 * Created by Hitvardhan on 09-12-2016.
 */

public class CommanUtils extends Activity{

    public static void displayLogs(String key, String value){
        boolean isDisplay = true;
        if(isDisplay )
            Log.e(""+key, value);
    }



    private static String strDate,strDateformat;
    private static ArrayList<String> todaydates;

    /**
     *
     *
     *
     * */
    public static boolean isEmpty(String value){
        if(value == null || value.length() ==0)
            return true;
        else
            return false;
    }





    /*public static void  makeMarker(GoogleMap mMap, double Lat, double Lng, String markerTitle){
    LatLng sydney = new LatLng(Lat, Lng);
    mMap.addMarker(new MarkerOptions().position(sydney).title(markerTitle));
    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
    mMap.animateCamera(zoom);
    mMap.getUiSettings().setZoomControlsEnabled(true);
    mMap.getUiSettings().setAllGesturesEnabled(true);
    mMap.setTrafficEnabled(true);
    mMap.setBuildingsEnabled(true);
    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
}*/



public static ArrayList<String> getTodaysDateArray(){
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
    strDate = mdformat.format(calendar.getTime());
    todaydates = new ArrayList< >();
    for (String dates1: strDate.split("/")) {
        Log.d("DATES LIST_______---___", dates1);
        todaydates.add(dates1);
    }
    return todaydates;
}


    public static String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        strDate = mdformat.format(calendar.getTime());
        return strDate;
    }


    public static String getTodaysDateformat(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy ");
        strDate = mdformat.format(calendar.getTime());
        return strDate;
    }



    public static String getDateformat(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy ");
        strDateformat = mdformat.format(date);
        return strDateformat;
    }

}
