import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class DrawAdapter implements MouseMotionListener,MouseListener {

    public Graphics g;
    public int x1,y1,x2,y2;
    public Shape shape;
    public Data data = new Data();

    public DrawAdapter(){

    }

    public DrawAdapter(Graphics g1, Shape s){
        g = g1;
        shape = s;
    }

    public void mouseDragged(MouseEvent e){
        data.addEachPoint(e.getPoint());
        x2 = e.getX();
        y2 = e.getY();
        g.drawLine(x1,y1,x2,y2);
        x1 = x2;
        y1 = y2;
    }

    public void mousePressed(MouseEvent e) {
        shape.increment();
        x1=e.getX();
        y1=e.getY();
        System.out.println("Start! "+x1+" "+y1);//TODO DELETE
        data.addStartPoint(e.getPoint());//加入tempList
        data.addEachPoint(e.getPoint());//记录文件
    }

    public void mouseReleased(MouseEvent e) {
        x2=e.getX();
        y2=e.getY();
        System.out.println("End! "+x2+" "+y2);//TODO DELETE
        data.addAllPointSeperator();//松了鼠标 在记录全点的文件里加个隔离符号
    }

    public void mouseMoved(MouseEvent e){

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

}
