package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hitvardhan on 10-01-2017.
 */

public class Attributes_Ex implements Parcelable {

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


    protected Attributes_Ex(Parcel in) {
        type = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Attributes_Ex> CREATOR = new Parcelable.Creator<Attributes_Ex>() {
        @Override
        public Attributes_Ex createFromParcel(Parcel in) {
            return new Attributes_Ex(in);
        }

        @Override
        public Attributes_Ex[] newArray(int size) {
            return new Attributes_Ex[size];
        }
    };
}