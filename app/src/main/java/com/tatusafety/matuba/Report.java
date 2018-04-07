package com.tatusafety.matuba;

/**
 * Created by incentro on 4/7/2018.
 */

public class Report {
    private String name;
    private  String description;
    private int thumbnail;


    public Report(){

    }
    public Report(String name, String description,int thumbnail){
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
