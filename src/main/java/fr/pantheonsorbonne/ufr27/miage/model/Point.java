package fr.pantheonsorbonne.ufr27.miage.model;

public class Point {

    private final float x;
    private final float y;

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDistance(Point p){
        float tmp1 = p.getX() - this.x;
        float tmp2 = p.getY() - this.y;
        return (float) Math.sqrt(tmp1*tmp1-tmp2*tmp2);
    }
}
