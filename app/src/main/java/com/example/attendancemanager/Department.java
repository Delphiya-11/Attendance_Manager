package com.example.attendancemanager;

public class Department {
    public int id;
    public String dept;
    public String hod;

    public Department(){

    }

    public Department(int id, String dept, String hod){
        this.id=id;
        this.dept=dept;
        this.hod=hod;
    }

    public int getId(){return id;}

    public void setId(int id){this.id=id;}

    public String getDept(){return dept;}

    public void setDept(String dept){this.dept=dept;}

    public String  getHod(){return hod;}

    public void setHod(String hod){this.hod=hod;}
}
