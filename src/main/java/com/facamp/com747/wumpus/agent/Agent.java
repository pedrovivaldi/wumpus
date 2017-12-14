package com.facamp.com747.wumpus.agent;

import com.facamp.com747.wumpus.agent.AgentStateChangeListener;
import com.facamp.com747.wumpus.agent.Cave;
import com.facamp.com747.wumpus.agent.Dungeon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Agent {

    public static final Logger logger = LoggerFactory.getLogger(Agent.class);
    //Game id
    String sessionId;

    //Drools session
    KieContainer kc;
    StatefulKnowledgeSession ksession;
    Random r = new Random(111);

    //Agent attributes
    public int t;
    public int x;
    public int y;
    public boolean hasRock = true;
    public boolean alive = true;
    public boolean win = false;
    public double points = 0;

    public enum Level01GoalState {
        FIND_GOLD
    };

    public enum Level02GoalState {
        EXPLORE_NEIGHBORHOOD, PLAN_TRAVEL, TRAVEL_TO_SAFE, TRAVEL_TO_BEST_WUMPUS//, TRAVEL_TO_THROW_ROCK
    };

    public Level01GoalState state01 = Level01GoalState.FIND_GOLD;
    public Level02GoalState state02 = Level02GoalState.EXPLORE_NEIGHBORHOOD;

    AgentStateChangeListener agentStateChangeListener;
    Cave cave;

    long seed;

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.timeHasChanged();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.locationHasChanged();
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.locationHasChanged();
        }
    }

    public void debugView() {
        if (agentStateChangeListener != null) {
            agentStateChangeListener.locationHasChanged();
        }
    }

    @Override
    public String toString() {
        return "Agent [t=" + t + ", x=" + x + ", y=" + y + "]";
    }

    public Agent(long seed) {
        this.seed = seed;
        try {
            ksession = getKnowledgeBase().newStatefulKnowledgeSession();
            sessionId = createGame();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setProperty("drools.negatable", "off");
        }
    }

    private void logAllObjects() {
        logger.info("******");
        for (Object o : ksession.getObjects()) {
            logger.info(o.toString());
        }
        logger.info("******");
    }

    public void run() {
        Percept p = null;
        try {
            if (cave == null) {
                throw new Exception("Agent is not in a cave!");
            }
            //Insere conhecimento tácito
            Dungeon d = new Dungeon(0, 0);
            cave.addDungeon(d);
            ksession.insert(this);
            ksession.insert(d);
            ksession.insert(cave);

            p = percept();
            logger.info(p.toString());
            ksession.insert(p);
            ksession.fireAllRules();
            logAllObjects();

            if (this.win) {
                logger.info("WIN: " + points + " pts");
            } else {
                logger.info("LOSE: " + points + " pts");
            }

        } catch (Exception ex) {
            logger.error("ERROR: " + ex.getMessage(), ex);
        }

    }

    private static KnowledgeBase kbaseSingleton;

    private synchronized static KnowledgeBase getKnowledgeBase() throws IllegalArgumentException {
        if (kbaseSingleton == null) {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newInputStreamResource(Agent.class.getResourceAsStream("fly_1.drl")), ResourceType.DRL);
            KnowledgeBuilderErrors errors = kbuilder.getErrors();
            if (!errors.isEmpty()) {
                for (KnowledgeBuilderError error : errors) {
                    logger.error(String.format("KnowledgeBuilderError: %s", error.toString()));
                }
                throw new IllegalArgumentException("InferenceEngine cannot start with errors on the knowledge base. Please check the logs.");
            }
            kbaseSingleton = KnowledgeBaseFactory.newKnowledgeBase();
            kbaseSingleton.addKnowledgePackages(kbuilder.getKnowledgePackages());
        }

        return kbaseSingleton;
    }

    private String getJsonFromServer(URL url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            logger.debug("Output from Server .... \n");
            StringBuilder sb = new StringBuilder();
            String line;
            do {
                line = br.readLine();
                if (line != null) {
                    sb.append(line);
                }
            } while (line != null);
            conn.disconnect();
            line = sb.toString();
            logger.debug(String.format("Got: %s", line));

            return line;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String post(String json, URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
        connection.setRequestProperty("Accept", "*/*");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(json);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        do {
            line = in.readLine();
            if (line != null) {
                sb.append(line);
            }
        } while (line != null);
        logger.debug("REST Service Invoked Successfully..");
        in.close();
        return sb.toString();
    }

    class CreateGamePackage {

        String id;
    }

    private String createGame() throws MalformedURLException, IOException {
        String id = post("{email:pedro.cotta.vivaldi@gmail.com, seed:" + seed + "}", new URL("http://54.207.125.93:8080/wumpus/creategame"));
        CreateGamePackage pack = new Gson().fromJson(id, CreateGamePackage.class);
        logger.info("ID = {}", pack.id);
        return pack.id;
    }

    private Percept percept() throws MalformedURLException {
        String json = getJsonFromServer(new URL("http://54.207.125.93:8080/wumpus/percept?id=" + sessionId));
        logger.debug(json);
        Percept p = new Gson().fromJson(json, Percept.class);
        return p;
    }

    public Percept move(Direction d) throws IOException {
        String json = post("{id:" + sessionId + ", move:" + d + "}", new URL("http://54.207.125.93:8080/wumpus/action"));
        logger.debug(json);
        Percept p = new Gson().fromJson(json, Percept.class);
        return p;
    }

    public Percept shoot(Direction d) throws IOException {
        String json = post("{id:" + sessionId + ", fire:" + d + "}", new URL("http://54.207.125.93:8080/wumpus/action"));
        logger.debug(json);
        Percept p = new Gson().fromJson(json, Percept.class);
        return p;
    }

    public Percept grab() throws IOException {
        String json = post("{id:" + sessionId + ", grab:true}", new URL("http://54.207.125.93:8080/wumpus/action"));
        logger.debug(json);
        Percept p = new Gson().fromJson(json, Percept.class);
        return p;
    }

    public Percept throwRock(Direction d) throws IOException {
        String json = post("{id:" + sessionId + ", throwRock:" + d + "}", new URL("http://54.207.125.93:8080/wumpus/action"));
        logger.debug(json);
        Percept p = new Gson().fromJson(json, Percept.class);
        return p;
    }

    public Percept grabRock() throws IOException {
        String json = post("{id:" + sessionId + ", grabRock:true}", new URL("http://54.207.125.93:8080/wumpus/action"));
        logger.debug(json);
        Percept p = new Gson().fromJson(json, Percept.class);
        return p;
    }

    public Direction chooseRandomDirection() {
        Direction[] d = {Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST};
        int i = r.nextInt(4);
        logger.info("RANDOM: {}", i);
        return d[i];
    }

    public Level01GoalState getState01() {
        return state01;
    }

    public void setState01(Level01GoalState state01) {
        this.state01 = state01;
    }

    public Level02GoalState getState02() {
        return state02;
    }

    public void setState02(Level02GoalState state02) {
        this.state02 = state02;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.goalHasChanged();
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.stateHasChanged();
        };
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.stateHasChanged();
        }
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
        if (agentStateChangeListener != null) {
            agentStateChangeListener.stateHasChanged();
        };
    }

    public AgentStateChangeListener getAgentStateChangeListener() {
        return agentStateChangeListener;
    }

    public void setAgentStateChangeListener(AgentStateChangeListener agentStateChangeListener) {
        this.agentStateChangeListener = agentStateChangeListener;
    }

    public Cave getCave() {
        return cave;
    }

    public void setCave(Cave cave) {
        this.cave = cave;
    }

    public boolean isHasRock() {
        return hasRock;
    }

    public void setHasRock(boolean hasRock) {
        this.hasRock = hasRock;
    }

}
