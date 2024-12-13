package org.example.Laboratory_work_3.LeaderElection;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RaftLeaderElection {
    private static final int ELECTION_TIMEOUT = 1500;
    private static final int HEARTBEAT_INTERVAL = 1000;
    private static final int NUM_NODES = 5;

    private static final Map<Integer, Node> nodes = new HashMap<>();
    private static final AtomicBoolean isRunning = new AtomicBoolean(true);
    private static final Logger LOGGER = Logger.getLogger(RaftLeaderElection.class.getName());

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_NODES);

        for (int i = 0; i < NUM_NODES; i++) {
            Node node = new Node(i);
            nodes.put(i, node);
            executor.submit(node);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Simulation interrupted", e);
        }

        isRunning.set(false);
        executor.shutdownNow();
    }

    static class Node implements Runnable {
        private enum NodeState {
            FOLLOWER, CANDIDATE, LEADER
        }

        private enum MessageType {
            HEARTBEAT, VOTE_REQUEST, VOTE_GRANTED
        }

        private final int id;
        private NodeState state;
        private int currentTerm;
        private Integer votedFor;
        private int votesReceived;
        private long lastHeartbeatTime;
        private DatagramSocket socket;
        private final Map<Integer, Boolean> votes;
        private static final Logger LOGGER = Logger.getLogger(Node.class.getName());

        public Node(int id) {
            this.id = id;
            this.state = NodeState.FOLLOWER;
            this.currentTerm = 0;
            this.votedFor = null;
            this.votesReceived = 0;
            this.votes = new ConcurrentHashMap<>();

            try {
                this.socket = new DatagramSocket(5000 + id);
                socket.setSoTimeout(100);
            } catch (SocketException e) {
                LOGGER.log(Level.SEVERE, "Error creating socket for node " + id, e);
            }
        }

        @Override
        public void run() {
            LOGGER.info("Node " + id + " started as FOLLOWER.");
            lastHeartbeatTime = System.currentTimeMillis();

            while (isRunning.get()) {
                try {
                    checkElectionTimeout();
                    receiveMessages();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error in node " + id + " run loop", e);
                }
            }

            if (socket != null) {
                socket.close();
            }
        }

        private void checkElectionTimeout() {
            long currentTime = System.currentTimeMillis();
            if (state == NodeState.FOLLOWER &&
                    currentTime - lastHeartbeatTime > randomizedElectionTimeout()) {
                startElection();
            }
        }

        private long randomizedElectionTimeout() {
            Random random = new Random();
            return ELECTION_TIMEOUT + random.nextInt(ELECTION_TIMEOUT);
        }

        private void receiveMessages() {
            try {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                String[] parts = message.split(":");

                MessageType messageType = MessageType.valueOf(parts[0]);

                switch (messageType) {
                    case HEARTBEAT:
                        handleHeartbeat(message);
                        break;
                    case VOTE_REQUEST:
                        handleVoteRequest(message, packet.getAddress(), packet.getPort());
                        break;
                    case VOTE_GRANTED:
                        handleVoteGranted(message);
                        break;
                }
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error receiving message", e);
            }
        }

        private void startElection() {
            LOGGER.info("Node " + id + " starting election as CANDIDATE.");
            state = NodeState.CANDIDATE;
            currentTerm++;
            votedFor = id;
            votesReceived = 1;
            votes.clear();
            broadcastVoteRequest();
        }

        private void broadcastVoteRequest() {
            for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
                if (entry.getKey() != id) {
                    sendMessage(
                            "VOTE_REQUEST:" + currentTerm + ":" + id,
                            entry.getValue().getAddress(),
                            entry.getValue().getPort()
                    );
                }
            }
        }

        private void handleHeartbeat(String message) {
            String[] parts = message.split(":");
            int leaderTerm = Integer.parseInt(parts[1]);

            if (leaderTerm >= currentTerm) {
                LOGGER.info("Node " + id + " received heartbeat.");
                lastHeartbeatTime = System.currentTimeMillis();
                currentTerm = leaderTerm;

                if (state != NodeState.FOLLOWER) {
                    state = NodeState.FOLLOWER;
                    LOGGER.info("Node " + id + " became FOLLOWER after receiving heartbeat.");
                }
            }
        }

        private void handleVoteRequest(String message, InetAddress address, int port) {
            String[] parts = message.split(":");
            int candidateTerm = Integer.parseInt(parts[1]);
            int candidateId = Integer.parseInt(parts[2]);

            LOGGER.info("Node " + id + " received vote request from " + candidateId);

            if (candidateTerm > currentTerm) {
                currentTerm = candidateTerm;
                state = NodeState.FOLLOWER;
                votedFor = null;
            }

            boolean voteGranted = (candidateTerm == currentTerm) &&
                    (votedFor == null || votedFor == candidateId);

            if (voteGranted) {
                votedFor = candidateId;
                sendMessage("VOTE_GRANTED:" + currentTerm + ":" + id, address, port);
            }
        }

        private void handleVoteGranted(String message) {
            String[] parts = message.split(":");
            int receivedTerm = Integer.parseInt(parts[1]);
            int voterId = Integer.parseInt(parts[2]);

            if (state == NodeState.CANDIDATE && receivedTerm == currentTerm) {
                votes.put(voterId, true);
                votesReceived = votes.size() + 1; // +1 for self-vote

                if (votesReceived > NUM_NODES / 2) {
                    state = NodeState.LEADER;
                    LOGGER.info("Node " + id + " is elected as LEADER!");
                    sendHeartbeats();
                }
            }
        }

        private void sendHeartbeats() {
            while (state == NodeState.LEADER && isRunning.get()) {
                for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
                    if (entry.getKey() != id) {
                        sendMessage(
                                "HEARTBEAT:" + currentTerm + ":" + id,
                                entry.getValue().getAddress(),
                                entry.getValue().getPort()
                        );
                    }
                }

                try {
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Heartbeat interval interrupted", e);
                    break;
                }
            }
        }

        private void sendMessage(String message, InetAddress address, int port) {
            try {
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(packet);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error sending message", e);
            }
        }

        public InetAddress getAddress() {
            try {
                return InetAddress.getLocalHost();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error getting local address", e);
                return null;
            }
        }

        public int getPort() {
            return 5000 + id;
        }
    }
}