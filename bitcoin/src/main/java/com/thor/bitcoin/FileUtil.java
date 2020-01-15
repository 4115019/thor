package com.thor.bitcoin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author huangpin
 * @date 2020-01-07
 */
public class FileUtil {
    public static String getFileContent(String fullPath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(fullPath)));

        StringBuilder contentHolder = new StringBuilder();

        String lineContent = null;

        while ((lineContent = br.readLine()) != null) {
            contentHolder.append(lineContent);
        }

        br.close();

        return contentHolder.toString();

    }
}
