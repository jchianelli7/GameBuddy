package me.jchianelli7.Mine4Me;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import me.jchianelli7.Mine4Me.gui.KeyList;

public class MinerTest {
	public static MinerTest instance;
	public static final Settings settings = new Settings();

	private KeyList keyList;

	public int mouseButton;

	public JFrame frame;

	public FileTyper fileTyper;
	Robot bot;

	BufferedImage imgTonetta;
	BufferedImage imgTonettaSquare;

	boolean arePressed;
	boolean listening;

	public JSpinner betweenChars;
	public JSpinner betweenLines;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					instance = new MinerTest();
					instance.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MinerTest() {
		initialize();
		fileTyper = new FileTyper();
		keyList = new KeyList();
		loadImages();

		try {
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.OFF);
			logger.setUseParentHandlers(false);
			GlobalScreen.registerNativeHook();
			// GlobalScreen.addNativeKeyListener(new
			// me.jchianelli7.Mine4Me.NativeKeyListener(this));

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

	}

	private void loadImages() {
		try {
			imgTonetta = ImageIO.read(ClassLoader.getSystemResource("imgs/tonetta.jpg"));
			imgTonettaSquare = ImageIO.read(ClassLoader.getSystemResource("imgs/tonettaSquare.jpg"));
		} catch (IOException e) {
			exit();
		}
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(getSettings().getName());
		frame.setIconImage(imgTonettaSquare);

		frame.setSize(new Dimension(500, 500)); // (imgTonetta.getWidth(),
												// imgTonetta.getHeight())
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
				g.drawImage(imgTonetta, 0, 0, null);
			}
		};
		panel.setSize(new Dimension(frame.getWidth() / 2, frame.getHeight() / 2));
		// panel.setBorder(new TitledBorder("===="));

		setupMenuBar(frame);
		panel.setLayout(null);

		// JLabel
		JLabel label = new JLabel();
		label.setBounds(10, 11, 173, 14);
		label.setText("Navigate to Keys > Add to add key.");
		label.setOpaque(true);
		panel.add(label);

		JLabel label2 = new JLabel();
		label2.setBounds(10, 34, 109, 14);
		label2.setText("Press \"Pause\" to start.");
		label2.setOpaque(true);
		panel.add(label2);

		// JRadioButton
		JCheckBox Mouse1 = new JCheckBox("Mouse 1");
		Mouse1.setBounds(10, 55, 65, 23);
		JCheckBox Mouse2 = new JCheckBox("Mouse 2");
		Mouse2.setBounds(77, 55, 65, 23);

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

		panel.add(Mouse1);

		panel.add(Mouse2);

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

		JComboBox text_files = new JComboBox(txtFiles.toArray());
		text_files.setBounds(10, 119, 97, 20);
		text_files.setRenderer(new MyComboBoxRenderer("Choose..."));
		text_files.setSelectedIndex(-1);
		text_files.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		JButton clearSelectionButton = new JButton("Clear selection");
		clearSelectionButton.setBounds(10, 85, 103, 23);
		clearSelectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				text_files.setSelectedIndex(-1);
			}
		});

		panel.add(clearSelectionButton);
		panel.add(text_files);

		JButton passwordButton = new JButton("Press to start");
		passwordButton.setBounds(10, 139, 97, 23);
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
		panel.add(passwordButton);

		// JSpinner
		betweenChars = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 10));
		betweenChars.setBounds(10, 173, 63, 20);
		betweenLines = new JSpinner(new SpinnerNumberModel(50, 0, 1000, 50));
		betweenLines.setBounds(10, 195, 63, 20);

		panel.add(betweenChars);

		JLabel charsLabel = new JLabel();
		charsLabel.setBounds(77, 176, 49, 14);
		charsLabel.setText("Chars(ms)");
		charsLabel.setOpaque(true);

		panel.add(charsLabel);

		panel.add(betweenLines);

		JLabel linesLabel = new JLabel();
		linesLabel.setBounds(77, 198, 45, 14);
		linesLabel.setText("Lines(ms)");
		linesLabel.setOpaque(true);

		panel.add(linesLabel);

		JScrollPane keyListScroller = new JScrollPane();
		keyListScroller.setBounds(356, 87, 75, 250);
		keyListScroller.setPreferredSize(new Dimension(75, 250));

		panel.add(keyListScroller);

		frame.getContentPane().add(panel);
		JList<String> jList_keys = new JList<String>();
		jList_keys.setBounds(396, 65, 73, 248);
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
		frame.setVisible(true);

	}

}
