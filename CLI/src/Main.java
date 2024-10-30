public class Main {
    public static void main(String[] args) {

        // Load the configuration from a JSON file
        Configuration loadedConfig = Configuration.loadConfigFromFile("config.json");
        if (loadedConfig != null && loadedConfig.validateConfig()) {
            System.out.println("Configuration loaded successfully!");
        } else {
            System.out.println("Failed to load configuration.");
        }


        // Create a configuration object

        Configuration config = new Configuration();
        // Save the configuration to a JSON file
        config.saveConfigToFile("config.json");

    }
}
