package me.jchianelli7.Mine4Me;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//Using jnativehook for keyboard and mouse listeners.
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Miner implements NativeKeyListener {
	static Robot bot;

	public static void main(String[] args) throws AWTException {
		bot = new Robot();
		System.out.println("Starting Script...");
		setup();
	}

	public static void Click() throws InterruptedException {
		bot.mousePress(InputEvent.BUTTON1_MASK);
	}

	public static void Release() throws InterruptedException {
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	boolean isPressed = false;

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		System.out.println("Pressed");
		int key = e.getKeyCode();
		if (key == NativeKeyEvent.VC_PAUSE) {
			if (isPressed) {
				System.out.println("Releasing...........");
				try {
					Release();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				System.out.println("Pressing............");
				try {
					Click();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			isPressed = !isPressed;
		}
	}

	/**
	 * 
	 */
	public static void setup() {
		try {
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage()
					.getName());
			logger.setLevel(Level.OFF);
			logger.setUseParentHandlers(false);
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new Miner());

		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();

		BufferedImage img = null;
		BufferedImage image2 = null;
		try {
			img = ImageIO.read(ClassLoader.getSystemResource("imgs/toneta.jpg"));
			image2 = ImageIO.read(ClassLoader.getSystemResource("imgs/toneta2.jpg"));
		} catch (IOException e) {
			System.exit(1);
		}

		final TrayIcon trayIcon = new TrayIcon(img);
		trayIcon.setImageAutoSize(true);
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a pop-up menu components
		MenuItem aboutItem = new MenuItem("About");
		CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
		CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
		Menu displayMenu = new Menu("Display");
		MenuItem errorItem = new MenuItem("Error");
		MenuItem warningItem = new MenuItem("Warning");
		MenuItem infoItem = new MenuItem("Info");
		MenuItem noneItem = new MenuItem("None");
		MenuItem exitItem = new MenuItem("Exit");

		// Add components to pop-up menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(cb1);
		popup.add(cb2);
		popup.addSeparator();
		popup.add(displayMenu);
		displayMenu.add(errorItem);
		displayMenu.add(warningItem);
		displayMenu.add(infoItem);
		displayMenu.add(noneItem);
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

		JFrame frame = new JFrame("Mine4Me");

		JPanel panel = new JPanel();
		frame.add(panel);

		frame.setIconImage(img);
		frame.add(new JLabel(new ImageIcon(image2)));

		frame.setSize(image2.getWidth(), image2.getHeight());

		frame.repaint();

		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent windowEvent) {
				try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(1);
			}
		});
		frame.setVisible(true);

	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
	}

}
