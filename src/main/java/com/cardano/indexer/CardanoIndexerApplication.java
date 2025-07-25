package com.cardano.indexer;

import com.bloxbean.cardano.yaci.core.config.YaciConfig;
import com.cardano.indexer.service.CardanoNodeService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain
public class CardanoIndexerApplication implements QuarkusApplication {

    @Inject
    CardanoNodeService cardanoNodeService;

    @Override
    public int run(String... args) throws Exception {
        Log.info("Starting Cardano Elasticsearch Indexer");

        YaciConfig.INSTANCE.setReturnTxBodyCbor(true);
        
        // Add shutdown hook to gracefully stop the node service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Log.info("Shutdown signal received, stopping services...");
            cardanoNodeService.stop();
        }));
        
        try {
            // Start the Cardano node connection and chain sync
            cardanoNodeService.start();
            
            Log.info("Cardano Elasticsearch Indexer started successfully");
            
            // Keep the application running
            Quarkus.waitForExit();
            return 0;
            
        } catch (Exception e) {
            Log.error("Failed to start Cardano Elasticsearch Indexer", e);
            return 1;
        }
    }

    public static void main(String... args) {
        Quarkus.run(CardanoIndexerApplication.class, args);
    }
}