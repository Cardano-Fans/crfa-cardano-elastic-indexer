package com.cardano.indexer.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "elasticsearch")
public interface ElasticsearchConfig {
    String host();
    int port();
    
    Index index();
    
    interface Index {
        String transactions();
    }
}