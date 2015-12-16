package render.area;

import java.util.List;

import loader.ImageLoader;
import main.InformationManager;
import main.ViewerConfig;
import processing.core.PApplet;
import processing.core.PImage;
import render.agent.EntityShape;
import render.area.draw.RoadRender;
import render.effect.AreaEffect;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Edge;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;

public abstract class AreaShape implements EntityShape {
	protected int id;

	protected int scale;
	protected float areaHeight;

	protected float x;
	protected float y;

	protected float[] nords;
	protected boolean[] passable;

	protected List<EntityID> blockades;

	static final int FIRE = 0;
	static final int AMBULANCE = 1;
	static final int POLICE = 2;
	static final int REFUGE = 3;
	static final int GAS = 4;
	static final int HYDRANT = 5;

	protected int areaName;

	protected PImage icon;
	protected RoadRender roadrender;

	public AreaShape(Entity entity, ScreenTransform transform, int scale,
			PImage[] icons, float areaHeight) {
		this.id = entity.getID().getValue();
		this.scale = scale;
		this.areaHeight = areaHeight;

		Area area = (Area) entity;

		this.x = transform.xToScreen(area.getX());
		this.y = transform.yToScreen(area.getY());

		int size = area.getEdges().size();
		this.nords = new float[size * 2];
		this.passable = new boolean[size];
		int i = 0;
		for (Edge edge : area.getEdges()) {
			this.passable[i / 2] = edge.isPassable();
			this.nords[i] = transform.xToScreen(edge.getStartX());
			this.nords[i + 1] = transform.yToScreen(edge.getStartY());
			i += 2;
		}

		blockades = area.getBlockades();

		this.icon = null;
		setIcon(entity, icons);
		this.roadrender = new RoadRender();
	}

	public void drawShape(int count, int animationRate, PApplet applet,
			ViewerConfig config) {
		if (nords == null) return;
		// create Road
		this.roadrender.drawRoad(applet, this.areaName, this.REFUGE, this.nords);

		if (config.getFlag("Icon") && icon != null) {
			AreaEffect.drawAreaIcon(applet, x, y, areaHeight, scale, icon,
					config);
		}
	}

	public int update(Entity entity, ScreenTransform transform) {
		Area a = (Area) entity;

		blockades = a.getBlockades();

		return InformationManager.NO_CHANGE;
	}

	public void setBlockade(List<EntityID> blockadelist) {
		blockades = blockadelist;
	}

	public List<EntityID> getBlockades() {
		return blockades;
	}

	public int getID() {
		return id;
	}

	public void setIcon(Entity entity, PImage[] icons) {
		Area area = (Area) entity;
		switch (area.getStandardURN()) {
		case REFUGE:
			this.icon = icons[ImageLoader.REFUGE];
			areaName = REFUGE;
			areaHeight = 0;
			break;
		case GAS_STATION:
			this.icon = icons[ImageLoader.GAS];
			areaName = GAS;
			break;
		case FIRE_STATION:
			this.icon = icons[ImageLoader.FIRE];
			areaName = FIRE;
			break;
		case AMBULANCE_CENTRE:
			this.icon = icons[ImageLoader.AMBULANCE];
			areaName = AMBULANCE;
			break;
		case POLICE_OFFICE:
			this.icon = icons[ImageLoader.POLICE];
			areaName = POLICE;
			break;
		case HYDRANT:
			this.icon = icons[ImageLoader.HYDRANT];
			areaName = HYDRANT;
			break;
		default:
			break;
		}
	}
}
