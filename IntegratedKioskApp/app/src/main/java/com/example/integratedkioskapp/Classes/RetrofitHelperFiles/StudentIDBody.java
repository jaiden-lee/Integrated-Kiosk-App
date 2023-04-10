package com.example.integratedkioskapp.Classes.RetrofitHelperFiles;

import com.google.gson.annotations.SerializedName;

public class StudentIDBody {
    @SerializedName("studentID")
    private String studentID;

    public StudentIDBody (String studentID) {
        this.studentID = studentID;
    }
}
