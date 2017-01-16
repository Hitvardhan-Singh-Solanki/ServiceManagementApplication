package com.hitvardhan.project_app.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.hitvardhan.project_app.activity.MainActivity.client;

public class MakeNewTaskActivity extends AppCompatActivity {

    private HashMap<String, Object> fields;
    ImageView closeButtonCross;
    Geocoder geo;
    private Button makeATasButton;
    private EditText et_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_new_task);
        closeButtonCross = (ImageView) findViewById(R.id.close_task_making_cross);
        closeButtonCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        makeATasButton = (Button) findViewById(R.id.make_a_task_button);
        et_search = (EditText) findViewById(R.id.et_search);
        fields = new HashMap<String, Object>();
        fields.put("Name", "Example Task to create App");
        fields.put("Location__latitude__s", "26.8652");
        fields.put("Location__longitude__s", "75.65513");
        makeATasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToSalesforce(fields, getBaseContext());
                geo = new Geocoder(getBaseContext(),Locale.getDefault());
                try{  List<Address> addresses = geo.getFromLocationName(et_search.getText().toString(),5);
                    Log.d("List of Address",addresses.toString());
                    Log.d("Lattiture",String.valueOf(addresses.get(0).getLatitude()));
                }catch (IOException ex){
                    ex.printStackTrace();
                }
              }
        });
    }


    public static void sendDataToSalesforce(HashMap<String, Object> fields, final Context act) {
        RestRequest restRequest;

        try {
            restRequest = RestRequest.getRequestForCreate("v36.0", "Task__c", fields);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    Log.d("Created", "Yeah");
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


}
