package com.cardano.indexer.service;

import com.bloxbean.cardano.yaci.core.common.NetworkType;
import com.bloxbean.cardano.yaci.helper.reactive.BlockStreamer;
import com.cardano.indexer.config.CardanoConfig;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import reactor.core.Disposable;

@ApplicationScoped
public class CardanoNodeService {

    @Inject
    CardanoConfig cardanoConfig;

    @Inject 
    TransactionProcessingService transactionProcessingService;

    private BlockStreamer streamer;
    private Disposable subscription;

    @PostConstruct
    public void start() {
        Log.info("Starting Cardano node connection");
        
        try {
            // Create block streamer from latest point
            streamer = BlockStreamer.fromLatest(NetworkType.MAINNET);
            
            // Subscribe to the block stream
            subscription = streamer.stream().subscribe(
                block -> {
                    Log.info("Received Block >> " + block.getHeader().getHeaderBody().getBlockNumber());
                    Log.info("Total # of Txns >> " + block.getTransactionBodies().size());
                    
                    // Process each transaction in the block
                    if (block.getTransactionBodies() != null) {
                        block.getTransactionBodies().forEach(txBody -> {
                            try {
                                if (txBody.getCbor() == null) {
                                    Log.warn("Transaction body is null, skipping processing");
                                    return;
                                }

                                transactionProcessingService.processTransaction(block.getEra(), txBody, block.getHeader().getHeaderBody());
                            } catch (Exception e) {
                                Log.error("Failed to process transaction in block " + 
                                         block.getHeader().getHeaderBody().getBlockNumber(), e);
                            }
                        });
                    }
                },
                error -> {
                    Log.error("Error in block stream", error);
                },
                () -> {
                    Log.info("Block stream completed");
                }
            );
            
            Log.info("Cardano node service started successfully");
            
        } catch (Exception e) {
            Log.error("Failed to start Cardano node connection", e);
            throw new RuntimeException("Failed to connect to Cardano node", e);
        }
    }

    @PreDestroy
    public void stop() {
        Log.info("Stopping Cardano node connection");
        
        try {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
            if (streamer != null) {
                streamer.shutdown();
            }
        } catch (Exception e) {
            Log.warn("Error during Cardano node service shutdown", e);
        }
    }
}