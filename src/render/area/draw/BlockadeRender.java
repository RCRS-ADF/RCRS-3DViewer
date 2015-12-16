package render.area.draw;

import loader.OBJLoader;
import main.ViewerConfig;
import processing.core.PApplet;

public class BlockadeRender {
	public BlockadeRender() {

	}

	public void drawBlockade(PApplet applet, OBJLoader blockades, int[] apexes, int blockadeNum, float centerX,
			float centerY, float rotateY, int blockadeScale, int[] sideMaxApexes, ViewerConfig config) {
		applet.ambient(50, 50, 50);
		applet.noStroke();
		float[] top = new float[apexes.length];
		for (int i = 0; i < apexes.length; i += 2) {
			float[] x, y;
			x = this.compare(((float) apexes[i]), centerX);
			y = this.compare(((float) apexes[i + 1]), centerY);
			float dx = (x[0] - x[1]) / 2;
			float dy = (y[0] - y[1]) / 2;

			top[i] = x[0] - dx;
			top[i + 1] = y[0] - dy;
		}
		switch (config.getDetail()) {
		case ViewerConfig.HIGH:
			blockades.draw(centerX, centerY, 6, -applet.PI / 2, 0, 0, blockadeScale);
			break;
		case ViewerConfig.LOW:
		default:
			applet.pushStyle();
			applet.pushMatrix();
			applet.ambient(25, 25, 25);
			applet.beginShape();
			applet.fill(25);
			for (int i = 0; i < apexes.length - 1; i += 2) {
				applet.vertex(apexes[i], apexes[i + 1], 2);
			}
			applet.endShape();
			applet.popMatrix();
			applet.popStyle();
			break;
		}
	}

	private float[] compare(float a, float b) {
		float[] result = new float[2];
		if (a > b) {
			result[0] = a;
			result[1] = b;
		} else {
			result[0] = b;
			result[1] = a;
		}

		return result;
	}
}