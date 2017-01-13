package me.FrejNielsen.YAC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.ArrayList;

public class YAC {
	
	public static FileUtils fUtils;
	
	protected static ArrayList<String> yes = new ArrayList<>();
	protected static ArrayList<String> no = new ArrayList<>();
	
	public static void main(String[] args) throws ParseException, InterruptedException, IOException {
		fUtils = new FileUtils();
		if(args.length == 0) {
			new AlarmUtils();
			new YAC(null);
		} else
			new YAC(args);
	}
	
	@SuppressWarnings("resource")
	public YAC(String[] args) throws IOException, ParseException, InterruptedException {
		if(args == null) {readAnswers(); return;}
		if(args[0] == "--file") {
			FileChannel src = null;
			FileChannel dest = null;
			try {
				src = new FileInputStream(new File(args[1])).getChannel();
				dest = new FileOutputStream(fUtils.getFile("ytvids.txt")).getChannel();
				dest.transferFrom(src, 0, src.size());
			} finally {
				src.close();
				dest.close();
			}
			System.exit(0);
		}
	}
	
	private void readAnswers() throws IOException {
		BufferedReader br = new BufferedReader(fUtils.getStreamReader("answers.txt"));
		
		String currentAnswer = "";
		String line;
		while((line = br.readLine()) != null) {
			if(!line.trim().startsWith("-")) {
				if(line.startsWith("yes"))
					currentAnswer = "yes";
				else if(line.startsWith("no")) {
					currentAnswer = "no";
				}
			} else {
				String word = line.trim().substring(2);
				if(currentAnswer.equals("yes")) {
					yes.add(word);
					//System.out.println("Added " + word + " as a valid alternative to yes");
				} else if(currentAnswer.equals("no")) {
					no.add(word);
					//System.out.println("Added " + word + " as a valid alternative to no");
				}
			}
		}
		br.close();
	}
}
