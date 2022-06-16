package game.terrain;

import game.Constants;

public class Land {
    // TODO: TRAIN_RATE may be changed into another value

    private int income; // land has no owner initially

    String getName() {
        return "Land";
    }

    public String toString() {
        if(Constants.DEBUG) {
            return String.format("%c%d[%02d,%3d%%]", getName().charAt(0), getOwner(), getIncome(), (int)(getSanity() * 100));
        }else {
            return String.format("%c%d", getName().charAt(0), getOwner());
        }
    }
    
    public Land(int income) {
        this.income = income;
    }

    // getter
    public final int getIncome() {
        return income;
    }
    public int getOwner() {
        return Constants.GAIA;
    }
    public double getSanity() {
        return Constants.FULL_SANITY;
    }

    // setter
    public final void setIncome(int income) {
        this.income = income;
    }
    public void setOwner(int owner) {
        // do nothing
        // you can not set an owner for empty land
        throw new UnsupportedOperationException("You can not set an owner for empty land");
    }
    public void setSanity(double sanity) {
        // do nothing
        // you can not set a sanity for empty land
        throw new UnsupportedOperationException("You can not set a sanity for empty land");
    }

    // check if a player can enter this land
    public boolean canEnter(int player) {
        // everyone can enter this land by default
        return true;
    }
}
