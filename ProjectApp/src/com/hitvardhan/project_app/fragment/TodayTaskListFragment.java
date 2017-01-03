package com.hitvardhan.project_app.fragment;

/**
 * Created by Hitvardhan on 13-12-2016.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hitvardhan.project_app.Adapters.TaskAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;
import com.hitvardhan.project_app.utils.NetworkCallbackInterface;

import java.util.ArrayList;
import java.util.List;

import static com.hitvardhan.project_app.fragment.MoreTaskListFragment.MoreTaskName;

public class TodayTaskListFragment extends Fragment {

    private Response res;

    private RecyclerView mRcvTaskListV;

    private TaskAdapter mTaskAdapter;

    private TextView EmptyList;

    public static List<Record> todaysTaskName;
    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayoutlToday;

    public TodayTaskListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_one, container, false);
        mRcvTaskListV = (RecyclerView) view.findViewById(R.id.rcv_list_v);
        //EmptyList = (TextView) view.findViewById(R.id.todays_task_empty_view);
        // Inflate the layout for this fragment
        mTaskAdapter = new TaskAdapter(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRcvTaskListV.setLayoutManager(mLayoutManager);
        mRcvTaskListV.setItemAnimator(new DefaultItemAnimator());
        mRcvTaskListV.setAdapter(mTaskAdapter);
        mSwipeRefreshLayoutlToday = (SwipeRefreshLayout) view.findViewById(R.id
                .swipeRefreshLayoutMoreTaskOne);


        mSwipeRefreshLayoutlToday.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CommanUtils.refresh(getActivity(), view, ((MainActivity) getActivity()).client, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess(Response response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
                mSwipeRefreshLayoutlToday.setRefreshing(false);
            }
        });


        return view;
    }
    @Override
    public void onResume() {
        if(getArguments().getSerializable("TaskList") != null) {
            res = (Response) getArguments().getSerializable("TaskList");
            setListData();
        }
        super.onResume();
    }
    private void setListData() {
        if (getArguments().getSerializable("TaskList") != null) {
            res = (Response) getArguments().getSerializable("TaskList");
            if (res.getRecords() != null) {
                todaysTaskName = new ArrayList<>();
                todaysTaskName.clear();
                for (Record record : res.getRecords()) {
                    if (record.getDue_Date__c() != null) {
                        if (record.getDue_Date__c().trim()
                                .equalsIgnoreCase(CommanUtils.getTodaysDate().trim())) {
                            todaysTaskName.add(record);
                        }
                    }
                }


                if(mTaskAdapter != null && res != null && res.getRecords() != null)
                    mTaskAdapter.addItem(todaysTaskName);

                //If the list is empty
               /* if (mTaskAdapter.getItemCount() < 1) {
                    EmptyList.setVisibility(View.VISIBLE);

                }*/

            }

        }
    }

}