package game.controller;

import java.util.List;
import java.util.Scanner;

import game.Constants;
import game.operation.OperationTable;
import game.playerdata.Legion;
import game.playerdata.Player;
import game.playerdata.PlayerMgr;
import game.playerdata.Legion.LegionStatus;
import game.terrain.City;
import game.terrain.Land;
import game.terrain.LandMgr;

// the logic of a game
public class GameController {
    private PlayerMgr playerMgr;
    private LandMgr landMgr;
    private OperationTable operationTable = new OperationTable();

    // you need to design the playerMgr and landMgr before you start the game
    public GameController(PlayerMgr playerMgr, LandMgr landMgr) {
        this.playerMgr = playerMgr;
        this.landMgr = landMgr;
    }

    synchronized public void timeStep() {
        if(Constants.DEBUG) {
            System.out.println(" ---------- time step ---------- ");
        }
        playerMgr.untriggerAll(); // set all triggers to false

        dismiss();   // dismiss legions
        move();
        war();       // war will also affect the triggers
        idle();      // destory or mend the city
        train();     // only city with full sanity can train
        construct(); // you can only construct on empty land
        merge();
        fork();
        updateMoney();

        playerMgr.OpeIdleAll();
    }

    synchronized public void operate(String[] args) {
        if(Constants.DEBUG) {
            System.out.println("operate args: " + args[0]);
        }
        String[] newArgs = new String[args.length - 1];
        for(int i = 0; i < newArgs.length; i ++) { // delete the first element
            newArgs[i] = args[i + 1];
        }
        if(operationTable.getOperation(args[0]) != null) {
            operationTable.getOperation(args[0]).run(playerMgr, newArgs);
        }else {
            System.out.println("Operation '" + args[0] + "' not found");
        }
    }

    public static void main(String[] args) {
        GameController game = new GameController(PlayerMgr.createDemo1(), LandMgr.createDemo1());
        Scanner sc = new Scanner(System.in);
        while(true) {
            if(game.getPlayerMgr().finished()) {
                System.out.println("Game Over");
                System.out.print("Winner: player " + game.getPlayerMgr().getWinner() + " ");
                break;
            }
            System.out.print("\nPlease input your operation: ");
            String[] nargs = sc.nextLine().split(" ");
            if(nargs[0].equals("quit")) {
                break;
            }else
            if(nargs[0].equals("next")) {
                game.timeStep();
            }else 
            if(nargs[0].equals("show")) {
                game.show(); // show all the message of the game
            }else {
                game.operate(nargs);
            }
        }
        sc.close();
    }

    private void show() {
        System.out.println("[] show game status");
        playerMgr.show();
        landMgr.show();
    }

    public void fork() {
        List<Legion> legions = playerMgr.getAllLegions();
        for (Legion legion : legions) {
            if (legion.getStatus() == LegionStatus.FORKING && !legion.isTriggered()) {
                int x = legion.getPosX();
                int y = legion.getPosY();
                int owner = legion.getOwner();
                int populationNew;
                if(legion.getId() == 0) { // king's man
                    populationNew = legion.getPopulation() > Constants.KING_POPULATION ? (legion.getPopulation() - Constants.KING_POPULATION) / 2 : 0;
                }else {
                    populationNew = legion.getPopulation() / 2;
                }
                Player player = playerMgr.getPlayerById(owner);
                if(populationNew > 0 && player.hasEmptyLegion()) {
                    legion.setPopulation(legion.getPopulation() - populationNew);
                    int id = player.getNewLegionId();
                    player.setLegionById(id, new Legion(populationNew, x, y, owner, id));
                }
                legion.setTriggered(true);
            }
        }
    }

    // clearFailure
    public void clearFailure(int playerId) {
        Player player = playerMgr.getPlayerById(playerId);
        if(player.isFailure()) {
            playerMgr.clearFailure(playerId);
            landMgr.clearLand(playerId); // set the owner of all the land to gaia
        }
    }

    // upgrade money
    public void updateMoney() {
        if(Constants.DEBUG) {
            System.out.println("upgrade money");
        }
        List<Player> players = playerMgr.getAllPlayers();
        for (Player player : players) {
            int cnt = 0;
            int income = landMgr.getIncomeWithPlayerId(player.getId()); // set income before expend
            if(Constants.DEBUG) {
                System.out.println(" - player " + player.getId() + " income: " + income);
            }
            player.setMoney(player.getMoney() + income);

            int money = player.getMoney();
            while(player.getExpend() > money) {
                player.dismiss(); // dismiss the last few legion
                cnt ++;
            }
            player.setMoney(money - player.getExpend());
            if(Constants.DEBUG) {
                System.out.println(" - player " + player.getId() + " has " + player.getMoney() + " money after " + cnt + " dismiss");
            }
        }
        if(Constants.DEBUG) {
            System.out.println("upgrade money done\n");
        }
    }

    public int getIntegerFromDouble(double d) {
        if(d <= 0) return 0;
        int    intpart     = (int) d;
        double decimalpart = d - intpart;
        double random = Math.random();
        if(random < decimalpart) { // the greater the decimalpart, the greater the chance to get the next integer
            return intpart + 1;
        } else {
            return intpart;
        }
    }

    // idle: destroy or mend the city
    public void idle() {
        if(Constants.DEBUG) {
            System.out.println("idle ...");
        }
        List<Legion> legions = playerMgr.getAllLegions();
        double[][] fix = new double[landMgr.getHeight()][landMgr.getWidth()];

        for(Legion legion : legions) {
            if(legion.getStatus() != LegionStatus.IDLE || legion.isTriggered()) continue;
            int x = legion.getPosX();
            int y = legion.getPosY();
            Land land = landMgr.getLand(x, y);
            if(playerMgr.getAllianceById(land.getOwner()) == playerMgr.getAllianceById(legion.getOwner())) {
                // try to mend the city
                fix[x][y] += legion.getFixPower(); // same to destroy power
                if(Constants.DEBUG) {
                    System.out.println(" - player " + legion.getOwner() + " mend the city at " + x + " " + y + " with " + legion.getFixPower());
                }
            } else if(land.getOwner() != Constants.GAIA) {
                // destroy the city
                fix[x][y] -= legion.getFixPower();
                if(Constants.DEBUG) {
                    System.out.println(" - player " + legion.getOwner() + " destroy the city at " + x + " " + y + " with " + legion.getFixPower());
                }
            }
            legion.setTriggered(true);
        }

        // try to mend or destory the land
        for(int x = 0; x < fix.length; x++) {
            for(int y = 0; y < fix[x].length; y++) {
                if(fix[x][y] > 0) {
                    landMgr.mendLand(x, y, fix[x][y]);
                } else if(fix[x][y] < 0) {
                    landMgr.destroyLand(x, y, -fix[x][y]);
                }
            }
        }
        if(Constants.DEBUG) {
            System.out.println("idle done\n");
        }
    }

    // meger: merge the two legions
    public void merge() {
        if(Constants.DEBUG) {
            System.out.println("merge ...");
        }
        List<Legion> legions = playerMgr.getAllLegions();
        for(Legion legion : legions) {
            if(legion.getStatus() != LegionStatus.MERGING || legion.isTriggered()) continue;
            int x = legion.getPosX();
            int y = legion.getPosY();
            Legion mergeTo = playerMgr.getLegionByPlayerAndId(legion.getOwner(), legion.getMergeToId());
            if(mergeTo == null) {
                // the legion is dead, operation failed
                legion.setTriggered(true);
                continue;
            }else {
                // merge the two legions
                int mergeToX = mergeTo.getPosX();
                int mergeToY = mergeTo.getPosY();
                if(x == mergeToX && y == mergeToY && mergeTo.getId() != legion.getId()) {
                    // the two legions are in the same land, operation success
                    mergeTo.setPopulation(legion.getPopulation() + mergeTo.getPopulation());
                    mergeTo.setTriggered(true);
                    if(Constants.DEBUG) {
                        System.out.println(" - merge player " + legion.getOwner() + "'s " + legion.getId() + " to " + mergeTo.getId());
                    }
                    playerMgr.removeLegion(legion.getOwner(), legion.getId()); // remove current legion
                }else {
                    legion.setTriggered(true);
                    continue;
                }
            }
        }
        if(Constants.DEBUG) {
            System.out.println("merge done\n");
        }
    }

    // construct: you can only construct on empty land
    public void construct() {
        if(Constants.DEBUG) {
            System.out.println("construct ...");
        }
        List<Legion> legions = playerMgr.getAllLegions();
        for(Legion legion : legions) {
            if(legion.getStatus() != LegionStatus.CONSTRUCTING || legion.isTriggered()) continue;
            int x = legion.getPosX();
            int y = legion.getPosY();
            Land land = landMgr.getLand(x, y);
            Player player = playerMgr.getPlayerById(legion.getOwner());
            if(land.getOwner() == Constants.GAIA && player.getMoney() >= Constants.CONSTRUCT_COST) {
                // try to construct on empty land
                player.setMoney(player.getMoney() - Constants.CONSTRUCT_COST);
                landMgr.setLand(x, y, new City(land.getIncome(), legion.getOwner(), 0.01));
                if(Constants.DEBUG) {
                    System.out.println(" - player " + legion.getOwner() + " construct on " + x + " " + y);
                }
            }
            legion.setTriggered(true);
        }
        if(Constants.DEBUG) {
            System.out.println("construct done\n");
        }
    }

    // move all the legions to the next land
    public void move() {
        if(Constants.DEBUG) {
            System.out.println("move ...");
        }
        List<Legion> legions = playerMgr.getAllLegions();
        for(Legion legion : legions) {
            // it the legion want to move, it will be triggered
            if(!legion.isTriggered() && legion.getStatus() == LegionStatus.MOVING) {
                int player = legion.getOwner();
                int x = legion.getPosX();
                int y = legion.getPosY();
                int newX = x + legion.getDirection().getDx();
                int newY = y + legion.getDirection().getDy();
                if(landMgr.inGraph(newX, newY) && landMgr.getLand(newX, newY).canEnter(player)) {
                    legion.moveTo(newX, newY);
                    if(Constants.DEBUG) {
                        System.out.println(" - player " + legion.getOwner() + " legion " + legion.getId() + " move to " + newX + "," + newY);
                    }
                }
                legion.setTriggered(true);
            }
        }
        if(Constants.DEBUG) {
            System.out.println("move done\n");
        }
    }

    // train: only city with full sanity can train
    public void train() {
        if(Constants.DEBUG) {
            System.out.println("train begin...");
        }
        List<Legion> legions = playerMgr.getAllLegions();
        for(Legion legion : legions) {
            if(!legion.isTriggered() && legion.getStatus() == LegionStatus.TRAINING) {
                int player = legion.getOwner();
                int x = legion.getPosX();
                int y = legion.getPosY();
                // not population sum up but number of legion
                Land land = landMgr.getLand(x, y);
                if(land.getOwner() == player && land.getSanity() >= Constants.FULL_SANITY) {
                    int legionCnt = playerMgr.getPlayerLegionCountOnPos(player, x, y);
                    int addPopulation = getIntegerFromDouble(Constants.TRAIN_RATE / legionCnt);
                    legion.setPopulation(legion.getPopulation() + addPopulation);
                    if(Constants.DEBUG) {
                        if(addPopulation > 0) {
                            System.out.println(" - player: " + player + " at " + x + ", " + y + " legion " + legion.getId() + " addP " + addPopulation);
                        }
                    }
                }
                legion.setTriggered(true);
            }
        }
        if(Constants.DEBUG) {
            System.out.println("train end\n");
        }
    }

    public void dismiss() {
        if(Constants.DEBUG) {
            System.out.println("dismiss begin...");
        }
        List<Legion> legions = playerMgr.getAllLegions();
        for(Legion legion : legions) {
            if(!legion.isTriggered() && legion.getStatus() == LegionStatus.DISMISSING) {
                int player = legion.getOwner();
                playerMgr.getPlayerById(player).setLegionById(legion.getId(), null);
                if(Constants.DEBUG) {
                    // dismiss legion 0 to surrender
                    if(legion.getId() == 0) {
                        System.out.println(" - player " + player + " surrender by dismiss legion " + legion.getId());
                    }else {
                        System.out.println(" - player " + player + " dismiss legion " + legion.getId());
                    }
                }
                legion.setTriggered(true);
            }
        }
        if(Constants.DEBUG) {
            System.out.println("dismiss end\n");
        }
    }

    // check all the position and do war if necessary
    public void war() {
        if(Constants.DEBUG) {
            System.out.println("war start ...");
        }
        for(int x = 0; x < landMgr.getHeight(); x ++) {
            for(int y = 0; y < landMgr.getWidth(); y ++) {
                boolean hasWar = playerMgr.getAllianceByPos(x, y).size() >= 2;
                if(hasWar) {
                    // do war
                    if(Constants.DEBUG) {
                        System.out.println(" - war at " + x + "," + y);
                    }
                    List<Legion> legions = playerMgr.getLegionsByPos(x, y);
                    double[] suffer = new double[legions.size()]; // default 0
                    for(int i = 0; i < legions.size(); i++) { // dealing with each legion's attack
                        Legion legion   = legions.get(i);
                        int owner       = legion.getOwner();
                        double power    = legion.getAttackPower();
                        double suffer_i = power / playerMgr.getEnemySizeOnPos(x, y, owner); // individual suffer
                        for(int j = 0; j < legions.size(); j++) {
                            if(i == j) continue;
                            Legion enemy = legions.get(j);
                            int enemy_owner = enemy.getOwner();
                            // do not attack your own ally
                            if(playerMgr.getAllianceById(enemy_owner) != playerMgr.getAllianceById(owner)) {
                                suffer[j] += suffer_i * enemy.getPopulation();
                            }
                        }
                    }
                    for(int i = 0; i < legions.size(); i++) {
                        Legion legion = legions.get(i);
                        legion.setPopulation(legion.getPopulation() - getIntegerFromDouble(suffer[i]));
                        if(legion.getPopulation() <= 0) {
                            playerMgr.removeLegion(legion.getOwner(), legion.getId());
                            if(Constants.DEBUG) {
                                System.out.println(" - " + legion.getOwner() + " lost legion " + legion.getId());
                            }
                        }else {
                            legion.setTriggered(true);
                        }
                    }
                    // maybe someone fail after the war
                    clearFailure();
                }
            }
        }
        if(Constants.DEBUG) {
            System.out.println("war end\n");
        }
        // finish war
    }

    // for all the palyer check if they are dead
    public void clearFailure() {
        if(Constants.DEBUG) {
            System.out.println(" - clear failure ...");
        }
        List<Player> players = playerMgr.getAllPlayers();
        for(Player player : players) {
            if(player.isFailure()) {
                clearFailure(player.getId());
                if(Constants.DEBUG) {
                    System.out.println(" --- player " + player.getId() + " is dead");
                }
            }
        }
        if(Constants.DEBUG) {
            System.out.println(" - clear failure finished\n");
        }
    }

    public PlayerMgr getPlayerMgr() {
        return playerMgr;
    }
}
