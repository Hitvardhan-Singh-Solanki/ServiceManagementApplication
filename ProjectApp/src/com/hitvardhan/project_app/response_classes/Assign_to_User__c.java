package com.hitvardhan.project_app.response_classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Hitvardhan on 12-01-2017.
 */

public class Assign_to_User__c  implements Serializable, Parcelable
{

    private Attributes_Ex attributes;
    private String Name;
    private String Id;
    public final static Parcelable.Creator<Assign_to_User__c> CREATOR
            = new Creator<Assign_to_User__c>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Assign_to_User__c createFromParcel(Parcel in) {
            Assign_to_User__c instance = new Assign_to_User__c();
            instance.attributes = ((Attributes_Ex) in.readValue((Attributes_Ex.class.getClassLoader())));
            instance.Name = ((String) in.readValue((String.class.getClassLoader())));
            instance.Id = ((String)in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Assign_to_User__c[] newArray(int size) {
            return (new Assign_to_User__c[size]);
        }

    }
            ;
    private final static long serialVersionUID = -9136638130554362148L;

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

    public String getId(){ return Id; }

    public void serId(String Id){ this.Id = Id; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(attributes);
        dest.writeValue(Name);
        dest.writeValue(Id);
    }

    public int describeContents() {
        return 0;
    }

}
