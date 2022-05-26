package com.example.onlineattendance;

public class StudentModel {
    int St_id;
    String St_Name,St_Enrollment;
    boolean isPresent;

    public StudentModel() {
    }

    public StudentModel(int st_id, String st_Name, String st_Enrollment, boolean isPresent) {
        St_id = st_id;
        St_Name = st_Name;
        St_Enrollment = st_Enrollment;
        this.isPresent = isPresent;
    }

    public int getSt_id() {
        return St_id;
    }

    public void setSt_id(int st_id) {
        St_id = st_id;
    }

    public String getSt_Name() {
        return St_Name;
    }

    public void setSt_Name(String st_Name) {
        St_Name = st_Name;
    }

    public String getSt_Enrollment() {
        return St_Enrollment;
    }

    public void setSt_Enrollment(String st_Enrollment) {
        St_Enrollment = st_Enrollment;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
