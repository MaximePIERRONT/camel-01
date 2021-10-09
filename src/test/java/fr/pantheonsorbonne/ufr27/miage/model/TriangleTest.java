package fr.pantheonsorbonne.ufr27.miage.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TriangleTest {

    public static final int NUMBER_POINTS_TRIANGLE = 3;
    public static final String PATHNAME = "target/data/triangle/";

    @Test
    public void TriangleXMLTest() throws JAXBException {
        Point point1 = new Point(0,0);
        Point point2 = new Point(0,2);
        Point point3 = new Point(3,2);
        Triangle triangle = new Triangle(point1,point2,point3);
        triangle.getXMLFile("src/test/resources/triangle.xml");
    }

    

    @Test
    public void testProd(){

        File fileTest = new File(PATHNAME);
        File[] files = fileTest.listFiles();

        List<Triangle> trianglesList = new ArrayList<>();
        for (File file : files ) {
            trianglesList.add(createTriangleInstance(file));
        }
        //sent to the queue
    }

    private Triangle createTriangleInstance(File file) {
        String fileName = file.getName();
        if (file.isFile() && FilenameUtils.getExtension(fileName).equals("csv")) {
            try {
                FileReader reader = new FileReader(PATHNAME + fileName);
                CSVReader csvReader = new CSVReader(reader);
                List<String[]> list = csvReader.readAll();
                reader.close();
                csvReader.close();
                Point[] points = new Point[NUMBER_POINTS_TRIANGLE];
                int i = 0;
                for (String[] stringTab : list) {
                    points[i] = new Point(Float.parseFloat(stringTab[0]), Float.parseFloat((stringTab[1])));
                    i++;
                }
                return new Triangle(points[0], points[1], points[2]));
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

}
