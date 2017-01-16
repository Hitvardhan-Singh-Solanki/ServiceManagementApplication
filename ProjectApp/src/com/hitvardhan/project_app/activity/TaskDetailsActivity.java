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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Record;
import com.hitvardhan.project_app.response_classes.Response;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hitvardhan.project_app.R.id.toolbar;
import static com.hitvardhan.project_app.activity.MainActivity.client;

/**
 * Created by Hitvardhan on 12-12-2016.
 * To show Task Details and change the status
 */
public class TaskDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //variable declaration
    private String idOfTaskString;
    private TextView
            getStatusOfTaskView,
            getNameView,
            getDescView,
            getDueDateView,
            getContactNumberView,
            getAddressView;
    private Map<String, Object> fields;
    private Gson gson;
    private Response res;
    private ImageView closeButton;
    private Record getRecord;
    private Spinner statusSpinner;
    private Boolean isSpinnerChangedFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);

        isSpinnerChangedFlag = true;

        //Initialization
        fields = new HashMap<String, Object>();
        gson = new Gson();

        //get the respsonse from intent
        getRecord = (Record) getIntent().getParcelableExtra("RecordObject");

        //Remove the appended three characters form the id string
        idOfTaskString = getRecord.getId();

        //setup the view
        getNameView = (TextView) findViewById(R.id.task_name_detail);
        getDescView = (TextView) findViewById(R.id.task_description_detail);
        getDueDateView = (TextView) findViewById(R.id.DateOfTask);
        getContactNumberView = (TextView) findViewById(R.id.ContactInfoPhone);
        getAddressView = (TextView) findViewById(R.id.AddressOfTask);
        getStatusOfTaskView = (TextView) findViewById(R.id.Status);
        closeButton = (ImageView) findViewById(R.id.close_task_detail_cross);
        statusSpinner = (Spinner) findViewById(R.id.spinner_status_change);
        statusSpinner.setOnItemSelectedListener(this);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //changeStatusButton = (Button) findViewById(R.id.Button_to_change_status);

        //set the text from the parced object
        getNameView.setText(getRecord.getName());
        getDescView.setText(getRecord.getDescription__c());
        getDueDateView.setText(getRecord.getDue_Date__c());
        getContactNumberView.append("\n"+getRecord.getPhone_Number__c());
        getAddressView.setText(getRecord.getAddress__c());
        getStatusOfTaskView.append("\n"+getRecord.getStatus__c());


        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("En route");
        categories.add("Not Completed");
        categories.add("Completed");
        categories.add("In Progress");
        categories.add("Not Started");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_drop_dow_custom);
        // attaching data adapter to spinner
        statusSpinner.setAdapter(dataAdapter);


       int mySelectionPosition = dataAdapter.getPosition(getRecord.getStatus__c());
       statusSpinner.setSelection(mySelectionPosition,true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Update the status
     */
    public void ChangeStatusCall(String statusToChange) {
        fields.put("Status__c", statusToChange);
        new AlertDialog.Builder(this)
                .setTitle(R.string.updateTitle)
                .setMessage("Change Status to '"+statusToChange+"' of task " + getNameView.getText() + "?")
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

    /**
     * save data to salesforce
     * @param id
     * @param fields
     */
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
                    Log.d("STATUS CODE", String.valueOf(result.getStatusCode()));
                    if (result.getStatusCode() == 204) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getStatusOfTaskView.setText("Status: \nCompleted");
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



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
       if(!isSpinnerChangedFlag) {
           String item = parent.getItemAtPosition(position).toString();
           // Showing selected spinner item
           //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
           if (item.equalsIgnoreCase(getRecord.getStatus__c())) {
               Toast.makeText(this, "Status is " + item, Toast.LENGTH_SHORT).show();
           } else {
               ChangeStatusCall(item);
           }
       } else {
           isSpinnerChangedFlag = false;
       }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}