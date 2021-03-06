package com.hitvardhan.project_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.utils.CommanUtils;


import java.util.ArrayList;
import java.util.List;

import static com.hitvardhan.project_app.utils.CommanUtils.getDateformat;

/**
 * Created by Hitvardhan on 14-12-2016.
 * <p>
 * To prepare the adapter of the task recycler view list
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Record> mRcordList;

    private LayoutInflater mLayoutInflater;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView date;
        private TextView desc;
        private TextView SerialNumber1;
        private ImageView redDotStatus;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title_name_task);
            date = (TextView) view.findViewById(R.id.due_date_task);
            desc = (TextView) view.findViewById(R.id.TaskDescriptionDetailsRecyclerView);
            SerialNumber1 = (TextView) view.findViewById(R.id.SerialNumber);
            redDotStatus = (ImageView) view.findViewById(R.id.redDot_status);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public TaskAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mRcordList = new ArrayList<>();
    }
    public void addItem(List<Record> item) {
        clear();
        mRcordList.addAll(item);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.task_recycler_list_row_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Record mRecord = mRcordList.get(position);
        if (mRecord != null) {
            //Name
            if (!CommanUtils.isEmpty(mRecord.getName())) {
                String upperString = mRecord.getName().substring(0, 1).toUpperCase()
                        + mRecord.getName().substring(1);
                holder.name.setText(upperString);
            } else {
                System.out.print("NOT FOUND");
                holder.name.setText("");
            }
            //Due Date
            if (mRecord.getDue_Date__c() != null) {
                holder.date.setText(mRecord.getDue_Date__c());

            } else {
                System.out.print("Due Date NOT FOUND");
                holder.date.setText(R.string.setDate);
            }

            //Get description
            if (!CommanUtils.isEmpty(mRecord.getDescription__c())) {
                String upperString1 = mRecord.getDescription__c().substring(0, 1).toUpperCase()
                        + mRecord.getDescription__c().substring(1);
                holder.desc.setText(upperString1);
            } else {
                System.out.print("description NOT FOUND");
                holder.desc.setVisibility(View.GONE);
            }

            holder.SerialNumber1.setText("" + (position + 1));

            if (mRecord.getStatus__c().equalsIgnoreCase("Completed")) {
                holder.redDotStatus.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRcordList.size();
    }



    public void clear() {
        mRcordList.clear();
        notifyDataSetChanged();
    }



}

