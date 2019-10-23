/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import java.util.*;

import core.*;
import input.MessageDeleteEvent;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.PointsOfInterest;




/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class ShortestPathMapBasedMovement extends MapBasedMovement implements 
	SwitchableMovement {
	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;

	/** Points Of Interest handler */
	private PointsOfInterest pois;

	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public ShortestPathMapBasedMovement(Settings settings) {
		super(settings);
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.pois = new PointsOfInterest(getMap(), getOkMapNodeTypes(),
				settings, rng);
	}
	
	/**
	 * Copyconstructor.
	 * @param mbm The ShortestPathMapBasedMovement prototype to base 
	 * the new object to 
	 */
	protected ShortestPathMapBasedMovement(ShortestPathMapBasedMovement mbm) {
		super(mbm);
		this.pathFinder = mbm.pathFinder;
		this.pois = mbm.pois;
	}

	@Override
	public Path getPath() {
		Path p = new Path(generateSpeed());
		initePath(p);
		//write input pois

//		for(Message m: world.getNodeByAddress(55).getMessageCollection()){
//			if(m.getTo().getId()-m.getFrom().getId()<10)
//				world.getNodeByAddress(55).deleteMessage(m.getId(),false);
//		}

		pois.getTo(lastMapNode);
		//System.out.println(getHost().getMessageCollection());
		MapNode to = pois.selectDestination();

		//getHost().getRouter();
		//nodePath.add(to);
//		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);
		List<MapNode> nodePath = new ArrayList<>();
		nodePath.add(to);
		//System.out.println(SimClock.getIntTime()+"  "+(areaMap.get(to.getLocation().getX()))%10);
		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";
		//int i= areaMap.get(to.getLocation().getX())%10;
		//areaMap.get(lastMapNode.getLocation().getX())%10;

		//SimScenario.world.getNodeByAddress(areaMap.get(to.getLocation().getX()));
//		dif+=getFerrySum(PointsOfInterest.ferryB)-PointsOfInterest.ferryB[i];

		for (MapNode node : nodePath) { // create a Path from the shortest path
			p.addWaypoint(node.getLocation());
		}

		lastMapNode = to;

		return p;
	}	
	
	@Override
	public ShortestPathMapBasedMovement replicate() {
		return new ShortestPathMapBasedMovement(this);
	}


	public int getFerrySum(int[] a){
		int sum =0 ;
		for(int i: a)
			sum+=i;
		return sum;
	}

	public void initePath(Path p){
		p.setSpeed(60);
	}


}
