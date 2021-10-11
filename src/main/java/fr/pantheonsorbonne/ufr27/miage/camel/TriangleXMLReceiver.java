package fr.pantheonsorbonne.ufr27.miage.camel;

import fr.pantheonsorbonne.ufr27.miage.model.Triangle;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.json.simple.JSONObject;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public abstract class TriangleXMLReceiver {

    public static final String DATA_RECEPTION_TRIANGLE = "data/reception/triangle";
    public static final String XML = ".xml";
    public static final String DATA_RECEPTION_PERIMETRE = "data/reception/perimetre";
    public static final String JSON = ".json";
    public static final String PERIMETRE = "perimetre";

    //générateur de nombre aléatoire
    private final Random random = new Random();

    protected Triangle getTriangleFromQueue(String triangleXMLString, int i) throws IOException, JAXBException {
        String fileName = DATA_RECEPTION_TRIANGLE + i + XML;
        Files.writeString(Path.of(fileName), triangleXMLString, StandardCharsets.UTF_8);
        File file = new File(fileName);
        JAXBContext jaxbContext = JAXBContext.newInstance(Triangle.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Triangle) jaxbUnmarshaller.unmarshal(file);
    }

    protected void receiveMessageTriangle(JMSContext context, String queue) throws JMSException, IOException, JAXBException {
        Message message = context.createConsumer(context.createQueue(queue)).receive();
        if (message instanceof TextMessage){
            String triangleXMLString = ((TextMessage) message).getText();
            int i = random.nextInt(1000);
            Triangle triangle = getTriangleFromQueue(triangleXMLString, i);
            JSONObject jsonObject = new JSONObject();
            float perimeter = triangle.getPerimeter();
            jsonObject.put(PERIMETRE, perimeter);
            FileWriter json = new FileWriter(DATA_RECEPTION_PERIMETRE + i + JSON);
            json.write(jsonObject.toJSONString());
            json.close();
        }
    }
}
