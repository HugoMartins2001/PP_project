package Main;

import com.estg.core.AidBox;
import com.estg.core.Institution;
import com.estg.core.InstitutionImpl;
import com.estg.core.Container;
import com.estg.core.ItemType;
import com.estg.io.Save;
import com.estg.io.Importer;
import com.estg.io.ImporterImpl;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.PickingMapImpl;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.RouteGenerator;
import com.estg.pickingManagement.RouteGeneratorImpl;
import com.estg.pickingManagement.Strategy;
import com.estg.pickingManagement.StrategyImpl;
import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.RouteValidatorImpl;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.ReportImpl;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.VehicleImpl;
import com.estg.core.exceptions.PickingMapException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDateTime;

public class MenuOptions {

    /**
     * Displays the main menu and gets the user's choice.
     *
     * @param input Scanner object for reading user input
     * @return The chosen menu option
     */
    public static int showMenu(Scanner input) {
        int option = 0;
        boolean verifyInput = false;

        do {
            System.out.println("##----------------Menu---------------##");
            System.out.println("|------------------------------------|");
            System.out.println("| Option 1 - Generate Routes         |");
            System.out.println("| Option 2 - Add vehicles            |");
            System.out.println("| Option 3 - Picking Maps information|");
            System.out.println("| Option 4 - Institution Information |");
            System.out.println("| Option 5 - Containers Information  |");
            System.out.println("| Option 6 - Import Data (JSON)      |");
            System.out.println("| Option 0 - Exit                    |");
            System.out.println("|------------------------------------|");
            System.out.println("Enter an option :");

            try {
                option = input.nextInt();
                verifyInput = true;
            } catch (InputMismatchException exception) {
                System.out.println("Invalid option!!!");
                Save.SaveErrorToFile(exception);
                input.next();
            }
        } while (!verifyInput);

        return option;
    }

    /**
     * Generates routes for the given institution and displays the result.
     *
     * @param institution The institution for which to generate routes
     */
    public static void generateRoute(Institution institution) {
        RouteGenerator routesGenerator = new RouteGeneratorImpl();
        Strategy strategy = new StrategyImpl();
        RouteValidator validator = new RouteValidatorImpl();
        Report report = new ReportImpl();

        Route[] routes = routesGenerator.generateRoutes(institution, strategy, validator, report);

        System.out.println("Generated Routes: " + routes.length + "\n");
        System.out.println("-----------------------------------");
        System.out.println("Report: ");
        System.out.println(report.toString());
        System.out.println("-----------------------------------");

        if (routes.length > 0) {
            try {
                PickingMap pickingMap = new PickingMapImpl(LocalDateTime.now(), routes);
                institution.addPickingMap(pickingMap);
                System.out.println("Picking Map created and registered in the institution successfully!\n");
            } catch (Exception exception) {
                System.out.println("Error registering Picking Map: " + exception.getMessage());
                Save.SaveErrorToFile(exception);
            }

            System.out.println("Route details:");
            for (int i = 0; i < routes.length; i++) {
                Route r = routes[i];
                if (r == null) {
                    continue;
                }
                System.out.println("  Route " + (i + 1) + ":");
                System.out.println("    Vehicle: " + ((VehicleImpl) r.getVehicle()).getCode() + " (" + r.getVehicle().getSupplyType() + ")");
                System.out.println("    Distance: " + String.format("%.2f", r.getTotalDistance()) + " km");
                System.out.println("    Duration: " + String.format("%.2f", r.getTotalDuration()) + " min");

                AidBox[] routeBoxes = r.getRoute();
                System.out.print("    Path: Base");
                if (routeBoxes != null) {
                    for (int j = 0; j < routeBoxes.length; j++) {
                        if (routeBoxes[j] != null) {
                            System.out.print(" -> " + routeBoxes[j].getCode());
                        }
                    }
                }
                System.out.println(" -> Base");
            }
        }
    }

    /**
     * Displays the menu for adding a vehicle and processes the user input.
     *
     * @param input Scanner object for reading user input
     * @param institution The institution to which the vehicle will be added
     * @return The created vehicle
     */
    public static Vehicle MenuOfAddVehicle(Scanner input, Institution institution) {
        input.nextLine();
        System.out.println("Enter the Code Of The Vehicle:");
        String vehicleCode = input.nextLine().trim();

        System.out.println("Select the ItemType (Supply Type) for the vehicle:");
        System.out.println("1 - NON_PERISHABLE_FOOD");
        System.out.println("2 - PERISHABLE_FOOD");
        System.out.println("3 - MEDICINE");
        System.out.println("4 - CLOTHING");
        System.out.print("Option: ");

        int typeOpt = 0;
        try {
            typeOpt = input.nextInt();
        } catch (InputMismatchException exception) {
            System.out.println("Invalid Option!!!");
            Save.SaveErrorToFile(exception);
            input.next();
            return null;
        }

        ItemType type = null;
        switch (typeOpt) {
            case 1:
                type = ItemType.NON_PERISHABLE_FOOD;
                break;
            case 2:
                type = ItemType.PERISHABLE_FOOD;
                break;
            case 3:
                type = ItemType.MEDICINE;
                break;
            case 4:
                type = ItemType.CLOTHING;
                break;
            default:
                System.out.println("Invalid type choice!");
                return null;
        }

        double capacityOfTheVehicle = -1;
        boolean validInput = false;

        do {
            System.out.println("Enter The Vehicle Capacity:");

            try {
                capacityOfTheVehicle = input.nextDouble();
                validInput = true;
            } catch (InputMismatchException exception) {
                System.out.println("Invalid Option!!!");
                Save.SaveErrorToFile(exception);
                input.next();
            }
        } while (!validInput || capacityOfTheVehicle < 0);

        return new VehicleImpl(vehicleCode, type, capacityOfTheVehicle, true);
    }

    /**
     * Retrieves the current picking map for the institution.
     *
     * @param institution The institution for which to retrieve the picking map
     * @return The current picking map
     * @throws PickingMapException If an error occurs while retrieving the
     * picking map
     * @throws com.estg.core.exceptions.PickingMapException If an error occurs
     * while retrieving the picking map
     */
    private PickingMap getPickingMap(Institution institution) throws PickingMapException {
        return institution.getCurrentPickingMap();
    }

    /**
     * Displays Picking Maps information.
     *
     * @param input Scanner object for reading user input
     * @param institution The institution to query
     */
    public static void PickingMapsInformation(Scanner input, Institution institution) {
        try {
            PickingMap[] maps = institution.getPickingMaps();
            if (maps == null || maps.length == 0) {
                System.out.println("No picking maps available in the institution.");
                return;
            }
            System.out.println("--- Picking Maps List (" + maps.length + ") ---");
            for (int i = 0; i < maps.length; i++) {
                if (maps[i] != null) {
                    System.out.println("- Picking Map " + (i + 1) + " | Date: " + maps[i].getDate());
                }
            }
        } catch (Exception exception) {
            System.out.println("Error displaying picking maps information!");
            Save.SaveErrorToFile(exception);
        }
    }

    /**
     * Displays the institution information menu and processes the user input.
     *
     * @param institution The institution for which to display information
     * @param input Scanner object for reading user input
     * @return Information about the institution or its vehicles
     */
    public static String InstitutionInformation(Institution institution, Scanner input) {
        int option = 0;
        boolean validInput = false;

        do {
            // Display the institution information menu
            System.out.println("##-------------Institution Menu---------------##");
            System.out.println("|---------------------------------------------|");
            System.out.println("| Option 1 - Institution information          |");
            System.out.println("| Option 2 - Show vehicles in the institution |");
            System.out.println("| Option 3 - Back                             |");
            System.out.println("|---------------------------------------------|");
            System.out.println("Enter an option :");

            try {
                option = input.nextInt();
                validInput = true;
            } catch (InputMismatchException exception) {
                System.out.println("Invalid option!!!");
                Save.SaveErrorToFile(exception);
                input.next();
            }
        } while (!validInput || option < 1 || option > 3);

        switch (option) {
            case 1:
                return MenuOptions.InstitutionInformation(institution);
            case 2:
                return MenuOptions.VehiclesInformation(institution);
            default:
                return "";
        }
    }

    /**
     * Retrieves information about the institution.
     *
     * @param institution The institution for which to retrieve information
     * @return A string containing information about the institution
     */
    private static String InstitutionInformation(Institution institution) {
        StringBuilder sb = new StringBuilder();
        sb.append(institution.toString()).append("\n");

        sb.append("--- Detailed Aid Boxes & Containers ---\n");
        AidBox[] aidBoxes = institution.getAidBoxes();
        if (aidBoxes == null || aidBoxes.length == 0) {
            sb.append("No Aid Boxes registered.\n");
        } else {
            for (AidBox ab : aidBoxes) {
                if (ab == null) {
                    continue;
                }
                sb.append("AidBox: ").append(ab.getCode()).append(" (").append(ab.getZone()).append(")\n");
                Container[] containers = ab.getContainers();
                if (containers == null || containers.length == 0) {
                    sb.append("  No containers.\n");
                } else {
                    for (Container c : containers) {
                        if (c == null) {
                            continue;
                        }
                        double lastVal = 0.0;
                        com.estg.core.Measurement[] ms = c.getMeasurements();
                        if (ms != null && ms.length > 0) {
                            lastVal = ms[ms.length - 1].getValue();
                        }
                        sb.append("  - Container: ").append(c.getCode())
                                .append(" | Type: ").append(c.getType())
                                .append(" | Capacity: ").append(c.getCapacity())
                                .append(" | Current Load: ").append(lastVal).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Retrieves information about the vehicles in the institution.
     *
     * @param institution The institution for which to retrieve vehicle
     * information
     * @return A string containing information about the vehicles
     */
    private static String VehiclesInformation(Institution institution) {
        StringBuilder sb = new StringBuilder("Vehicles in the institution:\n");

        for (Vehicle vehicle : institution.getVehicles()) {
            sb.append("-> ").append(((VehicleImpl) vehicle).getCode()).append(" | Status: ").append(((VehicleImpl) vehicle).getStatusToString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Displays the aid box information menu and processes the user input.
     *
     * @param input Scanner object for reading user input
     * @param institution The institution for which to display aid box
     * information
     * @return Information about the specified aid box
     */
    public static String AidboxInformation(Scanner input, Institution institution) {
        int option = 0;
        boolean validInput = false;

        do {
            System.out.println("##----------------AidBox Menu-----------------##");
            System.out.println("|---------------------------------------------|");
            System.out.println("| Option 1 - Information in a specific aidbox |");
            System.out.println("| Option 2 - Back                             |");
            System.out.println("|---------------------------------------------|");
            System.out.println("Enter an option :");

            try {
                option = input.nextInt();
                validInput = true;
            } catch (InputMismatchException exception) {
                System.out.println("Invalid option!!!");
                Save.SaveErrorToFile(exception);
                input.next();
            }
        } while (!validInput || option < 1 || option > 2);

        switch (option) {
            case 1:
                return MenuOptions.AidBoxInformation(input, institution);
            default:
                return "";
        }
    }

    /**
     * Retrieves information about a specific aid box.
     *
     * @param input Scanner object for reading user input
     * @param institution The institution for which to retrieve aid box
     * information
     * @return A string containing information about the specified aid box
     */
    private static String AidBoxInformation(Scanner input, Institution institution) {
        input.nextLine(); // Consume newline leftover
        System.out.print("Enter the Code of an AidBox: ");
        String codeOfTheAidBox = input.nextLine().trim();

        AidBox[] aidBoxes = ((InstitutionImpl) institution).getAidBoxes();

        for (AidBox aidBox : aidBoxes) {
            if (aidBox.getCode().equalsIgnoreCase(codeOfTheAidBox)) {
                return aidBox.toString();
            }
        }

        return "The code is invalid!!!";
    }

    /**
     * Imports data from the JSON file to populate the institution.
     *
     * @param institution The institution to populate
     */
    public static void importData(Institution institution) {
        Importer importer = new ImporterImpl();
        try {
            importer.importData(institution);
            System.out.println("Data imported successfully!");
            System.out.println("Total AidBoxes in institution: " + institution.getAidBoxes().length);
        } catch (Exception exception) {
            System.out.println("Error importing data!");
            Save.SaveErrorToFile(exception);
        }
    }
}
