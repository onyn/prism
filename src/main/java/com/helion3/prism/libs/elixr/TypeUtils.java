package com.helion3.prism.libs.elixr;

import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public class TypeUtils {

    /**
     * Is the string numeric
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static float formatDouble(double val) {
        return Float.parseFloat((new DecimalFormat("#.##")).format(val));
    }

    /**
     * Replaces string template placeholders with values in a Hashtable.
     * Text should be formatted with %(key) type placeholders.
     */
    public static String getStringFromTemplate(String msg, Hashtable<String, String> replacer) {
        if (msg != null && !replacer.isEmpty()) {
            for (Map.Entry<String, String> entry : replacer.entrySet()) {
                msg = msg.replace("%(" + entry.getKey() + ")", entry.getValue());
            }
        }
        return msg;
    }

    /**
     * Converts colors place-holders.
     */
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Strips all text format codes - from colors codes like &2 to text format codes
     * like &k
     */
    public static String stripTextFormatCodes(String text) {
        return ChatColor.stripColor(text.replaceAll("(&+([a-z0-9A-Z])+)", ""));
    }

    /**
     * Joins an arraylist together by a delimiter
     */
    public static String join(List<String> s, String delimiter) {
        StringBuilder buffer = new StringBuilder();
        Iterator<?> iter = s.iterator();

        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }

        return buffer.toString();
    }

    /**
     * Method to join array elements of type string
     *
     * @param inputArray Array which contains strings
     * @param glueString String between each array element
     * @return String containing all array elements separated by glue string
     * @author Hendrik Will, imwill.com, bug fixes by viveleroi
     */
    public static String join(String[] inputArray, String glueString) {
        String output = "";
        if (inputArray.length > 0) {
            StringBuilder sb = new StringBuilder();
            if (!inputArray[0].isEmpty()) {
                sb.append(inputArray[0]);
            }

            for (int i = 1; i < inputArray.length; ++i) {
                if (!inputArray[i].isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(glueString);
                    }

                    sb.append(inputArray[i]);
                }
            }

            output = sb.toString();
        }

        return output;
    }

    public static String strToUpper(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Java implementation of preg_match_all by https://github.com/raimonbosch
     */
    public static String[] preg_match_all(Pattern p, String subject) {
        Matcher m = p.matcher(subject);
        StringBuilder out = new StringBuilder();
        boolean split = false;
        while (m.find()) {
            out.append(m.group());
            out.append("~");
            split = true;
        }
        return (split) ? out.toString().split("~") : new String[0];
    }

    public static int subStrOccurences(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = str.indexOf(findStr, lastIndex);
            if (lastIndex != -1) {
                ++count;
                lastIndex += findStr.length();
            }
        }

        return count;
    }

    public static String padStringRight(String str, int desiredLength) {
        if (str.length() >= desiredLength) {
            return str.substring(0, desiredLength);
        }
        StringBuilder sb = new StringBuilder();
        int rest = desiredLength - str.length();

        for (int i = 1; i < rest; ++i) {
            sb.append(" ");
        }

        return str + sb;
    }
}
