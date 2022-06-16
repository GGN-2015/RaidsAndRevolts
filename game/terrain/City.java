package game.terrain;

import game.Constants;

public class City extends Land {
    private int owner;
    private double sanity;

    @Override
    public String getName() {
        return "City";
    }

    public City(int income, int owner) {
        super(income);
        this.owner = owner; // player id
        this.sanity = Constants.FULL_SANITY;
    }

    public City(int income, int owner, double sanity) {
        super(income);
        this.owner = owner; // player id
        this.sanity = sanity;
    }

    // getters
    @Override public final int getOwner() {
        return owner;
    }
    @Override public final double getSanity() {
        return sanity;
    }

    // setters
    @Override public final void setOwner(int owner) {
        this.owner = owner;
    }
    @Override public final void setSanity(double sanity) {
        this.sanity = sanity;
        // if sanity <= 0, destroy this city into land with same income
        // in landMgr.java, you can use setLand(x, y, new Land(income))
    }
}
