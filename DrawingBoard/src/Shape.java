public class Shape {
    public static int lineNum = 0;//static使得其他类可以共享这个数据
    public static Data data = new Data();

    public void setZero(){
        this.lineNum = 0;
    }

    public void increment(){
        this.lineNum++;
    }

    public String getShapeName(){
        switch (this.lineNum){
            case 1:
                return "Round";
            case 2:
                return "Paralled";
            case 3:
                return "Triangle";
            case 4:
                return "Square";
            case 5:
                return "Star";
            default:
                return "Don't know";
        }
    }

}
