import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Data {

    public static Data data = new Data();
    public static String allPoint = "data/allPoint.txt"; // 相对路径，如果没有则要建立一个新的output.txt文件
    public static String centerPointLabel = "data/centerPointLabel.txt";
    public static String startPoint = "data/startPoint.txt";
    public static ArrayList<Integer> tempXStartList = new ArrayList<Integer>();
    public static ArrayList<Integer> tempYStartList = new ArrayList<Integer>();

    /** 清空所有数据 **/
    public void removeAllData(){
        writeFile("",allPoint,false);
        writeFile("",centerPointLabel,false);
        writeFile("",startPoint,false);
    }

    /** 针对开始点的操作 **/
    //一次往列表里加一个
    public void addStartPoint(Point point){
        tempXStartList.add(point.x);
        tempYStartList.add(point.y);
    }

    //动态生成规范图形时要用到
    public ArrayList<Integer> getTempXStartList() {
        return tempXStartList;
    }
    public ArrayList<Integer> getTempYStartList() {
        return tempYStartList;
    }

    //确定下次重绘要出现时再把列表里的点一起记录到文件里
    public void recordStartPoint(){
        for(int i=0;i<tempXStartList.size();i++){
            writeFile(tempXStartList.get(i)+" ",startPoint,true);
            writeFile(tempYStartList.get(i)+",",startPoint,true);
        }
        writeFile(";\r\n",startPoint,true);
    }

    //清楚列表里的临时开始点
    public void clearTempPoint(){
        tempXStartList.clear();
        tempYStartList.clear();
    }

    //得到所有起始点 以便重绘标准图形
    public ArrayList<String[]> getStartPoint(){
        return formatData(startPoint);
    }


    /** 针对图形中点的操作 **/
    //得到中点“坐标” 且纪录文件
    public ArrayList<Integer> getCenterPoint(){
        ArrayList<Integer> centerPoint = new ArrayList<Integer>();
        int sumX = 0;
        int sumY = 0;
        int meanX = 0;
        int meanY = 0;
        int len = tempXStartList.size();
        if(len!=0){
            for(Integer x:tempXStartList){
                sumX+=x;
            }
            for(Integer y:tempYStartList){
                sumY+=y;
            }
            meanX =(int)(sumX/len);
            meanY = (int)(sumY/len);
            System.out.println("CENTER"+meanX+" "+meanY);

            //写入文件
            writeFile(meanX+" ",centerPointLabel,true);
            writeFile(meanY+",",centerPointLabel,true);

            centerPoint.add(meanX);
            centerPoint.add(meanY);
        }

        return centerPoint;
    }

    //记录中点的”名字“ 且记录文件
    public void recordShapeName(String name){
        writeFile(name,centerPointLabel,true);
        writeFile(";\r\n",centerPointLabel,true);
    }

    //得到中点坐标和标签名 以便重绘
    public ArrayList<String[]> getCenterAndLabel(){
        return formatData(centerPointLabel);
    }


    /** 针对所有点的操作 **/
    //把鼠标拖拽过程中的所有点都记录文件
    public void addEachPoint(Point point){
        int x = point.x;
        int y = point.y;

        writeFile(x+" ",allPoint,true);
        writeFile(y+",",allPoint,true);
    }

    //记录文件时以一条线为单位 结束一条线加一个间隔符
    public void addAllPointSeperator(){
        writeFile(";\r\n",allPoint,true);
    }

    //把文件里的所有拖拽点都得到 以便重绘
    public ArrayList<String[]> getAllPoint(){
        return formatData(allPoint);
    }


    /** 工具方法 **/
    //写文件
    private void writeFile(String s,String filePath,boolean isAppended){
        try {
            //清空原文件存的数据
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filePath,isAppended)));
            out.write(s);

            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    //读文件
    public String readFile(String filePath){
        String content = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String temp = "";

            while((temp = br.readLine())!=null){
                content+=temp;
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return content;
    }

    //把读取到的内容按照一样的容器规格返还 方便使用
    public ArrayList<String[]> formatData(String fileName){
        String s = readFile(fileName);
        ArrayList<String[]> arrStr = new ArrayList<String[]>();
        if(s!=""){ //确保文件非空
            String[] arr = s.split(";");//所有图形
            for(int i=0;i<arr.length;i++){
                arrStr.add(arr[i].split(","));//每个元素是中点坐标和标签的数组
            }
        }
        return arrStr;
    }

}
