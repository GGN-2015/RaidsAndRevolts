package game.terrain;

public class Mountain extends Land {
    public Mountain(int income) {
        super(income);
    }

    @Override
    public String getName() {
        return "Mount";
    }
    
    @Override
    public boolean canEnter(int player) {
        return false;
    }
}
