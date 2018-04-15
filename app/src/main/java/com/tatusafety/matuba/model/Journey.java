package com.tatusafety.matuba.model;



public class Journey {


    private String totalDistance;

    private String modes;

    private String wDistance;

    private String wDirections;

    private String tDistance;

    private String tFare;

    private String stopName;


    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getModes() {
        return modes;
    }

    public void setModes(String modes) {
        this.modes = modes;
    }

    public String getwDistance() {
        return wDistance;
    }

    public void setwDistance(String wDistance) {
        this.wDistance = wDistance;
    }

    public String getwDirections() {
        return wDirections;
    }

    public void setwDirections(String wDirections) {
        this.wDirections = wDirections;
    }

    public String gettDistance() {
        return tDistance;
    }

    public void settDistance(String tDistance) {
        this.tDistance = tDistance;
    }

    public String gettFare() {
        return tFare;
    }

    public void settFare(String tFare) {
        this.tFare = tFare;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public Journey(String totalDistance, String modes, String wDistance, String wDirections, String tDistance, String tFare, String stopName) {
        this.totalDistance = totalDistance;
        this.modes = modes;
        this.wDistance = wDistance;
        this.wDirections = wDirections;
        this.tDistance = tDistance;
        this.tFare = tFare;
        this.stopName = stopName;
    }
}
