package com.cardano.indexer.service;

import com.bloxbean.cardano.yaci.core.model.Block;
import com.bloxbean.cardano.yaci.core.model.Era;
import com.bloxbean.cardano.yaci.helper.model.Transaction;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
    public class TransactionProcessingService {

    @Inject
    ElasticsearchService elasticsearchService;

    public void processTransaction(Era era, Block block, Transaction transaction) {
        try {
            String txHash = transaction.getTxHash();

            Log.debug("Processing transaction: " + txHash);

            // Index the transaction
            elasticsearchService.indexTransaction(era, block, transaction);
            
        } catch (Exception e) {
            Log.error("Failed to process transaction", e);
        }
    }

    public void processTransaction(Era era, Block block, List<Transaction> transactions) {
        transactions.forEach(t -> processTransaction(era, block, t));
    }

}
