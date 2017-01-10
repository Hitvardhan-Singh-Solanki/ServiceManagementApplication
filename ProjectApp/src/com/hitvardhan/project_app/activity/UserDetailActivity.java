package com.hitvardhan.project_app.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;

public class UserDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Intent nameForTheUser = getIntent();
        nameForTheUser.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        String Name = nameForTheUser.getExtras().getString("NAME");
        TextView initialTextView = (TextView) findViewById(R.id.initial_text);

        initialTextView.append(Name);

    }

}
