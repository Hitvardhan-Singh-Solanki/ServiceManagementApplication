package com.hitvardhan.project_app.fragment;

/**
 * Created by Hitvardhan on 13-12-2016.
 */

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

public class MoreTaskListFragment extends Fragment{

    private Response res;

    private RecyclerView mRcvTaskListV;

    private TaskAdapter mTaskAdapter;

    private TextView EmptyListMore;

    public static List<Record> MoreTaskName = new ArrayList< Record >();


    public MoreTaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        EmptyListMore = (TextView) view.findViewById(R.id.more_task_empty_view);


        mRcvTaskListV = (RecyclerView)view.findViewById(R.id.rcv_list_v);

        // Inflate the layout for this fragment
        if(getArguments().getSerializable("TaskList") != null) {
            res = (Response) getArguments().getSerializable("TaskList");
            setListData();
        }
        return view;

    }


    public void setListData(){
        MoreTaskName.clear();
            if(res.getRecords() != null){


                for (Record record: res.getRecords()) {
                    if(record.getDue_Date__c()!=null){
                        if(!record.getDue_Date__c().trim().equalsIgnoreCase(CommanUtils
                                .getTodaysDate().trim())){
                            MoreTaskName.add(record);
                        }
                    }
                    else{
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Set Date")
                                .setMessage("Seems like "+ record.getName()+
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






                //If the list is empty
                if(MoreTaskName.size() < 1){
                    EmptyListMore.setVisibility(View.VISIBLE);
                }




                mTaskAdapter = new TaskAdapter(MoreTaskName);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mRcvTaskListV.setLayoutManager(mLayoutManager);
                mRcvTaskListV.setItemAnimator(new DefaultItemAnimator());
                mRcvTaskListV.setAdapter(mTaskAdapter);
            }

        }
}