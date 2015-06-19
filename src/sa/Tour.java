package sa;

import tsp.Point;

import java.util.ArrayList;
import java.util.Collections;

public class Tour {

    SimulatedAnnealing simulatedAnnealing;
    // 保持城市的列表
    private ArrayList<Point> tour = new ArrayList<Point>();
    // 缓存距离
    private double distance = 0;

    // 生成一个空的路径
    public Tour(SimulatedAnnealing simulatedAnnealing) {
        this.simulatedAnnealing = simulatedAnnealing;
        for (int i = 0; i < simulatedAnnealing.points.size(); i++) {
            tour.add(null);
        }
    }

    // 复杂路径
    public Tour(ArrayList<Point> tour) {
        this.tour = (ArrayList<Point>) tour.clone();
    }

    public ArrayList getTour() {
        return tour;
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int pointIndex = 0; pointIndex < simulatedAnnealing.points.size(); pointIndex++) {
            setPoint(pointIndex, simulatedAnnealing.points.get(pointIndex));
        }
        // 随机的打乱
        Collections.shuffle(tour);
    }

    // 获取一个城市
    public Point getPoint(int pointPosition) {
        return (Point) tour.get(pointPosition);
    }

    public void setPoint(int tourPosition, Point point) {
        tour.set(tourPosition, point);
        // 重新计算距离
        distance = 0;
    }

    // 获得当前距离的 总花费
    public double getDistance() {
        if (distance == 0) {
            double tourDistance = 0;
            for (int pointIndex = 0; pointIndex < tourSize(); pointIndex++) {
                Point fromCity = getPoint(pointIndex);
                Point destinationCity;
                if (pointIndex + 1 < tourSize()) {
                    destinationCity = getPoint(pointIndex + 1);
                } else {
                    destinationCity = getPoint(0);
                }
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            distance = tourDistance;
        }
        return distance;
    }

    // 获得当前路径中城市的数量
    public int tourSize() {
        return tour.size();
    }

}
