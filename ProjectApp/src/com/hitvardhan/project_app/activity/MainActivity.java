package com.hitvardhan.project_app.activity;

/**
 *Created by Hitvardhan
 * Main Activity class to controll and show the Main screen UI with map and tab layout
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.hitvardhan.project_app.DataParser;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.fragment.TodayTaskListFragment;
import com.hitvardhan.project_app.fragment.MoreTaskListFragment;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.util.EventsObservable;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mikepenz.iconics.Iconics.TAG;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        NavigationView.OnNavigationItemSelectedListener {


    //Variable Declaration
    private TextView mTxtvHeader;
    public static RestClient client;
    private Gson gson = new Gson();
    private Response res;
    private PasscodeManager passcodeManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    public TextView UserName;
    public TextView UserEmailId;
    public ImageView imgProfile;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public List<Double> lattitueOfTasks, LongitudesOfTask;
    public TextView LogoutButtonNavigation;

    ArrayList<LatLng> MarkerPoints;


    private String serverKey = "AIzaSyBB4Ryb2PjvJZcOBfFebY1NNMh0EoSFgNY";
    private LatLng camera = new LatLng(37.782437, -122.4281893);
    private LatLng origin = new LatLng(37.7849569, -122.4068855);
    private LatLng dest = new LatLng(37.7814432, -122.4460177);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup view
        setContentView(R.layout.main_activity_with_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.header_color));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.setDrawerListener(toggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader = navigationView.getHeaderView(0);
        // Gets an instance of the passcode manager.
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();
        initUi();

        UserName = (TextView) navHeader.findViewById(R.id.Client_name_from_request);
        UserEmailId = (TextView) navHeader.findViewById(R.id.Client_email_id_from_request);
        imgProfile = (ImageView) navHeader.findViewById(R.id.imageView_profile);
        LogoutButtonNavigation = (TextView) findViewById(R.id.Navigation_logout_button);

        LogoutButtonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

       // LogoutButtonNavigation.setOnClickListener(LogoutHandler);
        //Get tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //Get the layout for each tab
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //Check for availabilty of playservices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        // Initializing
        MarkerPoints = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Handles the logout action

            //Show an alert dialog box before logging out
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Are you sure?")
                    .setMessage("Want to Logout?")
                    .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
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
            return true;
        } else if (id == R.id.action_refresh) {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            //Handles refresh action
            if (isNetworkAvailable()) {
                try {
                    Snackbar.make((View) findViewById(R.id.root), "You are Online",
                            Snackbar.LENGTH_SHORT).show();
                    /*getDetailsofTask();*/
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("No Network")
                        .setMessage("Seems like your device is offline")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.ic_alert_exclamation_mark)
                        .show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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







        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already two locations
                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() >= 2) {
                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    Log.d("onMapClick", url.toString());
                    FetchUrl FetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }

            }
        });
























        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        if (isNetworkAvailable()) {
            try {
                getDetailsofTask();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No Network")
                    .setMessage("Seems like your device is offline")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.ic_alert_exclamation_mark)
                    .show();
        }
    }




















    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }




    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }







    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
































    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();



    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
       /* Log.d("Current Location->",String.valueOf(mLastLocation.getLatitude()+mLastLocation.getLongitude()));*/
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_main_2_1));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void initUi() {
        mTxtvHeader = (TextView) findViewById(R.id.mainActivity);

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
                    .setTitle("Are you sure?")
                    .setMessage("Want to Logout?")
                    .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
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

            return true;
        } else if (id == R.id.refresh) {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            //Handles refresh action
            if (isNetworkAvailable()) {
                try {
                    getDetailsofTask();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("No Network")
                        .setMessage("Seems like your device is offline")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SalesforceSDKManager.getInstance().logout(MainActivity.this);
                            }
                        })
                        .setIcon(R.drawable.ic_alert_exclamation_mark)
                        .show();


                return true;
            }
        }
        return true;
    }

    public void backPressedCustom(View v) {
    }

    /**
     * @throws Exception
     */
    public void getDetailsofTask() throws Exception {
        getDetailFromSalesforce("SELECT Id, Name, Due_Date__c, Description__c, Status__c," +
                " External_id__c, Email_Id__c, Location__c,Phone_Number__c, Address__c " +
                "FROM Task__c");
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

    private void onResumeClient(RestClient client) {

        Log.i(TAG, "in onResumeClient!");
        Log.e("print the token", client.getClientInfo().userId);
        // Keeping reference to rest client
        this.client = client;
        if (client != null) {
            Bitmap bmp = null;
            String userName = client.getClientInfo().displayName;
            String userEmail = client.getClientInfo().email;
            Log.d("User name" + userName, "Email of user" + userEmail);
            UserName.setText(userName);
            UserEmailId.setText(userEmail);


            // Show everything
            findViewById(R.id.root).setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                Snackbar.make((View) findViewById(R.id.root), "You are Online",
                        Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                Picasso.with(getBaseContext()).load("https://media.licdn.com/mpr/mpr/" +
                        "shrinknp_200_200/" +
                        "AAEAAQAAAAAAAATDAAAAJGRiODBlMTE5LTA1NDAtNGE5MS04OT" +
                        "kyLWQwNjUwYjY2OWVkMw.jpg")
                        .into(imgProfile);
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("No Network")
                        .setMessage("Seems like your device is offline")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_alert_exclamation_mark)
                        .show();
            }
        }
    }


    /**
     * @throws Exception
     */
    public void getUserNameofCurrentUser() throws Exception {
        //Text Views to populate
        mTxtvHeader.setText("APPLICATION");

    }

    /**
     * @param soql
     * @throws Exception
     */
    private void getDetailFromSalesforce(String soql) throws Exception {
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Fetching data...");
        mProgressDialog.show();
        RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings
                .getVersionNumber(this), soql);
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
                }
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                updateDataOnUi();
            }

            @Override
            public void onError(final Exception exception) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                MainActivity.this.getString(SalesforceSDKManager.getInstance()
                                                .getSalesforceR().stringGenericError(),
                                        exception.toString()),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     *
     */
    private void updateDataOnUi() {
        if (res != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("TaskList", res);
                    TodayTaskListFragment mFragementToday = new TodayTaskListFragment();
                    mFragementToday.setArguments(bundle);
                    MoreTaskListFragment mFragementMore = new MoreTaskListFragment();
                    mFragementMore.setArguments(bundle);
                    // setupViewPager(viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    adapter.addFragment(mFragementToday,
                            "Today's Task");
                    adapter.addFragment(mFragementMore, "Pending Task");
                    viewPager.setAdapter(adapter);


                    lattitueOfTasks = new ArrayList<Double>();
                    LongitudesOfTask = new ArrayList<Double>();


                    for (Record r : res.getRecords()) {
                        Double Latitude = r.getLocation__c().getLatitude();
                        Double Longitude = r.getLocation__c().getLongitude();
                        LatLng markerTasks = new LatLng(Latitude, Longitude);


                        lattitueOfTasks.add(Latitude);
                        LongitudesOfTask.add(Longitude);

                        if (r.getDue_Date__c() != null) {
                            if (!r.getDue_Date__c().trim().equalsIgnoreCase(CommanUtils.getTodaysDate()
                                    .trim())) {
                                mMap.addMarker(new
                                        MarkerOptions().position(markerTasks)
                                        .title(r.getName() + " Task\'s Location")
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.ic_marker_pending_1)));
                            } else {
                                mMap.addMarker(new
                                        MarkerOptions().position(markerTasks)
                                        .title(r.getName() + " Task\'s Location")
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.ic_marker_today_1)));
                            }
                        }
                        mMap.setBuildingsEnabled(true);
                        mMap.setTrafficEnabled(true);

                    }


                }
            });
        } else {
            setupViewPager(viewPager);
        }
    }




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TodayTaskListFragment(), "Today's Task (" + CommanUtils
                .getTodaysDateArray().get(2) + "/" + CommanUtils.getTodaysDateArray().get(1) + ")");
        adapter.addFragment(new MoreTaskListFragment(), "Pending Task");
        viewPager.setAdapter(adapter);
    }


    public void getTaskDetails(View view) {

        TextView Name = (TextView) view.findViewById(R.id.title_name_task);
        Name.getText();
        for (Record r : res.getRecords()) {
            if (r.getName().equalsIgnoreCase(Name.getText().toString())) {
                Intent i = new Intent(getBaseContext(), TaskDetailsActivity.class);
                if (i != null)
                i.putExtra("NAME_OF_USER", r.getName());
                i.putExtra("Descp_of_task", r.getDescription__c());
                i.putExtra("0", r.getDue_Date__c());
                i.putExtra("1", r.getAddress__c());
                i.putExtra("2", r.getPhone_Number__c());
                i.putExtra("3", r.getStatus__c());
                i.putExtra("4", r.getId());
                i.putExtra("5", r.getAttributes().getType());
                //i.putExtra("6",client);
                startActivity(i);
            }
        }

    }

    //Check for network connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void LogoutClickedNavigation(View view) {
        //Show an alert dialog box before logging out
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure?")
                .setMessage("Want to Logout?")
                .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
    }


}