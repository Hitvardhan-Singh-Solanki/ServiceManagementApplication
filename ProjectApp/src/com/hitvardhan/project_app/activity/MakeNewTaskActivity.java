package com.hitvardhan.project_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.response_classes.Record;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.hitvardhan.project_app.activity.MainActivity.client;

public class MakeNewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private HashMap<String, Object> fields;
    ImageView closeButtonCross;
    Geocoder geo;
    private TextView makeATaskButton;
    private EditText taskName, eMailEdit, taskDescription, taskAddress, taskPhoneNumber,
            inputDueDate;
    private TextInputLayout inputLayoutTaskName,
            inputLayoutEmail,
            inputLayoutDescription,
            inputLayoutAddress,
            inputLayoutPhone,
            inputlauoutTaskDueDate;
    private Spinner statusSpinner, assignmentSpinner;
    private Activity appActivity;
    private String getStatusFromSpinner;
    private String getNameFromSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_task);
        fields = new HashMap<String, Object>();
        appActivity = this;
        statusSpinner = (Spinner) findViewById(R.id.spinner_status_change_new_task);
        assignmentSpinner = (Spinner) findViewById(R.id.spinner_assign_user_new_task);

        statusSpinner.setOnItemSelectedListener(this);
        assignmentSpinner.setOnItemSelectedListener(this);


        //input layouts
        inputLayoutTaskName = (TextInputLayout) findViewById(R.id.input_layout_task_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_description);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.input_layout_address);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputlauoutTaskDueDate = (TextInputLayout)findViewById(R.id.new_task_due_date);

        //EditText
        taskName = (EditText) findViewById(R.id.input_task_name);
        eMailEdit = (EditText) findViewById(R.id.input_email);
        taskDescription = (EditText) findViewById(R.id.description_new_task);
        taskAddress = (EditText) findViewById(R.id.address_new_task);
        taskPhoneNumber = (EditText) findViewById(R.id.phone_new_task);
        inputDueDate = (EditText) findViewById(R.id.input_due_date);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("En route");
        categories.add("Not Completed");
        categories.add("Completed");
        categories.add("In Progress");
        categories.add("Not Started");


        // Spinner Drop down elements
        List<String> categoriesErNames = new ArrayList<String>();
        categoriesErNames.add("Jane Doe");
        categoriesErNames.add("John Doe");

        //textWatcher
        taskName.addTextChangedListener(new MyTextWatcher(taskName));
        eMailEdit.addTextChangedListener(new MyTextWatcher(eMailEdit));
        taskAddress.addTextChangedListener(new MyTextWatcher(taskAddress));


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categories);
        ArrayAdapter<String> dataAdapterErNames = new ArrayAdapter<String>(this, R.layout.spinner_item, categoriesErNames);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_drop_dow_custom);
        dataAdapterErNames.setDropDownViewResource(R.layout.spinner_drop_dow_custom);
        // attaching data adapter to spinner
        statusSpinner.setAdapter(dataAdapter);
        assignmentSpinner.setAdapter(dataAdapterErNames);
        //DatePicker

        //close Button
        closeButtonCross = (ImageView) findViewById(R.id.close_task_making_cross);
        closeButtonCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Make a task Button
        makeATaskButton = (TextView) findViewById(R.id.make_a_task_button);


        makeATaskButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                String getLatFromAddress, getLongFromAddress;
                final ProgressDialog mProgressValidate;
                mProgressValidate = new ProgressDialog(appActivity);
                mProgressValidate.setMessage("Validating Data");
                mProgressValidate.show();
                validateForm();
                if (mProgressValidate.isShowing())
                    mProgressValidate.dismiss();

                Toast.makeText(appActivity, "Data is Valid", Toast.LENGTH_SHORT).show();

                //TODO: do a network check
                final ProgressDialog mProgressCreate;
                mProgressCreate = new ProgressDialog(appActivity);
                mProgressCreate.setMessage("Creating Record");
                mProgressCreate.show();
                //Creating a hashMap
                if (taskName.getText() != null)
                    fields.put("Name", String.valueOf(taskName.getText()));
                if (eMailEdit.getText() != null)
                    fields.put("Email_Id__c", String.valueOf(eMailEdit.getText()));
                if (taskDescription.getText() != null)
                    fields.put("Description__c", String.valueOf(taskDescription.getText()));
                if (taskAddress != null)
                    fields.put("Address__c", String.valueOf(taskAddress.getText()));
                if (taskPhoneNumber != null)
                    fields.put("Phone_Number__c", String.valueOf(taskPhoneNumber.getText()));
                if(getStatusFromSpinner != null)
                    fields.put("Status__c",String.valueOf(getStatusFromSpinner));
                if(getNameFromSpinner != null)
                    fields.put("Assign_to_User__c.Name",String.valueOf(getNameFromSpinner));
                //get the LatLong based on address
                geo = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geo.getFromLocationName(taskAddress.getText()
                            .toString(), 5);
                    if (addresses.size() > 0) {
                        getLatFromAddress = String.valueOf(addresses.get(0).getLatitude());
                        getLongFromAddress = String.valueOf(addresses.get(0).getLongitude());
                        fields.put("Location__latitude__s", getLatFromAddress);
                        fields.put("Location__longitude__s", getLongFromAddress);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //method to send data to salesforce
                sendDataToSalesforce(fields, getBaseContext());


                if (mProgressCreate != null || mProgressCreate.isShowing() == true) {
                    mProgressCreate.dismiss();
                }

                return false;
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinner_status_change_new_task)
        {
            String item = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item
            getStatusFromSpinner = item;
        }
        else if(spinner.getId() == R.id.spinner_assign_user_new_task)
        {
            String itemOfUser = parent.getItemAtPosition(position).toString();
            Log.d("Name of the assigned us",itemOfUser);
            getNameFromSpinner = itemOfUser;
        }


    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void sendDataToSalesforce(HashMap<String, Object> fields, final Context act) {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForCreate("v36.0", "Task__c", fields);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    Log.d("Request From the Server",String.valueOf(request.getRequestBody()));
                    Log.d("Result Form server", String.valueOf(result.getStatusCode()));
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Validating form
     */
    private void validateForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateAddress()) {
            return;
        }
    }


    private boolean validateName() {
        if (taskName.getText().toString().trim().isEmpty()) {
            inputLayoutTaskName.setError(getString(R.string.err_msg_name));
            requestFocus(taskName);
            return false;
        } else {
            inputLayoutTaskName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = eMailEdit.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(eMailEdit);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAddress() {
        if (taskAddress.getText().toString().trim().isEmpty()) {
            inputLayoutAddress.setError(getString(R.string.err_msg_address));
            requestFocus(taskAddress);
            return false;
        } else {
            inputLayoutAddress.setErrorEnabled(false);
        }

        return true;
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean isValidDate(String date){
        if(date != null){
        return true;
        }
        return false;
    }
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_task_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.address_new_task:
                    validateAddress();
                    break;
            }
        }
    }




}
