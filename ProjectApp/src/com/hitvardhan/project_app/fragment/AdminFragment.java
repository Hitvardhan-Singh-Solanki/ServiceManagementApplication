package com.hitvardhan.project_app.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.constants.SoqlQueries;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.utils.LocationPermission;
import com.hitvardhan.project_app.interfaces.NetworkCallbackInterface;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment  implements OnMapReadyCallback{
    //Global Variable Declaration
    private ArrayList<LatLng> markerPoints;
    private View contentViewAdmin;
    private GoogleMap mMapAdminView;
    private ViewPager viewPagerAdmin;
    private TabLayout tabLayoutAdmin;
    public Response resForAdmin;
    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentViewAdmin = inflater.inflate(R.layout.fragment_admin, container, false);

        //Get tabs
        viewPagerAdmin = (ViewPager) contentViewAdmin.findViewById(R.id.viewpagerAdminView);

        //Get the tab layout
        tabLayoutAdmin = (TabLayout) contentViewAdmin.findViewById(R.id.tabsAdminView);

        //setup the view pager
        tabLayoutAdmin.setupWithViewPager(viewPagerAdmin);

        //Check for availability of playservices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LocationPermission.checkLocationPermission(getActivity());
        }

        // Initializing
        markerPoints = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragmentAdmin = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapAdminView));
        mapFragmentAdmin.getMapAsync(this);



        /*//Get Details form salesforce
        getDetailsofTask(this);*/

        //finally return the view
        return contentViewAdmin;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapAdminView = googleMap;
        mMapAdminView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               // buildGoogleApiClient();
               // mMapAdminView.setMyLocationEnabled(true);
            }
        } else {
          //  buildGoogleApiClient();
           // mMapAdminView.setMyLocationEnabled(true);
        }


        if (CommanUtils.isNetworkAvailable(getActivity())) {
            try {
                getDetailsOfTask(getActivity(), ((MainActivity) getActivity()).client,
                        new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess(Response response) {


                        //SHOW THE DETAILS FORM THE SERVER ON THE UI

                    }

                    @Override
                    public void onError() {
                        //do nothing
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.no_network_title)
                    .setMessage(R.string.no_network_desc)
                    .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.ic_no_network)
                    .show();
        }
    }



//Public methods

    /**
     * get Details ffrom the server
     * @param context
     * @param client
     * @param callbackInterface
     * @throws Exception
     */
    public static void getDetailsOfTask(Context context, RestClient client,
                                        NetworkCallbackInterface callbackInterface)
            throws Exception {
        getDetailFromSalesforceAdmin(context,client,
                SoqlQueries.soqlForEngineers,
                callbackInterface);
    }

    /**
     * Establish a main connection from the salesforce and fetch data from the server
     * The real magic happens
     *
     * @param soql
     * @throws Exception
     */
    public static void getDetailFromSalesforceAdmin(final Context context, RestClient client,
                                               String soql,
                                               final NetworkCallbackInterface callbackInterface)
            throws Exception {

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
                    Log.e("RESPONSE SERVER ADMIN", record);

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

}
