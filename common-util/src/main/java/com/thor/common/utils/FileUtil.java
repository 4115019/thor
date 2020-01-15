package com.thor.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangpin
 * @date 2019-06-14
 */
public class FileUtil {
    public static List<String> getListFile(String path) {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = FileUtil.class.getClassLoader().getResourceAsStream(path);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            List<String> fileList = new ArrayList<>();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                fileList.add(str);
            }
            inputStream.close();
            bufferedReader.close();
            return fileList;

        } catch (Exception e) {
            return null;
        }
    }

    public static List<Integer> getListIntegerFile(String path) {
        List<String> listFile = getListFile(path);
        if (listFile == null || listFile.size() == 0) {
            return new ArrayList<>();
        }

        return new ArrayList<Integer>() {{
            listFile.forEach(one -> add(Integer.parseInt(one)));
        }};
    }
}
