package com.hitvardhan.project_app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitvardhan.project_app.AlertDialogueUtils.AlertDialogCallbacks;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.hitvardhan.project_app.R.id.map;

/**
 * Created by Hitvardhan on 09-12-2016.
 *
 * Most comman utils used throughout the app
 */

public class CommanUtils extends Activity{

    private static String strDate,strDateformat;
    private static ArrayList<String> todaydates;

    /**
     * method to display the logs
     * @param key
     * @param value
     */
    public static void displayLogs(String key, String value){
        boolean isDisplay = true;
        if(isDisplay )
            Log.e(""+key, value);
    }


    /**
     * check if a string is empty
     * @param value
     * @return
     */
    public static boolean isEmpty(String value){
        if(value == null || value.length() ==0)
            return true;
        else
            return false;
    }

    /**
     * return an array of todays date in format
     * [YYYY,MM,DD]
     * @return
     */
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
    /**
     * date in simple format
     * @return
     */
    public static String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        strDate = mdformat.format(calendar.getTime());
        return strDate;
    }


    /**
     * get date in required format
     * @param date
     * @return
     */
    public static String getDateformat(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy ");
        strDateformat = mdformat.format(date);
        return strDateformat;
    }

    /**
     * check for network error
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    /**
     * to show comn alert box
     * @param message
     * @param positive
     * @param negative
     * @param context
     * @param callbacks
     */
    public static void showAlertDialog(String message, String positive, String negative,
                                       Context context, final AlertDialogCallbacks callbacks) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        callbacks.onPositiveClick();
                        break;


                    case DialogInterface.BUTTON_NEGATIVE:
                        callbacks.onNegativeClick();
                        break;
                }
                dialog.dismiss();
            }
        };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(positive, dialogClickListener)
                .setNegativeButton(negative, dialogClickListener)
                .show();
    }
}
