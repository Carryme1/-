package util;

import report.DeliveredMessagesReport;

import java.io.*;
import java.util.Arrays;

import static java.lang.Float.valueOf;

public class OutputResult {
    public float inDelay = 0;
    public float allDelay = 0;
    public int all = 0;
    public int count1 = 0;
    public int count =0;
    public float outDelay = 0;

    public void getOutResult(){


        try {
            BufferedReader in = new BufferedReader(new FileReader("reports//default_scenario_DeliveredMessagesReport.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                if(str.contains("time"))
                    break;
            }
            String[] s = null;
            while((str = in.readLine())!=null){
                if(str.contains("f")) {
                    count++;
                    s = str.split(" ");
                    outDelay+=Float.valueOf(s[5]);
                    System.out.println(count+" "+s[s.length - 1]+" "+s[5]);
                }
            }

            in.close();

        }catch (Exception e){
            e.printStackTrace();
    }
    }




    public void getInResult(){
        try {
            BufferedReader in = new BufferedReader(new FileReader("reports//default_scenario_DeliveredMessagesReport.txt"));
            String str;
            while ((str = in.readLine()) != null) {
                if(str.contains("time"))
                    break;
            }
            String[] s = null;
            while((str = in.readLine())!=null){
                s = str.split(" ");
                if(!str.contains("f")) {
                    count1++;

                    inDelay+=Float.valueOf(s[5]);
                    System.out.println(count1+" "+s[s.length - 1]+" "+s[5]);
                }
                allDelay+=Float.valueOf(s[5]);
                all++;
            }
            System.out.println("区域内平均延迟"+inDelay/count1);
            System.out.println("综合平均延迟时间"+allDelay/all);
            in.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
//
//    public void getDelivery(){
//        DeliveredMessagesReport deliveredMessagesReport= new DeliveredMessagesReport();
//        System.out.println("==================区域内数据递交率====================");
//        System.out.println(count1/deliveredMessagesReport.inNrofNode);
//        System.out.println("==================区域间数据递交率====================");
//        System.out.println(count/deliveredMessagesReport.outNrofNode);
//
//
//
//    }

    public static void main(String[] args){
        OutputResult outputResult = new OutputResult();
//        DeliveredMessagesReport deliveredMessagesReport = new DeliveredMessagesReport();
        outputResult.getOutResult();
        System.out.println("==================区域内====================");
        outputResult.getInResult();
        System.out.println("区域间平均延迟"+outputResult.outDelay/outputResult.count);
        //System.out.println("=================数据递交率=====================");
//        int in = deliveredMessagesReport.nrof[0];
//        int out =deliveredMessagesReport.nrof[1];
//        if(in==0&&out==0)
//            System.out.println("没有产生包");
//        else {
//            System.out.println("区域内数据递交率：" + in / (out + in));
//            System.out.println("区域间数据递交率：" + out / (out + in));
//        }
    }


}

