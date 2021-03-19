package com.example.roadaccidentsafetysystem;

public class ModelViewAccident {

    String accDescription, address, city, latitudeAccident, longitudeAccident, peopleInjured, subLocal, timestamp, accidentPostedBy;

    public ModelViewAccident() {
    }

    public ModelViewAccident(String accDescription, String address, String city, String latitudeAccident, String longitudeAccident, String peopleInjured, String subLocal, String timestamp, String accidentPostedBy) {
        this.accDescription = accDescription;
        this.address = address;
        this.city = city;
        this.latitudeAccident = latitudeAccident;
        this.longitudeAccident = longitudeAccident;
        this.peopleInjured = peopleInjured;
        this.subLocal = subLocal;
        this.timestamp = timestamp;
        this.accidentPostedBy = accidentPostedBy;
    }

    public String getAccDescription() {
        return accDescription;
    }

    public void setAccDescription(String accDescription) {
        this.accDescription = accDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitudeAccident() {
        return latitudeAccident;
    }

    public void setLatitudeAccident(String latitudeAccident) {
        this.latitudeAccident = latitudeAccident;
    }

    public String getLongitudeAccident() {
        return longitudeAccident;
    }

    public void setLongitudeAccident(String longitudeAccident) {
        this.longitudeAccident = longitudeAccident;
    }

    public String getPeopleInjured() {
        return peopleInjured;
    }

    public void setPeopleInjured(String peopleInjured) {
        this.peopleInjured = peopleInjured;
    }

    public String getSubLocal() {
        return subLocal;
    }

    public void setSubLocal(String subLocal) {
        this.subLocal = subLocal;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccidentPostedBy() {
        return accidentPostedBy;
    }

    public void setAccidentPostedBy(String accidentPostedBy) {
        this.accidentPostedBy = accidentPostedBy;
    }

}
