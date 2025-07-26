package com.cardano.indexer.config;

import com.bloxbean.cardano.yaci.core.protocol.chainsync.messages.Point;
import com.bloxbean.cardano.yaci.helper.BlockSync;
import com.bloxbean.cardano.yaci.helper.TipFinder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class AppConfig {

    @Inject
    CardanoConfig cardanoConfig;

    @Produces
    @ApplicationScoped
    public TipFinder tipFinder() {
        TipFinder tipFinder = new TipFinder(
                cardanoConfig.host(),
                cardanoConfig.port(),
                Point.ORIGIN,
                cardanoConfig.protocolMagic()
        );
        tipFinder.start();

        return tipFinder;
    }

    @Produces
    @ApplicationScoped
    public BlockSync blockSync() {
        return new BlockSync(
                cardanoConfig.host(),
                cardanoConfig.port(),
                cardanoConfig.protocolMagic(),
                Point.ORIGIN
        );
    }

}
