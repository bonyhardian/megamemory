package com.knepe.megamemory;

import android.app.Activity;

public class TimerHud {
	private long startTime;
	private long stopTime;
	
	public TimerHud(){
		startTime = 0;
	}
	
	public void start(){
		startTime = System.currentTimeMillis();
	}
	
	public void reset(){
		startTime = 0;
	}
	
	public long getStopSeconds(){
		return stopTime / 1000;
	}
	
	public String getTime(){
		long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds     = seconds % 60;

        return String.format("%d:%02d", minutes, seconds);
	}
	
	public void stop(){
		stopTime = System.currentTimeMillis() - startTime;
	}
	
	public String getStopTime(Activity activity){
		long millis = stopTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds     = seconds % 60;
    	
        String strMinutes = minutes == 1 ? activity.getString(R.string.str_minute) : activity.getString(R.string.str_minute);
		String strSeconds = seconds == 1 ? activity.getString(R.string.str_second) : activity.getString(R.string.str_seconds);
		
        return minutes == 0 ? String.format("%d %s", seconds, strSeconds) : String.format("%d %s %s %d %s", minutes, strMinutes, activity.getString(R.string.str_and), seconds, strSeconds);
	}
}
