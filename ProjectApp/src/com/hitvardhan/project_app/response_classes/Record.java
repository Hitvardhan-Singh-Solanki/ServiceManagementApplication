package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hitvardhan on 08-12-2016.
 */
public class Record implements Parcelable {
    private Attributes attributes;
    private String Name;
    private String Due_Date__c;
    private String Description__c;
    private String Status__c;
    private String Id;
    private String id;
    private String Email_Id__c;
    private Location__c Location__c;
    private String Phone_Number__c;
    private String address__c;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDue_Date__c() {
        return Due_Date__c;
    }

    public void setDue_Date__c(String due_Date__c) {
        Due_Date__c = due_Date__c;
    }

    public String getDescription__c() {
        return Description__c;
    }

    public void setDescription__c(String description__c) {
        Description__c = description__c;
    }


    public String getStatus__c(){
        return Status__c;
    }

    public void setStatus__c(String setStatus__c){
        setStatus__c  = setStatus__c;
    }


    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        Id = Id;
    }


    public String getEmail_Id__c() {
        return Email_Id__c;
    }

    public void setEmail_Id__c(String email_Id__c) {
        Email_Id__c = email_Id__c;
    }

    public com.hitvardhan.project_app.response_classes.Location__c getLocation__c() {
        return Location__c;
    }

    public void setLocation__c(com.hitvardhan.project_app.response_classes.Location__c location__c)
    {
        Location__c = location__c;
    }

    public String getPhone_Number__c() {
        return Phone_Number__c;
    }

    public void setPhone_Number__c(String phone_Number__c) {
        Phone_Number__c = phone_Number__c;
    }

    public String getAddress__c() {
        return address__c;
    }

    public void setAddress__c(String address__c) {
        this.address__c = address__c;
    }

    protected Record(Parcel in) {
        attributes = (Attributes) in.readValue(Attributes.class.getClassLoader());
        Name = in.readString();
        Due_Date__c = in.readString();
        Description__c = in.readString();
        Status__c = in.readString();
        Id = in.readString();
        id = in.readString();
        Email_Id__c = in.readString();
        Location__c = (Location__c) in.readValue(Location__c.class.getClassLoader());
        Phone_Number__c = in.readString();
        address__c = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(attributes);
        dest.writeString(Name);
        dest.writeString(Due_Date__c);
        dest.writeString(Description__c);
        dest.writeString(Status__c);
        dest.writeString(Id);
        dest.writeString(id);
        dest.writeString(Email_Id__c);
        dest.writeValue(Location__c);
        dest.writeString(Phone_Number__c);
        dest.writeString(address__c);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Record> CREATOR = new Parcelable.Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}