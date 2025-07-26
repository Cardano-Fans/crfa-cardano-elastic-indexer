package com.cardano.indexer.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "cardano.node")
public interface CardanoConfig {

    String host();

    int port();

    String network();

    long protocolMagic();

}