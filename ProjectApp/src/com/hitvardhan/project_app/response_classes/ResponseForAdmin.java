package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hitvardhan on 10-01-2017.
 */

public class ResponseForAdmin implements Parcelable {

    private List<RecordForAdmin> records = null;

    public List<RecordForAdmin> getRecords() {
        return records;
    }

    public void setRecords(List<RecordForAdmin> records) {
        this.records = records;
    }

    protected ResponseForAdmin(Parcel in) {
        if (in.readByte() == 0x01) {
            records = new ArrayList<RecordForAdmin>();
            in.readList(records, RecordForAdmin.class.getClassLoader());
        } else {
            records = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (records == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(records);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ResponseForAdmin> CREATOR = new Parcelable.Creator<ResponseForAdmin>() {
        @Override
        public ResponseForAdmin createFromParcel(Parcel in) {
            return new ResponseForAdmin(in);
        }

        @Override
        public ResponseForAdmin[] newArray(int size) {
            return new ResponseForAdmin[size];
        }
    };
}