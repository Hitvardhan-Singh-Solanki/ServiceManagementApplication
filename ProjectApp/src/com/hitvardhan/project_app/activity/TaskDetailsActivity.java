package com.hitvardhan.project_app.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Response;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.util.HashMap;
import java.util.Map;

import static com.hitvardhan.project_app.R.id.toolbar;
import static com.hitvardhan.project_app.activity.MainActivity.client;

/**
 * Created by Hitvardhan on 12-12-2016.
 * <p>
 * <p>
 * To show Task Details only no other requirement and change the status
 */
public class TaskDetailsActivity extends ActionBarActivity {

    public String Id_of_task, ObjectType;
    public Map<String, Object> fields = new HashMap<String, Object>();
    TextView getStatusOfTask;
    Gson gson = new Gson();
    Response res = new Response();
    String UsersName;
    String Status_of_task;

    private TextView updatedStatus;
    @Override public void onResume(){
        MainActivity.isStatusChanged=false;
        super.onResume();
    }

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
        Status_of_task = getTheNameIntent.getExtras().getString("3");
        Id_of_task = getTheNameIntent.getExtras().getString("4");
        Id_of_task = Id_of_task.substring(0, Id_of_task.length() - 3);
        ObjectType = getTheNameIntent.getExtras().getString("5");


        TextView getNameSample = (TextView) findViewById(R.id.TaskIdDetails);
        TextView getDescSample = (TextView) findViewById(R.id.TaskDescriptionDetails);
        TextView getDue_Date = (TextView) findViewById(R.id.DateOfTask);
        TextView getContactNumber = (TextView) findViewById(R.id.ContactInfoPhone);
        TextView getAddressTask = (TextView) findViewById(R.id.AddressOfTask);
        getStatusOfTask = (TextView) findViewById(R.id.Status);
        updatedStatus = (TextView) findViewById(R.id.Status);


        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain1);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (UsersName != null) {
            getNameSample.setText(UsersName);
        }
        if (Desc != null) {
            getDescSample.setText(Desc);
        }
        if (Due_Date != null) {
            getDue_Date.append("" + Due_Date);
        }
        if (ContactNumber != null) {
            getContactNumber.append("\n" + ContactNumber);
        }
        if (Address_Of_task != null) {
            getAddressTask.setText(Address_Of_task);
        }
        if (Status_of_task != null) {
            getStatusOfTask.append(" \n" + Status_of_task);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public void OnChangeClicked(View view){

        String textOfStatus = Status_of_task;
        if(!textOfStatus.equalsIgnoreCase("Completed")) {
            ChangeStatusCall();
        }
        else{

            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Already Updated!")
                    .setMessage("")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //UPDATE on POSITIVE RESPONSE

                        }
                    })
                    .setIcon(R.drawable.ic_alert_exclamation_mark)
                    .show();
        }
    }





    public void ChangeStatusCall() {
        fields.put("Status__c", "Completed");

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("UPDATE")
                .setMessage("CHANGE STATUS TO COMPLETED OF \n TASK: " + UsersName + "?")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //UPDATE on POSITIVE RESPONSE
                        try {
                            saveData(Id_of_task, fields);

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


    private void saveData(String id, Map<String, Object> fields) {
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
                    if (result.getStatusCode() == 204) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("UI thread", "I am the UI thread");
                                updatedStatus.setText("Status: \nCompleted");
                            }
                        });


                        MainActivity.isStatusChanged=true;
                    }
                    Toast.makeText(getApplicationContext(), "Your data has been updated succecssfully!", Toast.LENGTH_SHORT);
                    wait(1000);
                    TaskDetailsActivity.this.finish();
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