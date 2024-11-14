import java.util.Scanner;

class ConfigControl {
    public static Configuration checkConfiguration() {
        // Load the configuration from a JSON file
        Configuration loadedConfig = Configuration.loadConfigFromFile("config.json");
        loadedConfig.setTotalTickets();
        if (loadedConfig != null && loadedConfig.validateConfig()) {
            System.out.println("Configuration loaded successfully!");
        } else {
            System.out.println("Failed to load configuration.");
        }
        return loadedConfig;
    }

    public static void editConfiguration() {
        if (promptYesOrNo("Do you want to edit the configuration (y/n)? : ")) {
            Configuration config = new Configuration();

//            config.setTotalTickets(promptForInt("Enter total tickets (must be > 0): ", value -> value > 0));
            config.setTicketReleaseRate(promptForInt("Enter ticket release rate (must be > 0): ", value -> value > 0));
            config.setCustomerRetrievalRate(promptForInt("Enter customer retrieval rate (must be > 0): ", value -> value > 0));
            config.setMaxTicketCapacity(promptForInt("Enter max ticket capacity (must be >= total tickets): ", value -> value >= config.getTotalTickets()));

            if (promptYesOrNo("Do you want to save the configuration (y/n)? : ")) {
                config.saveConfigToFile("config.json");
            } else {
                System.out.println("Configuration changes were not saved.\n");
            }
        } else {
            System.out.println("Exiting edit configuration.\n");
        }
    }

    public static void viewConfiguration(Configuration config) {
        System.out.println("Total tickets: " + config.getTotalTickets()+ ", Ticket Release Rate: " +
                config.getTicketReleaseRate()+ ", Customer Retrieval Rate: " + config.getCustomerRetrievalRate() +
                ", Max Ticket Capacity: " + config.getMaxTicketCapacity() +" \n");
    }


    private static int promptForInt(String message, java.util.function.IntPredicate condition) {
        Scanner input = new Scanner(System.in);
        System.out.print(message);
        while (true) {
            if (input.hasNextInt()) {
                int value = input.nextInt();
                if (condition.test(value)) {
                    return value;
                } else {
                    System.out.print("Invalid input. " + message);
                }
            } else {
                System.out.print("Invalid input. Enter a valid integer. " + message);
                input.next(); // Consume the invalid input
            }
        }
    }

    private static boolean promptYesOrNo(String message) {
        Scanner input = new Scanner(System.in);
        System.out.print(message);
        String answer = input.nextLine().trim().toLowerCase();
        while (!answer.equals("y") && !answer.equals("n")) {
            System.out.print("Please enter a valid option (y/n): ");
            answer = input.nextLine().trim().toLowerCase();
        }
        return answer.equals("y");
    }
}
