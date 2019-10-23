/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement.map;

import input.WKTReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Coord;
import core.SettingsError;
import util.BufferState;

import static movement.MapBasedMovement.map;

/**
 * A route that consists of map nodes. There can be different kind of routes
 * and the type is determined by the type parameter ({@value #CIRCULAR}
 * or {@value #PINGPONG}).
 */
public class MapRoute {
	/** Type of the route ID: circular ({@value}). 
	 * After reaching the last node on path, the next node is the first node */
	public static final int CIRCULAR = 1;
	/** Type of the route ID: ping-pong ({@value}).
	 * After last node on path, the direction on path is reversed */
	public static final int PINGPONG = 2;
	
	
	private List<MapNode> stops;
	private int type; // type of the route
	private int index; // index of the previous returned map node
	private boolean comingBack;
	
	/**
	 * Creates a new map route
	 * @param stops The stops of this route in a list
	 * @param type Type of the route (e.g. CIRCULAR or PINGPONG)
	 */
	public MapRoute(int type, List<MapNode> stops) {
		assert stops.size() > 0 : "Route needs stops";
		assert index < stops.size() : "Too big start index for route";		
		this.type = type;
		this.stops = stops;
		this.index = 0;
		this.comingBack = false;
	}
	
	/**
	 * Sets the next index for this route
	 * @param index The index to set
	 */
	public void setNextIndex(int index) {
		if (index > stops.size()) {
			index = stops.size();
		}
		
		this.index = index;
	}
	
	/**
	 * Returns the number of stops on this route
	 * @return the number of stops on this route
	 */
	public int getNrofStops() {
		return stops.size();
	}
	
	public List<MapNode> getStops() {
		return this.stops;
	}
	//

	public static Map<Double,Integer> areaMap = new HashMap<>();
	public static void setAreaMap(){
		areaMap.put(650.0,50);
		areaMap.put(1200.0,51);
		areaMap.put(2650.0,52);
		areaMap.put(1800.0,53);
		areaMap.put(2500.0,54);
		areaMap.put(0.0,0);
	}

	public static Map<Integer,MapNode> idMapnode=new HashMap<>();
	public static void setidMap(){
		idMapnode.put(0, map.getNodes().get(4));
		idMapnode.put(1, map.getNodes().get(2));
		idMapnode.put(2, map.getNodes().get(1));
		idMapnode.put(3, map.getNodes().get(0));
		idMapnode.put(4, map.getNodes().get(3));
	}

	public List<Integer> lsit=new ArrayList<>();
	public List<MapNode> nodePath;

	public double[] getdistance(MapNode s){
		double[] dis=new double[5];
		double[] x={650,1200,2650,1800,2500};
		double[] y={1100,600,1200,3000,2000};
		double s_x = s.getLocation().getX();
		double s_y = s.getLocation().getY();
		for(int i=0;i<x.length;i++){
			dis[i]=Math.pow((s_x-x[i])*(s_x-x[i])+(s_y-y[i])*(s_y-y[i]),0.5);
		}
		return dis;
	}


//	public MapNode getTo(MapNode s){
//		setAreaMap();
//		setidMap();
//		double[] distance = getdistance(s);
//		BufferState bufferState = new BufferState();
//		bufferState.getFerryBuffer();
//		//bufferState.getDistance("E:\\one-master\\util\\MapNode.txt",5);
//		float[] need = new float[5];
//		for(int fix=50;fix<55;fix++){
//			need[fix%10] = bufferState.getFixedBuffer(fix);
//		}
//
//		for (int i = 0; i < 5; i++) {
//			if(distance[i]!=0.0) {
//				need[i] = (float) ((bufferState.FerryBuffer[i] + need[i]) / distance[i]);
//				System.out.print(need[i] + " ");
//			}
//			else{
//				need[i]=0;
//			}
//		}
//		System.out.println("");
//		float max=need[0];
//		int key=0;
//
//		for(int k=0;k<need.length;k++){
//			if(max<=need[k]) {
//				max=need[k];
//				key = k;
//			}
//		}
//
//		System.out.println(key);
//
//
//		return idMapnode.get(key);
//	}

	/**
	 * Returns the next stop on the route (depenging on the route mode)
	 * @return the next stop on the route
	 */
	public MapNode nextStop() {	
		if (comingBack) {
			index--; // ping-pong coming back
		}
		else {
			index++;
		}
		
		if (index < 0) { // returned to beginning in ping-pong
			comingBack = false; // start next round
			index = 1;
		}
		
		if (index >= stops.size()) { // reached last stop
			if (type == PINGPONG) {
				comingBack = true;
				index = stops.size() - 1; // go next to prev to last stop
			}
			else {
				index = 0; // circular goes back to square one
			}
		}
		
		return stops.get(index);
		
	}
	
	/**
	 * Returns a new route with the same settings
	 * @return a replicate of this route
	 */
	public MapRoute replicate() {
		return new MapRoute(type, stops);
	}
	
	public String toString() {
		return ((type == CIRCULAR) ? "Circular" : "Ping-pong") + " route with "+
			getNrofStops() + " stops";
	}
	
	/**
	 * Reads routes from files defined in Settings
	 * @param fileName name of the file where to read routes
	 * @param type Type of the route
	 * @param map SimMap where corresponding map nodes are found
	 * @return A list of MapRoutes that were read
	 */
	public static List<MapRoute> readRoutes(String fileName, int type, 
			SimMap map) {
		List<MapRoute> routes = new ArrayList<MapRoute>();
		WKTReader reader = new WKTReader();
		List<List<Coord>> coords;
		File routeFile = null;
		boolean mirror = map.isMirrored();
		double xOffset = map.getOffset().getX();
		double yOffset = map.getOffset().getY();
		
		if (type != CIRCULAR && type != PINGPONG) {
			throw new SettingsError("Invalid route type (" + type + ")");
		}
		
		try {
			routeFile = new File(fileName);
			coords = reader.readLines(routeFile);
		}
		catch (IOException ioe){
			throw new SettingsError("Couldn't read MapRoute-data file " + 
					fileName + 	" (cause: " + ioe.getMessage() + ")");
		}
		
		for (List<Coord> l : coords) {			
			List<MapNode> nodes = new ArrayList<MapNode>();
			for (Coord c : l) {
				// make coordinates match sim map data
				if (mirror) {
					c.setLocation(c.getX(), -c.getY());
				}
				c.translate(xOffset, yOffset);
				
				MapNode node = map.getNodeByCoord(c);
				if (node == null) {
					Coord orig = c.clone();
					orig.translate(-xOffset, -yOffset);
					orig.setLocation(orig.getX(), -orig.getY());
					
					throw new SettingsError("MapRoute in file " + routeFile + 
							" contained invalid coordinate " + c + " orig: " +
							orig);
				}
				nodes.add(node);
			}
			
			routes.add(new MapRoute(type, nodes));
		}
		
		return routes;
	}
}
