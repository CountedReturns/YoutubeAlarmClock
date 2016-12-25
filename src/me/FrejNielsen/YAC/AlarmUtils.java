package me.FrejNielsen.YAC;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
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
		while (running) {
			if (sc.hasNextLine()) {
				String cmd = sc.nextLine();
				if (cmd.toLowerCase().startsWith("set")) {
					String[] arr = cmd.split(" ");

					switch (arr.length) {
					case 1:
						System.err.println("Usage: set <name> <dd/MM/yyyy hh:mm:ss>.");
						break;
					case 2:
						System.err.println("Usage: set <name> <dd/MM/yyyy hh:mm:ss>.");
						break;
					case 3:
						createAlarm(0, arr[1], arr[2]);
						break;
					case 4:
						createAlarm(1, arr[1], arr[2], arr[3]);
						break;
					}
				} else if (cmd.equalsIgnoreCase("list")) {
					for (Alarm alarm : alarms) {
						String enabled = alarm.isEnabled() ? "Enabled" : "Not enabled";
						System.out.println(alarm.getName() + " - " + alarm.getTime() + " - " + enabled);
					}
				} else if (cmd.toLowerCase().startsWith("addlink")) {
					String[] arr = cmd.split(" ");
					
					switch(arr.length) {
					case 1:
						System.err.println("Usage: addlink <link>.");
						break;
					case 2:
						try {
							YAC.fUtils.addLink(arr[1]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					default:
						System.err.println("Usage: addlink <link>.");
						break;
					}
				}  else if (cmd.toLowerCase().startsWith("removelink")) {
					String[] arr = cmd.split(" ");
					
					switch(arr.length) {
					case 1:
						System.err.println("Usage: removelink <link>.");
						break;
					case 2:
						try {
							YAC.fUtils.removeLink(arr[1]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					default:
						System.err.println("Usage: removelink <link>.");
						break;
					}
				} else {
					System.err.println("Unknown command! Only valid commands are set, list, addlink and removelink!");
				}
			}
		}
		sc.close();
	}

	private boolean alarmExists(String name) {
		for (Alarm alarm : alarms) {
			if (alarm.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("resource")
	private void createAlarm(int info, String name, String... time) {
		if (alarmExists(name)) {
			Scanner in = new Scanner(System.in);

			System.out.println("This will override the existing alarm with the name of " + name + ", continue?");

			String override = in.nextLine();

			if (YAC.no.contains(override.toLowerCase())) {
				return;
			} else if (YAC.yes.contains(override.toLowerCase())) {
				Iterator<Alarm> iterator = alarms.iterator();
				while(iterator.hasNext()) {
					Alarm alarm = iterator.next();
					if(alarm.getName().equals(name)) {
						iterator.remove();
					}
				}
			} else {
				System.err.println("Not a valid answer.");
				createAlarm(info, name, time);
			}
		}

		if (info == 0) {
			ZoneId zoneId = ZoneId.systemDefault();
			ZonedDateTime now = ZonedDateTime.now();
			ZonedDateTime alarm = null;

			LocalTime alarmTime = LocalTime.parse(time[0]);

			if (now.toLocalTime().isBefore(alarmTime)) {
				alarm = ZonedDateTime.of(now.toLocalDate(), alarmTime, zoneId);
			} else {
				alarm = ZonedDateTime.of(now.toLocalDate().plusDays(1), alarmTime, zoneId);
			}

			Alarm alarmInstance = new Alarm(alarm.toEpochSecond() * 1000, this, name, alarms.size());
			alarmInstance.setEnabled(true);

			Thread thread = new Thread(alarmInstance);
			thread.start();

			alarms.add(alarmInstance);
		} else if (info == 1) {
			ZoneId zoneId = ZoneId.systemDefault();
			ZonedDateTime alarm = null;

			LocalDate date = LocalDate.parse(time[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));

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

		String[] ids = TimeZone.getAvailableIDs();
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
