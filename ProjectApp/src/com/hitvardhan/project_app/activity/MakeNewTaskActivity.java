package com.hitvardhan.project_app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hitvardhan.project_app.R;

public class MakeNewTaskActivity extends AppCompatActivity {

    ImageView closeButtonCross;
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
    }
}
