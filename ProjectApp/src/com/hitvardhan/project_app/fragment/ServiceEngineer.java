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
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.utils.FetchUrl;
import com.hitvardhan.project_app.utils.LocationPermission;
import com.hitvardhan.project_app.interfaces.NetworkCallbackInterface;
import com.hitvardhan.project_app.interfaces.ReloadButtonHandler;
import com.hitvardhan.project_app.utils.getRouteLatlngURL;
import com.salesforce.androidsdk.rest.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceEngineer extends Fragment  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ReloadButtonHandler{


    //Variable Declaration
    public static RestClient client;
    public Response res;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker mCurrLocationMarker;
    public static LocationRequest mLocationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public List<Double> lattitueOfTasks, longitudesOfTasks;
    public static LatLng latLngMyLoc;
    private ArrayList<LatLng> markerPoints;
    private ArrayList<LatLng> latLngOfTasks;
    private View contentView;
    private TodayTaskListFragment mFragementToday;
    private MoreTaskListFragment mFragementMore;
    private ViewPagerAdapter adapter;
    private Fragment thisFragment;
    public ServiceEngineer() {
        // Required empty public constructor
    }

    @Override
    public void onReload() {
        CommanUtils.refresh(getActivity(), getActivity().findViewById(R.id.main_root), client
                , new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess(Response response) {
                        ((MainActivity)getActivity()).updateUi(response);
                    }

                    @Override
                    public void onError() {
                        //do nothing
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_service_engineer, container, false);

        //Get the fragment

        thisFragment = this;
        //Get tabs
        viewPager = (ViewPager) contentView.findViewById(R.id.viewpager);

        //Get the layout for each tab

        tabLayout = (TabLayout) contentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        //Check for availability of playservices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LocationPermission.checkLocationPermission(getActivity());
        }


        // Initializing
        markerPoints = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        //     updateDataOnUi();
        return contentView;
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
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        if (CommanUtils.isNetworkAvailable(getActivity())) {
            try {
                CommanUtils.getDetailsofTask(getActivity(), ((MainActivity) getActivity()).client,
                        new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess(Response response) {
                        updateDataOnUi(response);
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
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //Get the realtime location in the application
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Activity mActivity = getActivity();
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
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
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        latLngMyLoc = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngMyLoc);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_marker_main_2_1));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngMyLoc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
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

    /**
     * method to update the fetched data on the UI
     */
    public void updateDataOnUi(Response rs) {
        res = rs;
        if (res != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //creating a bundle
                    Bundle bundle = new Bundle();
                    //adding data to bundle
                    bundle.putSerializable(getString(R.string.taskList), res);

                    if( adapter == null) {
                        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

                        mFragementToday = new TodayTaskListFragment();
                        mFragementToday.setArguments(bundle);
                        adapter.addFragment(mFragementToday, getString(R.string.todays_task_title));

                        mFragementMore = new MoreTaskListFragment();
                        mFragementMore.setArguments(bundle);
                        adapter.addFragment(mFragementMore, getString(R.string.pending_task_title));
                        viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        mFragementToday.setListData(res);
                        mFragementMore.setListData(res);
                    }

                    //Initialization of the location list
                    lattitueOfTasks = new ArrayList<Double>();
                    longitudesOfTasks = new ArrayList<Double>();
                    latLngOfTasks = new ArrayList<LatLng>();

                    for (Record r : res.getRecords()) {
                        Double Latitude = r.getLocation__c().getLatitude();
                        Double Longitude = r.getLocation__c().getLongitude();
                        LatLng markerTasks = new LatLng(Latitude, Longitude);
                        LatLng inListElements = new LatLng(Latitude, Longitude);

                        lattitueOfTasks.add(Latitude);
                        longitudesOfTasks.add(Longitude);
                        latLngOfTasks.add(inListElements);

                        if (r.getDue_Date__c() != null) {
                            if (!r.getDue_Date__c().trim()
                                    .equalsIgnoreCase(CommanUtils.getTodaysDate()
                                            .trim())) {
                                mMap.addMarker(new
                                        MarkerOptions().position(markerTasks)
                                        .title(r.getName() + getString(R.string.task_location))
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.ic_marker_pending_1)));
                            } else {
                                mMap.addMarker(new
                                        MarkerOptions().position(markerTasks)
                                        .title(r.getName() + getString(R.string.task_location))
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.ic_marker_today_1)));
                            }
                        }
                        mMap.setBuildingsEnabled(true);
                        mMap.setTrafficEnabled(true);

                        //ROUTE BUILDING

                        //Adding LAT LONG of my device location
                        markerPoints.add(latLngMyLoc);


                        //pass the 1th param of LATLNG into the argument
                        //TODO: Make the shortest route between the given markers
                        markerPoints.add(latLngOfTasks.get(0));


                        // Creating MarkerOptions
                        MarkerOptions options = new MarkerOptions();

                        // Setting the position of the marker
                        if (latLngMyLoc != null)
                            options.position(latLngMyLoc);

                        // Checks, whether start and end locations are captured
                        if (markerPoints.size() >= 2) {
                            LatLng origin = markerPoints.get(0);
                            LatLng dest = markerPoints.get(1);

                            // Getting URL to the Google Directions API
                            if (origin != null && dest != null) {
                                String url = getRouteLatlngURL.getUrl(origin, dest);
                                FetchUrl FetchUrl = new FetchUrl();

                                // Start downloading json data from Google Directions API
                                FetchUrl.execute(url);
                                //move map camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                                mMap.getUiSettings().setAllGesturesEnabled(true);
                                mMap.setTrafficEnabled(true);
                                mMap.setBuildingsEnabled(true);
                            }
                        }
                    }
                }
            });
        } else {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity()
                    .getSupportFragmentManager());
            adapter.addFragment(new TodayTaskListFragment(), "Today's Task");
            adapter.addFragment(new MoreTaskListFragment(), "Pending Task");
            viewPager.setAdapter(adapter);
             adapter.notifyDataSetChanged();
        }
    }



    /**
     * setup the view pager to show the Recycler View
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new TodayTaskListFragment(), "Today's Task");
        adapter.addFragment(new MoreTaskListFragment(), "Pending Task");
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        }
}


