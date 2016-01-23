package me.jchianelli7.Mine4Me;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
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
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

//Using jnativehook for keyboard and mouse listeners.
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import me.jchianelli7.Mine4Me.gui.KeyList;
import me.jchianelli7.Mine4Me.gui.Theme;

public class Miner {

	public static Miner instance;
	public static Settings settings;

	private KeyList keyList;

	public int mouseButton;

	public JFrame frame;

	public FileTyper fileTyper;
	Robot bot;

	boolean arePressed;
	boolean listening;

	public JSpinner betweenChars;
	public JSpinner betweenLines;

	public static void main(String[] args) throws AWTException {
		System.out.println("Starting");
		instance = new Miner();
	}

	public Miner() {
		settings = new Settings();
		fileTyper = new FileTyper();
		keyList = new KeyList();

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

	/**
	 * @wbp.parser.entryPoint
	 */
	private boolean setupJFrame() {
		Theme theme = getSettings().getCurrentTheme();

		frame = new JFrame(getSettings().getName());
		frame.setIconImage(theme.getIconImage());

		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent windowEvent) {
				exit();
			}

		});

		@SuppressWarnings("serial")
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (getSettings().getCurrentTheme().hasBackgroundImage()) {
					g.drawImage(getSettings().getCurrentTheme().getBackgroundImage(), 0, 0, null);
				}
			}
		};

		if (theme.hasBackgroundImage()) {
			panel.setPreferredSize(
					new Dimension(theme.getBackgroundImage().getWidth(), theme.getBackgroundImage().getHeight()));
			System.out.println(
					theme.getBackgroundImage().getWidth() + "x" + theme.getBackgroundImage().getHeight() + "y");

		} else {
			panel.setPreferredSize(new Dimension(480, 360));
		}

		setupMenuBar(frame);

		// JComboBox
		ArrayList<String> txtFiles = new ArrayList<String>();

		try {
			File filesFolder = new File(ClassLoader.getSystemResource("files").toURI());
			File[] fileList = filesFolder.listFiles();

			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile()) {
					txtFiles.add(fileList[i].getName());
				}
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		panel.setLayout(null);

		frame.getContentPane().add(panel);
		JLabel label = new JLabel();
		label.setBounds(10, 11, 218, 19);
		panel.add(label);
		label.setText("Navigate to Keys > Add to add key.");

		label.setOpaque(true);

		JLabel label2 = new JLabel();
		label2.setBounds(10, 28, 152, 25);
		panel.add(label2);
		label2.setText("Press \"Pause\" to start.");
		label2.setOpaque(true);

		JLabel label3 = new JLabel();
		label3.setBounds(10, 139, 103, 25);
		panel.add(label3);
		label3.setText("Select .txt File");
		label3.setOpaque(true);

		// JRadioButton
		JCheckBox Mouse1 = new JCheckBox("Mouse 1");
		Mouse1.setBounds(10, 56, 75, 25);
		panel.add(Mouse1);
		JCheckBox Mouse2 = new JCheckBox("Mouse 2");
		Mouse2.setBounds(87, 56, 89, 24);
		panel.add(Mouse2);
		JButton clearSelectionButton = new JButton("Clear selection");
		clearSelectionButton.setBounds(10, 88, 103, 25);
		panel.add(clearSelectionButton);

		JComboBox text_files = new JComboBox(txtFiles.toArray());
		text_files.setBounds(10, 165, 119, 25);
		panel.add(text_files);
		text_files.setRenderer(new MyComboBoxRenderer("Choose..."));
		text_files.setSelectedIndex(-1);

		JButton passwordButton = new JButton("Press to start");
		passwordButton.setBounds(10, 192, 119, 25);
		panel.add(passwordButton);

		// JSpinner
		betweenChars = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 10));
		betweenChars.setBounds(10, 219, 50, 25);
		panel.add(betweenChars);

		betweenLines = new JSpinner(new SpinnerNumberModel(50, 0, 1000, 50));
		betweenLines.setBounds(10, 243, 50, 25);
		panel.add(betweenLines);

		JLabel charsLabel = new JLabel();
		charsLabel.setBounds(70, 224, 66, 14);
		panel.add(charsLabel);
		charsLabel.setText("Chars(ms)");
		charsLabel.setOpaque(true);

		JLabel linesLabel = new JLabel();
		linesLabel.setBounds(70, 248, 59, 14);
		panel.add(linesLabel);
		linesLabel.setText("Lines(ms)");
		linesLabel.setOpaque(true);

		JList<String> jList_keys = new JList<String>();
		jList_keys.setBounds(355, 32, 73, 248);
		panel.add(jList_keys);
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
		passwordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileTyper.isRunning()) {
					fileTyper.stop();
					((JButton) e.getSource()).setText("Press to start");
				} else {
					fileTyper.run("40wordcommon.txt");
					((JButton) e.getSource()).setText("Running");
				}

			}
		});
		clearSelectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				text_files.setSelectedIndex(-1);
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

		frame.pack();
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

		// adding File>Upload
		JMenuItem file_upload = new JMenuItem("Upload (in construction)");
		file_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// add method to upload.
			}
		});

		file.add(file_upload);
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

		keys.add(key_clear);
		
		//Adding Themes
		JMenu graphics = new JMenu("Graphics");
		for (Theme t : getSettings().getThemes()) {
			JMenuItem themeMenuItem = new JMenuItem(t.getName());
			themeMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					getSettings().setTheme(((JMenuItem) event.getSource()).getText());
				}
			});
			graphics.add(themeMenuItem);
		}

		menuBar.add(graphics);
		frame.setJMenuBar(menuBar);
	}

	private boolean setupSystemTray() {
		if (!SystemTray.isSupported()) {
			return false;
		}

		TrayIcon trayIcon;

		if (getSettings().getCurrentTheme().hasIconImage()) {
			trayIcon = new TrayIcon(getSettings().getCurrentTheme().getIconImage());
		} else {
			trayIcon = null;
		}

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

	public Settings getSettings() {
		return settings;
	}

	// JComboBox title setup
	class MyComboBoxRenderer extends JLabel implements ListCellRenderer {
		private String _title;

		public MyComboBoxRenderer(String title) {
			_title = title;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean hasFocus) {
			if (index == -1 && value == null)
				setText(_title);
			else
				setText(value.toString());
			return this;
		}

	}
}
