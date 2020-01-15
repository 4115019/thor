package com.thor.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangpin
 * @date 2019-07-26
 */
public class StreamUtil {

    private static class Model{
        String name;
    }

    public static void main(String[] args) {

        List<String> stringList = new ArrayList<String>(){{
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
        }};

        System.out.println(stringList.stream().limit(100).collect(Collectors.toList()));
    }
}
