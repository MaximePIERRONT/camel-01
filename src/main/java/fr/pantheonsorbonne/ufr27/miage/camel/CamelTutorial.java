package fr.pantheonsorbonne.ufr27.miage.camel;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CamelTutorial extends RouteBuilder {

    @Override
    public void configure() throws Exception {


        from("jms:queue/prices").to("file:data/prices");

    }
}