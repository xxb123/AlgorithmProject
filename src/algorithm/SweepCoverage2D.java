package algorithm;

import sa.SimulatedAnnealing;
import sa.Tour;
import tsp.Point;
import tsp.Tsp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Created by Theodore on 6/15/2015.
 */

public class SweepCoverage2D extends JPanel{
    static final double epsilon = 1e-5;
    static final int maxStep = 500;
    static final int maxOuterIter = 50;
    static final int CANVAS_SIZE = 600;
    static final int MAX_AREA = 500;
    Tsp t;
    SimulatedAnnealing s;
    List<List<Integer>> allPath;
    List<Tour> allSaPath;
    List<Point> points;
    int nPoints;
    int k;
    List<Color> myColors;
    List<List<Point>> bestClusters;
    List<Point> bestCenterList;
    

    public SweepCoverage2D(int n, int m) {
        this.points = new ArrayList<Point>();
        this.bestClusters = null;
        this.bestCenterList = null;

        this.nPoints = n;
        this.k = m;
        this.myColors = new ArrayList<Color>();
        Random rand = new Random();
        for (int i = 0; i < this.k; i++) {
            // Choose a color
            Color c = new Color(rand.nextInt(0xffffff));
            myColors.add(c);
        }
    }

    public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2) {

        double H = 10; // 箭头高度
        double L = 4; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];

        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        g2.drawLine(sx, sy, ex, ey);
        //
        GeneralPath triangle = new GeneralPath();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.closePath();
        //实心箭头
        g2.fill(triangle);
        //非实心箭头
        //g2.draw(triangle);

    }

    public static double[] rotateVec(int px, int py, double ang,
                                     boolean isChLen, double newLen) {

        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }



    // Get Euclid distance between two points
    double getDistance(Point p1, Point p2) {
        return Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +
                (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
    }

    // Get new center for a cluster of points
    Point getNewCenter(List<Point> clusterPoints) {
        double meanX = 0, meanY = 0;
        int sz = clusterPoints.size();

        for (Point p:clusterPoints) {
            meanX += p.getX();
            meanY += p.getY();
        }
        // The new coordinate is just the mean of all X and all Y
        return new Point(meanX / sz, meanY / sz);
    }

    // Get total distance from all points in a cluster to their corresponding cluster center
    // We use this to determine whether this is a better cluster partition (the smaller the better)
    double getTotalDistance(List<Point> centerList, List<List<Point>> clusters) {
        int k = centerList.size();
        double newDis = 0;
        for (int i = 0; i < k; i++) {
            Point center = centerList.get(i);
            List<Point> cluster = clusters.get(i);
            newDis += getDistanceSum(center, cluster);
        }
        return newDis;
    }

    // Get total distance from a cluster of points to their center.
    // We want to find a point with biggest sum of distances to all points in centerList currently.
    double getDistanceSum(Point p, List<Point> centerList) {
        double dis = 0;
        for (Point center : centerList) {
            dis += getDistance(p, center);
        }
        return dis;
    }

    TwoTuple<List<Point>, List<List<Point>>> runKMeans(int n, int m) {
        double totalDistance = Double.MAX_VALUE;
        int outerIter = 0;
        List<Point> bestCenterList = null;
        List<List<Point>> bestClusters = null;
        TwoTuple<List<Point>, List<List<Point>>> resultTwoTuple = new TwoTuple<List<Point>, List<List<Point>>>();


        while (outerIter < maxOuterIter) {
            System.out.printf("========================Out iteration %d===================== \n", outerIter);
            // Since we always choose the first point in points as first center, so we shuffle here to create
            // some randomness
            Collections.shuffle(points);

            List<List<Point>> clusters = new ArrayList<List<Point>>();
            for (int i = 0; i < k; i++) {
                clusters.add(new ArrayList<Point>());
            }
            List<Point> centerList = new ArrayList<Point>();

            // Pick first center
            Point firstCenter = points.get(0);
            centerList.add(firstCenter);
            int centerNum = 1;
            List<Point> pointsCopy = new ArrayList<Point>();
            // Add the rest points to pointsCopy
            for (int i = 1; i < nPoints; i++) {
                pointsCopy.add(points.get(i));
            }
            // Find the rest k-1 initial centers
            while (centerNum < k) {
                double maxDis = 0;
                int newCenterPointIndex = 0;
                for (int i = 0; i < pointsCopy.size(); i++) {
                    // Find point in the remaining set that has maximum distance sum to the centers in centerList
                    double dis = getDistanceSum(pointsCopy.get(i), centerList);
                    if (dis > maxDis)
                        newCenterPointIndex = i;
                }
                centerList.add(pointsCopy.get(newCenterPointIndex));
                pointsCopy.remove(newCenterPointIndex);
                centerNum++;
            }

            // Now we have initial centers which should be far from each other
            System.out.println("Initial centers:");
            for (int i = 0; i < k; i++) {
                System.out.printf("Center %d %s\n", i, centerList.get(i).getPos());
            }

            boolean isStable = false;
            int iter = 0;
            // We iterate until the center is stable or we hit max iteration
            while (!isStable && iter < maxStep) {
                for (int i = 0; i < k; i++) {
                    clusters.get(i).clear();
                }

                // Process each point to put them into clusters
                for (int i = 0; i < nPoints; i++) {
                    Point currentPoint = points.get(i);
                    int minCenterListIndex = 0;
                    double minDistance = getDistance(currentPoint, centerList.get(0));

                    // Calculate which center is the nearest to the point
                    for (int j = 1; j < k; j++) {
                        double distanceCenterJ = getDistance(currentPoint, centerList.get(j));
                        if (distanceCenterJ < minDistance) {
                            minCenterListIndex = j;
                            minDistance = distanceCenterJ;
                        }
                    }
                    // add the point to that cluster
                    clusters.get(minCenterListIndex).add(currentPoint);
                    System.out.printf("Point %s ------> %d\n", currentPoint.getPos(), minCenterListIndex);
                }


                boolean thereIsEmpty = false;
                for (int i = 0; i < k; i++) {
                    // If there is cluster doesn't possess any point
                    if (clusters.get(i).size() == 0)
                        thereIsEmpty = true;
                }
                // Then we don't have k clusters, we skip this iteration
                if (thereIsEmpty)
                    continue;


                isStable = true;
                List<Point> newCenters = new ArrayList<Point>();
                for (int j = 0; j < k; j++) {
                    Point newCenter = getNewCenter(clusters.get(j));
                    newCenters.add(newCenter);
                    if (getDistance(newCenter, centerList.get(j)) > epsilon) {
                        // The center is still moving
                        isStable = false;
                    }
                }

                if (!isStable) {
                    iter++;
                    System.out.printf("After iter: %d, centers\n", iter);
                    for (int j = 0; j < k; j++) {
                        Point p = newCenters.get(j);
                        System.out.printf("%s\n", p.getPos());
                        // update center
                        centerList.set(j, p);
                    }
                }
            }
            // We exit because we reached the maxStep
            if (!isStable) {
                System.out.println("Exit due to hitting max iteration, and we assume it is quite close");
            }
            // If the new cluster partition is "closer"
            if (totalDistance > getTotalDistance(centerList, clusters)) {
                // We set it as the current best choice
                bestCenterList = centerList;
                bestClusters = clusters;
                totalDistance = getTotalDistance(bestCenterList, bestClusters);
            }
            outerIter++;
        }

        resultTwoTuple.setFirst(bestCenterList);
        resultTwoTuple.setSecond(bestClusters);
        return resultTwoTuple;



    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // We initial state, we just draw them as black cycles
        if (bestCenterList == null && bestClusters == null) {
            for (Point p : points) {
                g2d.setColor(Color.BLACK);
                g2d.draw(new Ellipse2D.Double(p.getX(), p.getY(), 5, 5));
            }
        }
        if(allPath != null){
            // Now we have our clusters, we draw them with different colors
        	Point p;
            for (int i = 0; i < k; i++) {
                List<Point> cluster = bestClusters.get(i);
                for (int j = 0; j < cluster.size(); j++) {
                	p = cluster.get(j);
                    g2d.setColor(myColors.get(i));
            //        g2d.drawString(j+"", (float)(p.getX()+6), (float)(p.getY()+6));
                    g2d.draw(new Ellipse2D.Double(p.getX(), p.getY(), 5, 5));
                }
            }

            for (int i = 0; i < allPath.size(); i++){
            	List<Point> cluster = bestClusters.get(i);
            	for(int j = 0; j < allPath.get(i).size()-1; j++){
            		int x1 = Math.round((float)(cluster.get(allPath.get(i).get(j)).getX()));
            		int y1 = Math.round((float)(cluster.get(allPath.get(i).get(j)).getY()));
            		int x2 = Math.round((float)(cluster.get(allPath.get(i).get(j+1)).getX()));
            		int y2 = Math.round((float)(cluster.get(allPath.get(i).get(j+1)).getY()));
            		g2d.setColor(myColors.get(i));
            		drawAL(x1, y1, x2, y2,g2d);
            	}
            }
        }
        if (allSaPath != null) {
            Point p;

            for (int i = 0; i < k; i++) {
                List<Point> cluster = bestClusters.get(i);
                for (int j = 0; j < cluster.size(); j++) {
                    p = cluster.get(j);
                    g2d.setColor(myColors.get(i));
                    //           g2d.drawString(j+"", (float)(p.getX()+6), (float)(p.getY()+6));
                    g2d.draw(new Ellipse2D.Double(p.getX(), p.getY(), 5, 5));
                }
            }

            List<Point> path;

            int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            int i = 0, j = 0;

           
            for (i = 0; i < allSaPath.size(); i++) {
                 path = allSaPath.get(i).getTour();
                 for (j = 0; j < path.size() - 1; j++) {
                     x1 = Math.round((float) (path.get(j).getX()));
                     y1 = Math.round((float) (path.get(j).getY()));
                     x2 = Math.round((float) (path.get(j + 1).getX()));
                     y2 = Math.round((float) (path.get(j + 1).getY()));
                     g2d.setColor(myColors.get(i));
                     drawAL(x1, y1, x2, y2, g2d);
                 }

                 x1 = Math.round((float) (path.get(j).getX()));
                 y1 = Math.round((float) (path.get(j).getY()));
                 x2 = Math.round((float) (path.get(0).getX()));
                 y2 = Math.round((float) (path.get(0).getY()));
                 drawAL(x1, y1, x2, y2, g2d);
             }
        }
    }

    public class DrawTsp extends JPanel {
        List<Point> points;
        List<Integer> path;

        public DrawTsp(List<Point> points, List<Integer> path) {
            this.points = points;
            this.path = path;
        }


        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                g2d.setColor(new Color(0x000000));
                g2d.draw(new Ellipse2D.Double(p.getX(), p.getY(), 5, 5));
                g2d.drawString(i + "", (float) (p.getX() + 6), (float) (p.getY() + 6));
            }
            g2d.setColor(Color.BLACK);

            for (int j = 0; j < path.size() - 1; j++) {
                int x1 = Math.round((float) (points.get(path.get(j)).getX()));
                int y1 = Math.round((float) (points.get(path.get(j)).getY()));
                int x2 = Math.round((float) (points.get(path.get(j + 1)).getX()));
                int y2 = Math.round((float) (points.get(path.get(j + 1)).getY()));
                g2d.setColor(Color.BLACK);
                drawAL(x1, y1, x2, y2, g2d);
            }
        }
    }

    public class DrawSa extends JPanel {
        List<Point> path;

        public DrawSa(List<Point> path) {
            this.path = path;
        }

        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                g2d.setColor(Color.BLACK);
                g2d.draw(new Ellipse2D.Double(p.getX(), p.getY(), 5, 5));
                g2d.drawString(i + "", (float) (p.getX() + 6), (float) (p.getY() + 6));
            }
            g2d.setColor(Color.BLACK);

            int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            int i = 0;

            g2d.setColor(Color.RED);
            g2d.draw(new Ellipse2D.Double(path.get(0).getX(), path.get(0).getY(), 5, 5));
            for (i = 0; i < path.size() - 1; i++) {
                x1 = Math.round((float) (path.get(i).getX()));
                y1 = Math.round((float) (path.get(i).getY()));
                x2 = Math.round((float) (path.get(i + 1).getX()));
                y2 = Math.round((float) (path.get(i + 1).getY()));
                g2d.setColor(Color.BLACK);
                drawAL(x1, y1, x2, y2, g2d);
            }

            x1 = Math.round((float) (path.get(i).getX()));
            y1 = Math.round((float) (path.get(i).getY()));
            x2 = Math.round((float) (path.get(0).getX()));
            y2 = Math.round((float) (path.get(0).getY()));
            drawAL(x1, y1, x2, y2, g2d);
        }
    }

    public static void main(String[] args) {
        int n = 0, m = 0;
        String method = "";
        // n is the number of targets, m is the number of sensors
        try {
            method = args[0];
            n = Integer.parseInt(args[1]);
            m = Integer.parseInt(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.printf("Usage: SweepCoverage2D n m\n");
            e.printStackTrace();
            System.exit(-1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        if (!(n > 0 && m > 0)) {
            System.err.println("n and m should be bigger than 0!");
            System.exit(-1);
        }
        if (method.equals("method1")) {
            /*TODO: Run TSP (DP or SA, depends on size of n) on it and split the segments.
              TODO: Give the length of the path of each sensor. Also draw the sensors on their starting points.                       
            */
        	SweepCoverage2D kmeans = new SweepCoverage2D(n,1);       	
        	Random rand = new Random();
            
            Tsp t = new Tsp();
            SimulatedAnnealing s = new SimulatedAnnealing();

            // Put nPoints points randomly on the canvas
            for (int i = 0; i < kmeans.nPoints; i++) {
                kmeans.points.add(new Point(rand.nextDouble() * MAX_AREA, rand.nextDouble() * MAX_AREA));
            }
            if (n <= 10) {
            	List<Integer> path;
            	path = t.computeTsp(kmeans.points);
            	SweepCoverage2D.DrawTsp dTsp = kmeans.new DrawTsp(kmeans.points,path);
            	JFrame frame_tsp = new JFrame("TSPFrame"); 
                frame_tsp.add(dTsp);
                frame_tsp.setBackground(new Color(0xffffff));
                frame_tsp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame_tsp.setSize(CANVAS_SIZE, CANVAS_SIZE);
                frame_tsp.setLocationRelativeTo(null);
                frame_tsp.setVisible(true);
            	
            } else {
            	Tour best;
            	best = s.computeSa(kmeans.points);
            	SweepCoverage2D.DrawSa dSa = kmeans.new DrawSa(best.getTour());
                
                JFrame frame_sa = new JFrame("SAFrame"); 
                frame_sa.add(dSa);
                frame_sa.setBackground(new Color(0xffffff));
                frame_sa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame_sa.setSize(CANVAS_SIZE, CANVAS_SIZE);
                frame_sa.setLocationRelativeTo(null);
                frame_sa.setVisible(true);
            }

        } else if (method.equals("method2")){
            // The second traditional method, we use kmeans to generate clusters first and run TSP on each cluster.
            SweepCoverage2D kmeans = new SweepCoverage2D(n, m);
            Random rand = new Random();
            
            Tsp t = new Tsp();
            SimulatedAnnealing s = new SimulatedAnnealing();

            // Put nPoints points randomly on the canvas
            for (int i = 0; i < kmeans.nPoints; i++) {
                kmeans.points.add(new Point(rand.nextDouble() * MAX_AREA, rand.nextDouble() * MAX_AREA));
            }

            JFrame frame_k = new JFrame("KMeansFrame");
            frame_k.add(kmeans);
            frame_k.setBackground(Color.WHITE);
            frame_k.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame_k.setSize(CANVAS_SIZE, CANVAS_SIZE);
            frame_k.setLocationRelativeTo(null);
            frame_k.setVisible(true);
            TwoTuple<List<Point>, List<List<Point>>> result = kmeans.runKMeans(n, m);

            System.out.println("leave kmeans");

            kmeans.bestCenterList=result.getFirst();
            kmeans.bestClusters=result.getSecond();

            // *** Which method to compute the path determines on the size of n and m.        
            if (n <= 10) {
                //TODO: If targets in each cluster is smaller than 10, we use DP to do TSP.
            	kmeans.allPath = new ArrayList<List<Integer>>();
            	 for (int j = 0; j < kmeans.k; j++) {
            		 kmeans.allPath.add(t.computeTsp(kmeans.bestClusters.get(j)));
            	}
            	kmeans.repaint();

            } else {
                kmeans.allSaPath = new ArrayList<Tour>();
                // The scale is too large for DP, we use SA
                for (int j = 0; j < kmeans.k; j++) {
                    kmeans.allSaPath.add(s.computeSa(kmeans.bestClusters.get(j)));
                }
                kmeans.repaint();
            }

        } else {
            System.err.println("First argument should be method1 or method2!");
            System.exit(-1);
        }
    }
}
