package com.hitvardhan.project_app.utils;

import android.util.Log;

import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.Record;

import java.util.ArrayList;
import java.util.List;

import static com.hitvardhan.project_app.activity.MainActivity.latLngMyLoc;
import static com.hitvardhan.project_app.activity.MainActivity.res;

/**
 * Created by Hitvardhan on 24-12-2016.
 */

public class nearestPointUtil {


    private Double LatDiff, LngDiff, MyLocLat, MyLocLng, taskLat, taskLng;
    public static ArrayList<Double> OriginDestDiff;

    //function to calculate the Differce between the Origin Lat Lng
    // and Destination Lat Lng Respectively
    private ArrayList<Double> calcDistance(
            Double originLat
            , Double destinationLat
            , Double originLng
            , Double destinationLng) {
        LatDiff = destinationLat - originLat;
        LngDiff = destinationLng - originLng;
        OriginDestDiff.add(LatDiff);
        OriginDestDiff.add(LngDiff);
        return OriginDestDiff;
    }


    public ArrayList<ArrayList<Double>> getTheSortedArrayofLatLng() {


        MyLocLat = latLngMyLoc.latitude;
        MyLocLng = latLngMyLoc.longitude;
        List<Record> records = res.getRecords();
        ArrayList<Double> distance = new ArrayList<Double>();
        ArrayList<ArrayList<Double>> distanceMeasured = new ArrayList<ArrayList<Double>>();
        for (Record rec : records) {
            taskLat = rec.getLocation__c().getLatitude();
            taskLng = rec.getLocation__c().getLongitude();
            distance = calcDistance(MyLocLat, taskLat, MyLocLng, taskLng);
        }
        distanceMeasured.add(distance);
        return distanceMeasured;
    }

}
