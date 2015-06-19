package sa;

import tsp.Point;

import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing {

    public List<Point> points;

    public SimulatedAnnealing() {
        points = new ArrayList<Point>();
    }

    //���� ���ܵĸ���
    public double acceptanceProbability(double energy, double newEnergy, double temperature) {
        // ����µĽ���������ţ��ͽ���
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

    //���ؽ��Ƶ� �������·��
    private Tour sa() {
        // ��ʼ���¶�
        double temp = 10000;

        // ��ȴ����
        double coolingRate = 0.003;

        // ��ʼ���Ľ������
        Tour currentSolution = new Tour(this);
        currentSolution.generateIndividual();


        System.out.println("Initial solution distance: " + currentSolution.getDistance());

        // ���õ�ǰΪ���ŵķ���
        Tour best = new Tour(currentSolution.getTour());

        // ѭ��֪��ϵͳ��ȴ
        while (temp > 1) {
            // ����һ���ھ�
            Tour newSolution = new Tour(currentSolution.getTour());

            // ��ȡ���λ��
            int tourPos1 = (int) (newSolution.tourSize() * Math.random());
            int tourPos2 = (int) (newSolution.tourSize() * Math.random());

            Point citySwap1 = newSolution.getPoint(tourPos1);
            Point citySwap2 = newSolution.getPoint(tourPos2);

            // ����
            newSolution.setPoint(tourPos2, citySwap1);
            newSolution.setPoint(tourPos1, citySwap2);

            // ����µĽ�������Ļ���
            double currentEnergy = currentSolution.getDistance();
            double neighbourEnergy = newSolution.getDistance();

            // �����Ƿ�����µ� ����
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                currentSolution = new Tour(newSolution.getTour());
            }

            // ��¼�ҵ������ŷ���
            if (currentSolution.getDistance() < best.getDistance()) {
                best = new Tour(currentSolution.getTour());
            }

            // ��ȴ
            temp *= 1 - coolingRate;
        }
        return best;
    }

}
