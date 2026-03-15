package Plants;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Label;

public class Seeds {
    public static int radishSeeds = 0;
    public static int cornSeeds = 0;
    public static int spinachSeeds = 0;
    public static int cucumberSeeds = 0;

    public static Label seedLabel = new Label("Seeds used:"
            + "\nCorn: " + cornSeeds + " $"
            + "\nCucumber: " + cucumberSeeds + " $"
            + "\nRadish: " + radishSeeds + " $"
            + "\nSpinach: " + spinachSeeds + " $");

    public static Label pricesLabel = new Label("Current Seed Prices Per Seed:");

    private static final String SUPABASE_URL = "https://gfhumlllzogmqckhaxmx.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdmaHVtbGxsem9nbXFja2hheG14Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjk5ODg1NTcsImV4cCI6MjA0NTU2NDU1N30.6xC8iC_2kQqdDwgsdsytSj8bwX2Z7bXK_qnb2aWZh4E";
    private static final String TABLE_NAME = "Seed%20Prices";

    private static List<SeedPrice> seedPrices = new ArrayList<>();
    private static Map<Integer, Double> priceMap = new HashMap<>();

    static {
        System.out.println("Loading Seed Prices...");
        loadSeedPrices(); // Load prices once at startup
        updatePricesLabel();
        System.out.println(pricesLabel);
    }

    private static void updatePricesLabel() {
        StringBuilder pricesText = new StringBuilder("Current Seed Prices Per Seed:\n");
        for (SeedPrice seedPrice : seedPrices) {
            pricesText.append(seedPrice.getName())
                      .append(": $")
                      .append(seedPrice.getPrice())
                      .append("\n");
        }
        pricesLabel.setText(pricesText.toString());
    }

    private static void loadSeedPrices() {
        try {
            // Build the URL to access the table
            String url = SUPABASE_URL + "/rest/v1/" + TABLE_NAME;
    
            // Create an HTTP client
            HttpClient client = HttpClient.newHttpClient();
    
            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .build();
    
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            // Get the response body
            String responseBody = response.body();
            System.out.println("Response Body: " + responseBody); // Debug print
    
            // Check if the response contains valid data
            if (!responseBody.isEmpty()) {
                // Parse the JSON response
                String[] entries = responseBody.replace("[", "").replace("]", "").split("\\},\\s*\\{");
    
                for (String entry : entries) {
                    // Clean up the entry string
                    entry = entry.replaceAll("[{}]", ""); // Remove the braces
    
                    // Split the fields by comma
                    String[] fields = entry.split(",");
    
                    // Extract ID, Price, and Name from the fields
                    int id = Integer.parseInt(fields[0].split(":")[1].trim());
                    double price = Double.parseDouble(fields[1].split(":")[1].trim());
                    String name = fields[2].split(":")[1].replace("\"", "").trim(); // Remove quotes and trim
    
                    // Create and store the seed price object
                    SeedPrice seedPrice = new SeedPrice(id, price, name);
                    seedPrices.add(seedPrice);
                    priceMap.put(id, price); // Add to price map for quick access
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getCost(int plantID) {
        return priceMap.getOrDefault(plantID, 0.0); // Retrieve the cost from the map
    }

    public static void incrementSeeds(String curPlant) {
        switch (curPlant) {
            case "radish":
                radishSeeds += 16; // seeds per plant
                break;
            case "corn":
                cornSeeds += 4;
                break;
            case "cucumber":
                cucumberSeeds += 1;
                break;
            case "spinach":
                spinachSeeds += 9;
                break;
        }
        updateSeedsLabel();
    }

    public static void updateSeedsLabel()
    {
        seedLabel.setText("Seeds used:"
        + "\nCorn: " + cornSeeds + " $" + String.format("%.2f", (cornSeeds * getCost(2)))
        + "\nCucumber: " + cucumberSeeds + " $" + String.format("%.2f", (cucumberSeeds * getCost(3)))
        + "\nRadish: " + radishSeeds + " $" + String.format("%.2f", (radishSeeds * getCost(1)))
        + "\nSpinach: " + spinachSeeds + " $" + String.format("%.2f", (spinachSeeds * getCost(4))));
    }
}
