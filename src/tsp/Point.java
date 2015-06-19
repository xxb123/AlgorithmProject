package tsp;

public class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double distanceTo(Point point) {
        double xDistance = Math.abs(getX() - point.getX());
        double yDistance = Math.abs(getY() - point.getY());
        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return distance;
    }

    public String getPos() {
        return String.format("(%f, %f)", getX(), getY());
    }

}
