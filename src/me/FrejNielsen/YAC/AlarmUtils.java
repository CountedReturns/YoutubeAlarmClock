package me.FrejNielsen.YAC;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;

public class AlarmUtils implements Runnable {
	
	private FileUtils fUtils;
	
	private boolean running;
	
	private ArrayList<Alarm> alarms = new ArrayList<Alarm>();
	
	public AlarmUtils() throws ParseException, IOException, InterruptedException {
		fUtils = new FileUtils();
		
		running = true;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void fireAlarm(int id) {
		Random r = new Random();
		
		URI vid = URI.create(fUtils.getLinks().get(r.nextInt(fUtils.getLinks().size())));
		fUtils.openWebpage(vid);
		alarms.get(id).setEnabled(false);
	}

	public void run() {
		Scanner sc = new Scanner(System.in);
		while(running) {
			String cmd = sc.nextLine();
			if(cmd.startsWith("set")) {
				String[] arr = cmd.split(" ");
				
				switch(arr.length) {
				case 1:
					System.err.println("No name specified.");
					break;
				case 2:
					System.err.println("No time specified.");
					break;
				case 3:
					createAlarm(0, arr[1], arr[2]);
					break;
				case 4:
					createAlarm(1, arr[1], arr[2], arr[3]);
					break;
				}
			} else if(cmd.equalsIgnoreCase("list")) {
				for(Alarm alarm : alarms) {
					String enabled = alarm.isEnabled() ? "Enabled" : "Not enabled";
					System.out.println(alarm.getName() + " - " + alarm.getTime() + " - " + enabled);
				}
			}
		}
		sc.close();
	}
	
	private boolean alarmExists(String name) {
		for(Alarm alarm : alarms) {
			if(alarm.getName() == name) {
				return true;
			}
		}
		return false;
	}
	
	private void createAlarm(int info, String name, String... time) {
		if(alarmExists(name)) {
			Scanner sc = new Scanner(System.in);
			
			System.out.println("This will override the existing alarm with the name of " + name + ", continue?");
			
			boolean override = sc.nextBoolean();
			
			sc.close();
			
			if(!override)
				return;
		}
		
		if(info == 0) {
			ZoneId zoneId = ZoneId.systemDefault();
			ZonedDateTime now = ZonedDateTime.now();
			ZonedDateTime alarm = null;
			
			LocalTime alarmTime = LocalTime.parse(time[0]);
			
			if(now.toLocalTime().isBefore(alarmTime)) {
				alarm = ZonedDateTime.of(now.toLocalDate(), alarmTime, zoneId);
			} else {
				alarm = ZonedDateTime.of(now.toLocalDate().plusDays(1), alarmTime, zoneId);
			}
			
			Alarm alarmInstance = new Alarm(alarm.toEpochSecond() * 1000, this, name, alarms.size());
			alarmInstance.setEnabled(true);
			
			Thread thread = new Thread(alarmInstance);
			thread.start();
			
			alarms.add(alarmInstance);
		} else if(info == 1) {
			ZoneId zoneId = ZoneId.systemDefault();
			ZonedDateTime alarm = null;
			
			LocalDate date = LocalDate.parse(time[0]);
			
			LocalTime alarmTime = LocalTime.parse(time[1]);
		
			alarm = ZonedDateTime.of(date, alarmTime, zoneId);
			
			Alarm alarmInstance = new Alarm(alarm.toEpochSecond() * 1000, this, name, alarms.size());
			alarmInstance.setEnabled(true);
			
			Thread thread = new Thread(alarmInstance);
			thread.start();
			
			alarms.add(alarmInstance);
		}
	}
	
	public static TimeZone getTimezone() {
	    Calendar cal = Calendar.getInstance();
	    long milliDiff = cal.get(Calendar.ZONE_OFFSET);

	    String [] ids = TimeZone.getAvailableIDs();
	    String name = null;
	    for (String id : ids) {
	      TimeZone tz = TimeZone.getTimeZone(id);
	      if (tz.getRawOffset() == milliDiff) {
	        // Found a match.
	        name = id;
	        break;
	      }
	    }
	    return TimeZone.getTimeZone(name);
	}
}
