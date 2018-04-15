package com.tatusafety.matuba.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JourneyResponse {


    @SerializedName("totalDistance")
    private String totalDistance;

    @SerializedName("modes")
    private String modes;

    @SerializedName("wDistance")
    private String wDistance;

    @SerializedName("wDirections")
    private String wDirections;

    @SerializedName("tDistance")
    private String tDistance;

   @SerializedName("tFare")
    private String tFare;

   @SerializedName("stopName")
   private String stopName;

    @SerializedName("legs")
    private List<Journey> legs;

    public List<Journey> getLegs() {
        return legs;
    }

    public void setLegs(List<Journey> legs) {
        this.legs = legs;
    }


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
}