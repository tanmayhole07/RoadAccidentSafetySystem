package com.example.roadaccidentsafetysystem.Models;

public class ModelEmergencyContact {

    String name, contact, timeStamp;

    public ModelEmergencyContact() {
    }

    public ModelEmergencyContact(String name, String contact, String timeStamp) {
        this.name = name;
        this.contact = contact;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
