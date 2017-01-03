package com.hitvardhan.project_app.utils;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.google.gson.Gson;
import com.hitvardhan.project_app.AlertDialogueUtils.AlertDialogCallbacks;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.fragment.ServiceEngineer;
import com.hitvardhan.project_app.response_classes.Response;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

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


    /**
     * method to call the connection from the salesforce
     *
     * @throws Exception
     */
    public static void getDetailsofTask(Context context, RestClient client,NetworkCallbackInterface callbackInterface) throws Exception {
        getDetailFromSalesforce(context,client, context.getString(R.string.soql_query), callbackInterface);
    }

    /**
     * Establish a main connection from the salesforce and fetch data from the server
     * The real magic happes
     *
     * @param soql
     * @throws Exception
     */
    public static void getDetailFromSalesforce(final Context context, RestClient client, String soql, final NetworkCallbackInterface callbackInterface) throws Exception {

        //Show the progress dialog box when the data loads
        final ProgressDialog mProgressDialog;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Fetching data...");
        mProgressDialog.show();

        //make a request for query to salesfoce
        RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings
                .getVersionNumber(context), soql);

        //call the async method
        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {
                Response res = null;
                result.consumeQuietly(); // consume before going back to main thread
                try {
                    String record = result.asJSONObject().toString();
                    res = new Gson().fromJson(record, Response.class);
                    Log.e("RESPONSE FROM SERVER", record);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("Exception", String.valueOf(ex));
                }
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                //finally update the changes to the UI
                callbackInterface.onSuccess(res);
            }

            @Override
            public void onError(final Exception exception) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.no_network_title)
                                .setMessage(R.string.no_network_desc)
                                .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(R.drawable.ic_no_network)
                                .show();
                    }
                });
                callbackInterface.onError();
            }
        });
    }

    /**
     * refresh the task activity
     *
     * @param ctx
     * @return
     */
    public static boolean refresh(Context ctx,View rootView, RestClient client,NetworkCallbackInterface callbackInterface) {
        if (CommanUtils.isNetworkAvailable(ctx)) {
            try {
                Snackbar.make(rootView, "You are Online",
                        Snackbar.LENGTH_SHORT).show();
                getDetailsofTask( ctx,  client, callbackInterface);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

            new AlertDialog.Builder(ctx)
                    .setTitle(R.string.no_network_title)
                    .setMessage(R.string.no_network_desc)
                    .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.ic_no_network)
                    .show();
        }
        return true;
    }
}
