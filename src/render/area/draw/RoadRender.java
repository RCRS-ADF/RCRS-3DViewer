package render.area.draw;

import processing.core.PApplet;

public class RoadRender {
	public RoadRender() {

	}

	public void drawRoad(PApplet applet, int areaName, int REFUGE, float[] nords) {
		try {
			applet.pushStyle();
			
			if (areaName == REFUGE) {
				applet.ambient(125, 125, 125);
				applet.stroke(100, 255, 100);
			}/* else {
				applet.ambient(100, 100, 100);
				applet.stroke(150);
			}*/
			
			applet.pushMatrix();
			applet.beginShape();
			for (int i = 0; i < nords.length; i += 2) {
				applet.vertex(nords[i], nords[i + 1]);
			}
			applet.endShape();
			applet.popMatrix();
			applet.popStyle();
		} catch(Exception e) {
			for (int i = 0; i < nords.length; i += 2) {
				applet.vertex(nords[i], nords[i + 1]);
				System.out.println(nords[i]);
				System.out.println(nords[i+1]);
			}
		}
	}
}