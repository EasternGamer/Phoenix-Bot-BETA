/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rising.file;

import net.Logging;
import net.rising.read.EReader;
import net.rising.write.EWriter;

import java.io.File;
import java.io.IOException;

/**
 * @author crysi
 */
public class EFile {

    private static final Logging LOGGING = new Logging();
    public static String fileLocation;
    public static String fileName;

    public EFile(String fileLocation, String fileName) {
        EFile.fileLocation = fileLocation;
        EFile.fileName = fileName;
    }

    public void createNewFile(String fileLocation, String fileName) {
        File file = new File(fileLocation + fileName);
        EFile.fileLocation = fileLocation;
        EFile.fileName = fileName;

        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                LOGGING.log(ex.getMessage());
            }
        }
    }

    public File getFile() {
        File file = new File(getFileLocation() + getFileName());
        return file;
    }

    public EReader getReader() {
        return new EReader();
    }

    public EWriter getWriter() {
        return new EWriter();
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getFileName() {
        return fileName;
    }
}
