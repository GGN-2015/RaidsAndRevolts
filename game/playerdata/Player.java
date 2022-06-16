package game.playerdata;

import java.util.ArrayList;
import java.util.List;

import game.Constants;
import game.playerdata.Legion.LegionStatus;

public class Player {
    private int id;
    private Legion[] legions = new Legion[Constants.LEGION_CNT_MAX];
    private int money;

    public Player(int id, int money, int kingPopulation, int armyPopulation, int posx, int posy) {
        if(id <= 0) {
            throw new IllegalArgumentException("Player id must be positive");
        }
        this.id = id;
        legions[0] = new Legion(kingPopulation, posx, posy, id, 0);
        if(armyPopulation > 0) {
            // create an army legion when armyPopulation > 0
            legions[1] = new Legion(armyPopulation, posx, posy, id, 1);
        }
        this.money = money;
    }

    // getters
    public final int getId() {
        return id;
    }
    public final Legion getLegionById(int legionId) {
        if(legionId < 0 || legionId >= legions.length) {
            throw new IllegalArgumentException("Legion id must be between 0 and " + legions.length);
        }
        return legions[legionId];
    }
    public final int getMoney() {
        return money;
    }

    // setters
    public final void setId(int id) {
        this.id = id;
    }
    public final void setLegionById(int legionId, Legion newLegion) {
        if(legionId < 0 || legionId >= legions.length) {
            throw new IllegalArgumentException("Legion id must be between 0 and " + legions.length);
        }
        legions[legionId] = newLegion; // covers the old legion
    }
    public final void setMoney(int money) {
        this.money = money;
    }

    // other methods
    public boolean isFailure() {
        // army 0 is always the king's army
        // dead army should be cleared at once, so (legions[0].getPopulation() <= 0) will not happen
        return legions[0] == null || legions[0].getPopulation() <= 0;
    }

    // get legions by position
    public List<Legion> getLegionsByPos(int posx, int posy) {
        List<Legion> ansList = new ArrayList<Legion>();
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null && legions[i].getPosX() == posx && legions[i].getPosY() == posy) {
                ansList.add(legions[i]);
            }
        }
        return ansList;
    }
    public void untriggerAll() {
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null) {
                legions[i].setTriggered(false);
            }
        }
    }

    public List<Legion> getAllLegions() {
        List<Legion> ansList = new ArrayList<Legion>();
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null && legions[i].getPopulation() > 0) {
                ansList.add(legions[i]);
            }
        }
        return ansList;
    }

    public int getPlayerLegionCountOnPos(int x, int y) {
        int cnt = 0;
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null && legions[i].getPosX() == x && legions[i].getPosY() == y) {
                cnt ++;
            }
        }
        return cnt;
    }

    public boolean hasEmptyLegion() {
        for(int i = 1; i < legions.length; i++) { // you can not use 0 as the index of the first legion
            if(legions[i] == null) {
                return true;
            }
        }
        return false;
    }

    public int getNewLegionId() {
        for(int i = 1; i < legions.length; i++) {
            if(legions[i] == null) {
                return i;
            }
        }
        throw new IllegalStateException("No empty legion");
    }

    public int getExpend() { // population - population of the king
        int sum = 0;
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null) {
                sum += legions[i].getPopulation();
            }
        }
        return Math.max(sum - Constants.KING_POPULATION, 0);
    }

    // when your money is less than the expend, you will lose at least a legion
    public void dismiss() {
        int pos = 0;
        for(int i = legions.length - 1; i >= 1; i--) { // find the last legion
            if(legions[i] != null) {
                pos = i;
                break;
            }
        }
        if(pos > 0) {
            legions[pos] = null;
        }else {
            if(legions[pos].getPopulation() > Constants.KING_POPULATION) {
                legions[pos].setPopulation(Constants.KING_POPULATION); // set to default population
            }else {
                throw new IllegalStateException("When there is only king's man, expend != 0");
            }
        }
    }

    public void moveLegion(int legionId, Direction direction) {
        if(legions[legionId] == null) {
            throw new IllegalStateException("legionId " + legionId + " for player " + id + " is not exist");
        }
        legions[legionId].setStatus(LegionStatus.MOVING);
        legions[legionId].setDirection(direction);
    }

    public void mergeLegion(int legionId, int mergeToId) {
        if(legions[legionId] == null) {
            throw new IllegalStateException("legionId " + legionId + " for player " + id + " is not exist");
        }
        if(legions[mergeToId] == null) {
            throw new IllegalStateException("legionId " + mergeToId + " for player " + id + " is not exist");
        }
        legions[legionId].setMergeToId(mergeToId);
        legions[legionId].setStatus(LegionStatus.MERGING); 
        // the legion will be merged after the war
        // maybe the mergeToId is dead when mergeLegion is called
    }

    public void OpeIdle() {
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null) {
                legions[i].setStatus(LegionStatus.IDLE);
            }
        }
    }

    public void forkLegion(int legionId) {
        if(legions[legionId] == null) {
            throw new IllegalStateException("legionId " + legionId + " for player " + id + " is not exist");
        }
        legions[legionId].setStatus(LegionStatus.FORKING);
    }

    public void trainLegion(int legionId) {
        if(legions[legionId] == null) {
            throw new IllegalStateException("legionId " + legionId + " for player " + id + " is not exist");
        }
        legions[legionId].setStatus(LegionStatus.TRAINING);
    }

    public void constructLegion(int legionId) {
        if(legions[legionId] == null) {
            throw new IllegalStateException("legionId " + legionId + " for player " + id + " is not exist");
        }
        legions[legionId].setStatus(LegionStatus.CONSTRUCTING);
    }

    public void idleLegion(int legionId) {
        if(legions[legionId] == null) {
            throw new IllegalStateException("legionId " + legionId + " for player " + id + " is not exist");
        }
        legions[legionId].setStatus(LegionStatus.IDLE);
    }

    public void show() {
        System.out.println("player " + id + ":");
        for(int i = 0; i < legions.length; i++) {
            if(legions[i] != null) {
                legions[i].show();
            }
        }
        System.out.println();
    }

    public void dismissLegion(int legionId) {
        legions[legionId].setStatus(LegionStatus.DISMISSING);
        // DO NOT dismiss directly
    }
}
