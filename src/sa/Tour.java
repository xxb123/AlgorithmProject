package sa;

import tsp.Point;

import java.util.ArrayList;
import java.util.Collections;

public class Tour {

    SimulatedAnnealing simulatedAnnealing;
    // ���ֳ��е��б�
    private ArrayList<Point> tour = new ArrayList<Point>();
    // �������
    private double distance = 0;

    // ����һ���յ�·��
    public Tour(SimulatedAnnealing simulatedAnnealing) {
        this.simulatedAnnealing = simulatedAnnealing;
        for (int i = 0; i < simulatedAnnealing.points.size(); i++) {
            tour.add(null);
        }
    }

    // ����·��
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
        // ����Ĵ���
        Collections.shuffle(tour);
    }

    // ��ȡһ������
    public Point getPoint(int pointPosition) {
        return (Point) tour.get(pointPosition);
    }

    public void setPoint(int tourPosition, Point point) {
        tour.set(tourPosition, point);
        // ���¼������
        distance = 0;
    }

    // ��õ�ǰ����� �ܻ���
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

    // ��õ�ǰ·���г��е�����
    public int tourSize() {
        return tour.size();
    }

}
