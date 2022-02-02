package org.acme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class TransactionConsumer {





    private static final Logger LOGGER = Logger.getLogger("TransactionCins");

//    @Incoming("txn-from-kafka")
//    public void receive(Transaction transaction) {
//        System.out.println("inside consumer");
//        LOGGER.infof("Received movie:", transaction.getId(), transaction.getAmount());
//        try {
//            if (transaction.getMerchantId().equals("MERCH0001") && transaction.getCountry().equals("IR")) {
//                LOGGER.info("message check failed");
//            } else if(transaction.getCountry().equals("US") && transaction.getMerchantId().equals("MERCH0002")){
//                LOGGER.info("message check failed");
//            } else {
//                System.out.println("publised");
//                final JsonObject jsonObject = JsonObject.mapFrom(transaction);
//
//            }
//        }catch (Exception e){
//            System.out.println("Lets handle it");
//        }
//    }
//
    @Incoming("txn-kafka")
    @Outgoing("txn")
    @Broadcast
    public String broadCastMessage(String transaction) throws JsonProcessingException {
        Map valueMap = new ObjectMapper().readValue(transaction, Map.class);
//        String resp = (String) valueMap.get("data");
//        return resp.replace("\\","");
        System.out.println(transaction);
        return transaction;

    }



}
