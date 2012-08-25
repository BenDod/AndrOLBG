package com.bendod.androlbg.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;

public class Utils
{

	private static PackageInfo getPackageInfo(final Context context) {
		try{
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		}catch(Exception e){
			return null;
		}
	}
	
	public static String getVersionName(final Context context) {
		final PackageInfo packageInfo = getPackageInfo(context);
		return packageInfo != null ? packageInfo.versionName : "";
	}

	/**
     * Tests if a string is blank: null, empty, or only whitespace (" ", \r\n, \t, etc)
     * @param string string to test
     * @return if stng is blank
     */
    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0)
            return true;

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isWhitespace(string.codePointAt(i)))
                return false;
        }
        return true;
    }
    
    public static String rot13(String text) {
        if (text == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        // plaintext flag (do not convert)
        boolean plaintext = false;

        final int length = text.length();
        int c;
        int capitalized;
        for (int index = 0; index < length; index++) {
            c = text.charAt(index);
            if (c == '[') {
                plaintext = true;
            } else if (c == ']') {
                plaintext = false;
            } else if (!plaintext) {
                capitalized = c & 32;
                c &= ~capitalized;
                c = ((c >= 'A') && (c <= 'Z') ? ((c - 'A' + 13) % 26 + 'A') : c)
                        | capitalized;
            }
            result.append((char) c);
        }
        return result.toString();
    }
    
    /**
     * Searches for the pattern p in the data. If the pattern is not found defaultValue is returned
     *
     * @param data
     *            Data to search in
     * @param p
     *            Pattern to search for
     * @param trim
     *            Set to true if the group found should be trim'ed
     * @param group
     *            Number of the group to return if found
     * @param defaultValue
     *            Value to return if the pattern is not found
     * @param last
     *            Find the last occurring value
     * @return defaultValue or the n-th group if the pattern matches (trimed if wanted)
     */
    public static String getMatch(final String data, final Pattern p, final boolean trim, final int group, final String defaultValue, final boolean last) {
        if (data != null) {

            String result = null;
            final Matcher matcher = p.matcher(data);

            if (matcher.find()) {
                result = matcher.group(group);
                if(result == null && matcher.groupCount()>group){
                	result = matcher.group(group+1);
                }
            }
            if (null != result) {
                return trim ? new String(result).trim() : new String(result);
                // Java copies the whole page String, when matching with regular expressions
                // later this would block the garbage collector, as we only need tiny parts of the page
                // see http://developer.android.com/reference/java/lang/String.html#backing_array
                // Thus the creating of a new String via String constructor is necessary here!!

                // And BTW: You cannot even see that effect in the debugger, but must use a separate memory profiler!
            }
        }
        return defaultValue;
    }

    /**
     * Searches for the pattern p in the data. If the pattern is not found defaultValue is returned
     *
     * @param data
     *            Data to search in
     * @param p
     *            Pattern to search for
     * @param trim
     *            Set to true if the group found should be trim'ed
     * @param defaultValue
     *            Value to return if the pattern is not found
     * @return defaultValue or the first group if the pattern matches (trimmed if wanted)
     */
    public static String getMatch(final String data, final Pattern p, final boolean trim, final String defaultValue) {
        return Utils.getMatch(data, p, trim, 1, defaultValue, false);
    }

    /**
     * Searches for the pattern p in the data. If the pattern is not found defaultValue is returned
     *
     * @param data
     *            Data to search in
     * @param p
     *            Pattern to search for
     * @param defaultValue
     *            Value to return if the pattern is not found
     * @return defaultValue or the first group if the pattern matches (trimmed)
     */
    public static String getMatch(final String data, final Pattern p, final String defaultValue) {
        return Utils.getMatch(data, p, true, 1, defaultValue, false);
    }

    /**
     * Searches for the pattern p in the data.
     *
     * @param data
     * @param p
     * @return true if data contains the pattern p
     */
    public static boolean matches(final String data, final Pattern p) {
        if (data == null) {
            return false;
        }
        // matcher is faster than String.contains() and more flexible - it takes patterns instead of fixed texts
        return p.matcher(data).find();

    }

}
