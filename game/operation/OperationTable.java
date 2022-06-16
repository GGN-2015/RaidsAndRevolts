package game.operation;

import java.util.HashMap;
import java.util.Map;

public class OperationTable {
    private Map<String, Operation> operations = new HashMap<String, Operation>();
    
    public OperationTable() {
        operations.put("move"       , new OperationMove());
        operations.put("train"      , new OperationTrain());
        operations.put("construct"  , new OperationConstruct());
        operations.put("merge"      , new OperationMerge());
        operations.put("fork"       , new OperationFork());
        operations.put("idle"       , new OperationIdle());
        operations.put("dismiss"    , new OperationDismiss());
    }

    public void setOperation(String str, Operation ope) {
        operations.put(str, ope);
    }

    public Operation getOperation(String str) {
        return operations.get(str); // null when not found
    }
}
