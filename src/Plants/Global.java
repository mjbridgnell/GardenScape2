package Plants;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Pair;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Class to hold a bunch of variables and functions 

public class Global {
    public static int numPlants = 0;
    public static int cornHarvested = 0;
    public static int spinachHarvested = 0;
    public static int cucumberHarvested = 0;
    public static int radishHarvested = 0;
    static String currentPlant;
    public static LocalDateTime currentDateTime = LocalDateTime.now();
    public static int month = currentDateTime.getMonthValue();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static String formattedDateTime = currentDateTime.format(formatter);
    public static String season = "";
    public static Label dateLabel = new Label("Date: " + formattedDateTime
            + "\nSeason: " + season);
    public static Label totalPlants = new Label("Total Plants: " + numPlants
            + "\nCorn Harvested: " + cornHarvested
            + "\nSpinach Harvested: " + spinachHarvested
            + "\nCucumber Harvested: " + cucumberHarvested
            + "\nRadish Harvested: " + radishHarvested);
    public static HashMap<Pair<Integer, Integer>, Plant> plantMap2 = new HashMap<>();
    public static int globalCols = 0;
    public static int globalRows = 0;

    public static long getGrowTime(String plantName)
    {
        switch (plantName)
        {
            case "corn": return 7776000; // 90 days
            case "cucumber": return 4320000; // 50 days
            case "spinach": return 2592000; // 30 days
            case "radish": return 2160000; // 25 days
            default: return 0;
        }
    }

    // Greates and saves gamestate
    public static void saveGameState() {
        GameState saveGame = new GameState(globalRows, globalCols, plantMap2, 
            cornHarvested, spinachHarvested, cucumberHarvested, spinachHarvested,
            Seeds.cornSeeds, Seeds.cucumberSeeds, Seeds.radishSeeds, Seeds.spinachSeeds);
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("test.dat"));
            output.writeObject(saveGame);
            output.close();
        } catch (IOException ioe) {
            System.err.println("ERROR SAVING");
        }
    }

    // Returns current season
    public static String getSeason(LocalDateTime dateTime) {
        month = dateTime.getMonthValue();

        if (month == 12) {
            return "Early Winter";
        } else if (month == 1) {
            return "Winter";
        } else if (month == 2) {
            return "Late Winter";
        } else if (month == 3) {
            return "Early Spring";
        } else if (month == 4) {
            return "Spring";
        } else if (month == 5) {
            return "Late Spring";
        } else if (month == 6) {
            return "Early Summer";
        } else if (month == 7) {
            return "Summer";
        } else if (month == 8) {
            return "Late Summer";
        } else if (month == 9) {
            return "Early Fall";
        } else if (month == 10) {
            return "Fall";
        } else if (month == 11) {
            return "Late Fall";
        }

        return "Unknown Season";
    }

    public static void startClock() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(formatter);
            season = getSeason(currentDateTime);

            dateLabel.setText("Date: " + formattedDateTime
                    + "\nSeason: " + season);
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void setGrid(int cols, int rows) {
        globalCols = cols;
        globalRows = rows;
    }

    public static void incrementPlants() {
        totalPlants.setText("Total Plants: " + plantMap2.size()
            + "\nCorn Harvested: " + cornHarvested
            + "\nSpinach Harvested: " + spinachHarvested
            + "\nCucumber Harvested: " + cucumberHarvested
            + "\nRadish Harvested: " + radishHarvested);
    }

    public static void addPlanttoCell2(int col, int row, Plant curPlant) {
        if (plantMap2 == null) {
            plantMap2 = new HashMap<>();
        }
        plantMap2.put(new Pair<>(col, row), curPlant);
    }

    public static void updatePlanttoCell(int col, int row, long newTimestamp) {
        Pair<Integer, Integer> key = new Pair<>(col, row);
        System.out.println(col + " " + row);
        if (plantMap2.containsKey(key)) {
            System.out.println("UPDATE");
            Plant existingPlant = plantMap2.get(key);
            existingPlant.setTimestamp(newTimestamp);
            plantMap2.put(key, existingPlant);
        } else {
            System.out.println("No plant found at the specified coordinates.");
        }
    }

    public static void removePlantFromCell(int col, int row) {
        Pair<Integer, Integer> cellKey = new Pair<>(col, row);

        if (plantMap2.containsKey(cellKey)) {
            plantMap2.remove(cellKey);
        }
    }

    // Method to get the plant name at a specific cell
    public static String getPlantAtCell(int col, int row) {
        Plant curPlant = plantMap2.getOrDefault(new Pair<>(col, row), null);
        if (curPlant == null)
        {
            return "no plant";
        }
        else
        {
            return curPlant.getPlantName(); 
        }
    }

    public static void setCurrentPlant(String plant) {
        currentPlant = plant;
    }

    public static String getCurrentPlant() {
        return currentPlant;
    }

    public static String getMonth() {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";

        }
        return "";
    }
}
