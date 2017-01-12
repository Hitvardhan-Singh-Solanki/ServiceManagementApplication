package com.hitvardhan.project_app.activity;

        import android.app.ActionBar;
        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TextView;

        import com.hitvardhan.project_app.R;
        import com.hitvardhan.project_app.activity.MainActivity;
        import com.hitvardhan.project_app.response_classes.RecordForAdmin;

public class UserDetailActivity extends AppCompatActivity {
    TextView userNameView;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Intent nameForTheUser = getIntent();
        RecordForAdmin userRecordForAdmin = nameForTheUser
                .getParcelableExtra("RecordObjectForAdmin");
        setDataOnTheUIPanel(userRecordForAdmin);
    }

    private void setDataOnTheUIPanel(RecordForAdmin userRecordForAdmin) {
        userNameView = (TextView) findViewById(R.id.user_name_detail);
        userName = userRecordForAdmin.getName();
        userNameView.setText(userName);
    }


    public void closeTheUserDetailsPage(View view) {
        finish();
    }
}
