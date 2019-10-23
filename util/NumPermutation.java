package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumPermutation {
    public List<int []> list = new ArrayList<>();
    static int count;
    public void perm(int[] nums, int start, int len) {
        //判断递归出口，当start == len - 1时，也就是要做的全排列只有一个值 ，那么就可以输出了
        int[] num =new int[nums.length];
        if(start == len - 1) {
            count++;
            System.out.println(Arrays.toString(nums));
            for(int j= 0;j<nums.length;j++)
                num[j] = nums[j];
            list.add(num);
        }else {
            /*
             * 没有到递归出口时，这一段代码是关键
             * for循环模拟的是：
             * { r1, perm(p1) } + { r2, perm(p2) } + {r3, perm(p3) } + …… + {rn, perm(pn) }
             * 从r1, r2, r3 一直到 rn 作为第一位，求剩下的全排列
             */
            for(int i = start; i < len; i++) {
                swap(nums, start, i);//通过交换，依次将每个数放在第一位
                perm(nums, start + 1, len);//递归调用
                swap(nums, start, i);//交换回来，保证原数组不会变，以进行下一轮全排列
            }
        }
    }

    //交换数组中的两个值
    public void swap(int[] nums, int i, int j) {
        int tem = nums[i];
        nums[i] = nums[j];
        nums[j] = tem;
    }

    float[][] distance = new float[6][6];
    public float[][] getDistance(String filename,int cityNum) {
        int[] x;
        int[] y;

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

    public float getSum(int[] nums,float[][] distance){
        int k = nums[0];
        int p= nums[nums.length-1];
        float sta=distance[0][k-1];
        float end=distance[p-1][0];
        float sum=0;
        for(int i=0; i<nums.length-1;i++){
            sum+=distance[nums[i]-1][nums[i+1]-1];
        }
        return sta+sum+end;
    }

    public List<Float> listSum =new ArrayList<>();
    public void getIndex(){
        for(int[] nums:list) {
            listSum.add(getSum(nums,distance));
        }
    }

    public int getMaxofList(){
        float b=listSum.get(0);
        int key = 0;
        for(int i= 1;i<listSum.size();i++){
            if(listSum.get(i)<b) {
                b = listSum.get(i);
                key = i;
            }
        }
        return key;
    }




    public static void main(String[] args){
        NumPermutation numPermutation = new NumPermutation();
        int[] nums = {2, 3, 4, 5};
        numPermutation.perm(nums,0,4);
        numPermutation.getDistance("E:\\one-master\\util\\MapNode.txt",5);
        numPermutation.getIndex();
        System.out.println("穷举次数："+count++);
        int[] re = numPermutation.list.get(numPermutation.getMaxofList());
        System.out.println("TSP path:"+Arrays.toString(re));
        System.out.println("路径长度："+numPermutation.getSum(re,numPermutation.distance));
    }

}
