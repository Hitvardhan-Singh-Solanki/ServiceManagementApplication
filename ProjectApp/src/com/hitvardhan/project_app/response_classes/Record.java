package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hitvardhan on 08-12-2016.
 */
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Record implements Serializable, Parcelable
{

    private Attributes attributes;
    private String Id;
    private String Name;
    private String Due_Date__c;
    private String Description__c;
    private String Status__c;
    private String Email_Id__c;
    private Location__c Location__c;
    private String Phone_Number__c;
    private String Address__c;
    private Assign_to_User__c Assign_to_User__r;
    public final static Parcelable.Creator<Record> CREATOR = new Creator<Record>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Record createFromParcel(Parcel in) {
            Record instance = new Record();
            instance.attributes = ((Attributes) in.readValue((Attributes.class.getClassLoader())));
            instance.Id = ((String) in.readValue((String.class.getClassLoader())));
            instance.Name = ((String) in.readValue((String.class.getClassLoader())));
            instance.Due_Date__c = ((String) in.readValue((String.class.getClassLoader())));
            instance.Description__c = ((String) in.readValue((String.class.getClassLoader())));
            instance.Status__c = ((String) in.readValue((String.class.getClassLoader())));
            instance.Email_Id__c = ((String) in.readValue((String.class.getClassLoader())));
            instance.Location__c = ((Location__c) in.readValue((Location__c.class
                    .getClassLoader())));
            instance.Phone_Number__c = ((String) in.readValue((String.class.getClassLoader())));
            instance.Address__c = ((String) in.readValue((String.class.getClassLoader())));
            instance.Assign_to_User__r = ((Assign_to_User__c) in.readValue((Assign_to_User__c.class
                    .getClassLoader())));
            return instance;
        }

        public Record[] newArray(int size) {
            return (new Record[size]);
        }

    }
            ;
    private final static long serialVersionUID = 5488633207311048229L;

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

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getDue_Date__c() {
        return Due_Date__c;
    }

    public void setDue_Date__c(String Due_Date__c) {
        this.Due_Date__c = Due_Date__c;
    }

    public String getDescription__c() {
        return Description__c;
    }

    public void setDescription__c(String Description__c) {
        this.Description__c = Description__c;
    }

    public String getStatus__c() {
        return Status__c;
    }

    public void setStatus__c(String Status__c) {
        this.Status__c = Status__c;
    }

    public String getEmail_Id__c() {
        return Email_Id__c;
    }

    public void setEmail_Id__c(String Email_Id__c) {
        this.Email_Id__c = Email_Id__c;
    }

    public Location__c getLocation__c() {
        return Location__c;
    }

    public void setLocation__c(Location__c Location__c) {
        this.Location__c = Location__c;
    }

    public String getPhone_Number__c() {
        return Phone_Number__c;
    }

    public void setPhone_Number__c(String Phone_Number__c) {
        this.Phone_Number__c = Phone_Number__c;
    }

    public String getAddress__c() {
        return Address__c;
    }

    public void setAddress__c(String Address__c) {
        this.Address__c = Address__c;
    }

    public Assign_to_User__c getAssign_to_User__r() {
        return Assign_to_User__r;
    }

    public void setAssign_to_User__r(Assign_to_User__c Assign_to_User__r) {
        this.Assign_to_User__r = Assign_to_User__r;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(attributes);
        dest.writeValue(Id);
        dest.writeValue(Name);
        dest.writeValue(Due_Date__c);
        dest.writeValue(Description__c);
        dest.writeValue(Status__c);
        dest.writeValue(Email_Id__c);
        dest.writeValue(Location__c);
        dest.writeValue(Phone_Number__c);
        dest.writeValue(Address__c);
        dest.writeValue(Assign_to_User__r);
    }

    public int describeContents() {
        return 0;
    }

}