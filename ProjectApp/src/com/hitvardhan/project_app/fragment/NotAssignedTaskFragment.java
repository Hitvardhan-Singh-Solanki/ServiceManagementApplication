package com.hitvardhan.project_app.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hitvardhan.project_app.Adapters.AdminTaskAdapter;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotAssignedTaskFragment extends Fragment {

    private View notAssignedTaskListFragmentView;
    private RecyclerView mRcycerViewForTaskNotAssigned;
    private AdminTaskAdapter mNotAssignedTaskAdminAdapter;
    public static List<Record> notAssignedTaskName = new ArrayList<Record>();
    private SwipeRefreshLayout mSwipeToRefeshLayoutInAdminNotAssigned;

    public NotAssignedTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        notAssignedTaskListFragmentView = inflater.inflate(R.layout.fragment_not_assigned_task,
                container, false);

        //Recycler view
        mRcycerViewForTaskNotAssigned = (RecyclerView) notAssignedTaskListFragmentView
                .findViewById(R.id.rcv_list_v_admin_task_not_assigned);

        //Swipe refresh layout view
        mSwipeToRefeshLayoutInAdminNotAssigned = (SwipeRefreshLayout)
                notAssignedTaskListFragmentView
                .findViewById(R.id.swipeToRefreshLayoutAdminTaskNotAssigned);

        //Set the on click listner refresh
        mSwipeToRefeshLayoutInAdminNotAssigned
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (getArguments().getSerializable("TaskListss") != null) {
                            mNotAssignedTaskAdminAdapter.clear();
                            TaskAdminFragment newTask = new TaskAdminFragment();
                            newTask.getTheDetailsForAdmin();
                            Response res = (Response) getArguments().getSerializable("TaskListss");
                            setListDataAdmin(res);
                        }

                        mSwipeToRefeshLayoutInAdminNotAssigned.setRefreshing(false);
                    }

                });
        mSwipeToRefeshLayoutInAdminNotAssigned
                .setColorSchemeResources(
                        R.color.refresh_progress_1,
                        R.color.refresh_progress_2,
                        R.color.refresh_progress_3);

        mNotAssignedTaskAdminAdapter = new AdminTaskAdapter(getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        mRcycerViewForTaskNotAssigned.setLayoutManager(mLayoutManager);

        mRcycerViewForTaskNotAssigned.setAdapter(mNotAssignedTaskAdminAdapter);

        mRcycerViewForTaskNotAssigned.setItemAnimator(new DefaultItemAnimator());

        mNotAssignedTaskAdminAdapter.notifyDataSetChanged();

        return notAssignedTaskListFragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getSerializable("TaskListss") != null) {
            Response res = (Response) getArguments().getSerializable("TaskListss");
            setListDataAdmin(res);
        }

    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * set the data for the recycler view
     *
     * @param rs
     */
    public void setListDataAdmin(Response rs) {
        notAssignedTaskName.clear();
        if (rs.getRecords() != null) {
            for (Record record : rs.getRecords()) {
                if (record.getAssign_to_User__r() == null) {
                    notAssignedTaskName.add(record);
                    mNotAssignedTaskAdminAdapter.notifyDataSetChanged();
                }
            }

            if (mNotAssignedTaskAdminAdapter != null && rs != null && rs.getRecords() != null) {
                mNotAssignedTaskAdminAdapter.addItem(notAssignedTaskName);
                mRcycerViewForTaskNotAssigned.setAdapter(mNotAssignedTaskAdminAdapter);
            }


        }
    }

}
