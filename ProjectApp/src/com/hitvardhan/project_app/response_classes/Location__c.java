
package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/**
 * Created by Hitvardhan on 15-12-2016.
 */

public class Location__c implements Serializable, Parcelable {
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


    protected Location__c(Parcel in) {
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Location__c> CREATOR =
            new Parcelable.Creator<Location__c>() {
        @Override
        public Location__c createFromParcel(Parcel in) {
            return new Location__c(in);
        }

        @Override
        public Location__c[] newArray(int size) {
            return new Location__c[size];
        }
    };
}