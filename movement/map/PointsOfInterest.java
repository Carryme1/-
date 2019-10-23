/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement.map;

import input.WKTReader;

import java.io.*;
import java.util.*;

import util.BufferState;
import util.Tuple;

import core.Coord;
import core.Settings;
import core.SettingsError;

/**
 * Handler for points of interest data.
 */
public class PointsOfInterest {
	/** Points Of Interest settings namespace ({@value})*/
	public static final String POI_NS = "PointsOfInterest";
	/** Points Of Interest file path -prefix id ({@value})*/
	public static final String POI_FILE_S = "poiFile";
	
	/** 
	 * Per node group setting used for selecting POI groups and their
	 * probabilities ({@value}).<BR>Syntax: 
	 * <CODE>poiGroupIndex1, groupSelectionProbability1, groupIndex2, prob2,
	 *  etc...</CODE><BR>
	 *  Sum of probabilities must be less than or equal to one (1.0). If the sum
	 *  is less than one, chance of getting a random MapPoint is 
	 *  <CODE>1-sum</CODE>.
	 */
	public static final String POI_SELECT_S = "pois";
	/** map whose points all POIs are */
	public static SimMap map;
	/** map node types that are OK to visit */
	private int [] okMapNodeTypes;
	/** list of all this POI instance's POI lists */
	private ArrayList<List<MapNode>> poiLists;
	/** list of probabilites of choosing a POI group */
	private List<Tuple<Double, Integer>> poiProbs;
	/** (pseudo) random number generator */
	private Random rng;




	/**
	 * Constructor.
	 * @param parentMap The map whose MapNodes' subset the POIs are
	 * @param okMapNodeTypes Array of map node types that are OK to visit or
	 * null if all nodes are OK
	 * @param settings The Settings object where settings are read from
	 * @param rng The random number generator to use
	 */
	public PointsOfInterest(SimMap parentMap, int [] okMapNodeTypes, 
			Settings settings, Random rng) {
		this.poiLists = new ArrayList<List<MapNode>>();
		this.poiProbs = new LinkedList<Tuple<Double, Integer>>();
		this.map = parentMap;
		this.okMapNodeTypes = okMapNodeTypes;
		this.rng = rng;
		readPois(settings);
	}
	
	/**
	 * Selects a random destination from POIs or all MapNodes. Selecting among
	 * POI groups is done by their probabilities. If sum of their probabilities
	 * is less than 1.0 and the drawn random probability is bigger than the sum,
	 * a random MapNode is selected from the SimMap. 
	 * @return A destination among POIs or all MapNodes
	 */
	public MapNode selectDestination() {
		double random = rng.nextDouble();
		double acc = 0;
		//pro

		for (Tuple<Double, Integer> t : pro) {
			acc += t.getKey();
			if (acc > random) {
				// get the lucky POI group
				List<MapNode> pois = poiLists.get(t.getValue());
				// return a random POI from that group
				return pois.get(rng.nextInt(pois.size()));
			}
		}
		
		// random was bigger than sum of probs -> return a random map node
		// that is still OK (if OK node types are defined)
		List<MapNode> allNodes = map.getNodes();
		MapNode node;
		do {
			node = allNodes.get(rng.nextInt(allNodes.size()));
		} while (okMapNodeTypes != null && !node.isType(okMapNodeTypes));

		pro.clear();
		return node;
	}
 
	/**
	 * Reads POI selections and their probabilities from given Settings and
	 * stores them to <CODE>poiLists</CODE> and <CODE>poiProbs</CODE>.
	 * @param s The settings file where group specific settings are read
	 * @throws Settings error if there was an error while reading the file or
	 * some of the settings had invalid value(s).
	 */
	private void readPois(Settings s) {
		Coord offset = map.getOffset();
		if (!s.contains(POI_SELECT_S)) {
			return; // no POIs for this group
		}
		double[] groupPois = s.getCsvDoubles(POI_SELECT_S);
		
		// fully qualified setting name for error messages
		String fqSetting = s.getFullPropertyName(POI_SELECT_S);
		
		if (groupPois.length % 2 != 0) {
			throw new SettingsError("Invalid amount of POI selection-"+
					"probability values (" + groupPois.length + "). Must be " + 
					"divisable by 2 in " + fqSetting);
		}
		
		// read POIs from the requested indexes and assign defined probabilites
		for (int i=0; i<groupPois.length-1; i+=2) {
			int index = (int)groupPois[i];
			double prob = groupPois[i+1]; 
			
			if (prob < 0.0 || prob > 1.0) {
				throw new SettingsError("Invalid probability value (" + prob +
						") for POI at index " + index + " in " + fqSetting);
			}
			
			// check that there's no list of POIs for that index yet
			if (index < poiLists.size() && poiLists.get(index) != null) {
				throw new SettingsError("Duplicate definition for POI index " +
						index + " in " + fqSetting);
			}
			
			List<MapNode> nodes = readPoisOf(index, offset);
			if (poiLists.size() <= index) {
				// list too small -> fill with nulls up to index
				for (int j = poiLists.size(); j <= index; j++) {
					poiLists.add(j,null);
				}
			}
			poiLists.set(index, nodes);
			poiProbs.add(new Tuple<Double,Integer>(groupPois[i+1], index));
		}
		
		// check the sum of probabilites
		double probSum = 0;
		for (Tuple <Double,Integer> t : pro) {
			probSum += t.getKey();
		}
		if (probSum > 1.0) {
			throw new SettingsError("Sum of POI probabilities (" + 
					String.format("%.2f", probSum) + 
					") exceeds 1.0 in " + fqSetting);
		}
		
	}
	
	/**
	 * Reads POIs from a file <CODE>{@value POI_FILE_S} + index</CODE> defined
	 * in Settings' namespace {@value POI_NS}.
	 * @param index The index of the POI file
	 * @param offset Offset of map data
	 * @return A list of MapNodes read from the POI file
	 * @throws Settings error if there was an error while reading the file
	 * or some coordinate in POI-file didn't match any MapNode in the SimMap
	 */
	private List<MapNode> readPoisOf(int index, Coord offset) {
		List<MapNode> nodes = new ArrayList<MapNode>();
		Settings fileSettings = new Settings(POI_NS);
		WKTReader reader = new WKTReader();
		
		File poiFile = null;
		List<Coord> coords = null;
		try {
			poiFile = new File(fileSettings.getSetting(POI_FILE_S + index));
			coords = reader.readPoints(poiFile);
		}
		catch (IOException ioe){
			throw new SettingsError("Couldn't read POI-data from file '" + 
					poiFile + "' defined in setting " + 
					fileSettings.getFullPropertyName(POI_FILE_S + index) + 
					" (cause: " + ioe.getMessage() + ")");
		}
		
		if (coords.size() == 0) {
			throw new SettingsError("Read a POI group of size 0 from "+poiFile);
		}
		
		for (Coord c : coords) {
			if (map.isMirrored()) { // mirror POIs if map data is also mirrored
				c.setLocation(c.getX(), -c.getY()); // flip around X axis
			}
			
			// translate to match map data
			c.translate(offset.getX(), offset.getY());
			
			
			MapNode node = map.getNodeByCoord(c);
			if (node != null) {
				if (okMapNodeTypes != null && !node.isType(okMapNodeTypes)) {
					throw new SettingsError("POI " + node + " from file " +
							poiFile + " is on a part of the map that is not "+
							"allowed for this movement model");
				}
				nodes.add(node);
			}
			else {
				throw new SettingsError("No MapNode in SimMap at location " + 
						c + " (after translation) from file " + poiFile);
			}
		}
				
		return nodes;
	}

	public int pre;

	public double[] getdistance(MapNode s){
		double[] dis=new double[5];
		double[] x={650,2500,2650,2500,1800};
		double[] y={1100,600,1400,2000,2500};
		double s_x = s.getLocation().getX();
		double s_y = s.getLocation().getY();
		for(int i=0;i<x.length;i++){
			dis[i]=Math.pow((s_x-x[i])*(s_x-x[i])+(s_y-y[i])*(s_y-y[i]),0.5);
		}
		return dis;
	}

	public List<Tuple<Double,Integer>> pro=new ArrayList<>();
	public List<Tuple<Double, Integer>> getTo(MapNode s){
		float[] fixBuff= new float[5];
		float[] need = new float[5];
		double[] distance = getdistance(s);
		BufferState bufferState = new BufferState();
		int[] ferryBuffer=bufferState.getFerryBuffer();
		for(int fix=80;fix<85;fix++){
			fixBuff[fix%10] = bufferState.getFixedBuffer(fix);
		}
        float sum_d=0;
		float gyh_d=0;
		float sum_b=0;
		float gyh_b=0;
		for(double a:distance) {
			sum_d += a;
		}
		for (int i = 0; i < 5; i++) {
				need[i] = (float) (ferryBuffer[i] + fixBuff[i]);
			}
		for(float c :need){
			sum_b += c;
		}
        for(int j=0;j<need.length;j++){
        	if(distance[j]!=0.0) {
                gyh_d = (float) (distance[j] / sum_d);
                gyh_b = need[j]/ sum_b;
                need[j] = gyh_b/gyh_d;
            }
            else {
                need[j] = (float) 0.00000001;
            }
        }
		float max=need[0];
		int key=0;
		for(int k=0;k<need.length;k++){
			if(max<=need[k]) {
				max=need[k];
				key = k;
			}
		}

//		System.out.println(key);
		while(pre==key) {
			key = rng.nextInt(5);
		}

//		System.out.println(key);
		for(int p=0;p<5;p++) {
			if(p==key)
				pro.add(new Tuple<Double, Integer>(0.6,p+1));
			else
				pro.add(new Tuple<Double, Integer>(0.1,p+1));
		}
		pre = key;
		return pro;
	}

}
