package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import processing.core.PApplet;
import rescuecore2.config.Config;
import rescuecore2.connection.Connection;
import rescuecore2.connection.ConnectionException;
import rescuecore2.connection.ConnectionListener;
import rescuecore2.connection.TCPConnection;
import rescuecore2.messages.Command;
import rescuecore2.messages.Message;
import rescuecore2.messages.control.KVConnectError;
import rescuecore2.messages.control.KVConnectOK;
import rescuecore2.messages.control.KVTimestep;
import rescuecore2.messages.control.Shutdown;
import rescuecore2.messages.control.VKAcknowledge;
import rescuecore2.messages.control.VKConnect;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.Entity;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.worldmodel.properties.EntityRefListProperty;
import rescuecore2.worldmodel.properties.EntityRefProperty;
import rescuecore2.worldmodel.properties.IntArrayProperty;
import rescuecore2.worldmodel.properties.IntProperty;

class ViewerConnectionListener extends EntityManager implements
		ConnectionListener {
	private TCPConnection connection;
	private boolean started;

	// private ArrayList<EntityID> entityId = new ArrayList<EntityID>();

	public ViewerConnectionListener(int scale, Config config, PApplet applet,
			InformationManager info) {
		super(scale, config, applet, info);
		this.started = false;
	}

	public void connect(String host, int port) throws IOException,
			ConnectionException {
		connection = new TCPConnection(host, port);
		connection.addConnectionListener(this);
		connection.startup();
		connection.sendMessage(new VKConnect(1, 1, "rescue1.1"));
	}

	public void messageReceived(Connection c, Message msg) {
		if (msg instanceof KVTimestep) {
			KVTimestep t = (KVTimestep) msg;
			if (!started) {
				super.time = t.getTime();
				super.startTime = time;
				this.started = true;
			}
			super.changes[t.getTime()] = t.getChangeSet();
			if (t.getTime() == 1) {
				for (EntityID id : t.getChangeSet().getChangedEntities()) {
					if ((changes[t.getTime()].getEntityURN(id)) != null) {
						if ((changes[t.getTime()].getEntityURN(id))
								.equals("urn:rescuecore2.standard:entity:blockade")) {
							Blockade blockade = new Blockade(id);
							blockade.setApexes(((IntArrayProperty) (changes[t
									.getTime()].getChangedProperty(id,
									"urn:rescuecore2.standard:property:apexes")))
									.getValue());
							blockade.setX(((IntProperty) (changes[t.getTime()]
									.getChangedProperty(id,
											"urn:rescuecore2.standard:property:x")))
									.getValue());
							blockade.setY(((IntProperty) (changes[t.getTime()]
									.getChangedProperty(id,
											"urn:rescuecore2.standard:property:y")))
									.getValue());
							blockade.setPosition(((EntityRefProperty) (changes[t
									.getTime()]
									.getChangedProperty(id,
											"urn:rescuecore2.standard:property:position")))
									.getValue());
							blockade.setRepairCost(((IntProperty) (changes[t
									.getTime()]
									.getChangedProperty(id,
											"urn:rescuecore2.standard:property:repaircost")))
									.getValue());
							super.world.addEntity(blockade);
						}
					}
				}
				super.cBlockadeList = new HashMap<EntityID, List<EntityID>>();
				for (EntityID id : t.getChangeSet().getChangedEntities()) {
					if ((changes[t.getTime()].getEntityURN(id)) != null) {
						if ((changes[t.getTime()].getEntityURN(id))
								.equals("urn:rescuecore2.standard:entity:road")) {
							super.cBlockadeList
									.put(id,
											(((EntityRefListProperty) (changes[t
													.getTime()]
													.getChangedProperty(id,
															"urn:rescuecore2.standard:property:blockades")))
													.getValue()));
						}
					}
				}
			} else if (time > 1) {
				ArrayList<Entity> entities = new ArrayList<Entity>();
				for (EntityID id : t.getChangeSet().getChangedEntities()) {
					if ((changes[t.getTime()].getEntityURN(id)) != null) {
						if ((changes[t.getTime()].getEntityURN(id))
								.equals("urn:rescuecore2.standard:entity:blockade")) {
							if ((((IntProperty) (changes[t.getTime()]
									.getChangedProperty(id,
											"urn:rescuecore2.standard:property:repaircost")))
									.getValue()) != 0) {
								Blockade blockade = new Blockade(id);
								blockade.setApexes(((IntArrayProperty) (changes[t
										.getTime()]
										.getChangedProperty(id,
												"urn:rescuecore2.standard:property:apexes")))
										.getValue());
								// blockade.setX(((IntProperty)(changes[t.getTime()].getChangedProperty(id,
								// "urn:rescuecore2.standard:property:x"))).getValue());
								// blockade.setY(((IntProperty)(changes[t.getTime()].getChangedProperty(id,
								// "urn:rescuecore2.standard:property:y"))).getValue());
								// blockade.setPosition(((EntityRefProperty)(changes[t.getTime()].getChangedProperty(id,
								// "urn:rescuecore2.standard:property:position"))).getValue());
								blockade.setRepairCost(((IntProperty) (changes[t
										.getTime()]
										.getChangedProperty(id,
												"urn:rescuecore2.standard:property:repaircost")))
										.getValue());
								entities.add(blockade);
							} else {
								super.world.removeEntity(id);
							}
						}
						if ((changes[t.getTime()].getEntityURN(id))
								.equals("urn:rescuecore2.standard:entity:road")) {
							super.cBlockadeList
									.put(id,
											(((EntityRefListProperty) (changes[t
													.getTime()]
													.getChangedProperty(id,
															"urn:rescuecore2.standard:property:blockades")))
													.getValue()));
						}
					}
				}
				// super.world.merge(entities);
			}

			List<Command> cc = t.getCommands();
			if (cc != null) {
				commands[super.time] = new Command[cc.size()];
				int i = 0;
				for (Command com : cc) {
					commands[super.time][i] = com;
					i++;
				}
			} else {
				commands[super.time] = null;
			}
		} else if (msg instanceof KVConnectOK && super.transform == null) {
			KVConnectOK ok = (KVConnectOK) msg;
			try {
				connection.sendMessage(new VKAcknowledge(ok.getRequestID(), ok
						.getViewerID()));
				super.world = new StandardWorldModel();
				super.startWorld = new StandardWorldModel();
				super.world.addEntities(ok.getEntities());
				for (Entity e : super.world.getAllEntities()) {
					super.startWorld.addEntity(e.copy());
				}

				Config config = ok.getConfig();
				super.endTime = Integer.parseInt(config
						.getValue("kernel.timesteps"));
				super.changes = new ChangeSet[super.endTime + 1];
				for (int i = 0; i < super.changes.length; ++i) {
					super.changes[i] = null;
				}
				super.commands = new Command[super.endTime + 1][];
				for (int i = 0; i < super.commands.length; ++i) {
					commands[i] = null;
				}
				super.createScreenTransform(world);
			} catch (ConnectionException ce) {
				// connection error
				ce.printStackTrace();
			}
		} else if (msg instanceof KVConnectError) {
			// error
		} else if (msg instanceof Shutdown) {
			super.endTime = super.time;
			super.simulationEnded = true;
		}
	}
}