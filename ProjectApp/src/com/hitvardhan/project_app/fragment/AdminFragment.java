package com.hitvardhan.project_app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.UserDetailActivity;
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
public class AdminFragment extends Fragment {


    //Global Variable Declaration
    private View contentViewAdmin;
    private ListView listViewOfUsers;
    private ArrayList<RecordForAdmin> listOfUsers = new ArrayList<RecordForAdmin>();
    private ArrayAdapter adapter;

    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        contentViewAdmin = inflater.inflate(R.layout.fragment_admin, container, false);
        listViewOfUsers = (ListView) contentViewAdmin.findViewById(R.id.listtOfUsers);


        try {
            getDetailsofEngineers(getContext(), ((MainActivity) getActivity()).client,
                    new NetworkCallbackForAdmin() {
                @Override
                public void onSuccess(ResponseForAdmin responseAdmin) {
                    for (RecordForAdmin rA : responseAdmin.getRecords()) {
                        if (rA.getName() != null)
                            listOfUsers.add(rA);
                    }
                    upDataOnAdminUi(getActivity());

                }


                @Override
                public void onError() {
                    getActivity().finish();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return contentViewAdmin;
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

        //Show the progress dialog box when the data loads
        final ProgressDialog mProgressDialog;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Fetching Users...");
        mProgressDialog.show();

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
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                //finally update the changes to the UI
                callbackInterface.onSuccess(resAdmin);
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



    public void upDataOnAdminUi(final Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listOfUsers.size() >= 1) {
                    adapter = new ArrayAdapter(getContext(),
                            R.layout.activity_listview, listOfUsers);
                    listViewOfUsers.setAdapter(adapter);

                    listViewOfUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        Activity thisActivity = activity;
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String nameOfTheEngineer = listOfUsers.get(position).getName();
                            Intent intentForAdminToOpenUserPage = new Intent(thisActivity, UserDetailActivity.class);
                            intentForAdminToOpenUserPage.putExtra("NAME",nameOfTheEngineer);
                            intentForAdminToOpenUserPage.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intentForAdminToOpenUserPage);
                        }
                    });
                }
            }
        });
    }


}
