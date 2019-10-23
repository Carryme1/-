package util;

import core.DTNHost;
import core.Message;

import java.util.*;

import static core.SimScenario.world;

public class FixedBuffer {

    public static float buffer[][]=new float[5][5];
    public Map<Integer,Integer> areaMap;

    public void setAreaMap(){
        areaMap=new HashMap<>();
        areaMap.put(60,0);
        areaMap.put(61,1);
        areaMap.put(62,2);
        areaMap.put(63,3);
        areaMap.put(64,4);
    }

    public float[][] getFixed(int a){
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
        return buffer;
    }

    public float[][] getdiffBuffer(float[][] buffer){
        float[][] preBuffer = buffer;
        for(int i=0;i<buffer.length;i++)
            for(int j=0;j<buffer[0].length;j++){
                buffer[i][j]=buffer[i][j]-preBuffer[i][j];

            }

        return buffer;
    }




}
