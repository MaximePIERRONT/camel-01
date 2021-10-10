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

public class TriangleXMLReceiverAutres implements Runnable {

    public static final String QUEUE_MPI_AUTRES = "queue/MPI_autres";
    @Inject
    ConnectionFactory connectionFactory;

    //générateur de nombre aléatoire
    private final Random random = new Random();

    //indique si la classe est configurée pour recevoir les messages en boucle
    boolean running;

    //cette méthode démarre un nouveau thread exécutant l'instance en cours, jusqu'à ce que la variable running soit false.
    void onStart(@Observes StartupEvent ev) {
        running = true;
        new Thread(this).start();
    }


    void onStop(@Observes ShutdownEvent ev) {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
                //reçoit un message à partir de la queue queue/prices
                Message message = context.createConsumer(context.createQueue(QUEUE_MPI_AUTRES)).receive();
                if (message instanceof TextMessage){
                    String triangleXMLString = ((TextMessage) message).getText();
                    int i = random.nextInt(1000);
                    Triangle triangle = getTriangleFromQueue(triangleXMLString, i);
                    JSONObject jsonObject = new JSONObject();
                    float perimeter = triangle.getPerimeter();
                    jsonObject.put("perimetre", perimeter);
                    FileWriter json = new FileWriter("data/reception/perimetre" + i + ".json");
                    json.write(jsonObject.toJSONString());
                    json.close();
                }
            } catch (JMSException | IOException | JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    private Triangle getTriangleFromQueue(String triangleXMLString, int i) throws IOException, JAXBException {
        String fileName = "data/reception/triangle" + i + ".xml";
        Files.writeString(Path.of(fileName), triangleXMLString, StandardCharsets.UTF_8);
        File file = new File(fileName);
        JAXBContext jaxbContext = JAXBContext.newInstance(Triangle.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Triangle) jaxbUnmarshaller.unmarshal(file);
    }
}
