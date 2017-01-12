package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hitvardhan on 10-01-2017.
 */

public class UserRole implements Parcelable {
    private Attributes_Ex attributes;
    private String Name;

    public Attributes_Ex getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes_Ex attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    protected UserRole(Parcel in) {
        attributes = (Attributes_Ex) in.readValue(Attributes_Ex.class.getClassLoader());
        Name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(attributes);
        dest.writeString(Name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserRole> CREATOR = new Parcelable.Creator<UserRole>() {
        @Override
        public UserRole createFromParcel(Parcel in) {
            return new UserRole(in);
        }

        @Override
        public UserRole[] newArray(int size) {
            return new UserRole[size];
        }
    };
}