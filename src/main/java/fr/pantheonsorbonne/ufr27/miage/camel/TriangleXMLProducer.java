package fr.pantheonsorbonne.ufr27.miage.camel;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import org.apache.commons.io.FilenameUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Session;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TriangleXMLProducer implements Runnable {
    public static final String PATHNAME = "/target/data/";

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
        scheduler.scheduleAtFixedRate(this, 0L, 5L, TimeUnit.SECONDS);
    }

    //cette méthode est appellées lorsque quarkus s'arrète
    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    //cette méthode est exécutée en boucle par le scheduler
    @Override
    public void run() {


        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            //on crée un producteur et on y envoie un message dans une nouvelle queue "prices"
            //le message est     une chaine de caractères, contenant un entier tiré aléatoirement entre 1 et 100.
            context.createProducer().send(context.createQueue("queue/prices"), Integer.toString(random.nextInt(100)));
        }
    }
}
