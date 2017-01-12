package com.hitvardhan.project_app.interfaces;

import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.response_classes.ResponseForAdmin;

/**
 * Created by Hitvardhan on 10-01-2017.
 */

public interface NetworkCallbackForAdmin {
    void onSuccess(ResponseForAdmin responseAdmin);
    void onError();
}
