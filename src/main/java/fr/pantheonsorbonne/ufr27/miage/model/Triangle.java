package fr.pantheonsorbonne.ufr27.miage.model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;

@XmlRootElement
public class Triangle {

    @XmlElement(name = "point")
    private final Point[] points;

    public Triangle(){
        points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0)};
    }

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
        if( (points[0].getDistance(points[1]) == points[1].getDistance(points[2]))
            && (points[1].getDistance(points[2]) == points[2].getDistance(points[0]))){
            return true;
        }
        return false;
    }

    public void getXMLFile(String path) throws JAXBException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(this, new FileOutputStream(path));
        } catch (JAXBException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
