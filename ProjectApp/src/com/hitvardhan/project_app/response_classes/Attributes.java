package com.hitvardhan.project_app.response_classes;

import java.io.Serializable;

public class Attributes implements Serializable{

    private String type;
    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
