package com.hitvardhan.project_app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.google.gson.Gson;
import com.hitvardhan.project_app.Adapters.AdminTaskAdapter;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.constants.SoqlQueries;
import com.hitvardhan.project_app.interfaces.NetworkCallbackForAdmin;
import com.hitvardhan.project_app.interfaces.NetworkCallbackInterface;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.RecordForAdmin;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.response_classes.ResponseForAdmin;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignedTaskFragment extends Fragment {


    private View assignedTaskListFragmentView;
    private RecyclerView mRcycerViewForTask;
    private AdminTaskAdapter mTaskAdminAdapter;
    public static List<Record> AssignedTaskName = new ArrayList<Record>();
    private SwipeRefreshLayout mSwipeToRefeshLayoutInAdminAssigned;


    public AssignedTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assignedTaskListFragmentView = inflater.inflate(R.layout.fragment_assigned_task,
                container, false);
        mRcycerViewForTask = (RecyclerView) assignedTaskListFragmentView
                .findViewById(R.id.rcv_list_v_admin_task);
        mSwipeToRefeshLayoutInAdminAssigned = (SwipeRefreshLayout) assignedTaskListFragmentView
                .findViewById(R.id.swipeToRefreshLayoutAdminTask);
        mSwipeToRefeshLayoutInAdminAssigned
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (getArguments().getSerializable("TaskListss") != null) {
                            mTaskAdminAdapter.clear();
                            TaskAdminFragment newTask = new TaskAdminFragment();
                            newTask.getTheDetailsForAdmin();
                            Response res = (Response) getArguments().getSerializable("TaskListss");
                            setListDataAdmin(res);
                        }
                        mSwipeToRefeshLayoutInAdminAssigned.setRefreshing(false);
                    }

                });

        mSwipeToRefeshLayoutInAdminAssigned.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        mTaskAdminAdapter = new AdminTaskAdapter(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRcycerViewForTask.setLayoutManager(mLayoutManager);
        mRcycerViewForTask.setAdapter(mTaskAdminAdapter);
        mRcycerViewForTask.setItemAnimator(new DefaultItemAnimator());
        mTaskAdminAdapter.notifyDataSetChanged();
        return assignedTaskListFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getSerializable("TaskListss") != null) {
            mTaskAdminAdapter.clear();
            AssignedTaskName.clear();
            Response res = (Response) getArguments().getSerializable("TaskListss");
            setListDataAdmin(res);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * set the data of the recycler view
     *
     * @param rs
     */
    public void setListDataAdmin(Response rs) {
        AssignedTaskName.clear();
        if (rs.getRecords() != null) {
            for (Record record : rs.getRecords()) {
                if (record.getAssign_to_User__r() != null) {
                    AssignedTaskName.add(record);
                    mTaskAdminAdapter.notifyDataSetChanged();
                }
            }

            if (mTaskAdminAdapter != null && rs != null && rs.getRecords() != null) {
                mTaskAdminAdapter.addItem(AssignedTaskName);
                mRcycerViewForTask.setAdapter(mTaskAdminAdapter);
            }


        }
    }
}
