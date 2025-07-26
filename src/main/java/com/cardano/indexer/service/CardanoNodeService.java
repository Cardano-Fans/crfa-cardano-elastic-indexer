package com.cardano.indexer.service;

import com.bloxbean.cardano.yaci.core.model.Block;
import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.helper.BlockSync;
import com.bloxbean.cardano.yaci.helper.TipFinder;
import com.bloxbean.cardano.yaci.helper.listener.BlockChainDataListener;
import com.bloxbean.cardano.yaci.helper.model.Transaction;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CardanoNodeService implements BlockChainDataListener {

    @Inject
    BlockSync blockSync;

    @Inject
    TipFinder tipFinder;

    @Inject 
    TransactionProcessingService transactionProcessingService;

    @Override
    public void onBlock(Era era, Block block, List<Transaction> transactions) {
        transactionProcessingService.processTransaction(era, block, transactions);
    }

    @PostConstruct
    public void start() {
        Log.info("Starting Cardano ChainSync connection");
        
        try {
            // Start syncing from tip (latest blocks)
            blockSync.startSync(tipFinder.find().block().getPoint(), this);
            
            Log.info("Cardano ChainSync service started successfully");
            
        } catch (Exception e) {
            Log.error("Failed to start Cardano ChainSync connection", e);
            throw new RuntimeException("Failed to connect to Cardano node", e);
        }
    }

    @PreDestroy
    public void stop() {
        Log.info("Stopping Cardano ChainSync connection");
        
        try {
            if (blockSync != null) {
                blockSync.stop();
            }
        } catch (Exception e) {
            Log.warn("Error during Cardano ChainSync service shutdown", e);
        }
    }

}