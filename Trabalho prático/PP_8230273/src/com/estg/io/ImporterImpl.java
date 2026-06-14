package com.estg.io;

import com.estg.core.Institution;
import com.estg.core.exceptions.InstitutionException;
import com.estg.core.AidBox;
import com.estg.core.AidBoxImpl;
import com.estg.core.Container;
import com.estg.core.ContainerImpl;
import com.estg.core.GeographicCoordinates;
import com.estg.core.GeographicCoordinatesImpl;
import com.estg.core.ItemType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class ImporterImpl implements Importer {

    private File findFile(String baseName) {
        String[] paths = {
            baseName,
            "jsonFiles/" + baseName,
            "../jsonFiles/" + baseName,
            "../../jsonFiles/" + baseName,
            "Trabalho prático/jsonFiles/" + baseName,
            "../Trabalho prático/jsonFiles/" + baseName
        };
        for (int i = 0; i < paths.length; i++) {
            File f = new File(paths[i]);
            if (f.exists() && f.isFile()) {
                return f;
            }
        }
        return new File(baseName);
    }

    @Override
    public void importData(Institution institution) throws FileNotFoundException, IOException, InstitutionException {
        if (institution == null) {
            throw new InstitutionException("The Institution is null");
        }

        File aidBoxesFile = findFile("AidBoxes.json");
        if (!aidBoxesFile.exists()) {
            throw new FileNotFoundException("AidBoxes.json file not found");
        }

        // Read all content of the JSON file
        byte[] bytes = Files.readAllBytes(aidBoxesFile.toPath());
        String content = new String(bytes, "UTF-8");

        int index = 0;
        while (true) {
            // Find next occurrence of Codigo:
            int codigoIdx = content.indexOf("\"Codigo\":", index);
            if (codigoIdx == -1) {
                break;
            }

            // Extract Codigo
            int startQuote = content.indexOf("\"", codigoIdx + 9);
            int endQuote = content.indexOf("\"", startQuote + 1);
            String codigo = content.substring(startQuote + 1, endQuote);

            // Extract Zona
            int zonaIdx = content.indexOf("\"Zona\":", endQuote);
            startQuote = content.indexOf("\"", zonaIdx + 7);
            endQuote = content.indexOf("\"", startQuote + 1);
            String zona = content.substring(startQuote + 1, endQuote);

            // Extract Latitude
            int latIdx = content.indexOf("\"Latitude\":", endQuote);
            int commaIdx = content.indexOf(",", latIdx);
            String latStr = content.substring(latIdx + 11, commaIdx).trim();
            double latitude = Double.parseDouble(latStr);

            // Extract Longitude
            int lonIdx = content.indexOf("\"Longitude\":", commaIdx);
            int nextComma = content.indexOf(",", lonIdx);
            int endLon = nextComma;
            int nextBracket = content.indexOf("[", lonIdx);
            if (endLon == -1 || (nextBracket != -1 && endLon > nextBracket)) {
                endLon = content.indexOf("\"", lonIdx);
                if (endLon == -1) {
                    endLon = content.indexOf("}", lonIdx);
                }
            }
            String lonStr = content.substring(lonIdx + 12, endLon).trim();
            if (lonStr.contains(",")) {
                lonStr = lonStr.substring(0, lonStr.indexOf(",")).trim();
            }
            double longitude = Double.parseDouble(lonStr);

            // Create the GeographicCoordinates and the AidBox
            GeographicCoordinates coords = new GeographicCoordinatesImpl(latitude, longitude);
            AidBox aidBox = new AidBoxImpl(codigo, zona, zona, coords);

            // Parse the Containers list inside the "[" and "]" brackets
            int containersStart = content.indexOf("[", lonIdx);
            int containersEnd = content.indexOf("]", containersStart);
            String containersContent = content.substring(containersStart, containersEnd + 1);

            int containerIdx = 0;
            while (true) {
                int cCodeIdx = containersContent.indexOf("\"codigo\":", containerIdx);
                if (cCodeIdx == -1) {
                    break;
                }
                int cStartQuote = containersContent.indexOf("\"", cCodeIdx + 9);
                int cEndQuote = containersContent.indexOf("\"", cStartQuote + 1);
                String cCode = containersContent.substring(cStartQuote + 1, cEndQuote);

                int cCapIdx = containersContent.indexOf("\"capacidade\":", cEndQuote);
                int cEndCap = containersContent.indexOf("}", cCapIdx);
                int cNextComma = containersContent.indexOf(",", cCapIdx);
                if (cNextComma != -1 && cNextComma < cEndCap) {
                    cEndCap = cNextComma;
                }
                String cCapStr = containersContent.substring(cCapIdx + 13, cEndCap).trim();
                double capacity = Double.parseDouble(cCapStr);

                // Map first letter of container code to ItemType:
                // N -> NON_PERISHABLE_FOOD
                // P -> PERISHABLE_FOOD
                // M -> MEDICINE
                // V -> CLOTHING (Vestuário)
                ItemType type = null;
                if (cCode.length() > 0) {
                    char first = Character.toUpperCase(cCode.charAt(0));
                    if (first == 'N') {
                        type = ItemType.NON_PERISHABLE_FOOD;
                    } else if (first == 'P') {
                        type = ItemType.PERISHABLE_FOOD;
                    } else if (first == 'M') {
                        type = ItemType.MEDICINE;
                    } else if (first == 'V') {
                        type = ItemType.CLOTHING;
                    }
                }

                if (type != null) {
                    Container container = new ContainerImpl(capacity, cCode, type);
                    try {
                        container.addMeasurement(new com.estg.core.MeasurementImpl(java.time.LocalDateTime.now(), capacity * 0.8));
                    } catch (Exception e) {
                        // Ignore measurement issues
                    }
                    try {
                        aidBox.addContainer(container);
                    } catch (Exception e) {
                        // Ignore container insert issues
                    }
                }

                containerIdx = cEndCap;
            }

            try {
                institution.addAidBox(aidBox);
            } catch (Exception e) {
                // Ignore duplicates
            }

            index = containersEnd;
        }
    }
}
