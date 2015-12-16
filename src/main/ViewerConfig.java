package main;

import java.awt.TextField;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;

public class ViewerConfig extends PApplet {
	public static final int PLANE = 0;
	public static final int LOW = 1;
	public static final int HIGH = 2;

	private int detail; // true is high detail
	private int threshold2L;
	private int thresholdLH;
	private int threshold2L_2D;
	private int thresholdLH_2D;
	private int threshold2L_3D;
	private int thresholdLH_3D;

	private TextField th2LField;
	private TextField thLHField;

	public int cameraV;
	private TextField cameraVField;

	public String host;
	private TextField hostField;
	public int port;
	private TextField portField;

	private Map<String, Button> buttons;

	private double roll;
	private double yaw;

	private boolean drawStop;

	public ViewerConfig() {
		detail = LOW;
		threshold2L = 10000;
		thresholdLH = 1000;
		threshold2L_2D = 10;
		thresholdLH_2D = 1;
		threshold2L_3D = threshold2L;
		thresholdLH_3D = thresholdLH;
		th2LField = new TextField("10000");
		th2LField.setBounds(10, 30, 50, 25);
		thLHField = new TextField("1000");
		thLHField.setBounds(10, 75, 50, 25);

		cameraV = 8;
		cameraVField = new TextField("" + cameraV);
		cameraVField.setBounds(10, 120, 50, 25);

		buttons = new HashMap<String, Button>();

		buttons.put("FireBrigade", new Button("Fire\nBrigade", 0, 180, 75, 40, true));
		buttons.put("PoliceForce", new Button("Police\nForce", 75, 180, 75, 40, true));
		buttons.put("AmbulanceTeam", new Button("Ambulance\nTeam", 150, 180, 75, 40, true));
		buttons.put("Civilian", new Button("\nCivilian", 225, 180, 75, 40, true));

		buttons.put("Building", new Button("\nBuilding", 0, 220, 75, 40, true));
		buttons.put("Road", new Button("\nRoad", 75, 220, 75, 40, true));
		buttons.put("Blockade", new Button("\nBlockade", 150, 220, 75, 40, true));

		buttons.put("WalterTank", new Button("\nWalterTank", 0, 260, 75, 40, true));
		buttons.put("HP", new Button("\nHP", 75, 260, 75, 40, true));
		buttons.put("AgentEffect", new Button("\nAgentEffect", 150, 260, 75, 40, true));

		buttons.put("Marker", new Button("\nMarker", 0, 300, 75, 40, true));
		buttons.put("GridLine", new Button("\nGridLine", 75, 300, 75, 40, false));
		buttons.put("Icon", new Button("\nIcon", 150, 300, 75, 40, true));

		buttons.put("apply", new Button("apply", 80, 465, 60, 20, true));
		buttons.put("clear", new Button("clear", 160, 465, 60, 20, true));

		host = "localhost";
		port = 7000;

		hostField = new TextField(host);
		hostField.setBounds(50, 370, 100, 25);
		portField = new TextField("" + port);
		portField.setBounds(200, 370, 50, 25);

		roll = 0;
		yaw = 0;

		this.setLayout(null);
		this.add(th2LField);
		this.add(thLHField);
		this.add(cameraVField);
		this.add(hostField);
		this.add(portField);
	}

	public void setup() {

	}

	public void draw() {
		background(255);
		line(0, 150, 300, 150);
		line(0, 350, 300, 350);
		line(0, 450, 300, 450);
		textSize(15);
		textAlign(LEFT, TOP);
		fill(120);
		text("Very busy how many visible entities", 0, 10);
		text("Busy how many visible entities", 0, 55);
		text("Camera Velocity", 0, 100);
		text("port", 150, 370);
		textAlign(CENTER, TOP);
		text("Visible", 25, 155);
		textSize(10);

		for (String key : buttons.keySet()) {
			Button b = buttons.get(key);
			b.draw(this);
		}

		if (!buttons.get("apply").isPushed()) {
			try {
				threshold2L = Integer.parseInt(th2LField.getText());
				threshold2L_3D = threshold2L;
			} catch (Exception e) {
				th2LField.setText("" + threshold2L);
			}
			try {
				thresholdLH = Integer.parseInt(thLHField.getText());
				thresholdLH_3D = thresholdLH;
			} catch (Exception e) {
				thLHField.setText("" + thresholdLH);
			}
			try {
				cameraV = Integer.parseInt(cameraVField.getText());
			} catch (Exception e) {
				cameraVField.setText("" + cameraV);
			}
			try {
				port = Integer.parseInt(portField.getText());
			} catch (Exception e) {
				portField.setText("" + port);
			}
			host = hostField.getText();
			buttons.get("apply").setFlag(true);
		} else if (!buttons.get("clear").isPushed()) {
			buttons.get("clear").setFlag(true);
		}
	}

	public void mousePressed() {
		for (String key : buttons.keySet()) {
			Button b = buttons.get(key);
			b.push();
		}
	}

	public boolean checkMousePos(int x, int y, int width, int height) {
		if ((this.mouseX >= x && x + width >= this.mouseX) && (this.mouseY >= y && y + height >= this.mouseY))
			return true;
		return false;
	}

	public int getDetail() {
		return detail;
	}

	public boolean getFlag(String label) {
		Button b = buttons.get(label);
		if (b == null)
			return false;
		return b.isPushed();
	}

	public void checkDetail(int n) {
		if (n >= threshold2L)
			detail = PLANE;
		else if (n >= thresholdLH)
			detail = LOW;
		else
			detail = HIGH;
	}

	public double getRoll() {
		return roll;
	}

	public void setRoll(double r) {
		roll = r;
	}

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double y) {
		yaw = y;
	}

	private class Button {
		private String label;
		private int x;
		private int y;
		private int width;
		private int height;

		private boolean on;

		public Button(String l, int x, int y, int width, int height, boolean b) {
			this.label = l;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;

			this.on = b;
		}

		public void draw(PApplet p) {
			p.pushStyle();
			p.stroke(100);

			if (on)
				p.fill(180);
			else
				p.fill(20);
			p.rect(x, y, this.width, this.height);

			if (on)
				p.fill(50);
			else
				p.fill(200);
			p.text(label, x + width / 2, y);

			p.popStyle();
		}

		public void push() {
			if (checkMousePos(this.x, this.y, this.width, this.height))
				on = !on;
		}

		public boolean isPushed() {
			return on;
		}

		public void setFlag(boolean b) {
			on = b;
		}
	}

	public void viewer2D() {
		detail = LOW;
		threshold2L = threshold2L_2D;
		thresholdLH = thresholdLH_2D;
	}

	public void viewer3D() {
		threshold2L = threshold2L_3D;
		thresholdLH = thresholdLH_3D;
	}
}