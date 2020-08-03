package com.example.attendancemanager;

public class Subject {
    public String sid, sname;

    public Subject(){

    }

    public Subject(String  sid, String sname){
        this.sid=sid;
        this.sname=sname;
    }

    public String getSid(){return sid;}

    public void setSid(String sid){this.sid=sid;}

    public String getSname(){return sname;}

    public void setSname(String sname){this.sname=sname;}
}
