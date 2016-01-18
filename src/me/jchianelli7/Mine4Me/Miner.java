package me.jchianelli7.Mine4Me;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

//Using jnativehook for keyboard and mouse listeners.
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import me.jchianelli7.Mine4Me.gui.KeyList;

public class Miner {

	private String title = "Mine4Me";

	private KeyList keyList;

	JFrame frame;

	Robot bot;

	BufferedImage imgTonetta;
	BufferedImage imgTonettaSquare;

	boolean arePressed;
	boolean listening;

	public static void main(String[] args) throws AWTException {
		System.out.println("Starting");
		new Miner();
	}

	public Miner() {

		keyList = new KeyList();
		loadImages();

		try {
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.OFF);
			logger.setUseParentHandlers(false);
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(new me.jchianelli7.Mine4Me.NativeKeyListener(this));

		} catch (NativeHookException e) {
			e.printStackTrace();
			exit();
		}

		try {
			bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			exit();
		}

		if (!setupSystemTray()) {
			System.out.println("SystemTray is not supported");
		}

		if (!setupJFrame()) {
			System.out.println("JFrame could not be setup!");
			exit();
		}

	}

	private void loadImages() {
		try {
			imgTonetta = ImageIO.read(ClassLoader.getSystemResource("imgs/tonetta.jpg"));
			imgTonettaSquare = ImageIO.read(ClassLoader.getSystemResource("imgs/tonettaSquare.jpg"));
		} catch (IOException e) {
			exit();
		}
	}

	private boolean setupJFrame() {
		frame = new JFrame(title);

		@SuppressWarnings("serial")
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(imgTonetta, 0, 0, null);
			}
		};

		// menu bar
		// adding File>Exit
		JMenuBar menuBar = new JMenuBar();

		JMenu file = new JMenu("File");
		JMenuItem file_exit = new JMenuItem("Exit");
		file_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				exit();
			}
		});

		file.add(file_exit);
		menuBar.add(file);

		// adding Keys>Clear
		JMenu keys = new JMenu("Keys");
		JMenuItem key_clear = new JMenuItem("Clear All");
		key_clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				keyList.clearAll();
			}
		});

		keys.add(key_clear);
		menuBar.add(keys);

		// adding Keys>Add
		JMenuItem key_add = new JMenuItem("Add");
		key_add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				listening=true;
			}
		});

		keys.add(key_add);
		menuBar.add(keys);

		frame.setJMenuBar(menuBar);
		
		/*JLabel label = new JLabel();
		label.setText("Navigate to Keys>Add to add key.");*/

		JList<String> jList_keys = new JList<String>();
		jList_keys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList_keys.setLayoutOrientation(JList.VERTICAL);
		jList_keys.setVisibleRowCount(-1);

		jList_keys.setModel(keyList.getListModel());

		jList_keys.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					@SuppressWarnings("unchecked")
					JList<String> list = (JList<String>) e.getSource();
					int row = list.locationToIndex(e.getPoint());
					keyList.removeKey(row);
				}
			}
		});

		JScrollPane keyListScroller = new JScrollPane(jList_keys);
		keyListScroller.setPreferredSize(new Dimension(80, 250));
		panel.add(keyListScroller);

		frame.add(panel);

		frame.setIconImage(imgTonettaSquare);

		frame.setSize(imgTonetta.getWidth(), imgTonetta.getHeight());
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent windowEvent) {
				exit();
			}

		});
		frame.setVisible(true);

		return true;
	}

	private boolean setupSystemTray() {
		if (!SystemTray.isSupported()) {
			return false;
		}

		final TrayIcon trayIcon = new TrayIcon(imgTonettaSquare);
		trayIcon.setImageAutoSize(true);

		final PopupMenu popup = new PopupMenu();

		MenuItem exitItem = new MenuItem("Exit");

		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		final SystemTray tray = SystemTray.getSystemTray();

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return false;
		}

		return true;
	}

	public void pressKeys() throws InterruptedException {
		for (Integer key : keyList.getKeys()) {
			bot.keyPress(key);
		}
	}

	public void releaseKeys() throws InterruptedException {
		for (Integer key : keyList.getKeys()) {
			bot.keyRelease(key);
		}
	}

	public void exit() {
		System.out.println("Exiting");

		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		System.exit(1);
	}

	public KeyList getKeyList() {
		return keyList;
	}
}
