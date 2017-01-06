package com.hitvardhan.project_app.fragment;

/**
 * Created by Hitvardhan on 13-12-2016.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hitvardhan.project_app.Adapters.TaskAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.interfaces.NetworkCallbackInterface;

import java.util.ArrayList;
import java.util.List;

public class MoreTaskListFragment extends Fragment {

    private RecyclerView mRcvTaskListV;

    private TaskAdapter mTaskAdapter;


    private SwipeRefreshLayout mSwipeRefreshLayoutl;
    private Fragment thisFragment;

    public static List<Record> MoreTaskName = new ArrayList<Record>();


    public MoreTaskListFragment() {
        // Required empty public constructor
    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_one, container, false);
        thisFragment = this;
        mRcvTaskListV = (RecyclerView) view.findViewById(R.id.rcv_list_v);

        mSwipeRefreshLayoutl = (SwipeRefreshLayout) view.findViewById(R.id
                .swipeRefreshLayoutMoreTaskOne);

        // Inflate the layout for this fragment
        mTaskAdapter = new TaskAdapter(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRcvTaskListV.setLayoutManager(mLayoutManager);
        mRcvTaskListV.setItemAnimator(new DefaultItemAnimator());
        mRcvTaskListV.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
        //mRcvTaskListV.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        if (getArguments().getSerializable("TaskList") != null) {
            Response res = (Response) getArguments().getSerializable("TaskList");
            setListData(res);
//            mTaskAdapter.notifyDataSetChanged();
        }


        mSwipeRefreshLayoutl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CommanUtils.refresh(getActivity(), view, ((MainActivity) getActivity()).client,
                        new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.i("RESPONSE",response.toString());
                        ((MainActivity)getActivity()).updateUi(response);

                    }
                    @Override
                    public void onError() {

                    }
                });
                mSwipeRefreshLayoutl.setRefreshing(false);
            }
        });
        return view;
    }


    public void setListData(Response rs) {
        MoreTaskName.clear();
        if (rs.getRecords() != null) {
            for (Record record : rs.getRecords()) {
                if (record.getDue_Date__c() != null) {
                    if (!record.getDue_Date__c().trim().equalsIgnoreCase(CommanUtils
                            .getTodaysDate().trim())) {
                        MoreTaskName.add(record);

                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Set Date")
                            .setMessage("Seems like " + record.getName() +
                                    " does not contain due" +
                                    " date please verify")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.ic_alert_exclamation_mark)
                            .show();
                    MoreTaskName.add(record);
                }
            }

            if (mTaskAdapter != null && rs != null && rs.getRecords() != null) {
                mTaskAdapter.addItem(MoreTaskName);
                mRcvTaskListV.setAdapter(mTaskAdapter);
                Log.i("excin","dssff");
            }


        }
    }
}