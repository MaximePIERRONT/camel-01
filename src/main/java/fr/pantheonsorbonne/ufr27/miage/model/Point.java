package fr.pantheonsorbonne.ufr27.miage.model;

public class Point {

    public final float x;
    public final float y;

    public Point(){
        this.x = 0;
        this.y = 0;
    }

    public Point(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getDistance(Point p){
        float tmp1 = p.x- this.x;
        float tmp2 = p.y - this.y;
        return (float) Math.sqrt(tmp1*tmp1-tmp2*tmp2);
    }
}
