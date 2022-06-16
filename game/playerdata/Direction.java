package game.playerdata;

public class Direction {
    private final int dx;
    private final int dy;
    private final String strName;

    private Direction(int dx, int dy, String strName) {
        this.dx = dx;
        this.dy = dy;
        this.strName = strName;
    }

    //getters
    public final int getDx() {
        return dx;
    }
    public final int getDy() {
        return dy;
    }

    // Directions
    public static final Direction NORTH = new Direction(-1, 0, "NORTH");
    public static final Direction SOUTH = new Direction(1, 0, "SOUTH");
    public static final Direction EAST  = new Direction(0, 1, "EAST");
    public static final Direction WEST  = new Direction(0, -1, "WEST");

    public static Direction valueOf(String strName) {
        switch(strName) {
            case "NORTH": return NORTH;
            case "SOUTH": return SOUTH;
            case "EAST" : return EAST;
            case "WEST" : return WEST;
            default:
                throw new IllegalArgumentException("Direction name must be NORTH, SOUTH, EAST or WEST");
        }
    }

    public String toString() {
        return strName;
    }
}
