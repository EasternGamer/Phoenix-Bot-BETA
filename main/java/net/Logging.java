/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import java.util.Date;

/**
 * @author crysi
 */
public class Logging {

    public void log(String error) {
        System.err.println("[" + timeNow() + "][FileAPI] " + error);
    }

    private String timeNow() {
        Date dateBase = new Date();
        String[] dataDate = dateBase.toString().split(" ");
        StringBuilder time = new StringBuilder();
        time.append(dataDate[3]);
        return time.toString();
    }
}
