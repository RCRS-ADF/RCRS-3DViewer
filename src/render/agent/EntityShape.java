package render.agent;

import main.ViewerConfig;
import processing.core.PApplet;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.worldmodel.Entity;

public interface EntityShape {
	public void drawShape(int count, int animationRate, PApplet applet,
			ViewerConfig config);

	public int update(Entity entity, ScreenTransform transform);

	public int getID();
}