package render.information.frame;

import processing.core.PApplet;

public abstract class LocalApplet extends PApplet {
	private PApplet app_parent;
	protected int x;
	protected int y;

	public LocalApplet(PApplet app_parent) {
		this.app_parent = app_parent;
		this.app_parent.setLayout(null);
		this.app_parent.add(this);
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void start() {
		try {
			this.addNotify();
		} catch (Exception e) {

		}
	}

	public void stop() {
		try {
			// this.destroy();
			// this.remove(this);
			this.removeNotify();
		} catch (Exception e) {

		}
	}

	abstract public void setup();

	abstract public void draw();
}