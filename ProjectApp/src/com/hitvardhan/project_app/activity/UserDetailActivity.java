package com.hitvardhan.project_app.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.response_classes.RecordForAdmin;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.salesforce.androidsdk.rest.RestClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.hitvardhan.project_app.activity.MainActivity.client;

public class UserDetailActivity extends AppCompatActivity {
    private TextView userNameView;
    private String userName;
    private Toolbar toolbar;
    private CollapsingToolbarLayout cTL;
    private ImageView usersProfileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        //get the intent
        Intent nameForTheUser = getIntent();
        RecordForAdmin userRecordForAdmin = nameForTheUser
                .getParcelableExtra("RecordObjectForAdmin");

        //finally set the data on the UI
        setDataOnTheUIPanel(userRecordForAdmin);
    }


    /**
     * OverRidden method to set the on Click of the back button
     * on toolbar
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * set the data on the UI panel local method
     *
     * @param userRecordForAdmin
     */
    private void setDataOnTheUIPanel(RecordForAdmin userRecordForAdmin) {
        toolbar = (Toolbar) findViewById(R.id.toolbarMainTestNewUserDetail);
        usersProfileImageView = (ImageView) findViewById(R.id.profile_image_of_user_from_activity);
        cTL = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparen_csutom));
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTheImageInCollapsibleToolbar(userRecordForAdmin.getFullPhotoUrl(),
                usersProfileImageView, client);
        cTL.setTitle(userRecordForAdmin.getName());


    }

    /**
     * set the image from the URL into the Image view
     *
     * @param url
     * @param imgView
     * @param client
     */
    private void setTheImageInCollapsibleToolbar(String url, ImageView imgView,
                                                 final RestClient client) {
        OkHttpClient clientForImage = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + client.getAuthToken())
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(getBaseContext())
                .downloader(new OkHttp3Downloader(clientForImage))
                .build();
        picasso.load(url).into(imgView);
    }
}
