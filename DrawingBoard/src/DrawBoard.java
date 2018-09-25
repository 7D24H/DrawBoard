import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.*;

public class DrawBoard extends JFrame{

    public static DrawBoard drawBoard = new DrawBoard();
    public static JPanel controlPanel = new JPanel();
    public static JPanel drawPanel = new JPanel();
    public static Shape shape = new Shape();//用来记录每次识别画图时的笔画数
    public static Data data = new Data();
    JButton startBtn = new JButton("Start");
    JButton endBtn = new JButton("End");
    JButton clearBtn = new JButton("Clear");

    public static void main(String args[]){
        DrawBoard db = new DrawBoard();
        db.initFrame();
    }

    public static DrawBoard getInstance() {
        return drawBoard;
    }

    /**组件的初始化**/
    //初始化真正画画的地方
    public void initDrawPanel(){
        this.add(drawPanel);
        drawPanel.setSize(1400,900);
        drawPanel.setBounds(0,0,1400,900);
        drawPanel.setBackground(Color.white);
        drawPanel.setLayout(null);
        drawPanel.setVisible(true);
        drawPanel.setCursor(new Cursor(1));//鼠标是十字

        //获取窗体的画笔（注意：必须得在设置窗体的可见性之后才可以拿到画笔)
        Graphics2D g= (Graphics2D)drawPanel.getGraphics();//拿到的当前窗体上的画笔
        BasicStroke bs = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g.setStroke(bs);//画笔粗细
        g.setColor(Color.DARK_GRAY);//画笔颜色

        //DrawListerer实现的是MouseListener接口，因为接口不能直接创建对象，监听器的使用
        DrawAdapter da = new DrawAdapter(g,shape);
        drawPanel.addMouseMotionListener(da);
        drawPanel.addMouseListener(da);

        System.out.println("initDrawPanel");
        repaint();//非常非常非常重要！！！会自己调用paint(Graphics g)方法！实现重绘以前的图的功能！
    }

    //初始化控制面板 即放按钮的地方
    public void initControlPanel(){
        startBtn.setFont(new Font("Courier New",Font.BOLD,19));
        endBtn.setFont(new Font("Courier New",Font.BOLD,18));
        clearBtn.setFont(new Font("Courier New",Font.BOLD,19));
        clearBtn.setForeground(Color.white);
        startBtn.setBackground(Color.pink);
        endBtn.setBackground(Color.pink);
        clearBtn.setBackground(Color.red);


        endBtn.setEnabled(false);//保证没按start就不能按END

        this.add(controlPanel);
        controlPanel.setSize(1400,100);
        controlPanel.setBounds(0,900,1400,100);
        controlPanel.setBackground(Color.GRAY);
        controlPanel.setVisible(true);

        controlPanel.setLayout(null);
        controlPanel.add(startBtn);
        controlPanel.add(endBtn);
        controlPanel.add(clearBtn);
        startBtn.setBounds(600,10,90,40);
        endBtn.setBounds(730,10,90,40);
        clearBtn.setBounds(1280,10,90,40);

        //增加按钮监听器
        startBtn.addActionListener(new startBtnListener());
        endBtn.addActionListener(new endBtnListener());
        clearBtn.addActionListener(new clearBtnListener());
    }

    //初始化窗体DrawBoard 即大框架
    public void initFrame(){
        //Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setResizable(false);//禁止最大化 禁止拉伸
        this.setSize(1400,1000);
        this.setTitle("DrawBoard");
        this.setDefaultCloseOperation(3); //这里其实是EXIT_ON_CLOSE 一个枚举值
        this.setLocationRelativeTo(null); //中间
        this.setVisible(true);
        this.setLayout(null);

        //调用包装起来的方法初始化Frame里的各Panel
        this.initDrawPanel();
        this.initControlPanel();

        repaint();
        System.out.println("intFrame repaint");

    }

    //画出原始的点
    public void repaintOriginalPoint(Graphics2D g){
        int xDeviation = 5;
        int yDeviation = 40;
        ArrayList<String[]> arrStr = data.getAllPoint();
        System.out.println("repaintOriginal "+arrStr.size());
        if(arrStr.size()>0){
            for(String[] line:arrStr){
                int x1 = Integer.parseInt(line[0].split(" ")[0]);//可以抽方法
                int y1 = Integer.parseInt(line[0].split(" ")[1]);
                int x2 = 0;
                int y2 = 0;
                for(int i=1;i<line.length;i++){
                    x2 = Integer.parseInt(line[i].split(" ")[0]);
                    y2 = Integer.parseInt(line[i].split(" ")[1]);
                    g.drawLine(x1 + xDeviation,y1 + yDeviation,x2 + xDeviation,y2 + yDeviation);
                    x1 = x2;
                    y1 = y2;
                }
            }
        }
    }

    //标出已有标识1.0（这里只是取到坐标和标签）
    public void repaintLabel(){
        ArrayList<String[]> centerLabel = data.getCenterAndLabel();
        if(centerLabel.size()>0){
            for(String[] cl:centerLabel){
                String[] center = cl[0].split(" ");//得到中点数组
                String label = cl[1];
                int x = Integer.parseInt(center[0]);
                int y = Integer.parseInt(center[1]);
                reAddLabel(label,x,y);
            }
        }
    }

    //标出已有标识2.0 真正添加到面板上
    public void reAddLabel(String name,int x,int y){
        int xDeviation = 0;
        int yDeviation = 0;
        JLabel jLabel = new JLabel(name);
        jLabel.setFont(new Font("Dialog",1,25));
        drawPanel.add(jLabel);
        jLabel.setBounds(x + xDeviation,y + yDeviation,getLabelWidth(name),30);
        drawPanel.revalidate();
    }

    //标出已有规范化线条
    public void repaintLine(Graphics2D g){
        int xDeviation = 5;
        int yDeviation = 40;
        g.setColor(Color.pink);
        ArrayList<String[]> arrStr = data.getStartPoint();
        if(arrStr.size()>0){
            for(String[] line:arrStr){
                int x0 = Integer.parseInt(line[0].split(" ")[0]);//可以抽方法
                int y0 = Integer.parseInt(line[0].split(" ")[1]);
                int x1 = x0;
                int y1 = y0;
                int x2 = 0;
                int y2 = 0;
                for(int i=1;i<line.length;i++){
                    x2 = Integer.parseInt(line[i].split(" ")[0]);
                    y2 = Integer.parseInt(line[i].split(" ")[1]);
                    g.drawLine(x1 + xDeviation,y1 + yDeviation,x2 + xDeviation,y2 + yDeviation);
                    x1 = x2;
                    y1 = y2;
                }
                if(x1!=x0&&y1!=y0){//圆的不重绘
                    g.drawLine(x1 + xDeviation,y1 + yDeviation,x0 + xDeviation,y0 + yDeviation);
                }

            }
        }
    }


    /** 重写paint函数 这个系统会自动调用 可以实现重绘和最大最小化窗口也不影响线条 **/
    public void paint(Graphics g){
        super.paint(g);//父类一定要调用

        Graphics2D g2 = (Graphics2D)g;  //g是Graphics对象
        BasicStroke bs = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(bs);
        g2.setColor(Color.DARK_GRAY);

        this.repaintOriginalPoint(g2);//重绘所有点
        this.repaintLine(g2);//重绘规范化线条
        this.repaintLabel();//重绘标签
    }


    /** 动态增加组件 **/
    //识别后加标签
    public void addLabel(String name){
        int a=0;
        int b=0;
        ArrayList<Integer> centerPoint = data.getCenterPoint();
        if(centerPoint.size()!=0){
            a = centerPoint.get(0);
            b = centerPoint.get(1);
            JLabel jLabel = new JLabel(name);
            jLabel.setFont(new Font("Dialog",1,25));
//            jLabel.setOpaque(false);//opaque不透明
//            jLabel.setBackground(new Color(255,0,0,1));
            drawPanel.add(jLabel);
            jLabel.setBounds(a,b,getLabelWidth(name),30);
            drawPanel.revalidate();
        }
    }

    public int getLabelWidth(String name){
        switch (name){
            case "Round":
                return 80;
            case "Triangle":
                return 100;
            case "Square":
                return 90;
            case "Star":
                return 50;
            case "Hexagon":
                return 110;
            case "Heptagon":
                return 115;
            case "Don't know":
                return 140;
            default:
                return 0;
        }
    }

    //识别后画出规整线条
    public void drawRegularShape(){
        ArrayList<Integer> tempX = data.getTempXStartList();
        ArrayList<Integer> tempY = data.getTempYStartList();

        Graphics2D g2= (Graphics2D)drawPanel.getGraphics();//拿到的当前窗体上的画笔
        BasicStroke bs = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(bs);//画笔粗细
        g2.setColor(Color.pink);//画笔颜色
//        drawPanel.paintComponents(g2);//

        if(tempX.size()>1){
            int x0 = tempX.get(0);
            int y0 = tempY.get(0);
            int x1=x0;
            int y1=y0;
            int x2=0;
            int y2=0;
            for(int i=1;i<=tempX.size()-1;i++){
                x2 = tempX.get(i);
                y2 = tempY.get(i);
                g2.drawLine(x1,y1,x2,y2);
                x1 = x2;
                y1 = y2;
            }
            g2.drawLine(x1,y1,x0,y0);
        }else if(tempX.size() == 1){ //圆
            System.out.println("Round!");
        }
    }


    /** 动态清空画板 **/
    public void cleanDrawPanel(){
        drawPanel.removeAll();
        drawPanel.repaint();
    }


    /** JButton 监听器**/
    //开始识别按钮
    class startBtnListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            shape.setZero(); //按了开始键 从新开始计算笔画数
            data.clearTempPoint();//清空记录开始点的临时数组
            endBtn.setEnabled(true);
        }
    }

    //结束识别按钮
    class endBtnListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(shape.getLineNum()>0){
                drawBoard.drawRegularShape();//画出识别出的正规图形
                drawBoard.addLabel(shape.getShapeName());//标出名称，且里面的getCenterPoint已经记录了标签到文件
                data.recordShapeName(shape.getShapeName());//记录标签名到文件
                data.recordStartPoint();//把正规图的起始点一口气记录到文件里（之前用的是tempList）
            }
            shape.setZero();
            data.clearTempPoint();//清空记录开始点的临时数组
            endBtn.setEnabled(false);//要等下次按了start才能恢复
        }
    }

    //清空按钮
    class clearBtnListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            drawBoard.cleanDrawPanel();
            data.removeAllData();
            data.clearTempPoint();
            shape.setZero();
        }
    }

}