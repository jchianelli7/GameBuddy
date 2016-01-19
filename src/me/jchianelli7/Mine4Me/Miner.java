package me.jchianelli7.Mine4Me;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

//Using jnativehook for keyboard and mouse listeners.
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import me.jchianelli7.Mine4Me.gui.KeyList;

public class Miner {

	private String title = "Mine4Me";

	private KeyList keyList;

	public int mouseButton;

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
		frame.setIconImage(imgTonettaSquare);

		frame.setSize(new Dimension(imgTonetta.getWidth(), imgTonetta.getHeight()));
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent windowEvent) {
				exit();
			}

		});

		@SuppressWarnings("serial")
		JPanel panel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(imgTonetta, 0, 0, null);
			}
		};
		panel.setSize(new Dimension(frame.getWidth()/2, frame.getHeight()/2));
		//panel.setBorder(new TitledBorder("===="));

		setupMenuBar(frame);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.NORTHWEST;

		// JLabel
		JLabel label = new JLabel();
		label.setText("Navigate to Keys > Add to add key.");
		label.setOpaque(true);
		
		panel.add(label, c);

		// JRadioButton
		JCheckBox Mouse1 = new JCheckBox("Mouse 1");
		JCheckBox Mouse2 = new JCheckBox("Mouse 2");
		

		Mouse1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Mouse1.isSelected()) {
					Mouse2.setSelected(false);
				} else {
					Mouse1.setSelected(false);
				}
				mouseButton = InputEvent.BUTTON1_DOWN_MASK;

			}
		});
		Mouse2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Mouse2.isSelected()) {
					Mouse1.setSelected(false);
				} else {
					Mouse2.setSelected(false);
				}
				mouseButton = InputEvent.BUTTON3_DOWN_MASK;
			}
		});
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		panel.add(Mouse1, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		panel.add(Mouse2, c);

		// JLabel
		JLabel label1 = new JLabel();
		label1.setText("Navigate to Keys>Add to add key.");
		label1.setOpaque(true);
		
	/*	JLabel label2 = new JLabel();
		label2.setText("Press \"Pause\" to start.");
		label2.setOpaque(true);
		panel.add(label2);*/
		
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
		keyListScroller.setPreferredSize(new Dimension(75, 250));
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		
		c.gridheight = 3;
		c.anchor = GridBagConstraints.EAST;
		panel.add(keyListScroller, c);
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;

		frame.add(panel);
		frame.setVisible(true);

		return true;
	}

	private void setupMenuBar(JFrame frame) {
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
		// adding Keys>Add
		JMenuItem key_add = new JMenuItem("Add");
		key_add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				listening = true;
			}
		});

		keys.add(key_add);

		menuBar.add(keys);
		frame.setJMenuBar(menuBar);
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
