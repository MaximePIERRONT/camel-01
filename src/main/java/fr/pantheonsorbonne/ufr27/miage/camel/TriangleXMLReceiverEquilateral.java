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

public class TriangleXMLReceiverEquilateral extends TriangleXMLReceiver implements Runnable {

    public static final String QUEUE_MPI_EQUILATERAL = "queue/MPI_equilateral";
    @Inject
    ConnectionFactory connectionFactory;

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
                receiveMessageTriangle(context,QUEUE_MPI_EQUILATERAL);
            } catch (JMSException | IOException | JAXBException e) {
                e.printStackTrace();
            }
        }
    }
}
