package Main;

import com.estg.core.Institution;
import com.estg.core.InstitutionImpl;
import com.estg.core.exceptions.InstitutionException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.io.Importer;
import com.estg.io.ImporterImpl;
import com.estg.io.Save;
import com.estg.pickingManagement.PickingMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
            throws IOException, FileNotFoundException, InstitutionException, VehicleException {
        Institution institution = new InstitutionImpl("ElSircarios a seguranca comeca em si");

        Importer importer = new ImporterImpl();
        importer.importData(institution);

        Scanner input = new Scanner(System.in);
        int Options;

        do {
            Options = MenuOptions.showMenu(input);

            switch (Options) {
                case 1:
                    MenuOptions.generateRoute(institution);
                    break;

                case 2:
                    try {
                        institution.addVehicle(MenuOptions.MenuOfAddVehicle(input, institution));
                        System.out.println("Vehicle Added Successfully");
                    } catch (VehicleException exception) {
                        System.out.println("Error: " + exception.getMessage() + "!!!");
                        Save.SaveErrorToFile(exception);
                    }
                    break;

                case 3:
                    try {
                        PickingMap currentPickingMap = institution.getCurrentPickingMap();
                        System.out.println(currentPickingMap);
                    } catch (PickingMapException exception) {
                        System.out.println(exception.getMessage());
                        Save.SaveErrorToFile(exception);
                    }
                    break;

                case 4:
                    System.out.println(MenuOptions.InstitutionInformation(institution, input));
                    break;

                case 5:
                    System.out.println(MenuOptions.AidboxInformation(input, institution));
                    break;

                case 6:
                    MenuOptions.importData(institution);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Invalid option!!!");
            }

        } while (Options != 0);

        input.close();
    }
}
