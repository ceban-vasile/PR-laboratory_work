package org.example.Laboratory_work_3.LeaderElection;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class RaftLeaderElectionSimulation {
    private static final int NUM_NODES = 5;
    private static final int MAX_SIMULATION_TIME = 60;

    public static class RaftNode implements Runnable {
        private final int nodeId;
        private final Random random = new Random();
        private final AtomicInteger currentTerm = new AtomicInteger(0);
        private final AtomicBoolean isLeader = new AtomicBoolean(false);
        private final AtomicInteger votedFor = new AtomicInteger(-1);
        private final List<RaftNode> cluster;

        public RaftNode(int nodeId, List<RaftNode> cluster) {
            this.nodeId = nodeId;
            this.cluster = cluster;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(randomElectionTimeout());

                    if (!isLeader.get()) {
                        tryStartElection();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private long randomElectionTimeout() {
            return 1000 + random.nextInt(2000);
        }

        private synchronized void tryStartElection() {
            int newTerm = currentTerm.incrementAndGet();

            votedFor.set(nodeId);
            int votesReceived = 1; // Vote for self

            System.out.println("Node " + nodeId + " starting election for term " + newTerm);

            for (RaftNode otherNode : cluster) {
                if (otherNode.nodeId != this.nodeId) {
                    boolean vote = simulateVote(newTerm);
                    if (vote) {
                        votesReceived++;
                    }
                }
            }

            if (votesReceived > NUM_NODES / 2) {
                becomeLeader(newTerm);
            }
        }

        private boolean simulateVote(int term) {
            boolean vote = random.nextDouble() < 0.7;

            if (vote) {
                System.out.println("Node received vote from Node " + nodeId);
            }

            return vote;
        }

        private void becomeLeader(int term) {
            isLeader.set(true);
            System.out.println(">>> Node " + nodeId + " BECAME LEADER in term " + term + " <<<");

            try {
                Thread.sleep(5000 + random.nextInt(5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            isLeader.set(false);
            System.out.println("Node " + nodeId + " stepped down from leadership");
        }
    }

    public static void main(String[] args) throws InterruptedException {

        List<RaftNode> nodes = new ArrayList<>();

        for (int i = 0; i < NUM_NODES; i++) {
            RaftNode node = new RaftNode(i, nodes);
            nodes.add(node);
        }

        ExecutorService executor = Executors.newFixedThreadPool(NUM_NODES);
        for (RaftNode node : nodes) {
            executor.submit(node);
        }

        Thread.sleep(MAX_SIMULATION_TIME * 1000);

        executor.shutdownNow();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Simulation Complete");
    }
}