package com.hitvardhan.project_app.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.utils.CommanUtils;


import java.util.List;

import static com.hitvardhan.project_app.utils.CommanUtils.getDateformat;

/**
 * Created by Hitvardhan on 14-12-2016.
 *
 * To prepare the adapter of the task recycler view list
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private List<Record> mRcordList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name;
        public TextView date;
        public TextView desc;
        public TextView SerialNumber1;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.title_name_task);
            date = (TextView) view.findViewById(R.id.due_date_task);
            desc = (TextView) view.findViewById(R.id.TaskDescriptionDetailsRecyclerView);
            SerialNumber1 = (TextView) view.findViewById(R.id.SerialNumber);

        }

        @Override
        public void onClick(View v) {

        }
    }

    public TaskAdapter(List<Record> taskList) {
        this.mRcordList = taskList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_recycler_list_row_layout, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Record mRecord = mRcordList.get(position);
        if(mRecord != null){
            //Name
            if(!CommanUtils.isEmpty(mRecord.getName())){
                String upperString = mRecord.getName().substring(0,1).toUpperCase()
                        + mRecord.getName().substring(1);
                holder.name.setText(upperString);
            }
            else{
                System.out.print("NOT FOUND");
                holder.name.setText("");
            }
            //Due Date
            if(mRecord.getDue_Date__c()!= null) {
                holder.date.setText(mRecord.getDue_Date__c());
            }
            else{
                System.out.print("NOT FOUND");
                holder.date.setText("");
            }

            //Get description
            if(!CommanUtils.isEmpty(mRecord.getDescription__c())) {
                String upperString1 = mRecord.getDescription__c().substring(0,1).toUpperCase()
                        + mRecord.getDescription__c().substring(1);
                holder.desc.setText(upperString1);
            }else{
                System.out.print("NOT FOUND");
                holder.desc.setText("");
            }

            holder.SerialNumber1.setText(""+(position+1));

        }
    }

    @Override
    public int getItemCount() {
        return mRcordList.size();
    }
}

