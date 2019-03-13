package com.thor.tao.bao.manager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangpin
 * @date 2018-12-10
 */
public class RegexpUtil {
    public static Matcher getMatcher(String text, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        return pattern.matcher(text);
    }
}
