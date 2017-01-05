package com.hitvardhan.project_app.fragment;


import android.Manifest;
import android.app.Activity;
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
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.utils.FetchUrl;
import com.hitvardhan.project_app.utils.LocationPermission;
import com.hitvardhan.project_app.utils.NetworkCallbackInterface;
import com.hitvardhan.project_app.utils.getRouteLatlngURL;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    //Global Variable Declaration
    private ArrayList<LatLng> markerPoints;
    private View contentViewAdmin;
    private GoogleMap mMapAdminView;
    private GoogleApiClient mGoogleApiClientAdmin;
    private LocationRequest mLocationRequestAdmin;
    private Marker mCurrLocationMarkerAdmin;
    private LatLng latLngAdminLoc;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ViewPager viewPagerAdmin;
    private TabLayout tabLayoutAdmin;
    public Response resForAdmin;
    private ViewPagerAdapter adapterAdmin;
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
       /* SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        SupportMapFragment mapFragmentAdmin = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapAdminView));
        mapFragmentAdmin.getMapAsync(this);

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
                buildGoogleApiClient();
                mMapAdminView.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMapAdminView.setMyLocationEnabled(true);
        }


        if (CommanUtils.isNetworkAvailable(getActivity())) {
            try {
                CommanUtils.getDetailsofTask(getActivity(), ((MainActivity) getActivity()).client, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess(Response response) {
                        updateDataOnAdminUi(response);
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
    //Method to connect to the GOOGLE API for location and routes
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientAdmin = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClientAdmin.connect();
    }

    //Get the realtime location in the application
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequestAdmin = new LocationRequest();
        mLocationRequestAdmin.setInterval(1000);
        mLocationRequestAdmin.setFastestInterval(1000);
        mLocationRequestAdmin.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Activity mActivity = getActivity();
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientAdmin,
                    mLocationRequestAdmin, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.connectionSuspension)
                .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.ic_alert_exclamation_mark)
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarkerAdmin != null) {
            mCurrLocationMarkerAdmin.remove();
        }
        //Place current location marker
        latLngAdminLoc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngAdminLoc);
        markerOptions.title("Admin\'s Location");
        markerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_marker_main_2_1));
        mCurrLocationMarkerAdmin = mMapAdminView.addMarker(markerOptions);
        //move map camera
        mMapAdminView.moveCamera(CameraUpdateFactory.newLatLng(latLngAdminLoc));
        mMapAdminView.animateCamera(CameraUpdateFactory.zoomTo(10));
        //stop location updates
        if (mGoogleApiClientAdmin != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientAdmin, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.suspendedConnection)
                .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.ic_alert_exclamation_mark)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClientAdmin == null) {
                            buildGoogleApiClient();
                        }
                        mMapAdminView.setMyLocationEnabled(true);
                        mMapAdminView.moveCamera(CameraUpdateFactory.newLatLng(latLngAdminLoc));
                        mMapAdminView.animateCamera(CameraUpdateFactory.zoomTo(10));
                        mMapAdminView.getUiSettings().setZoomControlsEnabled(true);
                        mMapAdminView.getUiSettings().setAllGesturesEnabled(true);
                        mMapAdminView.setTrafficEnabled(true);
                        mMapAdminView.setBuildingsEnabled(true);


                    }
                } else {
                    // Permission denied, Disable
                    // the functionality that depends
                    // on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


//Public methods


    public void updateDataOnAdminUi(Response rs){
        resForAdmin = rs;
        if(resForAdmin != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //creating a bundle
                    Bundle dataAboutServiceEngg = new Bundle();
                    dataAboutServiceEngg.putSerializable("EnggList",resForAdmin);
                    adapterAdmin = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
                }

            });
        }
    }


}
