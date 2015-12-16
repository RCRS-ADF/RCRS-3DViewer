package main;

import java.awt.Label;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import loader.LogFileReader;
import rescuecore2.config.Config;
import rescuecore2.connection.ConnectionException;
import rescuecore2.misc.CommandLineOptions;
import rescuecore2.misc.java.LoadableTypeProcessor;
import rescuecore2.registry.Registry;
import processing.core.*;

public class RCRSViewer extends PApplet {

	private static final int PRECISION = 3;

	private EntityManager manager;

	private int rate;
	private int scale; // Objects Detail max 4000
	private int updateRate; // Many seconds do you update

	private Panel information;
	private Label timeInfo;
	private Label scoreInfo;
	private Panel controlInfo;

	private JButton b_back;
	private JButton b_play;
	private JButton b_next;

	private ImageIcon i_play;
	private ImageIcon i_back;
	private ImageIcon i_next;
	private ImageIcon i_stop;

	private CameraParameter camera;

	private int count;

	private Config config;
	private NumberFormat format;

	private ViewerConfig viewerConfig;
	private InformationManager infoManager;

	private boolean preference;
	protected Frame preferenceff, graphff;
	protected boolean preferenceFlag, graphFlag;

	private boolean stopFlag;

	public void setup() {
		this.size(500, 500, P3D);
		rate = 20;
		scale = 3000;
		updateRate = 1;
		count = 0;

		i_play = new ImageIcon(
				sketchPath + "" + File.separator + "data" + File.separator + "button" + File.separator + "play.png");
		i_back = new ImageIcon(
				sketchPath + "" + File.separator + "data" + File.separator + "button" + File.separator + "back.png");
		i_next = new ImageIcon(
				sketchPath + "" + File.separator + "data" + File.separator + "button" + File.separator + "next.png");
		i_stop = new ImageIcon(
				sketchPath + "" + File.separator + "data" + File.separator + "button" + File.separator + "stop.png");
		stopFlag = false;

		preference = true;

		camera = new CameraParameter(scale);

		viewerConfig = new ViewerConfig();
		infoManager = new InformationManager();

		frameRate(rate);

		try {
			format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(PRECISION);

			String jarsDir = "";
			String configDir = "";
			String[] lines = loadStrings("viewer3d.cfg");
			for (int i = 0; i < lines.length; ++i) {
				String[] option = lines[i].split(" ");

				if (option[0].compareTo("viewer.jarsdir") == 0)
					jarsDir = option[1];
				else if (option[0].compareTo("viewer.configdir") == 0)
					configDir = option[1];
			}
			String[] args = { "-c", configDir };
			this.config = new Config();
			CommandLineOptions.processArgs(args, this.config);
			LoadableTypeProcessor processor = new LoadableTypeProcessor(this.config);
			processor.setDirectory(jarsDir);
			processor.addFactoryRegisterCallbacks(Registry.SYSTEM_REGISTRY);
			processor.process();
		} catch (Exception e) {
			e.printStackTrace();
		}

		manager = null;
		timeInfo = new Label("Time : not started", Label.CENTER);
		scoreInfo = new Label("Score : ???", Label.CENTER);

		controlInfo = new Panel();
		controlInfo.setPreferredSize(new Dimension(150, 50));
		controlInfo.setLayout(new GridLayout(1, 3));

		b_back = new JButton(i_back);
		b_back.setContentAreaFilled(false);
		b_back.setPreferredSize(new Dimension(50, 50));
		b_back.addActionListener(new ButtonListener());
		b_back.setActionCommand("b_back");
		b_play = new JButton(i_play);
		b_play.setContentAreaFilled(false);
		b_play.setPreferredSize(new Dimension(50, 50));
		b_play.addActionListener(new ButtonListener());
		b_play.setActionCommand("b_play");
		b_next = new JButton(i_next);
		b_next.setContentAreaFilled(false);
		b_next.setPreferredSize(new Dimension(50, 50));
		b_next.addActionListener(new ButtonListener());
		b_next.setActionCommand("b_next");
		controlInfo.add(b_back);
		controlInfo.add(b_play);
		controlInfo.add(b_next);

		information = new Panel();
		information.setPreferredSize(new Dimension(500, 50));
		information.setLayout(new GridLayout(1, 3));
		information.add(controlInfo);
		information.add(timeInfo);
		information.add(scoreInfo);
		this.add(information, BorderLayout.NORTH);

		preferenceff = new Frame();
		graphff = new Frame();

		preferenceFlag = false;
		graphFlag = false;
	}

	public void draw() {
		background(200);

		if (viewerConfig.getFlag("GridLine")) {
			beginShape(LINES);
			for (int i = 0; i <= scale; i += 100) {
				if (i % 200 == 0)
					stroke(155, 100, 155);
				else
					stroke(100, 155, 155);
				vertex(i, 0, 20);
				vertex(i, 4000, 20);
			}
			for (int i = 0; i <= scale; i += 100) {
				if (i % 200 == 0)
					stroke(155, 100, 155);
				else
					stroke(100, 155, 155);
				vertex(0, i, 20);
				vertex(4000, i, 20);
			}
			endShape();
		}

		if (mousePressed) {
			float vx = map((float) mouseX - width / 2, -1 * width / 2, width / 2, -0.01f, 0.01f);
			float vy = map((float) mouseY - height / 2, -1 * height / 2, height / 2, -0.01f, 0.01f);
			camera.rotate(vx * viewerConfig.cameraV);
			camera.angled(vy * viewerConfig.cameraV);
		}

		camera.camera(this);

		// rotateZ((float)(Math.PI*2-camera.getRoll()));

		if (manager != null && manager.isInitialized()) {
			ambientLight(255, 255, 255);
			// directionalLight(255,255,255,0,0,-1);
			try {
				manager.drawShapes(count, rate * updateRate, viewerConfig, camera);
			} catch (Exception e) {
				e.printStackTrace();
			}

			timeInfo.setText("Time : " + manager.getTime());
			scoreInfo.setText("Score : " + format.format(manager.getScore()));

			if (!stopFlag) {
				count++;
				if (count >= (rate * updateRate)) {
					manager.play();
					count = 0;
				}
				b_play.setIcon(i_stop);
			} else {
				b_play.setIcon(i_play);
			}
		}
	}

	public void keyPressed() {
		if (keyCode == UP)
			camera.angled(-0.01f * viewerConfig.cameraV);// camera.moveY(-10);
		else if (keyCode == DOWN)
			camera.angled(0.01f * viewerConfig.cameraV);// camera.moveY(10);
		else if (keyCode == LEFT)
			camera.rotate(0.01f * viewerConfig.cameraV);// moveX(-10);
		else if (keyCode == RIGHT)
			camera.rotate(-0.01f * viewerConfig.cameraV);// moveX(10);

		else if (key == 'a' || key == 'A')
			camera.moveRelativeY(-10 * viewerConfig.cameraV);
		else if (key == 'd' || key == 'D')
			camera.moveRelativeY(10 * viewerConfig.cameraV);
		else if (key == 'w' || key == 'W')
			camera.moveRelativeX(-10 * viewerConfig.cameraV);
		else if (key == 's' || key == 'S')
			camera.moveRelativeX(10 * viewerConfig.cameraV);

		else if (keyCode == KeyEvent.VK_PAGE_DOWN)
			camera.zoom(-10 * viewerConfig.cameraV);
		else if (keyCode == KeyEvent.VK_PAGE_UP)
			camera.zoom(10 * viewerConfig.cameraV);
	}

	public void keyTyped(KeyEvent e) {
		char key = Character.toLowerCase(e.getKeyChar());

		if (key == 'l') {
			this.selectInput("Select a log File", "fileSelected");
		} else if (key == 'e' || key == 'q') {
			exit();
		} else if (key == 'c') {
			connect();
		} else if (key == 'm') {
			/*------------------------------------------------------------------------
			if (this.frame.getMenuBar() != null) this.frame.remove(menu.getMenu());
			else this.frame.setMenuBar(menu.getMenu());
			--------------------------------------------------------------------------*/
		} else if (key == 'p') {
			if (preference) {
				preferenceFlag = isChamgeFlag(preferenceFlag);
				showFrame(viewerConfig, preferenceff, 300, 550, preferenceFlag);
			}
		} else if (key == 'g') {
			if (manager != null && manager.isInitialized()) {
				if (preference) {
					graphFlag = isChamgeFlag(graphFlag);
					showFrame(infoManager, graphff, 500, 1000, graphFlag);
				}
			}
		} else if (key == 'v') {
			if (manager != null && manager.isInitialized()) {
				// camera.isChangeView();
			}
		} else if (key == 't') {
			if (manager != null && manager.isInitialized())
				stopFlag = !stopFlag;
		} else if (key == '2') {
			viewerConfig.viewer2D();
		} else if (key == '3') {
			viewerConfig.viewer3D();
		}
	}

	private void showFrame(PApplet applet, Frame ff, int w, int h, boolean flag) {
		if (flag) {
			// appletの初期化
			applet.init();
			// appletのサイズの設定
			applet.size(w, h);
			// 画面の表示切り替え
			applet.start();
		} else {
			applet.stop();
			applet.destroy();
		}
		// Frameの設定
		ff.setSize(w, h);
		ff.setResizable(false);
		ff.add(applet);
		ff.addWindowListener(new FrameWindowListener());
		ff.setVisible(flag);
	}

	private void connect() {
		String host = viewerConfig.host;// JOptionPane.showInputDialog(this,
										// "input");
		ViewerConnectionListener listener = new ViewerConnectionListener(scale, config, this, infoManager);
		try {
			listener.connect(host, viewerConfig.port);
			listener.start();
			this.manager = listener;
		} catch (ConnectionException ce) {
			// JOptionPane.showMessageDialog(this, "invalid host");
			listener.stop();
		} catch (IOException ie) {
			// JOptionPane.showMessageDialog(this, "unknown host");
			listener.stop();
		}
	}

	public void fileSelected(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
			LogFileReader log = new LogFileReader(scale, config, this, infoManager);
			log.readFile(selection);
			log.start();
			manager = log;
		}
	}

	public boolean isChamgeFlag(boolean flag) {
		return !flag;
		/*
		 * if(flag == true){ return false; } else{ return true; }
		 */
	}

	public MenuListener getMenuListener() {
		return new MenuListener();
	}

	class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String menu = e.getActionCommand();
			if (menu.equals("Open LogFile")) {
				selectInput("Select a log File", "fileSelected");
			} else if (menu.equals("Connect Server")) {
				connect();
			} else if (menu.equals("Quit")) {
				exit();
			} else if (menu.equals("Preference")) {
				if (preference) {
					preferenceFlag = isChamgeFlag(preferenceFlag);
					showFrame(viewerConfig, preferenceff, 300, 550, preferenceFlag);
					preference = false;
				}
			}
		}
	}

	class FrameWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			preference = true;
		}
	}

	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();

			if (cmd.equals("b_back")) {
				manager.back();
				count = 0;
			} else if (cmd.equals("b_play")) {
				if (manager != null && manager.isInitialized())
					stopFlag = !stopFlag;
			} else if (cmd.equals("b_next")) {
				manager.play();
				count = 0;
			}
		}
	}
}