package com.bendod.androlbg.connector;

import java.util.regex.Pattern;

/**
 * These patterns have been optimized for speed. Improve them only if you can prove
 * that *YOUR* pattern is faster. Use RegExRealPerformanceTest to show.
 *
 * For further information about patterns have a look at
 * http://download.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html
 */
public class OLBGConstants {

	/**
     * Patterns for parsing result
     */
	public static final Pattern PATTERN_LOGIN_NAME = Pattern.compile("\'tipsterheading\'>([^<]+)</span>");
	public static final Pattern PATTERN_UNSETTLED_TIPS = Pattern.compile("\'big_money\'> (\\d+) Unsettled");
	public static final Pattern PATTERN_VIRTUAL_MONEY = Pattern.compile("\'makeatipblock\'>([0-9,]+)</span>");
	public static final Pattern PATTERN_USER_ID = Pattern.compile("about.php\\?id\\=(\\d+)\'>Profile");
	public static final Pattern PATTERN_SEARCH_OPTION_VALUE = Pattern.compile("value=(?:(?:\'([^\']+)\')|(\\d+))"); 
	public static final Pattern PATTERN_SEARCH_OPTION_TITLE = Pattern.compile(">(.+)</option>");
	public static final Pattern PATTERN_SEARCH_CONFTIME = Pattern.compile("\\((\\d+),");
	public static final Pattern PATTERN_SEARCH_ODDS = Pattern.compile(",([0-9.]+),");
	public static final Pattern PATTERN_SEARCH_BOOKIE = Pattern.compile(",\"([^\"]+)\"\\)");
	public static final Pattern PATTERN_HIDDEN_SPORT = Pattern.compile("id='sport' type='hidden' value=\'([^\']+)\'");
	public static final Pattern PATTERN_HIDDEN_SUBSPORT = Pattern.compile("id='subsport' type='hidden' value=\'([^\']+)\'");
	public static final Pattern PATTERN_HIDDEN_FEEDTABLE = Pattern.compile("id='feedtable' type='hidden' value=\'([^\']+)\'");
	public static final Pattern PATTERN_HIDDEN_COURSE = Pattern.compile("id='course' value=\'([^\']+)\'");;
}
