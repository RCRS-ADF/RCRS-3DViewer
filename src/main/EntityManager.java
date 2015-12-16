package main;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loader.ImageLoader;
import loader.OBJLoader;
import processing.core.PApplet;
import render.agent.AmbulanceTeamShape;
import render.agent.CivilianShape;
import render.agent.EntityShape;
import render.agent.FireBrigadeShape;
import render.agent.HumanShape;
import render.agent.PoliceForceShape;
import render.area.AreaShape;
import render.area.BlockadeShape;
import render.area.BuildingShape;
import render.area.RoadShape;
import rescuecore2.Timestep;
import rescuecore2.config.Config;
import rescuecore2.log.FileLogReader;
import rescuecore2.messages.Command;
import rescuecore2.misc.gui.ScreenTransform;
import rescuecore2.score.ScoreFunction;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityConstants.Fieryness;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.standard.messages.AKClear;
import rescuecore2.standard.messages.AKExtinguish;
import rescuecore2.standard.messages.AKLoad;
import rescuecore2.standard.messages.AKSpeak;
import rescuecore2.standard.messages.AKUnload;
import rescuecore2.standard.score.LegacyScoreFunction;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;

public abstract class EntityManager extends Thread {
	protected EntityShape[] shapes; // ビルやエージェントなどの描画用クラスがここに格納されている
	protected List<EntityShape> shapeList; // temporary list for shapes
	protected HashMap<Integer, Integer> indexIDMap;

	protected Map<EntityID, List<EntityID>> cBlockadeList;

	protected StandardWorldModel world; // ワールドモデルビルや道などの情報が管理されている
	protected StandardWorldModel startWorld;
	protected ChangeSet[] changes; // ワールドモデルの更新情報が管理されている
	protected ScreenTransform transform; // シミュレーション座標を実座標に変換するクラス
	protected Command[][] commands; // [time][id]

	protected int scale; // オブジェクトのスケール 値が大きいと描画が細かくなる
	protected int startTime; // シミュレーション開始時間
	protected int endTime; // シミュレーション終了時間 開始時間と終了時間は実行中のシミュレーションに接続した場合を想定している
	protected int time; // 現在の時刻
	protected double score;
	protected double maxScore;

	protected boolean initialized; // 初期化が完了したか
	protected boolean updated; // 次のステップへ進んだか
	protected boolean simulationEnded;
	private boolean loop;

	protected Config config;

	protected ScoreFunction scoreFunction;
	protected InformationManager information;

	protected PApplet applet;

	protected FileLogReader managerlog;

	protected ImageLoader image;
	protected OBJLoader blockades;
	protected OBJLoader firebrigades;
	protected OBJLoader policeforces;
	protected OBJLoader ambulanceteams;

	private ArrayList<Integer> burnings;

	private List<EntityID> refugeIDList;
	private Map<EntityID, Float> bHeightListMap;
	private ArrayList<EntityID> bAreaIDList;

	private int fire_count = 0, burnt_out = 0;
	private int livePopulation = 0, deadPopulation = 0;
	private int heatingBuilding = 0, burntoutBuilding = 0, extinguishBuilding = 0;
	private int refugepopulation = 0;
	private int blockadeCount = 0;

	protected ArrayList<EntityShape> blockadeShapeList;

	private boolean onMarkSet;

	public EntityManager(int scale, Config config, PApplet applet, InformationManager info) {
		this.applet = applet;

		this.shapes = null;
		this.shapeList = new ArrayList<EntityShape>();
		this.world = null;
		this.startWorld = null;

		this.cBlockadeList = new HashMap<EntityID, List<EntityID>>();

		this.transform = null;

		this.scale = scale;
		this.startTime = 0;
		this.endTime = 0;
		this.time = 0;
		this.score = 0;
		this.maxScore = 0;

		this.initialized = false;
		this.updated = false;
		this.simulationEnded = false;
		this.loop = false;

		this.config = config;
		this.information = info;

		this.refugeIDList = null;
		this.bHeightListMap = new HashMap<EntityID, Float>();

		this.managerlog = null;

		this.image = null;

		this.burnings = null;
		this.onMarkSet = true;// set marker set height flag

		this.blockadeShapeList = null;
	}

	protected void createScreenTransform(StandardWorldModel world) // シミュレーション座標を描画用座標に変換するクラスの作成
	{
		Rectangle2D b = world.getBounds();
		ScreenTransform t = new ScreenTransform(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY());
		t.rescale(scale, scale);
		this.transform = t;
	}

	public void drawShapes(int count, int animationRate, ViewerConfig viewerConfig, CameraParameter camera) {
		if (shapes == null)
			return;

		Rectangle sight = camera.getSight();
		int x = (int) transform.screenToX(sight.x);
		int y = (int) transform.screenToY(sight.y);
		int sWidth = (int) transform.screenToX(sight.width);
		int sHeight = (int) transform.screenToY(sight.height);
		Collection<StandardEntity> es = null;
		try {
			es = world.getObjectsInRectangle(x, y, sWidth, sHeight);
		} catch (NullPointerException npe) {
			// npe.printStackTrace();
		}
		burnings.clear();
		blockadeShapeList.clear();
		bAreaIDList.clear();

		if (es != null && shapes != null) {
			int numEntity = es.size();

			viewerConfig.checkDetail(numEntity);
			viewerConfig.setRoll(camera.getRoll());
			viewerConfig.setYaw(camera.getYaw());
			for (StandardEntity entity : es) {
				if (entity == null)
					break;
				int id = entity.getID().getValue();
				Integer index = indexIDMap.get(id);
				if (index != null) {
					if (index < shapes.length) {
						if (shapes[index] != null) {
							if (shapes[index] instanceof BuildingShape) {
								burnings.add(index);
								// ---------------------------------------------------------
								// 20141112tuika
							} else if (shapes[index] instanceof HumanShape) {
								if (this.onMarkSet) {
									float h = 0;
									for (float f : bHeightListMap.values()) {
										if (f > h)
											h = f;
									}
									((HumanShape) shapes[index]).setMarkHeight(h);
								}
							}
							// 3DViewer Draw Shape!!
							shapes[index].drawShape(count, animationRate, applet, viewerConfig);
							// ---------------------------------------------------------
							if (shapes[index] instanceof AreaShape) {
								List<EntityID> bs = ((AreaShape) shapes[index]).getBlockades();
								if (bs != null) {
									boolean k = true;
									for (EntityID temp : bs) {
										if (temp == null)
											break;
										Integer bi = indexIDMap.get(temp.getValue());
										EntityShape eShape = null;
										if (bi != null) {
											if (bi < shapes.length) {
												eShape = shapes[bi.intValue()];
											} else {
												if (shapeList != null) {
													if (shapeList.size() > bi - shapes.length)
														eShape = shapeList.get(bi - shapes.length);
												}
											}
										}
										// draw blockade
										if (eShape != null && k) {
											k = false;
											blockadeShapeList.add(eShape);
										}
									}
								}
							}
						}
					}
				}
			}
			this.onMarkSet = false;
			for (int i = 0; i < burnings.size(); i++) {
				Integer index = burnings.get(i);
				((BuildingShape) shapes[index]).drawShape(applet, viewerConfig);
			}
			for (int i = 0; i < blockadeShapeList.size(); i++) {
				EntityShape bShape = blockadeShapeList.get(i);
				bShape.drawShape(count, animationRate, applet, viewerConfig);
			}
		}
	}

	public int getEndTime() {
		return endTime;
	}

	public double getScore() {
		return score;
	}

	public EntityShape[] getShapes() {
		return shapes;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getTime() {
		return time;
	}

	private void information_initialize() {
		fire_count = 0;
		burnt_out = 0;
		livePopulation = 0;
		deadPopulation = 0;
		heatingBuilding = 0;
		blockadeCount = 0;

		extinguishBuilding = 0;
		refugepopulation = 0;
	}

	protected void initialize() // 表示用クラスの準備
	{
		if (world == null || transform == null || initialized)
			return;

		int count = 0;
		int population = 0;
		int size = world.getAllEntities().size();
		indexIDMap = new HashMap<Integer, Integer>(size * 4 / 3 + 1);
		shapes = new EntityShape[size];
		refugeIDList = new ArrayList<EntityID>();
		burnings = new ArrayList<Integer>(size);
		blockadeShapeList = new ArrayList<EntityShape>(size);

		bAreaIDList = new ArrayList<EntityID>();

		Rectangle2D wb = world.getBounds();
		double worldSize = wb.getWidth() * wb.getHeight(); // ビルの高さ計算用
		bHeightListMap.clear();
		int ink = 0;

		if (image == null) {
			image = new ImageLoader(this.applet);
		}

		if (blockades == null) {
			blockades = new OBJLoader(this.applet, "blockade.obj");
			firebrigades = new OBJLoader(this.applet, "firebrigade.obj");
			policeforces = new OBJLoader(this.applet, "policeforce.obj");
			ambulanceteams = new OBJLoader(this.applet, "ambulance.obj");
		}

		for (Entity entity : world.getAllEntities()) {
			indexIDMap.put(entity.getID().getValue(), count);
			if (entity instanceof Road) {
				shapes[count] = new RoadShape(entity, transform, this.scale, image.getRoadImage(), image.getIcons());
				ink++;
			} else if (entity instanceof Building) {
				// Calculation of the size of the building
				float h = scale / 50;
				Rectangle2D bounds = ((Building) entity).getShape().getBounds2D();
				double bSize = bounds.getWidth() * bounds.getHeight();
				float bScale = (float) bSize / (float) worldSize;
				if (bScale >= 0.4)
					bScale = 0.4f;
				h = h * applet.random(0.2f + bScale, 0.6f + bScale) * 2;
				float bHeight;// building height
				if (((Building) entity).getTotalArea() > 1000) {
					bHeight = h;
				} else if (((Building) entity).getTotalArea() > 400 && ((Building) entity).getTotalArea() <= 1000) {
					bHeight = 2.0f * h;
				} else {
					bHeight = 1.5f * h;
				}
				bHeightListMap.put(((Building) entity).getID(), bHeight);
				/*
				 * EntityID id = ((Building)entity).getID();
				 * if(!bHeightListMap.containsKey(id)){ for(EntityID
				 * neighboursID : ((Area)entity).getNeighbours()){
				 * if(!"urn:rescuecore2.standard:entity:road"
				 * .equals(world.getEntity(neighboursID).getURN())){
				 * if(bHeightListMap.containsKey(neighboursID)){ bHeight =
				 * bHeightListMap.get(neighboursID);
				 * 
				 * } else { bHeightListMap.put(neighboursID,bHeight);
				 * neighboursBuilding(id,neighboursID,bHeight); } } } } else {
				 * bHeight = bHeightListMap.get(id); }
				 */

				shapes[count] = new BuildingShape(entity, transform, bHeight, this.scale, image.getBuildingImage(),
						image.getFire(), image.getSmoke(), image.getIcons());
			} else if (entity instanceof Blockade) {
				shapes[count] = new BlockadeShape(entity, transform, this.scale, blockades);
			} else if (entity instanceof FireBrigade) {
				shapes[count] = new FireBrigadeShape(entity, transform, scale, image.getFirebrigadeImage(),
						firebrigades);
			} else if (entity instanceof PoliceForce) {
				shapes[count] = new PoliceForceShape(entity, transform, scale, image.getPoliceActionImage(),
						image.getPoliceImage(), policeforces);
			} else if (entity instanceof AmbulanceTeam) {
				shapes[count] = new AmbulanceTeamShape(entity, transform, scale, image.getActionAmbulanceImage(),
						image.getAmbulanceImage(), ambulanceteams);
			} else if (entity instanceof Civilian) {
				shapes[count] = new CivilianShape(entity, transform, scale);
				population++;
			}
			if (entity instanceof Area) {
				Area area = (Area) entity;
				switch (area.getStandardURN()) {
				case REFUGE:
					refugeIDList.add(area.getID());
					break;
				default:
					break;
				}
			}
			// if(shapes[count] instanceof BuildingShape) println(1);
			count++;
		}
		this.scoreFunction = new LegacyScoreFunction();
		this.scoreFunction.initialise(world, this.config);
		if (loop == false) {
			this.information.init(endTime, startTime, population);
		}

		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	private boolean isRefuge(EntityID civPosition) {
		for (int i = 0; i < refugeIDList.size(); i++) {
			if (refugeIDList.get(i).getValue() == civPosition.getValue()) {
				return true;
			}
		}
		return false;
	}

	public void play() {
		setTime(this.time + 1);
	}

	public void back() {
		setTime(this.time - 1);
	}

	public void run() // シミュレーション情報の更新
	{

		try {
			while (world == null || transform == null)
				Thread.sleep(1); // 情報読み込み待ち

			this.initialize(); // 初期化
			while (!initialized)
				Thread.sleep(1); // 初期化待ち

			while (true) {
				Thread.sleep(1);
				if (this.updated) {
					if (this.changes[time] != null) {
						try {
							if (managerlog != null) {
								startTime = 1;
							}
							if (commands[time] != null) {
								for (int i = 0; i < commands[time].length; ++i) {
									Integer index = indexIDMap.get(commands[time][i].getAgentID().getValue());
									if (commands[time][i] instanceof AKClear) {
										AKClear message = (AKClear) commands[time][i];
										Blockade b = (Blockade) world.getEntity(message.getTarget());
										if (shapes[index] instanceof HumanShape) {
											try {
												((HumanShape) shapes[index]).setActionTarget(
														transform.xToScreen(b.getX()), transform.yToScreen(b.getY()));
												;
											} catch (Exception e)// (NullPointerException
																	// e)
											{
												// e.printStackTrace();
											}
										}
									}
								}
							}
							this.world.merge(this.changes[time]);
							this.score = this.scoreFunction.score(world, new Timestep(time));
							this.information.nextTime(time);
							this.information.setScore(time, this.score);
							information_initialize();
							for (Entity entity : world.getAllEntities()) {
								EntityID id = entity.getID();
								Integer index = indexIDMap.get(id.getValue());
								if (index != null) {
									if (index < shapes.length) {
										shapes[index.intValue()].update(world.getEntity(id), transform);
									} else {
										shapeList.get(index - shapes.length).update(world.getEntity(id), transform);
										EntityShape eShape = shapeList.get(index - shapes.length);
										if (eShape instanceof BlockadeShape) {
											blockadeCount += ((BlockadeShape) eShape).getRepairCost();
										}
									}
								} else {
									// ---------blockade-----------//
									BlockadeShape bs = new BlockadeShape(world.getEntity(id), transform, this.scale,
											this.blockades);
									indexIDMap.put(id.getValue(), shapes.length + shapeList.size());
									blockadeCount += bs.getRepairCost();
									shapeList.add(bs);

								}
								if (managerlog == null && loop == false) {
									if ((cBlockadeList.get(id)) != null) {
										if (shapes[index.intValue()] instanceof AreaShape) {
											((AreaShape) shapes[index.intValue()]).setBlockade(cBlockadeList.get(id));
										}
									}
								}
								if (entity instanceof Building) {
									Building b = (Building) entity;
									Fieryness f = b.getFierynessEnum();
									switch (f) {
									case BURNING:
										// On fire a bit more.
										heatingBuilding++;
										fire_count++;
										break;
									case BURNT_OUT:
										// Completely burnt out.
										burntoutBuilding++;
										fire_count++;
										break;
									case HEATING:
										// On fire a bit.
										heatingBuilding++;
										fire_count++;
										break;
									case INFERNO:
										// On fire a lot.
										heatingBuilding++;
										fire_count++;
										break;
									case MINOR_DAMAGE:
										// Extinguished but minor damage.
										extinguishBuilding++;
										break;
									case MODERATE_DAMAGE:
										// Extinguished but moderate damage.
										extinguishBuilding++;
										break;
									case SEVERE_DAMAGE:
										// Extinguished but major damage.
										extinguishBuilding++;
										break;
									case UNBURNT:
										// Not burnt at all.
										extinguishBuilding++;
										break;
									case WATER_DAMAGE:
										// Not burnt at all, but has water
										// damage.
										extinguishBuilding++;
										break;
									}
								} else if (entity instanceof Civilian) {
									Civilian h = (Civilian) entity;
									if (h.getHP() == 0) {
										deadPopulation++;
									} else {
										livePopulation++;
									}
									EntityID civ = h.getPosition();
									if (isRefuge(civ) == true) {
										refugepopulation++;
										((HumanShape) shapes[index.intValue()]).setRefuge();
									}
								}
							}
							this.information.setBurnedBuilding(time - startTime, fire_count);
							this.information.setPopulation(time - startTime, livePopulation);
							this.information.setPopulationData(livePopulation, deadPopulation);
							this.information.setBuildingData(heatingBuilding, burntoutBuilding, extinguishBuilding);
							this.information.setRefugeData(refugepopulation);
							this.information.setBlockadeCount(time - startTime, blockadeCount);
							if (commands[time] != null) {
								for (int i = 0; i < commands[time].length; ++i) {
									Integer index = indexIDMap.get(commands[time][i].getAgentID().getValue());
									if (index != null) {
										if (commands[time][i] instanceof AKExtinguish) {
											AKExtinguish message = (AKExtinguish) commands[time][i];
											StandardEntity se = world.getEntity(message.getTarget());
											Area a = (Area) se;
											if (shapes[index] instanceof HumanShape) {
												((HumanShape) shapes[index]).setActionTarget(
														transform.xToScreen(a.getX()), transform.yToScreen(a.getY()),
														bHeightListMap.get(a.getID()));
											}
										} else if (commands[time][i] instanceof AKClear) {
											if (shapes[index] instanceof HumanShape) {
												try {
													((HumanShape) shapes[index]).setClearAction();
												} catch (Exception e)// (NullPointerException
																		// e)
												{
													PApplet.println("call");
													// e.printStackTrace();
												}
											}
										} else if (commands[time][i] instanceof AKLoad) {
											if (shapes[index] instanceof HumanShape) {
												((AmbulanceTeamShape) shapes[index]).setCarry();
											}
										} else if (commands[time][i] instanceof AKUnload) {
											if (shapes[index] instanceof HumanShape) {
												((AmbulanceTeamShape) shapes[index]).setnoCarry();
											}
										} else if (commands[time][i] instanceof AKSpeak) {
											AKSpeak message = (AKSpeak) commands[time][i];
											String m = new String(message.getContent());
											if (shapes[index] instanceof HumanShape) {
												HumanShape hs = (HumanShape) shapes[index];
												hs.setSay(m);
											}
										}
									}
								}
							}
						} catch (NullPointerException npe) {
							npe.printStackTrace();
							// time = startTime;
						}
					} else {
						time--; // サーバからまだ情報が送信されていない場合ステップを進めない
					}
					this.updated = false;
					this.onMarkSet = true;
				}
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			System.exit(-1);
		}
	}

	public void setTime(int time) {
		if (!updated && initialized) {
			if (time >= endTime) {
				if (managerlog != null) {
					try {
						// world =
						// StandardWorldModel.createStandardWorldModel(managerlog.getWorldModel(0));
						this.world.removeAllEntities();
						for (Entity e : StandardWorldModel.createStandardWorldModel(managerlog.getWorldModel(0))
								.getAllEntities()) {
							this.world.addEntity(e.copy());
						}
						initialized = false;
						this.onMarkSet = true;
						initialize();
						this.time = startTime;
					} catch (Exception e) {
						PApplet.println("selected wrong logfile.");
						e.printStackTrace();
					}
				} else {
					try {
						this.world.removeAllEntities();
						for (Entity e : this.startWorld.getAllEntities()) {
							this.world.addEntity(e.copy());
						}
						this.loop = true;
						initialized = false;
						this.onMarkSet = true;
						initialize();
						this.time = startTime;
					} catch (Exception e) {
						PApplet.println("selected wrong logfile.");
						e.printStackTrace();
					}
				}
				// if (time >= endTime) this.time = startTime - 1;
			} else if (time < startTime) {
				this.time = startTime;
			} else {
				this.time = time;
			}
			this.updated = true;
		}
	}
}