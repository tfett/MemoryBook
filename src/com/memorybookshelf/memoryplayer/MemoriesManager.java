package com.memorybookshelf.memoryplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;

public class MemoriesManager {
	// SDCard Path
	File sdPathFile = Environment.getExternalStorageDirectory();
	String filePath = sdPathFile.getAbsolutePath();
	final String MEDIA_PATH = new String(filePath + "/memory/");

	// final String MEDIA_PATH = new String("file:///android_asset");
	private ArrayList<HashMap<String, String>> memoriesList = new ArrayList<HashMap<String, String>>();

	// Constructor
	public MemoriesManager() {

	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList() {
		File home = new File(MEDIA_PATH);

		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> memory = new HashMap<String, String>();
				memory.put(
						"memoryTitle",
						file.getName().substring(0,
								(file.getName().length() - 4)));
				memory.put("memoryPath", file.getPath());

				// Adding each memory to MemoriesLis
				memoriesList.add(memory);
			}
		}
		// return memories list array
		return memoriesList;
	}

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".m4a"));
		}
	}
}
