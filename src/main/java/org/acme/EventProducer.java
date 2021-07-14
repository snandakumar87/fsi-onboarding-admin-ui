package org.acme;

import java.util.*;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.util.JSONPObject;

import io.vertx.core.json.Json;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;

@Path("/events")
@ApplicationScoped
public class EventProducer {
    @Inject
   KafkaController kafkaController;

    @POST
    @Path("/txn-event/{custId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void postCase(String json,@javax.ws.rs.PathParam("custId") String customerId) {

        try {

            CustomerEvents customerEvents = new CustomerEvents();
            customerEvents.setCustId(customerId);
            customerEvents.setEvent(json);
            kafkaController.produce(customerId,json);


        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}
