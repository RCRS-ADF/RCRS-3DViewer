package render.agent;

import java.util.ArrayList;

import loader.OBJLoader;
import main.InformationManager;
import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PImage;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Human;
import rescuecore2.worldmodel.Entity;

public abstract class HumanShape implements EntityShape {
	protected int id;
	protected int scale;

	protected int posX;
	protected int posY;

	protected int moveX;
	protected int moveY;

	protected int history[];
	protected float direction;

	protected int HP; // max 10000

	protected PImage[] images;
	protected OBJLoader models;

	protected boolean action;
	protected boolean carry;
	protected int targetX;
	protected int targetY;
	protected float targetZ;

	protected String sayMessage;

	protected float markHeight;
	protected boolean posRefuge;

	private boolean isCivilian;

	public HumanShape(Entity entity, ScreenTransform transform, int scale) {
		if (entity == null)
			return;
		this.id = entity.getID().getValue();
		Human human = (Human) entity;
		this.HP = human.getHP();

		this.scale = scale / 150; // magic number for calculating agent size
		try {
			this.posX = transform.xToScreen(human.getX());
			this.posY = transform.yToScreen(human.getY());
		} catch (Exception e) {
			// e.printStackTrace();
			this.posX = 0;
			this.posY = 0;
		}

		this.posRefuge = false;

		this.history = null;
		this.direction = 0;

		this.moveX = 0;
		this.moveY = 0;

		this.images = null;

		this.action = false;
		this.carry = false;
		this.targetX = 0;
		this.targetY = 0;

		this.sayMessage = null;
		if (entity instanceof Civilian) {
			isCivilian = true;
		} else {
			isCivilian = false;
		}

	}

	public HumanShape(Entity entity, ScreenTransform transform, int scale, PImage[] images, OBJLoader model) {
		this(entity, transform, scale);
		this.HP = ((Human) entity).getHP();
		this.images = images;
		this.models = model;
	}

	public void drawShape(int count, int animationRate, PApplet applet, ViewerConfig config) {
		// calculation agent animation path
		moveX = 0;
		moveY = 0;

		if (animationRate <= 0 || history == null || count >= animationRate || history.length <= 3) {
			moveX = this.posX;
			moveY = this.posY;
		} else {
			float rate = (float) animationRate / ((this.history.length / 2) - 1);
			int index = (int) ((count / rate)) * 2;

			if (this.history.length <= index + 2) {
				moveX = this.posX;
				moveY = this.posY;
			} else {
				float rateX = (this.history[index] - this.history[index + 2]) / rate;
				float rateY = (this.history[index + 1] - this.history[index + 3]) / rate;
				moveX = (int) (this.history[index] - (rateX * (count % rate)));
				moveY = (int) (this.history[index + 1] - (rateY * (count % rate)));
				float x = moveX - this.history[index];
				float y = moveY - this.history[index + 1];
				float r = applet.sqrt((x * x) + (y * y));
				float t = applet.acos(x / r);
				if (r != 0)
					this.direction = t;
			}
		}

		switch (config.getDetail()) {
		case ViewerConfig.HIGH:
			if (!isCivilian) {
				models.draw(moveX, moveY, -applet.PI / 2, this.direction + applet.PI, 0, 6);
			} else {
				drawAgent(applet, moveX, moveY, direction, scale, images);
			}
			if (config.getFlag("Marker")) {
				if (posRefuge) {
				} else {
					drawMarker(applet, moveX, moveY, this.markHeight, this.scale);
				}
			}
			break;

		case ViewerConfig.LOW:
			drawAgent(applet, moveX, moveY, direction, scale, images);
			if (config.getFlag("Marker")) {
				if (posRefuge) {
				} else {
					drawMarker(applet, moveX, moveY, this.markHeight, this.scale);
				}
			}
			break;
		default:
			drawAgent(applet, moveX, moveY, scale);
			break;
		}

		if (config.getFlag("HP")) {
			if (posRefuge) {
			} else {
				drawHPbar(applet, HP, moveX, moveY, scale, config);
			}
		}
	}

	public int update(Entity entity, ScreenTransform transform) {
		if (entity == null)
			return InformationManager.NO_CHANGE;
		;
		int result = InformationManager.NO_CHANGE;

		if (this.carry == false) {
			this.action = false;
		}
		this.sayMessage = null;

		Human h = (Human) entity;
		if (this.HP != h.getHP()) {
			this.HP = h.getHP();
			if (this.HP == 0) {
				result = InformationManager.HUMAN_DEAD;
			}
		}

		int[] humanHis = h.getPositionHistory();

		if (humanHis == null || humanHis.length <= 0) {
			try {
				this.posX = transform.xToScreen(h.getX());
				this.posY = transform.yToScreen(h.getY());
			} catch (NullPointerException npe) {
				// npe.printStackTrace();
				return InformationManager.NO_CHANGE;
			}
			this.history = null;
			return InformationManager.NO_CHANGE;
		}

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(this.posX);
		list.add(this.posY);
		int x = transform.xToScreen(humanHis[0]);
		int y = transform.yToScreen(humanHis[1]);
		if (!(x == this.posX && y == this.posY)) {
			list.add(x);
			list.add(y);
		}

		for (int i = 2; i < humanHis.length; i += 2) {
			int x1 = transform.xToScreen(humanHis[i - 2]);
			int y1 = transform.yToScreen(humanHis[i - 1]);
			int x2 = transform.xToScreen(humanHis[i]);
			int y2 = transform.yToScreen(humanHis[i + 1]);

			if (!(x1 == x2 && y1 == y2)) {
				list.add(x2);
				list.add(y2);
			}
		}
		try {
			this.posX = transform.xToScreen(h.getX());
			this.posY = transform.yToScreen(h.getY());
		} catch (NullPointerException npe) {
			// npe.printStackTrace();
			return InformationManager.NO_CHANGE;
		}

		int i = 0;
		this.history = new int[list.size()];
		for (Integer next : list) {
			this.history[i] = next;
			i++;
		}

		return result;

	}

	public int getID() {
		return id;
	}

	public void setActionTarget(int x, int y, float z) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
		this.action = true;
	}

	public void setActionTarget(int x, int y) {
		this.targetX = x;
		this.targetY = y;
	}

	public void setClearAction() {
		this.action = true;
	}

	public void setSay(String s) {
		sayMessage = s;
	}

	public void setMarkHeight(float markerHeight) {
		this.markHeight = markerHeight;
	}

	public void drawAgent(PApplet applet, int moveX, int moveY, float direction, int scale, PImage[] images)// High
	{
		int min = scale / 2 * -1;
		int max = scale / 2;
		applet.pushMatrix();
		// agent
		applet.translate(moveX, moveY, scale / 2);
		applet.rotateZ(direction);
		// applet.box(scale);
		applet.textureMode(applet.NORMAL);
		// top right left back front
		applet.beginShape(applet.QUAD);
		// top
		if (images != null)
			applet.texture(images[0]);
		applet.vertex(min, min, max, 1, 0);
		applet.vertex(min, max, max, 0, 0);
		applet.vertex(max, max, max, 0, 1);
		applet.vertex(max, min, max, 1, 1);
		applet.endShape();

		applet.beginShape(applet.QUAD);
		// right
		if (images != null)
			applet.texture(images[1]);
		applet.vertex(min, min, min, 0, 1);
		applet.vertex(min, min, max, 0, 0);
		applet.vertex(max, min, max, 1, 0);
		applet.vertex(max, min, min, 1, 1);
		applet.endShape();

		applet.beginShape(applet.QUAD);
		// left
		if (images != null)
			applet.texture(images[2]);
		applet.vertex(min, max, min, 0, 1);
		applet.vertex(min, max, max, 0, 0);
		applet.vertex(max, max, max, 1, 0);
		applet.vertex(max, max, min, 1, 1);
		applet.endShape();

		applet.beginShape(applet.QUAD);
		// back
		if (images != null)
			applet.texture(images[4]);
		applet.vertex(min, min, min, 0, 0);
		applet.vertex(min, min, max, 0, 1);
		applet.vertex(min, max, max, 1, 1);
		applet.vertex(min, max, min, 1, 0);
		applet.endShape();

		applet.beginShape(applet.QUAD);
		// front
		if (images != null)
			applet.texture(images[3]);
		applet.vertex(max, min, min, 0, 1);
		applet.vertex(max, min, max, 0, 0);
		applet.vertex(max, max, max, 1, 0);
		applet.vertex(max, max, min, 1, 1);
		applet.endShape();
		applet.popMatrix();
	}

	public void drawAgent(PApplet applet, int moveX, int moveY, int scale)// Low
	{
		applet.pushMatrix();
		// agent
		applet.translate(moveX, moveY, scale / 2);
		applet.ellipse(0, 0, scale, scale);
		applet.popMatrix();
		applet.pushMatrix();
		// agent
		applet.translate(moveX, moveY, scale / 2);
		applet.ellipse(0, 0, scale, scale);
		applet.popMatrix();
	}

	public void drawHPbar(PApplet applet, int HP, int moveX, int moveY, int scale, ViewerConfig config) {
		applet.pushStyle();
		applet.ambient(125, 255, 125);
		applet.noStroke();
		applet.pushMatrix();
		int hpBar = (int) applet.map(HP, 0, 10000, scale * -1, scale);
		applet.translate(moveX, moveY, scale + 50);
		applet.rotateZ((float) (Math.PI * 2 - config.getRoll()));
		applet.rotateX((float) (Math.PI * 2 - config.getYaw()));
		applet.beginShape();
		applet.vertex(scale * -1, scale / 4 * -1, 0);
		applet.vertex(scale * -1, scale / 4, 0);
		applet.vertex(scale, scale / 4, 0);
		applet.vertex(scale, scale / 4 * -1, 0);
		applet.endShape();
		applet.beginShape();
		applet.ambient(255, 125, 125);
		applet.vertex(hpBar, scale / 4 * -1, 0);
		applet.vertex(hpBar, scale / 4, 0);
		applet.vertex(scale, scale / 4, 0);
		applet.vertex(scale, scale / 4 * -1, 0);
		applet.endShape();
		applet.popMatrix();
		applet.popStyle();
	}

	public void drawMarker(PApplet applet, int moveX, int moveY, float markHeight, int scale) {
		applet.pushStyle();
		applet.pushMatrix();
		applet.noStroke();
		applet.translate(moveX, moveY, markHeight);
		applet.beginShape(applet.TRIANGLES);
		applet.vertex(scale * -1, scale, scale);
		applet.vertex(scale, scale, scale);
		applet.vertex(0, 0, scale * -1);
		applet.vertex(scale, scale, scale);
		applet.vertex(0, scale * -1, scale);
		applet.vertex(0, 0, scale * -1);
		applet.vertex(scale * -1, scale, scale);
		applet.vertex(0, scale * -1, scale);
		applet.vertex(0, 0, scale * -1);
		applet.endShape();
		applet.popMatrix();
		applet.popStyle();
	}

	public void setRefuge() {
		posRefuge = true;
	}

}