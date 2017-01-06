package com.hitvardhan.project_app.interfaces;

import com.hitvardhan.project_app.response_classes.Response;

/**
 * Created by Hitvardhan on 03-01-2017.
 */

public interface NetworkCallbackInterface {
    void onSuccess(Response response);
    void onError();
}
