package game.operation;

import game.Constants;
import game.controller.GameController;
import game.playerdata.Direction;
import game.playerdata.PlayerMgr;

// operation describes the player's action
public abstract class Operation {
    public abstract void run(PlayerMgr playerMgr, String[] args); // do nothing

    public final void run(GameController gc, String[] args) {
        run(gc.getPlayerMgr(), args);
    }
}


class OperationMove extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationMove.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            Direction direction = Direction.valueOf(args[2]);
            playerMgr.OpeMoveLegion(playerId, legionId, direction);
            if(Constants.DEBUG) {
                System.out.println("OperationMove: " + playerId + " " + legionId + " " + direction + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationMove: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class OperationTrain extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationTrain.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            playerMgr.OpeTrainLegion(playerId, legionId);
            if(Constants.DEBUG) {
                System.out.println("OperationTrain: " + playerId + " " + legionId + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationTrain: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class OperationConstruct extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationConstruct.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            playerMgr.OpeConstructLegion(playerId, legionId);
            if(Constants.DEBUG) {
                System.out.println("OperationConstruct: " + playerId + " " + legionId + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationConstruct: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class OperationMerge extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationMerge.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            int mergeToId = Integer.parseInt(args[2]);
            playerMgr.OpeMergeLegion(playerId, legionId, mergeToId);
            if(Constants.DEBUG) {
                System.out.println("OperationMerge: " + playerId + " " + legionId + " " + mergeToId + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationMerge: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class OperationIdle extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationIdle.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            playerMgr.OpeIdleLegion(playerId, legionId);
            if(Constants.DEBUG) {
                System.out.println("OperationIdle: " + playerId + " " + legionId + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationIdle: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class OperationFork extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationFork.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            playerMgr.OpeForkLegion(playerId, legionId);
            if(Constants.DEBUG) {
                System.out.println("OperationFork: " + playerId + " " + legionId + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationFork: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class OperationDismiss extends Operation {
    @Override
    public void run(PlayerMgr playerMgr, String[] args) {
        if(Constants.DEBUG) {
            System.out.print("OperationDismiss.run() ");
            for(int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
            System.out.println();
        }
        try {
            int playerId = Integer.parseInt(args[0]);
            int legionId = Integer.parseInt(args[1]);
            playerMgr.OpeDismissLegion(playerId, legionId);
            if(Constants.DEBUG) {
                System.out.println("OperationDismiss: " + playerId + " " + legionId + " set ");
            }
        }
        catch(Exception e) {
            System.out.println("OperationDismiss: " + e.getMessage());
            e.printStackTrace();
        }
    }
}