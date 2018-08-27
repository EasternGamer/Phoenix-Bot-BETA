/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.read;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import net.Logging;
import net.rising.file.EFile;
import net.rising.write.EWriter;

/**
 *
 * @author crysi
 */
public class EReader {

    private static final Logging LOGGING = new Logging();
    private static String fileLocation;
    private static String fileName;

    public EReader() {
        fileLocation = EFile.fileLocation;
        fileName = EFile.fileName;
    }

    public StringBuilder getStringBuilder() {
        StringBuilder build = new StringBuilder();
        File file = new File(fileLocation + fileName);
        try (FileReader reader = new FileReader(file); BufferedReader bufferedStream = new BufferedReader(reader)) {
            bufferedStream.lines()
                    .parallel()
                    .forEach(line -> {
                        build.append(line).append("\n");
                    });
        } catch (IOException ex) {
            LOGGING.log(ex.getMessage());
        }
        return build;
    }

    public String getContent() {
        StringBuilder build = new StringBuilder();
        File file = new File(fileLocation + fileName);
        try (FileReader reader = new FileReader(file); BufferedReader bufferedStream = new BufferedReader(reader)) {
            bufferedStream.lines()
                    .parallel()
                    .forEach(line -> {
                        build.append(line).append("\n");
                    });
        } catch (IOException ex) {
            LOGGING.log(ex.getMessage());
        }
        return build.toString();
    }

    public EFile getEFile() {
        return new EFile(EFile.fileLocation, EFile.fileName);
    }

    public EWriter getWriter() {
        return new EWriter();
    }
}
