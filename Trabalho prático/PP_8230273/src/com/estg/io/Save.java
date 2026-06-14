/*
* Name: <Rúben Tiago Martins Pereira>
* Number: <8230162>
* Class: <LsircT2>
*
* Name: <Hugo Leite Martins>
* Number: <8230273>
* Class: <LsircT2>
*/

package com.estg.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Save {

    /**
     * Saves the stack trace of an exception to a file named errors.txt.
     * @param exception The exception to save
     */
    public static void SaveErrorToFile(Exception exception) {
        try (FileWriter fw = new FileWriter("errors.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("Error occurred at: " + LocalDateTime.now());
            exception.printStackTrace(pw);
            pw.println("----------------------------------------");
        } catch (IOException e) {
            System.err.println("Could not save error to file: " + e.getMessage());
        }
    }
}
