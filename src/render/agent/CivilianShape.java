package render.agent;

import java.awt.Color;

import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import render.action.AgentAction;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.worldmodel.Entity;

public class CivilianShape extends HumanShape {
	private boolean isRoadOrBuild;// Road is true, build is false
	private float red, green, blue;
	private float messageHeight;

	public CivilianShape(Entity entity, ScreenTransform transform, int scale) {
		super(entity, transform, scale);
	}

	public void drawShape(int count, int animationRate, PApplet applet, ViewerConfig config) {
		if (!config.getFlag("Civilian"))
			return;

		if (this.isRoadOrBuild)
			messageHeight = scale + 50;// Road
		else
			messageHeight = super.markHeight + 50; // Build

		applet.ambient((int) super.HP / 100, (int) ((super.HP / 100) * (2.55)), (int) super.HP / 100);
		applet.fill(255);
		applet.stroke(200);

		if (config.getFlag("AgentEffect") && sayMessage != null) {
			AgentAction.drawCivilianAction(applet, moveX, moveY, messageHeight, sayMessage, config);
		}

		super.drawShape(count, animationRate, applet, config);
	}

	public int update(Entity entity, ScreenTransform transform) {
		return super.update(entity, transform);
	}

	public void setPosition(String position) {
		if ("urn:rescuecore2.standard:entity:road".equals(position))
			this.isRoadOrBuild = true;
		else if ("urn:rescuecore2.standard:entity:building".equals(position))
			this.isRoadOrBuild = false;
	}

	public void setRefuge() {
		super.posRefuge = true;
	}
}