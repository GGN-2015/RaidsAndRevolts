package game.playerdata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import game.Constants;

public class PlayerMgr {
    public static final int PLAYER_CNT_MAX = 7;
    private Player[] players = new Player[PLAYER_CNT_MAX]; // you can only use 1 ~ 6 because 0 is for gaia
    private int[] alliance = new int [PLAYER_CNT_MAX];

    public PlayerMgr() {
        // by default all the player is null
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            players[i] = null;
            alliance[i] = i; // alliance[i] = i means player i is not allied with anyone
        }
    }

    public boolean finished() {
        HashSet<Integer> set = new HashSet<Integer>();
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                set.add(alliance[i]);
            }
        }
        return set.size() <= 1;
    }

    public List<Integer> getWinner() {
        List<Integer> winnerList = new ArrayList<Integer>();
        if(!finished()) {
            throw new IllegalStateException("The game is not finished yet");
        }
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                winnerList.add(i);
            }
        }
        return winnerList;
    }

    public Player getPlayerById(int playerId) {
        if(playerId < 1 || playerId >= PLAYER_CNT_MAX) {
            throw new IllegalArgumentException("Player id must be between 0 and " + PLAYER_CNT_MAX);
        }
        return players[playerId];
    }

    public void setPlayerById(int playerId, Player newPlayer, int newAlliance) {
        if(playerId < 1 || playerId >= PLAYER_CNT_MAX) {
            throw new IllegalArgumentException("Player id must be between 1 and " + PLAYER_CNT_MAX);
        }
        if(newAlliance < 1 || newAlliance >= PLAYER_CNT_MAX) {
            throw new IllegalArgumentException("Player id must be between 1 and " + PLAYER_CNT_MAX);
        }
        players[playerId] = newPlayer;
        alliance[playerId] = newAlliance;
    }

    public int getAllianceById(int playerId) {
        if(playerId == Constants.GAIA) {
            return Constants.GAIA; // gaia is always allied with gaia (team 0)
        }
        if(playerId < 1 || playerId >= PLAYER_CNT_MAX) {
            throw new IllegalArgumentException("Player id must be between 1 and " + PLAYER_CNT_MAX);
        }
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        return alliance[playerId];
    }

    public void setAllianceById(int playerId, int allianceId) {
        if(playerId < 1 || playerId >= PLAYER_CNT_MAX) {
            throw new IllegalArgumentException("Player id must be between 1 and " + PLAYER_CNT_MAX);
        }
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        alliance[playerId] = allianceId;
    }

    public List<Legion> getLegionsByPos(int posx, int posy) {
        List<Legion> legions = new ArrayList<Legion>();
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                legions.addAll(players[i].getLegionsByPos(posx, posy));
            }
        }
        return legions;
    }

    public Set<Integer> getAllianceByPos(int posx, int posy) { // check how many alliances are there at the position
        Set<Integer> alliances = new HashSet<Integer>();
        List<Legion> legions = getLegionsByPos(posx, posy);
        for(Legion legion : legions) {
            alliances.add(getAllianceById(legion.getOwner()));
        }
        return alliances;
    }

    public void untriggerAll() {
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                players[i].untriggerAll();
            }
        }
    }

    public void clearFailure() {
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            clearFailure(i); // check and clear for all player
        }
    }

    public int getEnemySizeOnPos(int posx, int posy, int playerId) {
        int enemySize = 0;
        List<Legion> legions = getLegionsByPos(posx, posy);
        for(Legion legion : legions) {
            if(getAllianceById(legion.getOwner()) != getAllianceById(playerId)) {
                enemySize += legion.getPopulation();
            }
        }
        return enemySize;
    }

    public void removeLegion(int owner, int id) {
        if(players[owner] == null) {
            throw new IllegalArgumentException("Player id " + owner + " is not exist");
        }
        // delete the legion from the player
        players[owner].setLegionById(id, null);
    }

    public List<Legion> getAllLegions() {
        List<Legion> legions = new ArrayList<Legion>();
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                legions.addAll(players[i].getAllLegions());
            }
        }
        return legions;
    }

    public int getPlayerLegionCountOnPos(int player, int x, int y) {
        if(players[player] == null) {
            throw new IllegalArgumentException("player id " + player + " not eixst");
        }
        return players[player].getPlayerLegionCountOnPos(x, y);
    }

    public Legion getLegionByPlayerAndId(int owner, int mergeToId) {
        if(players[owner] == null) {
            throw new IllegalArgumentException("Player id " + owner + " is not exist");
        }
        return players[owner].getLegionById(mergeToId);
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<Player>();
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(this.players[i] != null && !this.players[i].isFailure()) { // if the player is not dead
                players.add(this.players[i]);
            }
        }
        return players;
    }

    public void clearFailure(int playerId) {
        if(players[playerId] != null && players[playerId].isFailure()) { // if the player is dead
            players[playerId] = null;
        }
    }

    public void OpeIdleAll() {
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                players[i].OpeIdle();
            }
        }
    }

    public void OpeMoveLegion(int playerId, int legionId, Direction direction) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].moveLegion(legionId, direction);
    }

    public void OpeMergeLegion(int playerId, int legionId, int mergeToId) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].mergeLegion(legionId, mergeToId);
    }

    public void OpeForkLegion(int playerId, int legionId) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].forkLegion(legionId);
    }

    public void OpeTrainLegion(int playerId, int legionId) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].trainLegion(legionId);
    }

    public void OpeConstructLegion(int playerId, int legionId) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].constructLegion(legionId);
    }

    public void OpeIdleLegion(int playerId, int legionId) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].idleLegion(legionId);
    }

    public static PlayerMgr createDemo1() {
        PlayerMgr playerMgr  = new PlayerMgr();
        playerMgr.players[1] = new Player(1, 200, 20, 20, 0, 0);
        playerMgr.players[2] = new Player(2, 200, 20, 20, 4, 4);
        return playerMgr;
    }

    public void show() {
        for(int i = 1; i < PLAYER_CNT_MAX; i++) {
            if(players[i] != null && !players[i].isFailure()) { // if the player is not dead
                players[i].show();
            }
        }
    }

    public void OpeDismissLegion(int playerId, int legionId) {
        if(players[playerId] == null) {
            throw new IllegalArgumentException("Player id " + playerId + " is not exist");
        }
        players[playerId].dismissLegion(legionId);
    }
}
