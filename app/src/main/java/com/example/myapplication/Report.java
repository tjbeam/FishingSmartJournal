package com.example.myapplication;

import java.util.ArrayList;

public class Report {
    //time of day constants
    public static final int TIME_DAWN = 0;
    public static final int TIME_EARLY_DAY = 1;
    public static final int TIME_LATE_DAY = 2;
    public static final int TIME_DUSK = 3;
    public static final int TIME_NIGHT = 4;

    //main static array list
    public static ArrayList<String> locations = new ArrayList<>();
    public static ArrayList<Report> reports = new ArrayList<>();
    //class variables
    public String location;
    public int numFish;
    public float tideLevel;
    public boolean isEbb;
    public int timeOfDay;
    public float timeFished;

    //constructor
    public Report(String location, int numFish, float tideLevel, boolean isEbb, float timeFished){
        this.location = location;
        this.numFish = numFish;
        this.tideLevel = tideLevel;
        this.isEbb = isEbb;
        this.timeFished = timeFished;

        reports.add(this);

    }
}
