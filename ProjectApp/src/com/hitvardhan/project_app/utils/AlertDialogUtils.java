package com.hitvardhan.project_app.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;

import com.hitvardhan.project_app.AlertDialog;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.TaskDetailsActivity;

/**
 * Created by Hitvardhan on 21-12-2016.
 */

public class AlertDialogUtils {


    public void CreateAlertDialogQuestion(Context ctx, String Title, String Message, String Yes_response, String No_response){
        new android.support.v7.app.AlertDialog.Builder(ctx)
                .setTitle(Title)
                .setMessage(Message)
                .setPositiveButton(Yes_response, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(No_response, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        })
                .setIcon(R.drawable.ic_question_mark)
                .show();


    }
}
