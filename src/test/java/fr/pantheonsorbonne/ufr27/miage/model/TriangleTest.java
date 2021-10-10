package fr.pantheonsorbonne.ufr27.miage.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TriangleTest {


    public static final String PATHNAME = "target/data/triangle/";

    @Test
    public void TriangleXMLTest() throws JAXBException, IOException {
        Point point1 = new Point(0,0);
        Point point2 = new Point(0,2);
        Point point3 = new Point(3,2);
        Triangle triangle = new Triangle(point1,point2,point3);
        triangle.getXMLFile("src/test/resources/","triangle.xml");
        float test = triangle.getPerimeter();
        System.out.println(test);
    }

    

    @Test
    public void testProd(){
        File fileTest = new File(PATHNAME);
        File[] files = fileTest.listFiles();
    }



}
