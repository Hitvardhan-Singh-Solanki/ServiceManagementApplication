package com.hitvardhan.project_app.activity;

import android.app.Activity;
import android.app.Dialog;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Record;
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
 * To show Task Details and change the status
 */
public class TaskDetailsActivity extends AppCompatActivity{



    //variable declaration
    private String
            usersNameString,
            statusOfTaskString,
            descOfTaskString,
            dueDateString,
            contactNumberString,
            addressOfTaskString,
            idOfTaskString,
            typeOfObjectString;

    private TextView getStatusOfTaskView,
            updatedStatusView,
            getNameView,
            getDescView,
            getDueDateView,
            getContactNumberView,
            getAddressView;

    private Map<String, Object> fields;
    private Gson gson;
    private Response res;
    private ImageView closeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.task_details);

        //Get the intent form the previous activity
        Intent getTheNameIntent = getIntent();

        //Initialization
        fields = new HashMap<String, Object>();
        gson = new Gson();
        res = new Response();


        //get the data from the intent and store in local variable
        usersNameString = getTheNameIntent.getExtras()
                .getString(getString(R.string.nameOfTask));

        descOfTaskString = getTheNameIntent.getExtras()
                .getString(getString(R.string.descOfTask));

        dueDateString = getTheNameIntent.getExtras()
                .getString(getString(R.string.dueDate));

        contactNumberString = getTheNameIntent.getExtras()
                .getString(getString(R.string.contactInfoOftask));

        addressOfTaskString = getTheNameIntent
                .getExtras().getString(getString(R.string.addressOfTask));

        statusOfTaskString = getTheNameIntent.getExtras()
                .getString(getString(R.string.statusOfTask));

        idOfTaskString = getTheNameIntent.getExtras()
                .getString(getString(R.string.taskID));

        typeOfObjectString = getTheNameIntent.getExtras()
                .getString(getString(R.string.taskType));


        //Remove the appended three characters form the id string
        idOfTaskString = idOfTaskString.substring(0, idOfTaskString.length() - 3);

        //setup the view
        getNameView = (TextView) findViewById(R.id.task_name_detail);
        getDescView = (TextView) findViewById(R.id.task_description_detail);
        getDueDateView = (TextView) findViewById(R.id.DateOfTask);
        getContactNumberView = (TextView) findViewById(R.id.ContactInfoPhone);
        getAddressView = (TextView) findViewById(R.id.AddressOfTask);
        getStatusOfTaskView = (TextView) findViewById(R.id.Status);
        updatedStatusView = (TextView) findViewById(R.id.Status);
        closeButton = (ImageView) findViewById(R.id.close_task_detail_cross);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils
                        .loadAnimation(getBaseContext(), R.anim.cross_animation));
                onBackPressed();
            }
        });

        //bind the data to the view
        if (usersNameString != null) {
            getNameView.setText(usersNameString);
        }
        if (descOfTaskString != null) {
            getDescView.setText(descOfTaskString);
        }
        if (dueDateString != null) {
            getDueDateView.append("" + dueDateString);
        }
        if (contactNumberString != null) {
            getContactNumberView.append("\n" + contactNumberString);
        }
        if (addressOfTaskString != null) {
            getAddressView.setText(addressOfTaskString);
        }
        if (statusOfTaskString != null) {
            getStatusOfTaskView.append(" \n" + statusOfTaskString);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public void OnChangeClicked(View view) {

        String textOfStatusView = statusOfTaskString;
        if (!textOfStatusView.equalsIgnoreCase(getString(R.string.completed))) {
            ChangeStatusCall();
        } else {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.updatedTitle)
                    .setMessage("")
                    .setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Just acknowledge

                        }
                    })
                    .setIcon(R.drawable.ic_alert_exclamation_mark)
                    .show();
        }
    }

    public void ChangeStatusCall() {
        fields.put("Status__c", "Completed");

        new AlertDialog.Builder(this)
                .setTitle(R.string.updateTitle)
                .setMessage(getString(R.string.updateMessage) + usersNameString + "?")
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //UPDATE on POSITIVE RESPONSE
                        try {
                            saveData(idOfTaskString, fields);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).setNegativeButton(R.string.no_response, new DialogInterface.OnClickListener() {
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
                                updatedStatusView.setText("Status: \nCompleted");
                            }
                        });

                    }
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