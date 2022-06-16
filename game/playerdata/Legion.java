package game.playerdata;

public class Legion {
    //public static int legionCount = 0;

    private int population;
    private int posx, posy;
    private int owner; // player0 is GAIA (reserved)
    private int id;

    private boolean triggered = false; // each army can only be triggered once

    public Legion(int population, int posx, int posy, int owner, int id) {
        //legionCount += 1;
        if(population <= 0) {
            throw new IllegalArgumentException("Legion population must be positive");
        }
        this.population = population;
        this.posx = posx;
        this.posy = posy;
        this.owner = owner;
        this.id = id; // inner id in the player's legion array
    }

    public enum LegionStatus {IDLE, MOVING, CONSTRUCTING, TRAINING, FORKING, MERGING, DISMISSING};
    private LegionStatus status = LegionStatus.IDLE;
    private Direction direction = Direction.NORTH;
    private int mergetoId = -1; // -1 means no merge

    // getters
    public final int getPopulation() {
        return population;
    }
    public final int getPosX() {
        return posx;
    }
    public final int getPosY() {
        return posy;
    }
    public final int getOwner() {
        return owner;
    }
    public final int getId() {
        return id;
    }
    public final LegionStatus getStatus() {
        return status;
    }
    public final Direction getDirection() {
        return direction;
    }
    public final int getMergeToId() {
        return mergetoId;
    }
    public final boolean isTriggered() {
        return triggered;
    }
    public double getAttackPower() {
        return population * 0.1;
    }
    public double getFixPower() {
        return population * 0.01; // 100 person to fix at once
    }

    // setters
    public final void setPopulation(int population) {
        this.population = population;
    }
    public final void setPosx(int posx) {
        this.posx = posx;
    }
    public final void setPosy(int posy) {
        this.posy = posy;
    }
    public final void setOwner(int owner) {
        this.owner = owner;
    }
    public final void setId(int id) {
        this.id = id;
    }
    public final void setStatus(LegionStatus status) {
        this.status = status;
    }
    public final void setDirection(Direction direction) {
        this.direction = direction;
    }
    public final void setMergeToId(int mergetoId) {
        this.mergetoId = mergetoId;
    }
    public final void setTriggered(boolean triggered) {
        this.triggered = triggered;
        if(triggered) {
            status = LegionStatus.IDLE; // clear the status when triggered
            this.direction = Direction.NORTH;
            this.mergetoId = -1; // clear the merge info when triggered
        }
    }

    // other methods
    public final void moveTo(int posx, int posy) {
        this.posx = posx;
        this.posy = posy;
    }
    public void show() {
        System.out.println(" - Legion " + id + ": " + population + " people, position: (" + posx + ", " + posy + ")");
    }
}
