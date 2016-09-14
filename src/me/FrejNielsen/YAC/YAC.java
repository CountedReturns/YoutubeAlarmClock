package me.FrejNielsen.YAC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;

public class YAC {
	
	private FileUtils fUtils;
	
	public static void main(String[] args) throws ParseException, InterruptedException, IOException {
		if(args.length == 0) {
			new AlarmUtils();
		} else
			new YAC(args);
	}
	
	@SuppressWarnings("resource")
	public YAC(String[] args) throws IOException, ParseException, InterruptedException {
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
}
