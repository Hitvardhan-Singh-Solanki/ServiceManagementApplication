package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hitvardhan on 10-01-2017.
 */

public class RecordForAdmin implements Parcelable {
    private Attributes attributes;
    private String Id;
    private String Name;
    private String FullPhotoUrl;
    private UserRole UserRole;
    private String MobilePhone;
    private Location__c Location__c;
    private String Email;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = Name;
    }

    public String getFullPhotoUrl() {
        return FullPhotoUrl;
    }

    public void setFullPhotoUrl(String FullPhotoUrl) {
        this.FullPhotoUrl = FullPhotoUrl;
    }

    public UserRole getUserRole() {
        return UserRole;
    }

    public void setUserRole(UserRole UserRole) {
        this.UserRole = UserRole;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(String MobilePhone) {
        this.MobilePhone = MobilePhone;
    }

    public Location__c getLocationC() {
        return Location__c;
    }

    public void setLocationC(Location__c Location__c) {
        this.Location__c = Location__c;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = Email;
    }

    protected RecordForAdmin(Parcel in) {
        attributes = (Attributes) in.readValue(Attributes.class.getClassLoader());
        Id = in.readString();
        Name = in.readString();
        FullPhotoUrl = in.readString();
        UserRole = (UserRole) in.readValue(UserRole.class.getClassLoader());
        MobilePhone = in.readString();
        Location__c = (Location__c) in.readValue(Location__c.class.getClassLoader());
        Email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(attributes);
        dest.writeString(Id);
        dest.writeString(Name);
        dest.writeString(FullPhotoUrl);
        dest.writeValue(UserRole);
        dest.writeString(MobilePhone);
        dest.writeValue(Location__c);
        dest.writeString(Email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RecordForAdmin> CREATOR = new Parcelable.Creator<RecordForAdmin>() {
        @Override
        public RecordForAdmin createFromParcel(Parcel in) {
            return new RecordForAdmin(in);
        }

        @Override
        public RecordForAdmin[] newArray(int size) {
            return new RecordForAdmin[size];
        }
    };
}