package Plants;
import java.io.Serializable;
import java.util.HashMap;

import javafx.util.Pair;

// Class to save a game

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int numRows;
    public int numCols;
    public HashMap<Pair<Integer, Integer>, Plant> plantMap2;
    public int cornHarvested;
    public int spinachHarvested;
    public int cucumberHarvested;
    public int radishHarvested;
    public int radishSeeds;
    public int cornSeeds;
    public int spinachSeeds;
    public int cucumberSeeds;
    
    public GameState(int numRows, int numCols, java.util.HashMap<Pair<Integer, Integer>, Plant> plantMap2, 
        int cornHarvested, int spinachHarvested, int cucumberHarvested, int radishHarvested,
        int cornSeeds, int cucumberSeeds, int radishSeeds, int spinachSeeds) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.plantMap2 = plantMap2;
        this.cornHarvested = cornHarvested;
        this.spinachHarvested = spinachHarvested;
        this.cucumberHarvested = cucumberHarvested;
        this.radishHarvested = radishHarvested;
        this.radishSeeds = radishSeeds;
        this.cornSeeds = cornSeeds;
        this.cucumberSeeds = cucumberSeeds;
        this.spinachSeeds = spinachSeeds;
    }

    public GameState()
    {
        
    }
}
