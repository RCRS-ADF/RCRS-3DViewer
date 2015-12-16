package render.information.frame;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;

public class InfomationFrame {
	private int x;
	private int y;
	private int width;
	private int height;

	private int index = 0;

	private ArrayList<String> label;
	private ArrayList<Boolean> on;
	private ArrayList<LocalApplet> applet;

	public InfomationFrame(int x, int y, int width, int height, boolean b) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private void setString(String value) {
		if (this.label != null) {
			this.label.add(value);
			this.on.add(Boolean.FALSE);
		} else {
			this.label = new ArrayList<String>();
			this.on = new ArrayList<Boolean>();
			this.applet = new ArrayList<LocalApplet>();

			this.label.add(value);
			this.on.add(Boolean.TRUE);
		}
	}

	private void setPApplet(LocalApplet applet) {
		if (applet != null) {
			applet.setBounds(applet.x + this.x, applet.y + this.height,
					applet.width, applet.height);
			applet.init();
		}
		this.applet.add(applet);
	}

	public void setValue(String value) {
		this.setString(value);
		this.setPApplet(null);
	}

	public void setValue(String value, LocalApplet applet) {
		this.setString(value);
		this.setPApplet(applet);
	}

	public void draw(PApplet p) {
		int tempH = this.width / 3;
		p.pushStyle();
		p.stroke(100);
		p.strokeWeight(5);
		try {
			for (int i = 0; i < this.label.size(); i++) {
				if (this.on.get(i))
					p.fill(180);
				else
					p.fill(20);
				p.rect(x + tempH * i, y, tempH, this.height);

				if (this.on.get(i))
					p.fill(50);
				else
					p.fill(200);
				p.textAlign(PConstants.CENTER, PConstants.CENTER);
				p.text(this.label.get(i), x + tempH / 2 + tempH * i,
						this.height / 2);

				if (i != this.index && this.applet.get(i) != null) {
					this.applet.get(i).stop();
				} else if (this.applet.get(i) != null) {
					// this.applet.get(this.index).init();
					this.applet.get(this.index).start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		p.popStyle();
	}

	public void push(int mouseX, int mouseY) {
		int tempH = this.width / 3;

		for (int i = 0; i < this.on.size(); i++) {
			if (checkMousePos(mouseX, mouseY, this.x + tempH * i, this.y,
					tempH, this.height) && !this.on.get(i)) {
				index = i;
			} else {
				this.on.set(i, Boolean.FALSE);
			}
		}
		this.on.set(this.index, Boolean.TRUE);
	}

	public boolean checkMousePos(int mouseX, int mouseY, int x, int y,
			int width, int height) {
		if ((mouseX >= x && x + width >= mouseX)
				&& (mouseY >= y && y + height >= mouseY))
			return true;
		return false;
	}
	public int getIndex(){
		return this.index;
	}
}