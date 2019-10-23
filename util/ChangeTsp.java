package util;

import core.Settings;
import movement.MapBasedMovement;
import movement.MapRouteMovement;
import movement.map.MapRoute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ChangeTsp {

    public int[] splitTsp(String path){
        String[] s=path.split("-->");
        int[] a=new int[s.length];
        for(int i=0;i<s.length;i++){
            a[i]=Integer.valueOf(s[i]);
        }
        return a;
    }



    public static String s1="";


    public void changRoute(int[] a){
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"650 -1100");
        map.put(1,"1200 -600");
        map.put(2,"2650 -1200");
        map.put(3,"2500 -2000");
        map.put(4,"1800 -3000");
        //String s="650 -1100,1200 -600,2650 -1200,2500 -2000,1800 -3000,650 -1100";
        String s="";
        String ss="";
        for(int i=0;i<a.length;i++){
            if(i!=a.length-1) {
                s += map.get(a[i])+",";
                ss += map.get(a[i])+",";
            }
            else {
                s += map.get(a[i]);
                ss+="";
            }

        }
        s1+=ss+" ";
        File file1 = new File("E:\\one-master\\data\\new\\1.wkt");
        FileWriter fw1 = null;
        //System.out.println(s);
        //System.exit(1);
        FileWriter fw= null;
        File file = new File("E:\\one-master\\data\\new\\ferryroute.wkt");
        try{
            if(!file.exists()){
                file.mkdir();
            }
            if(!file1.exists()){
                file1.mkdir();
            }
            fw1=new FileWriter(file1);
            fw1.write("LINESTRING ("+s1+"650 -1100)");
            fw1.flush();

            fw=new FileWriter(file);
            fw.write("LINESTRING ("+s+")");
            fw.flush();
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        //MapRouteMovement.allRoutes=MapRoute.readRoutes("E:\\one-master\\data\\new\\ferryroute.wkt",1,MapBasedMovement.getMap());
    }




    public static void main(String[] args){
        ChangeTsp changeTsp = new ChangeTsp();
        changeTsp.changRoute(changeTsp.splitTsp("0-->4-->3-->1-->2-->0"));

    }

}
