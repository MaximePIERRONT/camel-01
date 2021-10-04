package fr.pantheonsorbonne.ufr27.miage.model;

public class Triangle {

    private final Point[] points;

    public Triangle(Point a, Point b, Point c){
        this.points = new Point[]{a, b, c};
    }

    public Point[] getPoints() {
        return points;
    }

    public float getPerimeters(){
        return points[0].getDistance(points[1]) + points[1].getDistance(points[2]) + points[2].getDistance(points[0]);
    }

    public boolean isEquilateral(){
        if(points[0].getDistance(points[1]) == points[1].getDistance(points[2])){
            if(points[1].getDistance(points[2]) == points[2].getDistance(points[0])){
                return true;
            }
        }
        return false;
    }

}
