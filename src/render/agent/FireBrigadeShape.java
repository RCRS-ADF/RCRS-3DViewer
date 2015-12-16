package render.agent;

import loader.OBJLoader;
import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PImage;
import render.action.AgentAction;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.worldmodel.Entity;

public class FireBrigadeShape extends HumanShape {
	OBJLoader firebrigade;

	public FireBrigadeShape(Entity entity, ScreenTransform transform, int scale, PImage[] images,
			OBJLoader firebrigade) {
		super(entity, transform, scale, images, firebrigade);
		this.firebrigade = firebrigade;
		this.action = false;
	}

	public void drawShape(int count, int animationRate, PApplet applet, ViewerConfig config) {
		if (!config.getFlag("FireBrigade"))
			return;

		if (super.action) {
			AgentAction.drawFireBrigadeAction(applet, posX, posY, targetX, targetY, targetZ, config);
		}

		// applet.ambient(255,255,255);
		applet.ambient(255, 100, 100);
		super.drawShape(count, animationRate, applet, config);
	}
}