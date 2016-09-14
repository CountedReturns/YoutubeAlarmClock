package me.FrejNielsen.YAC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class Alarm implements Runnable {

	private boolean enabled;
	
	private long end;
	
	private String name;
	
	private int id;
	
	private AlarmUtils aUtils;
	
	public Alarm(long end, AlarmUtils aUtils, String name, int id) {
		this.end = end;
		this.aUtils = aUtils;
		this.name = name;
		this.id = id;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTime() {
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), zone);
		
		return ldt.toString().replace('T', ' ');
	}
	
	public void run() {
		System.out.println("Alarm will go off in " + DurationFormatUtils.formatDurationWords(end - System.currentTimeMillis(), true, true));
		while(enabled) {
			if(end - System.currentTimeMillis() <= 0)
				aUtils.fireAlarm(id);
		}
	}
}
