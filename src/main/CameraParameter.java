package main;

import processing.core.PApplet;
import java.awt.Rectangle;

class CameraParameter {
	private int scale;

	private float centerX;
	private float centerY;

	private double r;

	private double roll;
	private double yaw;

	private int sightX;
	private int sightY;
	private int sightWidth;
	private int sightHeight;
	private boolean viewpoint;

	public CameraParameter(int scale) {
		this.scale = scale;

		centerX = (float) (scale / 2.0);
		centerY = (float) (scale / 2.0);

		r = scale;

		roll = 0;
		yaw = 0.00;

		sightX = 0;
		sightY = 0;
		sightWidth = scale;
		sightHeight = scale;

		viewpoint = true;
	}

	public void camera(PApplet applet) {
		double[] vp = calcCameraPos();
		calcSight();
		// applet.camera(centerX,centerY+0.00043, 4000, centerX, centerY, 0, 0,
		// 0, -1);
		if (viewpoint) {
			applet.camera((float) vp[0] + centerX, (float) ((float) vp[1] + centerY + 0.00043), (float) vp[2], centerX,
					centerY, 0, 0, 0, -1);
		} else {
			applet.camera(centerX, (float) (centerY + 0.00043), 20, (float) vp[0] + centerX, (float) vp[1] + centerY,
					(float) vp[2], 0, 0, -1);
		}
	}

	private double cameraAngle(double[] vp) {
		double x = vp[0];
		double y = vp[1] + 0.00043;
		double atan = Math.atan2((double) (y), (double) (x));
		return atan;
	}

	public Rectangle getSight() {
		return new Rectangle(sightX, sightY, sightWidth, sightHeight);
	}

	public boolean moveRelativeX(int v) {
		double[] vp = calcCameraPos();
		double rad = cameraAngle(vp);
		float x = centerX + (float) (v * Math.cos(rad));
		float y = centerY + (float) (v * Math.sin(rad));
		setCenterX(x);
		setCenterY(y);
		return true;
	}

	public boolean moveRelativeY(int v) {
		double[] vp = calcCameraPos();
		double rad = cameraAngle(vp);
		float x = centerX + (float) (v * Math.sin(rad));
		float y = centerY - (float) (v * Math.cos(rad));
		setCenterX(x);
		setCenterY(y);
		return true;
	}

	public boolean moveX(int v) {
		float x = centerX + v;
		return setCenterX(x);
	}

	public boolean moveY(int v) {
		float y = centerY + v;
		return setCenterY(y);
	}

	public boolean zoom(float v) {
		setR(r + v);
		return true;
	}

	public boolean rotate(float v) {
		setRoll(roll + v);
		return true;
	}

	public boolean angled(float v) {
		setYaw(yaw + v);
		return true;
	}

	public boolean setR(double r) {
		if (r < 0 || r > scale)
			return false;
		this.r = r;
		return true;
	}

	public boolean setCenterX(float cx) {
		centerX = cx;
		return true;
	}

	public boolean setCenterY(float cy) {
		centerY = cy;
		return true;
	}

	public boolean setRoll(double r) {
		if (yaw <= 0)
			return false;

		roll = r;

		if (roll < 0)
			roll += Math.PI * 2;
		else if (roll > Math.PI * 2)
			roll -= Math.PI * 2;

		return true;
	}

	public boolean setYaw(double y) {
		yaw = y;

		if (y < 0)
			yaw = 0;
		else if (y > Math.PI / 2)
			yaw = Math.PI / 2;

		return true;
	}

	public double getRoll() {
		return this.roll;
	}

	public double getYaw() {
		return this.yaw;
	}

	public void isChangeView() {
		viewpoint = !viewpoint;
	}

	private double[] calcCameraPos() {
		double[] result = new double[3];

		double sinR = Math.sin(roll);
		double cosR = Math.cos(roll);
		double sinY = Math.sin(yaw);
		double cosY = Math.cos(yaw);

		result[0] = (sinR * sinY) * r;
		result[1] = (cosR * sinY) * r;
		result[2] = cosY * r;

		return result;
	}

	private double[] calcRotation(double x, double y) {
		double[] result = new double[3];
		double sinR = Math.sin(roll);
		double cosR = Math.cos(roll);

		result[0] = cosR * x + sinR * y;
		result[1] = sinR * x + cosR * y;
		result[2] = 0;

		return result;
	}

	private void calcSight() {
		int align = 400;

		//double[] vp = calcCameraPos();
		double dir = Math.atan2(1, 2);
		//double dirX = Math.atan2(vp[0], vp[2]);
		//double dirY = Math.atan2(vp[1], vp[2]);
		double normX1, normX2;
		double normY1, normY2;
		/*
		 * if(dirX+dir < Math.PI/2){ double l = r * Math.tan(dirX+dir) + r *
		 * Math.tan(Math.PI-dirX+dir); normX1 = (l/2+vp[0]); normX2 = normX1-l;
		 * }else{ normX1 = r*Math.tan(dir)*-1; normX2 = r*Math.tan(dir); }
		 * if(dirY+dir < Math.PI/2){ double l = r * Math.tan(dirY+dir) + r *
		 * Math.tan(Math.PI-dirY+dir); normY1 = (l/2+vp[1]); normY2 = normY1-l;
		 * }else{ normY1 = r*Math.tan(dir)*-1; normY2 = r*Math.tan(dir); }
		 * normX1 *= 0.1;
		 * 
		 * double[] pos1 = calcRotation(normX1,normY1); double[] pos2 =
		 * calcRotation(normX2,normY1); double[] pos3 =
		 * calcRotation(normX1,normY2); double[] pos4 =
		 * calcRotation(normX2,normY2);
		 * 
		 * double[] vx = compare(pos1[0], pos2[0], pos3[0], pos4[0]); double[]
		 * vy = compare(pos1[1], pos2[1], pos3[1], pos4[1]);
		 * 
		 * sightX = (int)(vx[1]+centerX)-align; sightY = (int)(vy[1]+centerY);
		 * sightWidth = (int)(vx[0]+centerX)+align; sightHeight =
		 * (int)(vy[0]+centerY)+align;
		 */
		double tempX = 0;
		double tempY1 = 0;
		double tempY2 = 0;

		// double temp = abs(abs(abs(roll-Math.PI) - Math.PI/2) - Math.PI/4) /
		// Math.PI/4;
		if (roll <= Math.PI * 1 / 4) {
			tempX = roll;
		} else if (roll <= Math.PI * 2 / 4) {
			tempX = Math.PI * 1 / 4 - (roll - Math.PI * 1 / 4);
		} else if (roll <= Math.PI * 3 / 4) {
			tempX = roll - Math.PI * 2 / 4;
		} else if (roll <= Math.PI * 4 / 4) {
			tempX = Math.PI * 1 / 4 - (roll - Math.PI * 3 / 4);
		} else if (roll <= Math.PI * 5 / 4) {
			tempX = roll - Math.PI * 4 / 4;
		} else if (roll <= Math.PI * 6 / 4) {
			tempX = Math.PI * 1 / 4 - (roll - Math.PI * 5 / 4);
		} else if (roll <= Math.PI * 7 / 4) {
			tempX = roll - Math.PI * 6 / 4;
		} else if (roll <= Math.PI * 8 / 4) {
			tempX = Math.PI * 1 / 4 - (roll - Math.PI * 7 / 4);
		}
		tempX = 1 - (tempX / (Math.PI / 4));

		tempY1 = yaw / (Math.PI / 2);
		tempY1 *= tempY1;
		tempY2 = tempY1;
		if (yaw > Math.PI / 4) {
			tempY1 -= (tempY1 - 0.25) * 1.5;
		}

		normX1 = r * Math.tan(dir) * -1 * (1.0 + tempX * 0.5);
		normX2 = r * Math.tan(dir) * (1.0 + tempX * 0.5);
		normY1 = r * Math.tan(dir) * -1 * (1.0 + tempY1 * 1.5);
		normY2 = r * Math.tan(dir) * (1.2 + tempY2 * 0.5);

		if (r < 1000) {
			normX1 *= 2 - r / 1000;
			normX2 *= 2 - r / 1000;
			normY1 *= 2 - r / 1000;
			normY2 *= 2 - r / 1000;
		}

		double[] pos1 = calcRotation(normX1, normY1);
		double[] pos2 = calcRotation(normX2, normY1);
		double[] pos3 = calcRotation(normX1, normY2);
		double[] pos4 = calcRotation(normX2, normY2);

		double[] vx = compare(pos1[0], pos2[0], pos3[0], pos4[0]);
		double[] vy = compare(pos1[1], pos2[1], pos3[1], pos4[1]);

		sightX = (int) (vx[1] + centerX) - align;
		sightY = (int) (vy[1] + centerY);
		sightWidth = (int) (vx[0] + centerX) + align;
		sightHeight = (int) (vy[0] + centerY) + align;

		// sightX = (int)(centerX-r);
		// sightY = (int)(centerY-r);
		// sightWidth = (int)(centerX+r);
		// sightHeight = (int)(centerY+r);
	}


	private double[] compare(double a, double b, double c, double d) {
		double[] result = new double[2];

		if (a >= b) {
			if (a >= c) {
				if (a >= d) {
					result[0] = a;
				} else {
					result[0] = d;
				}
			} else {
				if (c >= d) {
					result[0] = c;
				} else {
					result[0] = d;
				}
			}
		} else {
			if (b >= c) {
				if (b >= d) {
					result[0] = b;
				} else {
					result[0] = d;
				}
			} else {
				if (c >= d) {
					result[0] = c;
				} else {
					result[0] = d;
				}
			}
		}

		if (a <= b) {
			if (a <= c) {
				if (a <= d) {
					result[1] = a;
				} else {
					result[1] = d;
				}
			} else {
				if (c <= d) {
					result[1] = c;
				} else {
					result[1] = d;
				}
			}
		} else {
			if (b <= c) {
				if (b <= d) {
					result[1] = b;
				} else {
					result[1] = d;
				}
			} else {
				if (c <= d) {
					result[1] = c;
				} else {
					result[1] = d;
				}
			}
		}
		return result;
	}
}