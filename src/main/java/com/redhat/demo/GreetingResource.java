package com.redhat.demo;

import java.text.SimpleDateFormat;
import java.time.Duration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;

@Path("/hello")
public class GreetingResource {

    @ConfigProperty(name = "greeting.message", defaultValue = "Hey")
    String msg;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive - " + msg;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    @Path("/stream/{frequency}/{name}")
    public Multi<String> greetingAsStream(int frequency, String name) {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(frequency))
                .onItem()
                .transform(n -> String.format("Hello %s - %s", name,
                        new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())))
                .log()
                .invoke(() -> {
                    try {
                        Thread.sleep(100); // Simulate processing delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .select()
                .first(Duration.ofSeconds(10));
    }
}