package com.hitvardhan.project_app.activity;

/**
 * Created by Hitvardhan
 * Main Activity class to controll and show the Main screen UI with map and tab layout
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import com.hitvardhan.project_app.ImageCache.ImageLoader;
import com.hitvardhan.project_app.fragment.ServiceEngineer;
import com.hitvardhan.project_app.fragment.TaskAdminFragment;
import com.hitvardhan.project_app.response_classes.ResponseForAdmin;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.util.EventsObservable;


import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.hitvardhan.project_app.fragment.AdminFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener {


    //Variable Declaration
    private Gson gson = new Gson();
    public static Response res;
    private PasscodeManager passcodeManager;
    public TextView userNameView;
    public TextView userEmailId;
    public ImageView imgProfile;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static LatLng latLngMyLoc;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private LinearLayout logoutButtonNavigation
            , taskButtonnavigation
            , userButtonNavigation;
    private Fragment currentFragment;
    public static RestClient client;
    private String salesForceImageUrl;
    private View navHeader;
    private ResponseForAdmin mResponseForAdmin;
    public ImageView addTaskToolbar, refreshScreenToolbar;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // Setup view
        setContentView(R.layout.main_activity_with_navigation);


        //Setup Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarMainTest);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));


        //Setup Navigation Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Toggle button
        toggle = new ActionBarDrawerToggle(
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
        navHeader = navigationView.getHeaderView(0);


        // Gets an instance of the passcode manager.
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();

        //Set the username and otherInfo from the client to header
        userNameView = (TextView) navHeader.findViewById(R.id.Client_name_from_request);
        userEmailId = (TextView) navHeader.findViewById(R.id.Client_email_id_from_request);
        imgProfile = (ImageView) navHeader.findViewById(R.id.imageView_profile);


        addTaskToolbar =
                (ImageView) findViewById(R.id.add_task_admin);
        refreshScreenToolbar =
                (ImageView) findViewById(R.id.refresh_in_toolbar);


        refreshScreenToolbar.setVisibility(View.VISIBLE);
        refreshScreenToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminFragment)currentFragment).onRefreshClicked();
            }
        });

        taskButtonnavigation = (LinearLayout) navigationView
                .findViewById(R.id.navigation_tasks_admin);
        userButtonNavigation = (LinearLayout) navigationView
                .findViewById(R.id.navigation_users_button);

        taskButtonnavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment.getClass() != TaskAdminFragment.class) {
                    currentFragment = new TaskAdminFragment();
                    FragmentTransaction transaction =
                            getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frm_container, currentFragment, "");
                    transaction.addToBackStack(null);
                    refreshScreenToolbar.setVisibility(View.GONE);
                    addTaskToolbar.setVisibility(View.VISIBLE);
                    addTaskToolbar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent createANewTaskActivity =
                                    new Intent(getApplicationContext(),
                                            MakeNewTaskActivity.class);
                            startActivity(createANewTaskActivity);
                        }
                    });
                    transaction.commitAllowingStateLoss();
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        userButtonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment.getClass() != AdminFragment.class) {
                    currentFragment = new AdminFragment();
                    FragmentTransaction transaction =
                            getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frm_container, currentFragment, "");
                    transaction.addToBackStack(null);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    refreshScreenToolbar.setVisibility(View.VISIBLE);
                    addTaskToolbar.setVisibility(View.GONE);

                    transaction.commitAllowingStateLoss();
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
        currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        if(!(currentFragment instanceof ServiceEngineer) &&
                !(currentFragment instanceof AdminFragment)) {
            super.onBackPressed();
        }
        //Close the navigation slider
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
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


    /**
     * passcode chanllenge to handle the login/logout function using salesforce SDK
     */
    private void passcodeChallenge() {
        // Brings up the passcode screen if needed.
        if (passcodeManager.onResume(this)) {

            // work around to avoid a memory leak when we call getRestClient with the activity

            try {
                ClientManager clientManager = new ClientManager(getApplicationContext(),
                        SalesforceSDKManager.getInstance().getAccountType(),
                        SalesforceSDKManager.getInstance().getLoginOptions(),
                        SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked());

                client = clientManager.peekRestClient();
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
            salesForceImageUrl = client.getClientInfo().photoUrl;


            //caching of the image
            OkHttpClient clientForImage = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    .addHeader("Authorization", "Bearer " + client.getAuthToken())
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    })
                    .build();

            Picasso picasso = new Picasso.Builder(getBaseContext())
                    .downloader(new OkHttp3Downloader(clientForImage))
                    .build();
            picasso.load(salesForceImageUrl).into(imgProfile);


            // Show everything
            // findViewById(R.id.root).setVisibility(View.VISIBLE);

            if (CommanUtils.isNetworkAvailable(this)) {
                Snackbar.make((View) findViewById(R.id.main_root), R.string.online,
                        Snackbar.LENGTH_SHORT)
                        .setAction(R.string.action, null).show();
                if (client.getClientInfo().userId.equalsIgnoreCase(getString(R.string.adminUserId))) {
                    taskButtonnavigation.setVisibility(View.VISIBLE);
                    userButtonNavigation.setVisibility(View.VISIBLE);
                    //Inflate a fragment based on the ADMIN user logged in
                    if (currentFragment == null) {
                        currentFragment = new AdminFragment();
                    }
                    if (!currentFragment.isAdded()) {
                        FragmentTransaction transaction =
                                getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frm_container, currentFragment, "");
                        transaction.addToBackStack("");
                        transaction.commitAllowingStateLoss();
                    }
                } else {
                    //Inflate a fragment based on the service user logged in
                    if (currentFragment == null) {
                        currentFragment = new ServiceEngineer();
                    }

                    if (!currentFragment.isAdded()) {
                        FragmentTransaction transaction =
                                getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frm_container, currentFragment, "");
                        transaction.addToBackStack("");
                        transaction.commitAllowingStateLoss();
                    }
                }

            } else {
                new AlertDialog.Builder(this)
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

   /* public void replaceFragment (Fragment fragment, String backStateName{
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        if (!fragmentPopped){ //fragment not in back stack, create it.
             FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frm_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }*/

    public void setCurrentFragment(Fragment fragment) {
        currentFragment = fragment;
    }

    /**
     * Update on UI on load of a fragment
     *
     * @param response
     */
    public void updateUi(Response response) {
        ((ServiceEngineer) currentFragment).updateDataOnUi(response);
    }


    public ResponseForAdmin getmResponseForAdmin() {
        return mResponseForAdmin;
    }

    public void setmResponseForAdmin(ResponseForAdmin mResponseForAdmin) {
        this.mResponseForAdmin = mResponseForAdmin;
    }

    public void showHamburger() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    public void showBack() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}