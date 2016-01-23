package me.jchianelli7.Mine4Me;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import me.jchianelli7.Mine4Me.gui.Theme;

public class Settings {

	private String name = "Game Buddy";

	private Theme currentTheme;
	private ArrayList<Theme> themeList;

	public Settings() {
		themeList = new ArrayList<Theme>();

		try {
			File themesFolder = new File(ClassLoader.getSystemResource("./themes").toURI());
			File[] themeFileList = themesFolder.listFiles();

			for (int i = 0; i < themeFileList.length; i++) {
				if (themeFileList[i].isDirectory()) {
					themeList.add(new Theme(themeFileList[i].getName()));
				}
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		currentTheme = getDefaultTheme();

	}
	
	public void setTheme(String name) {
		currentTheme = getTheme(name);
		Miner.instance.frame.repaint();
	}
	
	public Theme getCurrentTheme() {
		return currentTheme;
	}
	
	public ArrayList<Theme> getThemes() {
		return themeList;
	}

	public String getName() {
		return name;
	}

	public Theme getDefaultTheme() {
		return getTheme("Default");
	}

	public Theme getTheme(String name) {
		for (Theme t : themeList) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}

}
