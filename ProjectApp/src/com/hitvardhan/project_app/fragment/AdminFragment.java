package com.hitvardhan.project_app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hitvardhan.project_app.Adapters.UserAdapter;
import com.hitvardhan.project_app.ImageCache.ImageLoader;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.constants.SoqlQueries;
import com.hitvardhan.project_app.interfaces.NetworkCallbackForAdmin;
import com.hitvardhan.project_app.response_classes.RecordForAdmin;
import com.hitvardhan.project_app.response_classes.ResponseForAdmin;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment implements OnMapReadyCallback {


    //Global Variable Declaration
    private View contentViewAdmin;
    private ArrayList<RecordForAdmin> listOfUsers = new ArrayList<RecordForAdmin>();
    private SwipeRefreshLayout mSwipeRefreshLayout2;
    private RecyclerView mRcvUserListV;
    private UserAdapter mUserAdapter;
    private GoogleMap mAdminUserPositionsMap;
    //Show the progress dialog box when the data loads
    private ProgressDialog mProgressDialog = null;

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentViewAdmin = inflater.inflate(R.layout.fragment_admin, container, false);


        //Recycler View
        mRcvUserListV = (RecyclerView) contentViewAdmin.findViewById(R.id.recycler_view_for_users);
        mUserAdapter = new UserAdapter(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false);
        mRcvUserListV.setLayoutManager(mLayoutManager);
        mRcvUserListV.setItemAnimator(new DefaultItemAnimator());

        mSwipeRefreshLayout2 = (SwipeRefreshLayout) contentViewAdmin.findViewById(R.id
                .swipeRefreshLayoutAdminView);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapForAdminFragmentFrag));
        mapFragment.getMapAsync(this);


        mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getDetailsofEngineers(getContext(), ((MainActivity) getActivity()).client,
                            new NetworkCallbackForAdmin() {
                                @Override
                                public void onSuccess(ResponseForAdmin responseAdmin) {
                                    for (RecordForAdmin rA : responseAdmin.getRecords()) {
                                        if (rA.getName() != null)
                                            listOfUsers.add(rA);
                                    }
                                    upDataOnAdminUi(getActivity(), responseAdmin);

                                }

                                @Override
                                public void onError() {
                                    getActivity().finish();
                                }
                            });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                mSwipeRefreshLayout2.setRefreshing(false);
            }

        });
        mSwipeRefreshLayout2.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);


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
        mAdminUserPositionsMap = googleMap;

        if (((MainActivity) getActivity()).getmResponseForAdmin() != null) {
            setListData(((MainActivity) getActivity()).getmResponseForAdmin());
        } else {

            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Fetching Users...");
            mProgressDialog.show();

            try {
                getDetailsofEngineers(getContext(), ((MainActivity) getActivity()).client,
                        new NetworkCallbackForAdmin() {
                            @Override
                            public void onSuccess(ResponseForAdmin responseAdmin) {
                                upDataOnAdminUi(getActivity(), responseAdmin);
                                if (mProgressDialog.isShowing() != false) {
                                    mProgressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onError() {
                                if (mProgressDialog.isShowing() != false) {
                                    getActivity().finish();
                                }
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private void setListData(ResponseForAdmin responseForAdmin) {
        listOfUsers.clear();
        Integer i = 0;
        Marker newMrker = null;
        ArrayList<Marker> markers = new ArrayList<Marker>();
        for (RecordForAdmin rec4A : responseForAdmin.getRecords()) {
            if (rec4A != null) {
                listOfUsers.add(rec4A);
            }

        }
        for (i = 0; i < responseForAdmin.getRecords().size(); i++) {
            LatLng anyLoc = new LatLng(responseForAdmin.getRecords().get(i)
                    .getLocationC().getLatitude(),
                    responseForAdmin.getRecords().get(i).getLocationC().getLongitude());
            Bitmap imageBitForMarker = null;

            ImageLoader imgLoadrer = new ImageLoader(getActivity());
            imgLoadrer.DisplayImage(responseForAdmin.getRecords().get(i)
                    .getFullPhotoUrl(), null, mAdminUserPositionsMap, getActivity(), anyLoc);

            newMrker = mAdminUserPositionsMap.addMarker(new MarkerOptions().position(anyLoc)
                    .title(responseForAdmin.getRecords().get(i).getName()));

            markers.add(newMrker);

        }

        Log.d("SIZE OF MARKER LIST", String.valueOf(markers.size()));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mAdminUserPositionsMap.animateCamera(cu);
        mAdminUserPositionsMap.setMaxZoomPreference(10);
        mUserAdapter.addItem(listOfUsers);
        mRcvUserListV.setAdapter(mUserAdapter);

    }

    public static void getDetailsofEngineers(Context context, RestClient client,
                                             NetworkCallbackForAdmin callbackInterface)
            throws Exception {
        getDetailOfEngineersFromSalesforce(context, client,
                SoqlQueries.soqlForEngineers,
                callbackInterface);
    }


    public static void getDetailOfEngineersFromSalesforce(final Context context, RestClient client,
                                                          String soql,
                                                          final NetworkCallbackForAdmin
                                                                  callbackInterface)
            throws Exception {


        //make a request for query to salesfoce
        RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings
                .getVersionNumber(context), soql);

        //call the async method
        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {
                ResponseForAdmin resAdmin = null;
                result.consumeQuietly(); // consume before going back to main thread
                try {
                    String recordForAdmin = result.asJSONObject().toString();
                    resAdmin = new Gson().fromJson(recordForAdmin, ResponseForAdmin.class);
                    Log.e("RESPONSE FROM SERVER", recordForAdmin);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("Exception", String.valueOf(ex));
                }
               /* if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();*/

                //finally update the changes to the UI
                callbackInterface.onSuccess(resAdmin);
            }

            @Override
            public void onError(final Exception exception) {
                /*if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();*/
                ((Activity) context).runOnUiThread(new Runnable() {
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

    public void upDataOnAdminUi(final Activity activity, final ResponseForAdmin responseAdmin) {
        final ResponseForAdmin rFa = responseAdmin;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rFa != null) {
                    ((MainActivity) getActivity()).setmResponseForAdmin(rFa);
                    setListData(rFa);
                }
            }
        });
    }
}
