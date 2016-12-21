
package com.hitvardhan.project_app.response_classes;

import java.io.Serializable;

/**
 * Created by Hitvardhan on 15-12-2016.
 */

public class Location__c implements Serializable {
    private Double latitude;
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
