package sa;

import tsp.Point;

import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing {

    public List<Point> points;

    public SimulatedAnnealing() {
        points = new ArrayList<Point>();
    }

    //计算 接受的概率
    public double acceptanceProbability(double energy, double newEnergy, double temperature) {
        // 如果新的解决方案较优，就接受
        if (newEnergy < energy) {
            return 1.0;
        }
        return Math.exp((energy - newEnergy) / temperature);
    }

 /*   public static void main(String[] args) {

    	SimulatedAnnealing s= new SimulatedAnnealing();    	
    	List<Point> points = new ArrayList<Point>();
    	
    	Point point = new Point(0, 0);
    	points.add(point);
    	Point point2 = new Point(0, 1);
    	points.add(point2);
    	Point point3 = new Point(1, 1);
    	points.add(point3);
	    Point point4 = new Point(1, 0);
	    points.add(point4);
	    
	    	    
	    s.computeSa(points);
    }*/

    public Tour computeSa(List<Point> points) {
        this.points = points;
        Tour best = this.sa();

        System.out.println("Final solution distance: " + best.getDistance());
        
    /*    for(int i = 0; i < best.getTour().size(); i++)
        {
        	Point p = (Point)best.getTour().get(i);
        	System.out.print("(" + p.getX() + "," + p.getY() + ") ");
        }*/

        return best;
    }

    //返回近似的 最佳旅行路径
    private Tour sa() {
        // 初始化温度
        double temp = 10000;

        // 冷却概率
        double coolingRate = 0.003;

        // 初始化的解决方案
        Tour currentSolution = new Tour(this);
        currentSolution.generateIndividual();


        System.out.println("Initial solution distance: " + currentSolution.getDistance());

        // 设置当前为最优的方案
        Tour best = new Tour(currentSolution.getTour());

        // 循环知道系统冷却
        while (temp > 1) {
            // 生成一个邻居
            Tour newSolution = new Tour(currentSolution.getTour());

            // 获取随机位置
            int tourPos1 = (int) (newSolution.tourSize() * Math.random());
            int tourPos2 = (int) (newSolution.tourSize() * Math.random());

            Point citySwap1 = newSolution.getPoint(tourPos1);
            Point citySwap2 = newSolution.getPoint(tourPos2);

            // 交换
            newSolution.setPoint(tourPos2, citySwap1);
            newSolution.setPoint(tourPos1, citySwap2);

            // 获得新的解决方案的花费
            double currentEnergy = currentSolution.getDistance();
            double neighbourEnergy = newSolution.getDistance();

            // 决定是否接受新的 方案
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                currentSolution = new Tour(newSolution.getTour());
            }

            // 记录找到的最优方案
            if (currentSolution.getDistance() < best.getDistance()) {
                best = new Tour(currentSolution.getTour());
            }

            // 冷却
            temp *= 1 - coolingRate;
        }
        return best;
    }

}
