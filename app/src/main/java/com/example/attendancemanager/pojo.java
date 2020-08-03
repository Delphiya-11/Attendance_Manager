package com.example.attendancemanager;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class pojo extends RealmObject {

    @PrimaryKey
    private String sid;
    private String prev;
    private int count;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
