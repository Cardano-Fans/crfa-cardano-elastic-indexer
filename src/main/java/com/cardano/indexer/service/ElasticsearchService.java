package com.cardano.indexer.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.cardano.indexer.config.ElasticsearchConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;


@ApplicationScoped
public class ElasticsearchService {

    @Inject
    ElasticsearchConfig elasticsearchConfig;

    private ElasticsearchClient client;
    private ElasticsearchTransport transport;
    private boolean connected = false;

    @PostConstruct
    public void init() {
        Log.info("Elasticsearch service initialized - connection will be established on first use");
    }
    
    private void ensureConnected() {
        if (connected) {
            return;
        }
        
        Log.info("Establishing Elasticsearch connection");
        
        try {
            // Create the low-level client
            RestClient restClient = RestClient.builder(
                new HttpHost(elasticsearchConfig.host(), elasticsearchConfig.port())
            ).build();

            // Create the transport with a Jackson mapper
            transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
            );

            // Create the API client
            client = new ElasticsearchClient(transport);
            
            // Index already created manually
            Log.info("Elasticsearch client ready");
            
            connected = true;
            Log.info("Elasticsearch client connected successfully");
            
        } catch (Exception e) {
            Log.warn("Failed to connect to Elasticsearch - indexing will be disabled", e);
            connected = false;
        }
    }

    @PreDestroy
    public void cleanup() {
        Log.info("Shutting down Elasticsearch client");
        try {
            if (transport != null) {
                transport.close();
            }
        } catch (Exception e) {
            Log.warn("Error during Elasticsearch client shutdown", e);
        }
    }

    
    public void indexTransaction(String transactionId, ObjectNode transactionDoc) {
        ensureConnected();
        
        if (!connected) {
            Log.debug("Elasticsearch not connected - skipping indexing of transaction: " + transactionId);
            return;
        }
        
        try {
            String indexName = elasticsearchConfig.index().transactions();
            
            // Use low-level client directly to avoid media-type issues  
            String endpoint = "/" + indexName + "/_doc/" + transactionId;
            String jsonString = transactionDoc.toString();
            
            org.elasticsearch.client.Request request = new org.elasticsearch.client.Request("PUT", endpoint);
            request.setEntity(new org.apache.http.nio.entity.NStringEntity(jsonString, org.apache.http.entity.ContentType.APPLICATION_JSON));
            
            RestClientTransport restTransport = (RestClientTransport) transport;
            RestClient lowLevelClient = restTransport.restClient();
            org.elasticsearch.client.Response response = lowLevelClient.performRequest(request);
            
            Log.debug("Indexed transaction: " + transactionId + " with status: " + response.getStatusLine().getStatusCode());
            
        } catch (Exception e) {
            Log.error("Failed to index transaction: " + transactionId, e);
            connected = false; // Reset connection on error
        }
    }
}