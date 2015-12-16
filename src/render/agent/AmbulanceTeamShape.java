package render.agent;

import loader.OBJLoader;
import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PImage;
import render.action.AgentAction;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.worldmodel.Entity;

public class AmbulanceTeamShape extends HumanShape {
	PImage ambulanceAction;
	boolean action;
	OBJLoader ambulanceteam;

	public AmbulanceTeamShape(Entity entity, ScreenTransform transform, int scale, PImage actionImage, PImage[] images,
			OBJLoader ambulanceteam) {
		super(entity, transform, scale, images, ambulanceteam);
		this.ambulanceAction = actionImage;
		this.ambulanceteam = ambulanceteam;
		this.action = false;
	}

	public void drawShape(int count, int animationRate, PApplet applet, ViewerConfig config) {
		if (!config.getFlag("AmbulanceTeam"))
			return;

		if (this.action) {
			AgentAction.drawAmbulanceTeamAction(applet, super.moveX, super.moveY, super.markHeight,
					this.ambulanceAction, config);

		}

		applet.ambient(255, 255, 255);
		// applet.fill(255);
		// applet.stroke(100);
		// super.drawShape(count, animationRate, applet, config);
		super.drawShape(count, animationRate, applet, config);
	}

	public void setCarry() {
		this.action = true;
	}

	public void setnoCarry() {
		this.action = false;
	}
}