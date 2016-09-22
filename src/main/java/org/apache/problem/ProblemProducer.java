package org.apache.problem;

import io.vertx.core.impl.Arguments;
import io.vertx.rxjava.core.Vertx;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by manishk on 9/22/16.
 */
@Component
public class ProblemProducer {

    @Autowired
    private Vertx vertx;

    @Autowired
    private IgniteClusterManager clusterManager;

    @Autowired
    private IgniteCacheEventsListener eventsListener;

    private Ignite ignite;

    private AtomicBoolean threadBlocked = new AtomicBoolean(false);

    @PostConstruct
    public void produceMyProblem() {

        ignite = Ignition.ignite(UUID.fromString(clusterManager.getNodeID()));
        ignite.events().remoteListen(null, eventsListener, IgniteCacheConfig.CACHE_EVENTS);
        ignite.events().localListen(eventsListener, IgniteCacheConfig.CACHE_EVENTS);

        vertx.setPeriodic(3000, handler -> {

            // Every one second we will check if there are more than 2 nodes in the cluster.
            // As soon as we have 2 nodes(node A & B), oldest node(A) will stop the current thread
            // execution for more than 5 seconds, which will cause oldest node(A) to be considered
            // segmented.
            // Once oldest node(A) is segmented, if we read from ignite cache, it's stuck forever.

            // Lets put something is cache, and ensure that we are able to read it.
            ignite.getOrCreateCache("someCache").put("someNumber", new Integer(1));
            Arguments.require(ignite.getOrCreateCache("someCache").get("someNumber") != null, "Data not found in cache");

            if (thereAreTwoNodesInCluster() && thisIsOldestNode() && threadIsNeverBlockedSoFar()) {

                // Lets block the thread for more than 5 seconds on the oldest node.
                try {
                    // Make current thread sleep which will cause segmentation.
                    Thread.sleep(6000);

                    // After thread is awaken, if we read the cache, it's causing the thread to be blocked forever.
                    ignite.getOrCreateCache("someCache").get("someNumber");

                    // If thread is blocked during cache read then this line never executed.
                    threadBlocked.set(false);
                } catch (InterruptedException e) {
                }
            }
        });

        vertx.setPeriodic(3000, handler -> {
            System.out.println("Thread on node " + ignite.cluster().forOldest().node().addresses() + (threadBlocked.get() ? " is blocked" : " is not blocked"));
        });
    }

    private boolean threadIsNeverBlockedSoFar() {
        if (threadBlocked.compareAndSet(false, true)) {

        }
        return false;
    }

    private boolean thisIsOldestNode() {
        return ignite.cluster().forOldest().node().isLocal();
    }

    private boolean thereAreTwoNodesInCluster() {
        return ignite.cluster().hostNames().size() > 1;
    }
}
