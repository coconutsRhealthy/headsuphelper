package com.lennart.model.action.actionbuilders.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LPO21630 on 9-3-2018.
 */
public class HandHistoryReader {

    public static void main(String[] args) throws Exception {
        String x = System.getProperty("user.home");

        System.out.println(x);

        File xmlFile = new File("/Users/LennartMac/sjaak.xml");
        Reader fileReader = new FileReader(xmlFile);
        BufferedReader bufReader = new BufferedReader(fileReader);

        String line = bufReader.readLine();

        List<String> xmlLines = new ArrayList<>();

        while(line != null) {
            xmlLines.add(line);
            line = bufReader.readLine();
        }

        System.out.println("wacht");


        bufReader.close();
        fileReader.close();

    }


    private List<String> readXmlFile() throws Exception  {
        File xmlFile = new File("/Users/LennartMac/sjaak.xml");
        Reader fileReader = new FileReader(xmlFile);
        BufferedReader bufReader = new BufferedReader(fileReader);

        String line = bufReader.readLine();

        List<String> xmlLines = new ArrayList<>();

        while(line != null) {
            xmlLines.add(line);
            line = bufReader.readLine();
        }

        bufReader.close();
        fileReader.close();

        return xmlLines;
    }



}
