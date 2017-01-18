package com.hitvardhan.project_app.constants;

/**
 * Created by Hitvardhan on 06-01-2017.
 */

public class SoqlQueries {

    /**
     * constant soql queries
     */
    public static String soqlForTasks =
            "SELECT Id, Name, Due_Date__c, Description__c, Status__c," +
                    " Email_Id__c, Location__c, Phone_Number__c" +
                    ", Address__c,Assign_to_User__r.Name, Assign_to_User__r.Id" +
                    "  FROM Task__c ";

    public static String soqlForEngineers =
            "SELECT Id, Name, fullphotoURL, UserRole.Name, MobilePhone," +
                    "Location__C, " +
                    "Email " +
                    "FROM User WHERE UserRole.name= 'Service Engineer'";

}
