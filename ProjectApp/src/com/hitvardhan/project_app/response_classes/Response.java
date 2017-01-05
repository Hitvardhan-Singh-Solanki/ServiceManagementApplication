package com.hitvardhan.project_app.response_classes;

/**
 * Created by Hitvardhan on 08-12-2016.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.hitvardhan.project_app.response_classes.Record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable, Parcelable {

    private List <Record> records = new ArrayList < > ();

    public List < Record > getRecords() {
        return records;
    }

    public void setRecords(List < Record > records) {
        this.records = records;
    }

    protected Response(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}