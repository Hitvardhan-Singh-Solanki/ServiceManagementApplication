package com.hitvardhan.project_app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.google.gson.Gson;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.constants.SoqlQueries;
import com.hitvardhan.project_app.interfaces.NetworkCallbackInterface;
import com.hitvardhan.project_app.response_classes.Response;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import hitAndTrial.Tab1;
import hitAndTrial.Tab2;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskAdminFragment extends Fragment {


    private View fragmentTaskAdminView;
    private AssignedTaskFragment mAssignedTaskFragment;

    public TaskAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentTaskAdminView = inflater.inflate(R.layout.fragment_task_admin, container, false);
        final ViewPager viewPager = (ViewPager) fragmentTaskAdminView
                .findViewById(R.id.pager_for_admin);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        TabLayout tabLayout = (TabLayout) fragmentTaskAdminView
                .findViewById(R.id.tab_for_task_fragment_admin);

        tabLayout.setupWithViewPager(viewPager);

        try {
            getDetailsOfTaskForAdmin(getContext(), ((MainActivity) getActivity()).client,
                    new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess(Response response) {
                            final Response res = response;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //creating a bundle
                                    Bundle bundle = new Bundle();
                                    //adding data to bundle
                                    bundle.putSerializable("TaskListss", res);
                                    AssignedTaskFragment tab1 = new AssignedTaskFragment();
                                    tab1.setArguments(bundle);
                                    Tab2 tab2 = new Tab2();
                                    adapter.addFragment(tab1, "Assigned");
                                    adapter.addFragment(tab2, "Not Assigned");
                                    viewPager.setAdapter(adapter);
                                }
                            });


                        }

                        @Override
                        public void onError() {
                            //do nothing
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragmentTaskAdminView;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void getDetailsOfTaskForAdmin(Context context, RestClient client,
                                                NetworkCallbackInterface callbackInterface)
            throws Exception {
        getDetailOfEngineersFromSalesforce(context, client,
                SoqlQueries.soqlForTasks,
                callbackInterface);
    }

    public static void getDetailOfEngineersFromSalesforce(final Context context, RestClient client,
                                                          String soql,
                                                          final NetworkCallbackInterface
                                                                  callbackInterface)
            throws Exception {
        //Show the progress dialog box when the data loads
        final ProgressDialog mProgressDialog;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Fetching Tasks...");
        mProgressDialog.show();

        //make a request for query to salesfoce
        RestRequest restRequest = RestRequest.getRequestForQuery(ApiVersionStrings
                .getVersionNumber(context), soql);

        //call the async method
        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, final RestResponse result) {
                Response resAdminForTask = null;
                result.consumeQuietly(); // consume before going back to main thread
                try {
                    String recordForAdmin = result.asJSONObject().toString();
                    resAdminForTask = new Gson().fromJson(recordForAdmin, Response.class);
                    Log.e("RESPONSE FROM SERVER", recordForAdmin);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("Exception", String.valueOf(ex));
                }
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                //finally update the changes to the UI
                callbackInterface.onSuccess(resAdminForTask);
            }

            @Override
            public void onError(final Exception exception) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.no_network_title)
                                .setMessage(R.string.no_network_desc)
                                .setPositiveButton(R.string.yes_response, new DialogInterface
                                        .OnClickListener() {
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
