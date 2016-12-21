package com.hitvardhan.project_app.response_classes;

/**
 * Created by Hitvardhan on 08-12-2016.
 */

import com.hitvardhan.project_app.response_classes.Record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Response implements Serializable{

    private List <Record> records = new ArrayList < > ();

    public List < Record > getRecords() {
        return records;
    }

    public void setRecords(List < Record > records) {
        this.records = records;
    }
}