package fr.pantheonsorbonne.ufr27.miage.camel;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import fr.pantheonsorbonne.ufr27.miage.model.Point;
import fr.pantheonsorbonne.ufr27.miage.model.Triangle;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import org.apache.commons.io.FilenameUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TriangleXMLProducer implements Runnable {
    public static final String PATHNAME = "data/triangle/";
    public static final String PATH_EQUILATERAL = "data/triangle/equilateral/";
    public static final String PATH_OTHER = "data/triangle/autres/";
    public static final int NUMBER_POINTS_TRIANGLE = 3;

    //nous récupérons à l'aide de CDI une fabrique de connexions JMS
    @Inject
    ConnectionFactory connectionFactory;

    //générateur de nombre aléatoire
    private final Random random = new Random();

    //planificateur d'exécution de tache
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    //cette méthode est appellées lorsque l'initialisation de quarkus est terminée
    void onStart(@Observes StartupEvent ev) {
        //on planifie l'exécution de la méthode run() de cette classe:
        // - immédiatement (initialDelay=0
        // - toute les 5s (period = 5L, unit = secondes)
        scheduler.scheduleAtFixedRate(this, 0L, 10L, TimeUnit.SECONDS);
    }

    //cette méthode est appellées lorsque quarkus s'arrète
    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    //cette méthode est exécutée en boucle par le scheduler
    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            sendTriangles(context);
        }

    }


    private void sendTriangles(JMSContext context) {
        File fileTest = new File("data/triangle");
        File[] files = fileTest.listFiles();
        for (File file : files ) {
            Triangle triangle = createTriangleInstance(file);
            if (Objects.isNull(triangle)){
            } else if (triangle.isEquilateral()){
                String result = getXMLString(file, triangle, PATH_EQUILATERAL);
                TextMessage textMessage = context.createTextMessage(result);
                context.createProducer().send(context.createQueue("queue/MPI_equilateral"),textMessage);
            } else {
                String result = getXMLString(file, triangle, PATH_EQUILATERAL);
                TextMessage textMessage = context.createTextMessage(result);
                context.createProducer().send(context.createQueue("queue/MPI_autres"),textMessage);
            }
        }
    }

    private static String getXMLString(File file, Triangle triangle, String path) {
        String fileName = file.getName();
        final String fileNameXML = fileName.substring(0, fileName.length() - 4) + ".xml";
        try {
            triangle.getXMLFile(path, fileNameXML);
            return Files.readString(Path.of(path+fileNameXML));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Triangle createTriangleInstance(File file) {
        String fileName = file.getName();
        return getTriangle(file, fileName);
    }


    private Triangle getTriangle(File file, String fileName) {
        if (file.isFile() && FilenameUtils.getExtension(fileName).equals("csv")) {
            try {
                Point[] points = getPoints(fileName);
                return new Triangle(points[0], points[1], points[2]);
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Point[] getPoints(String fileName) throws IOException, CsvException {
        List<String[]> list = readCSV(fileName);
        Point[] points = new Point[NUMBER_POINTS_TRIANGLE];
        int i = 0;
        for (String[] stringTab : list) {
            points[i] = new Point(Float.parseFloat(stringTab[0]), Float.parseFloat((stringTab[1])));
            i++;
        }
        return points;
    }

    private List<String[]> readCSV(String fileName) throws IOException, CsvException {
        CSVReader csvReader = new CSVReader(new FileReader(PATHNAME + fileName));
        List<String[]> list = csvReader.readAll();
        csvReader.close();
        return list;
    }
}
