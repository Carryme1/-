package util;


import report.DeliveredMessagesReport;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

    public class Tsp {

        private int cityNum;
        private float[][] distance;

        private int[] colable;
        private int[] row;
        private float[][] Buffer;

        public Tsp(int n,float[][] buffer) {
            cityNum = n;
           Buffer=buffer;
        }

        public void init(String filename) throws IOException {

            int[] x;
            int[] y;
            String strbuff;
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
                distance[i][i] = 0; // 对角线为0
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

//            for(int k=0;k<distance.length;k++)
//                for(int p=0;p<distance.length;p++)
//                {
//                    distance[k][p]=Buffer[k][p]*distance[k][p];
//                }
//
            for(int k=0;k<distance.length;k++)
                for(int p=0;p<distance[0].length;p++)
                {
                    if(p<4)
                        System.out.print(distance[k][p]+" ");
                    else
                        System.out.print(distance[k][p]+"\n");

                }

            distance[cityNum - 1][cityNum - 1] = 0;

            colable = new int[cityNum];
            colable[0] = 0;
            for (int i = 1; i < cityNum; i++) {
                colable[i] = 1;
            }

            row = new int[cityNum];
            for (int i = 0; i < cityNum; i++) {
                row[i] = 1;
            }

        }

        public String solve(){

            float[] temp = new float[cityNum];
            String path="0";

            float s=0;
            int i=0;
            int j=0;

            while(row[i]==1){

                for (int k = 0; k < cityNum; k++) {
                    temp[k] = distance[i][k];
                    //System.out.print(temp[k]+" ");
                }
                //System.out.println();

                j = selectmin(temp);

                row[i] = 0;
                colable[j] = 0;

                path+="-->" + j;
                //System.out.println(i + "-->" + j);
                //System.out.println(distance[i][j]);
                s = s + distance[i][j];
                i = j;
            }
            System.out.println("online ferryroute:" + path);
            System.out.println("总距离为:" + s);

           return path;
        }

        public int selectmin(float[] p){
            int j = 0, k = 0;
            float  m = p[0];
            while (colable[j] == 0) {
                j++;
                //System.out.print(j+" ");
                if(j>=cityNum){

                    m = p[0];
                    break;

                }
                else{
                    m = p[j];
                }
            }

            for (; j < cityNum; j++) {
                if (colable[j] == 1) {
                    if (m >= p[j]) {
                        m = p[j];
                        k = j;
                    }
                }
            }
            return k;
        }


        public void printinit() {
            System.out.println("print begin....");
            for (int i = 0; i < cityNum; i++) {
                for (int j = 0; j < cityNum; j++) {
                    System.out.print(distance[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println("print end....");
        }

        public static void main(String[] args) throws IOException {
            System.out.println("Start....");
            Tsp ts = new Tsp(6,DeliveredMessagesReport.buffer);
            ts.init("E:\\one-master\\util\\MapNode.txt");
            //ts.printinit();
            ts.solve();
        }

}
