/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.write;

import net.Logging;
import net.rising.file.EFile;
import net.rising.read.EReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author crysi
 */
public class EWriter {

    private static final Logging LOGGING = new Logging();
    private static String fileLocation;
    private static String fileName;

    public EWriter() {
        fileLocation = EFile.fileLocation;
        fileName = EFile.fileName;
    }

    public EWriter append(StringBuilder content) {
        File file = new File(fileLocation + fileName);

        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                LOGGING.log(ex.getMessage());
            }
        }

        try (FileWriter fileWriter = new FileWriter(file, true); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            if (! new EReader().getContent().isEmpty()) {
                bufferedWriter.newLine();
            }
            bufferedWriter.append(content.toString());
        } catch (IOException ex) {
            LOGGING.log(ex.getMessage());
        }

        return this;
    }

    public EWriter append(String content) {
        File file = new File(fileLocation + fileName);
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                LOGGING.log(ex.getMessage());
            }
        }

        try (FileWriter fileWriter = new FileWriter(file, true); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            if (! new EReader().getContent().isEmpty()) {
                bufferedWriter.newLine();
            }
            bufferedWriter.append(content);
        } catch (IOException ex) {
            LOGGING.log(ex.getMessage());
        }

        return this;
    }

    public EWriter setContent(String content) {
        File file = new File(fileLocation + fileName);
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                LOGGING.log(ex.getMessage());
            }
        }
        //Sets the content to nothing//
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.append("");
        } catch (IOException ex) {
            LOGGING.log(ex.getMessage());
        }

        //This adds the text to the file//
        try (FileWriter fileWriter = new FileWriter(file, true); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            if (content.contains("\n")) {
                StringBuilder build = new StringBuilder();
                char[] charArray = content.toCharArray();
                for (int j = 0; j < charArray.length; j++) {

                    //Checks when the character equals a new line//
                    if (charArray[j] != '\n') {
                        build.append(charArray[j]);
                    } else {
                        bufferedWriter.append(build.toString());
                        bufferedWriter.newLine();
                        build.delete(0, j);
                    }
                }
                bufferedWriter.append(build.toString());
            } else {
                fileWriter.append(content);
            }
        } catch (IOException ex) {
            LOGGING.log(ex.getMessage());
        }

        return this;
    }

    public EFile getEFile() {
        return new EFile(fileLocation, fileName);
    }

    public EReader getReader() {
        return new EReader();
    }
}
