package com.hitvardhan.project_app.activity;

/**
 * Created by Hitvardhan
 * Main Activity class to controll and show the Main screen UI with map and tab layout
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import com.hitvardhan.project_app.fragment.ServiceEngineer;
import com.hitvardhan.project_app.utils.NetworkCallbackInterface;
import com.hitvardhan.project_app.utils.ReloadButtonHandler;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.util.EventsObservable;


import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import com.hitvardhan.project_app.fragment.AdminFragment;


public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener {


    //Variable Declaration
    public RestClient client;
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
    private Fragment currentFragment;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //implemented butterKnife
        ButterKnife.bind(this);
        thisActivity = this;
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
                if (currentFragment instanceof ReloadButtonHandler) {
                    ((ReloadButtonHandler)currentFragment).onReload();
                }
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
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        currentFragment.onRequestPermissionsResult(requestCode,permissions, grantResults);
    }

    @Override
    public void onResume() {
        // Hide everything until we are logged in
        // findViewById(R.id.root).setVisibility(View.INVISIBLE);
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
                    CommanUtils.getDetailsofTask(thisActivity, client,
                            new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess(Response response) {
                            ((ServiceEngineer)currentFragment).updateDataOnUi(response);
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
            // findViewById(R.id.root).setVisibility(View.VISIBLE);

            if (CommanUtils.isNetworkAvailable(this)) {
                Snackbar.make((View) findViewById(R.id.main_root), R.string.online,
                        Snackbar.LENGTH_SHORT)
                        .setAction(R.string.action, null).show();
                if(client.getClientInfo().firstName.equalsIgnoreCase("Hitvardhan")) {
                    //Inflate a fragment based on the service user logged in
                    if(currentFragment == null) {
                        currentFragment = new ServiceEngineer();
                    }

                    if(!currentFragment.isAdded()) {
                        FragmentTransaction transaction =
                                getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frm_container, currentFragment, "");
                        transaction.commitAllowingStateLoss();
                    }
                }
                else{
                    //Inflate a fragment based on the ADMIN user logged in
                    AdminFragment mFragObjAdmin = new AdminFragment();
                    FragmentTransaction transaction =
                            getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frm_container, mFragObjAdmin, "");
                    transaction.commit();
                    currentFragment = mFragObjAdmin;
                }


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
    public void updateUi(Response response, Fragment fragment) {
        if(fragment != null) {
            ((AppCompatActivity)thisActivity).getSupportFragmentManager()
                    .beginTransaction().remove(fragment).commit();
        }
        ((ServiceEngineer)currentFragment).updateDataOnUi(response);
    }
}