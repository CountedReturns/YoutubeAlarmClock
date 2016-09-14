package me.FrejNielsen.YAC;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class FileUtils {
	
	private ArrayList<String> links = new ArrayList<String>();
	
	public FileUtils() throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/ytvids.txt")))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       links.add(line);
		    }
		}
	}
	
	public File getFile(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		return file;
	}

	public void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> getLinks() {
		return links;
	}
	
}
