package me.FrejNielsen.YAC;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class YAC {
	
	private static ArrayList<String> links = new ArrayList<String>();
	
	public static void main(String[] args) throws ParseException, InterruptedException, IOException {
		if(args.length == 0) { //Wrong arguments - show an error.
			System.err.println("ERROR! Usage: java YoutubeAlarmClock hh:mm:ss OR java YoutubeAlarmClock --file <pathtofile>");
			System.exit(1);
		}
		
		new YAC(args);
	}
	
	@SuppressWarnings("resource")
	public YAC(String[] args) throws IOException, ParseException, InterruptedException {
		if(args[0] == "--file") {
			FileChannel src = null;
			FileChannel dest = null;
			try {
				src = new FileInputStream(new File(args[1])).getChannel();
				dest = new FileOutputStream(getFile("ytvids.txt")).getChannel();
				dest.transferFrom(src, 0, src.size());
			} finally {
				src.close();
				dest.close();
			}
			System.exit(0);
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Calendar c = Calendar.getInstance();
			
			Date date = sdf.parse(c.get(Calendar.YEAR) + "-" + "0" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + args[0]);
			
			long diff = date.getTime() - System.currentTimeMillis();
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/ytvids.txt")))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       links.add(line);
			    }
			}
			
			System.out.println("Alarm will go off in " + DurationFormatUtils.formatDurationWords(diff, true, true));
			
			Thread.sleep(diff);
			
			System.out.println("Alarm going off..");
			
			Random r = new Random();
			
			URI vid = URI.create(links.get(r.nextInt(links.size())));
			openWebpage(vid);
		}
	}
	
	private File getFile(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		return file;
	  }
	
	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
