package game.terrain;

import game.Constants;

// import random
import java.util.Random;

// map
public class LandMgr {
    private final int width;
    private final int height;
    private Land[][] lands;

    public LandMgr(int width, int height) {
        this.width = width;
        this.height = height;

        // create lands by default: all the lands are empty
        this.lands = new Land[width][height];
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                this.lands[i][j] = new Land(0);
            }
        }
    }

    public static LandMgr createDemo1() {
        LandMgr landMgr = new LandMgr(5, 5);
        Random random = new Random();
        for(int i = 0; i < landMgr.getHeight(); i++) {
            for(int j = 0; j < landMgr.getWidth(); j++) {
                if(random.nextInt(100) > 80) {
                    landMgr.setLand(i, j, new Mountain(0));
                }else {
                    landMgr.setLand(i, j, new Land(random.nextInt(10)));
                }
            }
        }
        landMgr.setLand(0, 0, new City(20, 1));
        landMgr.setLand(landMgr.getHeight() - 1, landMgr.getWidth() - 1, new City(20, 2));
        return landMgr;
    }

    // getters
    public final int getWidth() {
        return width;
    }
    public final int getHeight() {
        return height;
    }
    public final Land getLand(int x, int y) {
        return lands[x][y];
    }

    // setters
    public final void setLand(int x, int y, Land land) {
        lands[x][y] = land;
    }

    // methods
    public boolean inGraph(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void mendLand(int x, int y, double d) {
        if(!inGraph(x, y)) return;
        Land land = getLand(x, y);
        land.setSanity(land.getSanity() + d);
        if(land.getSanity() >= Constants.FULL_SANITY - Constants.EPS) { // keep the sanity between 0 and 1
            land.setSanity(Constants.FULL_SANITY);
        }
    }

    public void destroyLand(int x, int y, double d) {
        if(!inGraph(x, y)) return;
        Land land = getLand(x, y);
        land.setSanity(land.getSanity() - d);
        if(land.getSanity() <= Constants.EPS) {
            setLand(x, y, new Land(land.getIncome())); // create a new land with the same income
        }
    }

    public void clearLand(int playerId) {
        for(int x = 0; x < height; x ++) {
            for(int y = 0; y < width; y ++) {
                Land land = getLand(x, y);
                if(land.getOwner() == playerId) {
                    setLand(x, y, new Land(land.getIncome())); // create a new land with the same income
                }
            }
        }
    }

    public int getIncomeWithPlayerId(int id) {
        int sum = 0;
        for(int x = 0; x < height; x ++) {
            for(int y = 0; y < width; y ++) {
                Land land = getLand(x, y);
                if(land.getOwner() == id && land.getSanity() >= Constants.FULL_SANITY) {  // if the land is owned by the player
                    sum += land.getIncome(); // add the income
                }
            }
        }
        return sum;
    }

    public void show() {
        System.out.println("LandMgr: " + width + "x" + height);
        for(int x = 0; x < height; x ++) {
            System.out.print(" - ");
            for(int y = 0; y < width; y ++) {
                Land land = getLand(x, y);
                System.out.print(land.toString() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
