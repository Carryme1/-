/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.io.IOException;
import java.util.*;

import core.*;
import movement.StationaryMovement;
import util.BufferState;
import util.ChangeTsp;
import util.FixedBuffer;
import util.Tsp;

import javax.swing.plaf.synth.SynthEditorPaneUI;

import static core.SimScenario.world;


/**
 * Report information about all delivered messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class DeliveredMessagesReport extends Report implements MessageListener {
	public static String HEADER = "# time  creatTime ID  size  hopcount  deliveryTime  " +
		"fromHost  toHost  remainingTtl  isResponse  path";

	/**
	 * Constructor.
	 */
	public DeliveredMessagesReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		write(HEADER);
	}

	/** 
	 * Returns the given messages hop path as a string
	 * @param m The message
	 * @return hop path as a string
	 */
	private String getPathString(Message m) {
		List<DTNHost> hops = m.getHops();
		String str = m.getFrom().toString();
		
		for (int i=1; i<hops.size(); i++) {
			str += "->" + hops.get(i); 
		}
		
		return str;
	}


	public static float[][] prebuff=new float[5][5];
	public static float[][] difbuffer=new float[5][5];
	public static float[][] buffer= new float[5][5];
	public static Map<Integer,Integer> areaMap=new HashMap<>();

	public void printArray(float[][] a){
		for(int i=0;i<a.length;i++)
			for(int j=0;j<a[0].length;j++){
				if(j<4)
					System.out.print(a[i][j]+" ");
				else
					System.out.print(a[i][j]+"\n");
			}

	}

	public static void setareaMap(){
		areaMap.put(60,0);
		areaMap.put(61,1);
		areaMap.put(62,2);
		areaMap.put(63,3);
		areaMap.put(64,4);
	}

	public int[] nrof = new int[2];

	public int[] getOutofMsg(Message m,DTNHost to){
		//nrof[0] in
		int[] num =new int[2];
		if(Math.abs(m.getTo().getAddress()-to.getAddress())<10||(to.getAddress()>40&&(Math.abs(m.getTo().getAddress()-to.getAddress())<20)))
			num[0]++;
		else
			num[1]++;
		return num;
	}

	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean firstDelivery) {

		//nrof = getOutofMsg(m,to);
		//System.out.println(m.getTo().getBufferOccupancy());

		if (!isWarmupID(m.getId()) && firstDelivery) {



//				getNrofMsg(m);


//			if(SimClock.getIntTime()%10==0) {
//					setareaMap();
//					getFixedBuffer(60);
//					getFixedBuffer(61);
//					getFixedBuffer(62);
//					getFixedBuffer(63);
//					getFixedBuffer(64);
//					//printArray(prebuff);
//					//printArray(buffer);
//
//					for(int k=0;k<prebuff.length;k++)
//						for(int p=0;p<prebuff[0].length;p++){
//							difbuffer[k][p]=buffer[k][p]-prebuff[k][p];
//						}
//						//printArray(difbuffer);
//						for(int i=0;i<prebuff.length;i++)
//							for(int j=0;j<prebuff[0].length;j++){
//								prebuff[i][j]=buffer[i][j];
//					}
//					Tsp ts = new Tsp(5, DeliveredMessagesReport.difbuffer);
//					ChangeTsp changeTsp = new ChangeTsp();
//					try {
//						ts.init("E:\\one-master\\util\\MapNode.txt");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					changeTsp.changRoute(changeTsp.splitTsp(ts.solve()));
//				}

			int ttl = m.getTtl();
			write(format(getSimTime()) + " " +m.getCreationTime()+" "+ m.getId() + " " +
					m.getSize() + " " + m.getHopCount() + " " + 
					format(getSimTime() - m.getCreationTime()) + " " + 
					m.getFrom() + " " + m.getTo() + " " +
					(ttl != Integer.MAX_VALUE ? ttl : "n/a") +  
					(m.isResponse() ? " Y " : " N ") + getPathString(m));
		}
	}



	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
		}
	}
	
	// nothing to implement for the rest
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		super.done();
	}

	//public static Map<Integer,Collection<Message>> fixMap=new HashMap<>();





//	public static void getdifBuffer(){
//		float[][] preBuffer=new float[5][5];
//		FixedBuffer fixedBuffer = new FixedBuffer();
//		fixedBuffer.setAreaMap();
//		for(int x=0;x<preBuffer.length;x++)
//			for (int y=0;y<preBuffer[0].length;y++){
//				preBuffer[x][y]=buffer[x][y];
//			}
//			fixedBuffer.getFixed(60);
//			fixedBuffer.getFixed(61);
//			fixedBuffer.getFixed(62);
//			fixedBuffer.getFixed(63);
//			fixedBuffer.getFixed(64);
//
//		for (int k = 0; k < preBuffer.length; k++)
//			for (int p = 0; p < preBuffer[0].length; p++) {
//				buffer[k][p] = buffer[k][p];
//			}
//
//	}




	public  void getFixedBuffer(int a){
		int i=0;
		int areaId = areaMap.get(world.getNodeByAddress(a).getAddress());
		List<Integer> nrofmsg=new ArrayList<>();
		Collection<Message> list1= world.getNodeByAddress(a).getMessageCollection();
		for(Message m1:list1){
			i=m1.getTo().getAddress();
			if(i>=0&&i<10)
				nrofmsg.add(0);
			else if(i>=10&&i<20)
				nrofmsg.add(1);
			else if(i>=20&&i<=30)
				nrofmsg.add(2);
			else if(i>30&&i<=40)
				nrofmsg.add(3);
			else
				nrofmsg.add(4);
		}

		for(int j:nrofmsg){

			if(j==0)
				buffer[areaId][0]+=1;
			if(j==1)
				buffer[areaId][1]+=1;
			if(j==2)
				buffer[areaId][2]+=1;
			if(j==3)
				buffer[areaId][3]+=1;
			if(j==4)
				buffer[areaId][4]+=1;
		}

	}

}
