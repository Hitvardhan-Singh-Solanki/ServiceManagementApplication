package com.hitvardhan.project_app.activity;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.fragment.MoreTaskListFragment;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.AlertDialogUtils;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hitvardhan.project_app.activity.MainActivity.client;

/**
 * Created by Hitvardhan on 12-12-2016.
 *
 *
 * To show Task Details only no other requirement and change the status
 */
public class TaskDetailsActivity extends Activity{

    public String Id_of_task, ObjectType;
    public Map<String, Object> fields = new HashMap<String, Object>();
    TextView getStatusOfTask;
    Gson gson = new Gson();
    Response res = new Response();
    String UsersName;

    private TextView UpdatedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.task_details);

        Intent getTheNameIntent = getIntent();

        UsersName = getTheNameIntent.getExtras().getString("NAME_OF_USER");
        String Desc = getTheNameIntent.getExtras().getString("Descp_of_task");
        String Due_Date = getTheNameIntent.getExtras().getString("0");
        String ContactNumber = getTheNameIntent.getExtras().getString("2");
        String Address_Of_task = getTheNameIntent.getExtras().getString("1");
        String Status_of_task = getTheNameIntent.getExtras().getString("3");
        Id_of_task = getTheNameIntent.getExtras().getString("4");
        Id_of_task=Id_of_task.substring(0,Id_of_task.length()-3);
        ObjectType = getTheNameIntent.getExtras().getString("5");



        TextView getNameSample = (TextView) findViewById(R.id.TaskIdDetails);
        TextView getDescSample = (TextView) findViewById(R.id.TaskDescriptionDetails);
        TextView getDue_Date = (TextView) findViewById(R.id.DateOfTask);
        TextView getContactNumber = (TextView) findViewById(R.id.ContactInfoPhone);
        TextView getAddressTask = (TextView) findViewById(R.id.AddressOfTask);
        getStatusOfTask = (TextView) findViewById(R.id.Status);
        UpdatedStatus = (TextView) findViewById(R.id.Status);



        if(UsersName != null)
        {getNameSample.setText(UsersName);}
        if(Desc != null)
        {getDescSample.setText(Desc);}
        if(Due_Date != null)
        {getDue_Date.append(""+Due_Date);};
        if(ContactNumber != null)
        {getContactNumber.append("\n"+ContactNumber);}
        if(Address_Of_task != null)
        {getAddressTask.setText(Address_Of_task);}
        if(Status_of_task != null)
        {getStatusOfTask.append(" \n"+Status_of_task);}
    }
    public void backPressedCustom(View v) {
        super.onBackPressed();
    }

    public void ChangeStatusCall(View view) {
        if(getStatusOfTask.getText().toString().equalsIgnoreCase("Completed")) {
            fields.put("Status__c", "Completed");

            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("UPDATE")
                    .setMessage("CHANGE STATUS TO COMPLETED OF \n TASK: " + UsersName + "?")
                    .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //UPDATE on POSITIVE RESPONSE
                            try {
                                saveData(Id_of_task, fields);
                                UpdatedStatus.setText("Status: \nCompleted");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
                    .setIcon(R.drawable.ic_update_alert)
                    .show();

        }
        else{
            //SHOW AND ALERT IF ALREADY UPDATED
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("ALREADY UPDATED")
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(R.drawable.ic_alert_exclamation_mark)
                    .show();
        }
}


    private void saveData(String id, Map<String, Object> fields)  {
        RestRequest restRequest;
        try {
            restRequest = RestRequest.getRequestForUpdate(
                    getString(R.string.api_version),
                    "Task__c", id, fields);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
            @Override
            public void onSuccess(RestRequest request, RestResponse result) {
                try {
                    //TaskDetailsActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }






}