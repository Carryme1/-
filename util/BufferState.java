package util;

import core.DTNHost;
import core.Message;
import core.SimScenario;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BufferState {

    //public List<Integer> nrofmsg = new ArrayList<>();
    //save nrofmsg
    //public int[] FerryBuffer = new int[5];
    public float[][] distace=new float[5][5];

    public static int countRelay;
    public static int countOutrelay;
    public static int difcount;
    public static int fixcount;

    public int[] getFerryBuffer(){
        //get
        int i=0;
        int j=0;
        int index=0;
        //ferry id
        List<Integer> list = new ArrayList<>();
        int[] FerryBuffer = new int[5];
        List<Integer> nrofmsg = new ArrayList<>();
        DTNHost ferry = SimScenario.world.getNodeByAddress(85);
        Collection<Message> FerryMsgList=ferry.getRouter().getMessageCollection();
        Iterator<Message> iterator=FerryMsgList.iterator();
        //System.out.println(FerryMsgList.size());
        //System.out.println(SimScenario.world.getNodeByAddress(55).getBufferOccupancy());
        while(iterator.hasNext()){
            Message m= iterator.next();
            i=m.getTo().getAddress();
            j=m.getFrom().getAddress();

            if ((i<10&&j<10)||(i>=10&&j>=10&&i<20&&j<20)||(i>=20&&j>=20&&i<30&&j<30)||(i>=30&&j>=30&&i<40&&j<40)||i>=40&&j>=40) {
//                iterator.remove();
//                countRelay++;
                continue;
            }

            if(i>=0&&i<10)
                nrofmsg.add(0);
            else if (i >= 10 && i < 20)
                nrofmsg.add(1);
            else if (i >= 20 && i < 30)
                nrofmsg.add(2);
            else if (i >= 30 && i < 40)
                nrofmsg.add(3);
            else
                nrofmsg.add(4);
        }
        //System.out.println(SimScenario.world.getNodeByAddress(55).getBufferOccupancy());
        //System.out.println(FerryMsgList.size());
//        for(Message m1:FerryMsgList){
//
//            System.out.println(m1.getId()+" "+m1.getFrom().getAddress()+" "+m1.getTo().getAddress());
//        }


        //System.out.println("=================");
//        for(Message m:FerryMsgList){
//
//            i=m.getTo().getAddress();
//            j=m.getFrom().getAddress();
//            System.out.println(m.getId()+" "+i+" "+j);
//
//            if(i-j<10) {
//                m.setTtl(1);
//            }
//            if(i>=0&&i<10)
//                nrofmsg.add(0);
//            else if (i >= 10 && i < 20)
//                nrofmsg.add(1);
//            else if (i >= 20 && i <= 30)
//                nrofmsg.add(2);
//            else if (i > 30 && i <= 40)
//                nrofmsg.add(3);
//            else
//                nrofmsg.add(4);
//        }

        for(int id:nrofmsg){

            if(id==0)
                FerryBuffer[0]+=1;
            if(id==1)
                FerryBuffer[1]+=1;
            if(id==2)
                FerryBuffer[2]+=1;
            if(id==3)
                FerryBuffer[3]+=1;
            if(id==4)
                FerryBuffer[4]+=1;
        }
        //System.out.println(FerryMsgList.size());



        return FerryBuffer;

        }


        public int getFixedBuffer(int a){
            int[] FixedBuffer=new int[2];
            List<Integer> nrofmsg = new ArrayList<>();
            int i=0;
            int j=0;
            //fixed node buffer id
            Collection<Message> FixedMsgList=SimScenario.world.getNodeByAddress(a).getMessageCollection();
            for(Message msg:FixedMsgList){
                i=msg.getTo().getAddress();
                j=msg.getFrom().getAddress();
                if ((i<10&&j<10)||(i>=10&&j>=10&&i<20&&j<20)||(i>=20&&j>=20&&i<30&&j<30)||(i>=30&&j>=30&&i<40&&j<40)||i>=40&&j>=40) {
//                iterator.remove();
//                countRelay++;
                    continue;
                }
                if(i>=0&&i<10)
                    nrofmsg.add(0);
                else if(i>=10&&i<20)
                    nrofmsg.add(1);
                else if(i>=20&&i<30)
                    nrofmsg.add(2);
                else if(i>=30&&i<40)
                    nrofmsg.add(3);
                else
                    nrofmsg.add(4);
            }

            //if min of nro
            for(int id:nrofmsg){

                if(id!=(a%10))
                    FixedBuffer[0]+=1;
                else
                    FixedBuffer[1]+=1;
            }

           // fixcount+=FixedBuffer[1];


            return FixedBuffer[0];
        }


        public float[][] getDistance(String filename,int cityNum) {
            int[] x;
            int[] y;
            float[][] distance = new float[5][5];
            String strbuff;
            try{
            BufferedReader data = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));
            distance = new float[cityNum][cityNum];
            x = new int[cityNum];
            y = new int[cityNum];
            for (int i = 0; i < cityNum; i++) {
                strbuff = data.readLine();
                String[] strcol = strbuff.split(" ");
                x[i] = Integer.valueOf(strcol[1]);
                y[i] = Integer.valueOf(strcol[2]);
            }
            data.close();

            for (int i = 0; i < cityNum - 1; i++) {
                distance[i][i] = 0;
                for (int j = i + 1; j < cityNum; j++) {
                    double rij = Math
                            .sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
                                    * (y[i] - y[j])));

                    int tij = (int) Math.round(rij);
                    if (tij < rij) {
                        distance[i][j] = tij + 1;
                        distance[j][i] = distance[i][j];
                    } else {
                        distance[i][j] = tij;
                        distance[j][i] = distance[i][j];
                    }
                }
            }

            }catch (Exception e){
                e.printStackTrace();
            }

            return distance;

    }
    //

    public int getDif(int[] a, int[] b){
        int sum_b=b[0];
        int sum_a = 0;
        for(int i: a)
            sum_a+=i;
        return Math.abs(sum_a-sum_b);
    }

    public int getSet(int a){
        Set<Message> ferryset = new HashSet<>();
        Set<Message> fixset = new HashSet<>();
        Collection<Message> ferryc=SimScenario.world.getNodeByAddress(55).getMessageCollection();
        for(Message m: ferryc){
            ferryset.add(m);
        }
        Collection<Message> fixc=SimScenario.world.getNodeByAddress(a).getMessageCollection();
        for(Message m1:fixc){
            fixc.add(m1);
        }

         ferryset.removeAll(fixc);
        return ferryset.size();

    }
    }


