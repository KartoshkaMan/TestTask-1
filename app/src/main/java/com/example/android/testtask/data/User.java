package com.example.android.testtask.data;

public class User {

    private int mId;
    private String mName;
    private String mSurname;
    private String mInfo;
    private String mDate;

    public int getId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public String getSurname() {
        return mSurname;
    }
    public void setSurname(String surname) {
        mSurname = surname;
    }

    public String getInfo() {
        return mInfo;
    }
    public void setInfo(String info) {
        mInfo = info;
    }

    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        mDate = date;
    }

    public String getFullName() {
        return mName + " " + mSurname;
    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + mName + '\'' +
                ", Surname='" + mSurname + '\'' +
                '}';
    }
}
