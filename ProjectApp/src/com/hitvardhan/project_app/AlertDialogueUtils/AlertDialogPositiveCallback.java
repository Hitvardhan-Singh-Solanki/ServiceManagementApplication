package com.hitvardhan.project_app.AlertDialogueUtils;

/**
 * Created by Hitvardhan on 08-12-2016.
 */

public abstract class AlertDialogPositiveCallback implements AlertDialogCallbacks{
    @Override
    public abstract void onPositiveClick();

    @Override
    public void onNegativeClick() {
    }
}