package com.hitvardhan.project_app.activity;

/**
 * Created by Hitvardhan
 * Main Activity class to controll and show the Main screen UI with map and tab layout
 */

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import com.hitvardhan.project_app.AlertDialogueUtils.AlertDialogPositiveCallback;
import com.hitvardhan.project_app.utils.LocationPermission;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.util.EventsObservable;


import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.fragment.TodayTaskListFragment;
import com.hitvardhan.project_app.fragment.MoreTaskListFragment;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.utils.FetchUrl;
import com.hitvardhan.project_app.utils.getRouteLatlngURL;

//import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        NavigationView.OnNavigationItemSelectedListener {


    //Variable Declaration
    public static RestClient client;
    private Gson gson = new Gson();
    public static Response res;
    private PasscodeManager passcodeManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker mCurrLocationMarker;
    public static LocationRequest mLocationRequest;
    public TextView userNameView;
    public TextView userEmailId;
    public ImageView imgProfile;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public List<Double> lattitueOfTasks, longitudesOfTasks;
    public static LatLng latLngMyLoc;
    private ArrayList<LatLng> markerPoints;
    private ArrayList<LatLng> latLngOfTasks;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private LinearLayout logoutButtonNavigation;
    private ImageView reloadButtonOnNavHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //implemented butterKnife
        ButterKnife.bind(this);

        // Setup view
        setContentView(R.layout.main_activity_with_navigation);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));


        //Setup Navigation Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Toggle button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toggle.syncState();
        //Setup the navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        //Get the navigation header
        View navHeader = navigationView.getHeaderView(0);


        // Gets an instance of the passcode manager.
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();

        //Set the username and otherInfo from the client to header
        userNameView = (TextView) navHeader.findViewById(R.id.Client_name_from_request);
        userEmailId = (TextView) navHeader.findViewById(R.id.Client_email_id_from_request);
        imgProfile = (ImageView) navHeader.findViewById(R.id.imageView_profile);
        reloadButtonOnNavHeader = (ImageView) navHeader.findViewById(R.id.reload_nav_header_image);


        //adding reload functionality to the nav header
        reloadButtonOnNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(MainActivity.this);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //Setup the navigation logout button.
        logoutButtonNavigation = (LinearLayout) navigationView
                .findViewById(R.id.navigation_logout_main);
        logoutButtonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Logout manager
                //Show an alert dialog box before logging out
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Want to Logout?")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Logout
                                SalesforceSDKManager.getInstance().logout(MainActivity.this);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_alert_exclamation_mark)
                        .show();
                //close the drawer after click
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //Get tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Get the layout for each tab

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        //Check for availability of playservices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LocationPermission.checkLocationPermission(MainActivity.this);
        }


        // Initializing
        markerPoints = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        if (CommanUtils.isNetworkAvailable(getBaseContext())) {
            try {
                getDetailsofTask();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(MainActivity.this)
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        new AlertDialog.Builder(MainActivity.this)
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
        new AlertDialog.Builder(MainActivity.this)
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
                    if (ContextCompat.checkSelfPermission(this,
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
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {

        // Hide everything until we are logged in
        findViewById(R.id.root).setVisibility(View.INVISIBLE);

        super.onResume();
        passcodeChallenge();
    }

    @Override
    public void onBackPressed() {
        //Close the navigation slider
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Move to home screen instead of the previous activity
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.onLogoutClicked) {

            //Handles the logout action
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            //Show an alert dialog box before logging out
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.are_you_sure_question)
                    .setMessage(R.string.Logout_text)
                    .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SalesforceSDKManager.getInstance().logout(MainActivity.this);
                        }
                    })
                    .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_alert_exclamation_mark)
                    .show();

            return true;
        } else if (id == R.id.refresh) {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            //Handles refresh action
            if (CommanUtils.isNetworkAvailable(getBaseContext())) {
                try {
                    getDetailsofTask();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.no_network_title)
                        .setMessage(R.string.no_network_desc)
                        .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_no_network)
                        .show();
                return true;
            }
        }
        return true;
    }


    @Override
    public void onPause() {
        passcodeManager.onPause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        passcodeManager.nolongerFrontActivity(this);
        super.onDestroy();
    }

    private void passcodeChallenge() {
        // Brings up the passcode screen if needed.
        if (passcodeManager.onResume(this)) {

            // work around to avoid a memory leak when we call getRestClient with the activity

            try {
                ClientManager clientManager = new ClientManager(getApplicationContext(),
                        SalesforceSDKManager.getInstance().getAccountType(),
                        SalesforceSDKManager.getInstance().getLoginOptions(),
                        SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked());

                RestClient client = clientManager.peekRestClient();
                if (client != null) {
                    //onResumeClient(client);
                    //showTheAppSelectionDialog(client);
                    onResumeClient(client);
                    return;
                }
            } catch (Exception e) {
                // continue with the below flow if we get any exceptions or if the client is null
                e.printStackTrace();
            }

            // Gets login options.
            final String accountType = SalesforceSDKManager.getInstance().getAccountType();
            final ClientManager.LoginOptions loginOptions = SalesforceSDKManager.getInstance()
                    .getLoginOptions();

            // Gets a rest client.
            new ClientManager(getApplicationContext(), accountType, loginOptions,
                    SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked())
                    .getRestClient(this, new ClientManager.RestClientCallback() {

                        @Override
                        public void authenticatedRestClient(RestClient client) {
                            if (client == null) {
                                SalesforceSDKManager.getInstance().logout(MainActivity.this);
                                return;
                            }

                            onResumeClient(client);

                            // showTheAppSelectionDialog(client);
                            // Lets observers know that rendition is complete.
                            EventsObservable.get().notifyEvent(EventsObservable
                                    .EventType.RenditionComplete);
                        }
                    });
        }
    }


    /**
     * Method to call the Rest Client and make request to the server
     *
     * @param client
     */
    private void onResumeClient(final RestClient client) {

        // Keeping reference to rest client
        this.client = client;
        if (client != null) {
            Bitmap bmp = null;
            String userName = client.getClientInfo().displayName;
            String userEmail = client.getClientInfo().email;
            userNameView.setText(userName);
            userEmailId.setText(userEmail);


            // Show everything
            findViewById(R.id.root).setVisibility(View.VISIBLE);

            if (CommanUtils.isNetworkAvailable(this)) {
                Snackbar.make((View) findViewById(R.id.root), R.string.online,
                        Snackbar.LENGTH_SHORT)
                        .setAction(R.string.action, null).show();

                /*Picasso.with(getBaseContext())
                .load("https://media.licdn.com/mpr/mpr/shrinknp_200_200/
                AAEAAQAAAAAAAATDAAAAJGRiODBlMTE5LTA1NDAtNGE5MS04OTkyLWQwNjUwYjY2OWVkMw.jpg")
                        .into(imgProfile);*/

                //An Async task to do in Backround thread
                //Creates a HTTP Connection with the header as Authetication Token as Bearer
                //and download the Image, saving it as a Bitmap
                new AsyncTask<String, Void, Void>() {

                    //Bitmap to hold the downloaded Image from Salesforce
                    Bitmap myBitmap;

                    //Background thread
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            //URL to connect
                            java.net.URL url = new java.net.URL(params[0]);
                            //Connection Request
                            HttpURLConnection connection =
                                    (HttpURLConnection) url.openConnection();
                            //set the Auth Token in header
                            connection.setRequestProperty("Authorization",
                                    "Bearer " + client.getAuthToken());
                            connection.setDoInput(true);
                            //make the final connection
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            //save the image
                            myBitmap = BitmapFactory.decodeStream(input);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        return null;
                    }


                    //method to do onPost Exectuion i.e. to update changes on UI
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        //set the Image
                        imgProfile.setImageBitmap(myBitmap);
                        Log.i("sth", "" + (myBitmap == null));
                    }
                }.execute(client.getClientInfo().photoUrl);//execute supplies the Image URL


            } else {
                new AlertDialog.Builder(MainActivity.this)
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
    }


    /**
     * method to call the connection from the salesforce
     *
     * @throws Exception
     */
    public void getDetailsofTask() throws Exception {
        getDetailFromSalesforce(getString(R.string.soql_query));
    }

    /**
     * Establish a main connection from the salesforce and fetch data from the server
     * The real magic happes
     *
     * @param soql
     * @throws Exception
     */
    public void getDetailFromSalesforce(String soql) throws Exception {

        //Show the progress dialog box when the data loads
        final ProgressDialog mProgressDialog;


        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Fetching data...");
        mProgressDialog.show();

        //make a request for query to salesfoce
        RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings
                .getVersionNumber(this), soql);

        //call the async method
        client.sendAsync(restRequest, new AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {
                result.consumeQuietly(); // consume before going back to main thread
                try {
                    String record = result.asJSONObject().toString();
                    res = gson.fromJson(record, Response.class);
                    Log.e("RESPONSE FROM SERVER", record);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("Exception", String.valueOf(ex));
                }
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                //finally update the changes to the UI
                updateDataOnUi();
            }

            @Override
            public void onError(final Exception exception) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(MainActivity.this)
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
            }
        });
    }

    /**
     * method to update the fetched data on the UI
     */
    private void updateDataOnUi() {
        if (res != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //creating a bundle
                    Bundle bundle = new Bundle();
                    //adding data to bundle
                    bundle.putSerializable(getString(R.string.taskList), res);

                    //Creating a new fragment of TodayTask
                    TodayTaskListFragment mFragementToday = new TodayTaskListFragment();
                    //Set the bundle data to the fragment
                    mFragementToday.setArguments(bundle);

                    //Creating the new fragment of MoreTask
                    MoreTaskListFragment mFragementMore = new MoreTaskListFragment();
                    //setting uo the bundle data of more task
                    mFragementMore.setArguments(bundle);

                    // setupViewPager(viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

                    //adding the fragment for todays tasks
                    adapter.addFragment(mFragementToday,
                            getString(R.string.todays_task_title));

                    //adding the fragment for pending tasks
                    adapter.addFragment(mFragementMore, getString(R.string.pending_task_title));

                    //finally setting the adapter
                    viewPager.setAdapter(adapter);

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

                        //Adding LAT LONG 0f my device location
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
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new TodayTaskListFragment(), "Today's Task");
            adapter.addFragment(new MoreTaskListFragment(), "Pending Task");
        }
    }

    /**
     * setup the view pager to show the Recycler View
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TodayTaskListFragment(), "Today's Task");
        adapter.addFragment(new MoreTaskListFragment(), "Pending Task");
        viewPager.setAdapter(adapter);
    }


    /**
     * send details to task_details_activity
     *
     * @param view
     */
    public void getTaskDetails(View view) {
        TextView Name = (TextView) view.findViewById(R.id.title_name_task);
        Name.getText();
        for (Record r : res.getRecords()) {
            if (r.getName().equalsIgnoreCase(Name.getText().toString())) {
                Intent i = new Intent(getBaseContext(), TaskDetailsActivity.class);
                if (i != null) {
                    i.putExtra(getString(R.string.nameOfTask), r.getName());
                    i.putExtra(getString(R.string.descOfTask), r.getDescription__c());
                    i.putExtra(getString(R.string.dueDate), r.getDue_Date__c());
                    i.putExtra(getString(R.string.addressOfTask), r.getAddress__c());
                    i.putExtra(getString(R.string.contactInfoOftask), r.getPhone_Number__c());
                    i.putExtra(getString(R.string.statusOfTask), r.getStatus__c());
                    i.putExtra(getString(R.string.taskID), r.getId());
                    i.putExtra(getString(R.string.taskType), r.getAttributes().getType());
                    i.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }
            }
        }
    }

    /**
     * refresh the task activity
     *
     * @param ctx
     * @return
     */
    public boolean refresh(Activity ctx) {
        if (CommanUtils.isNetworkAvailable(ctx)) {
            try {
                Snackbar.make((View) ctx.findViewById(R.id.root), "You are Online",
                        Snackbar.LENGTH_SHORT).show();
                getDetailsofTask();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {

            new AlertDialog.Builder(MainActivity.this)
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