package com.knepe.megamemory.models;

public class GameTimer {
    private long startTime;
    private long stopTime;

    public GameTimer(){
        startTime = 0;
    }

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public long getStopSeconds(){
        return stopTime / 1000;
    }

    public long getStopSecondsRealTime(){
        long stopTimeRealTime = System.currentTimeMillis() - startTime;
        return stopTimeRealTime / 1000;
    }

    public void stop(){
        stopTime = System.currentTimeMillis() - startTime;
    }

    public String getStopTime(){
        long millis = stopTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds     = seconds % 60;

        String strMinutes = minutes == 1 ? "min" : "mins";
        String strSeconds = seconds == 1 ? "sec" : "secs";

        return minutes == 0 ? String.format("%d %s", seconds, strSeconds) : String.format("%d %s %s %d %s", minutes, strMinutes, "and", seconds, strSeconds);
    }
}
