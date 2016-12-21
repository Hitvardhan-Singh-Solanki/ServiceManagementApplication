package com.hitvardhan.project_app.fragment;

/**
 * Created by Hitvardhan on 13-12-2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitvardhan.project_app.Adapters.TaskAdapter;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;

import java.util.ArrayList;
import java.util.List;

public class TodayTaskListFragment extends Fragment{

    private Response res;

    private RecyclerView mRcvTaskListV;

    private TaskAdapter mTaskAdapter;

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
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        mRcvTaskListV = (RecyclerView)view.findViewById(R.id.rcv_list_v);

        // Inflate the layout for this fragment

        setListData();
        return view;
    }

    private void setListData(){

        if(getArguments().getSerializable("TaskList") != null){
            res = (Response) getArguments().getSerializable("TaskList");

            if(res.getRecords() != null){
                List<Record> todaysTaskName = new ArrayList<>();
                todaysTaskName.clear();
                for (Record record: res.getRecords()) {
                    CommanUtils.displayLogs("server_date",""+ record.getDue_Date__c());
                    CommanUtils.displayLogs("todate_date", ""+CommanUtils.getTodaysDate());
                    if(record.getDue_Date__c()!=null){
                        if (record.getDue_Date__c().trim()
                                .equalsIgnoreCase(CommanUtils.getTodaysDate().trim())) {
                            todaysTaskName.add(record);
                        }
                    }
                }

                CommanUtils.displayLogs("todate_date",""+ todaysTaskName.size());
                mTaskAdapter = new TaskAdapter(todaysTaskName);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mRcvTaskListV.setLayoutManager(mLayoutManager);
                mRcvTaskListV.setItemAnimator(new DefaultItemAnimator());
                mRcvTaskListV.setAdapter(mTaskAdapter);
            }

        }
    }

}
