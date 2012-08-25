package com.bendod.androlbg.connector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.bendod.androlbg.LabelValuePair;
import com.bendod.androlbg.Tip;
import com.bendod.androlbg.enumerations.StatusCode;
import com.bendod.androlbg.utils.Utils;
import com.bendod.androlbg.network.Network;
import android.content.Context;

public abstract class OLBGParser
{
    private static List<LabelValuePair> parseSearch(final String pageContent, final String selectId) {
        if (Utils.isBlank(pageContent)) {
            return null;
        }

        String page = pageContent;
        final List<LabelValuePair> searchResult = new ArrayList<LabelValuePair>();

		if("eventid".equals(selectId)) {
			String sport = Utils.getMatch(page, OLBGConstants.PATTERN_HIDDEN_SPORT, "");
			searchResult.add(new LabelValuePair(sport,"sport"));
			String subsport = Utils.getMatch(page, OLBGConstants.PATTERN_HIDDEN_SUBSPORT, "");
			searchResult.add(new LabelValuePair(subsport,"subsport"));
			String feedtable = Utils.getMatch(page, OLBGConstants.PATTERN_HIDDEN_FEEDTABLE, "feedtable");
			searchResult.add(new LabelValuePair(feedtable,"feedtable"));
		}
		
        int startPos = page.indexOf("<select id=\'" + selectId + "\'");
        if (startPos == -1) {
            return null;
        }

        page = page.substring(startPos); // cut on <table

        startPos = page.indexOf('>');
        int endPos = page.indexOf("</select>");
        if (startPos == -1 || endPos == -1) {
            return null;
        }

        page = page.substring(startPos + 1, endPos); // cut between <table> and </table>

        final String[] rows = page.split("<option ");
        final int rows_count = rows.length;

        for (int z = 1; z < rows_count; z++) {
            String row = rows[z];
            String optionValue = null;
            String optionTitle = null;

            // value
            String result = Utils.getMatch(row, OLBGConstants.PATTERN_SEARCH_OPTION_VALUE, null);
            if (null != result) {
            	optionValue = result;
            }

            // title
            result = Utils.getMatch(row, OLBGConstants.PATTERN_SEARCH_OPTION_TITLE, null).replace("&nbsp;", "");
            if (null != result) {
            	optionTitle = result;
            }
            
            searchResult.add(new LabelValuePair(optionTitle, optionValue));
        }
		if("eventid".equals(selectId) && searchResult.size() == 3) {
			return null;
		}

        return searchResult;
    }

    public static List<LabelValuePair> searchById(final Context context, final int tipId) throws IOException, URISyntaxException {
        final String uri = "http://www.olbg.com/members/maketip.php";
        final String params = "id=" + Integer.toString(tipId);
        final String page = Login.getRequestLogged(uri, params, true);

        if (Utils.isBlank(page)) {
			List<LabelValuePair> searchResult = new ArrayList<LabelValuePair>();
			if(!Network.hasConnection(context)){
				searchResult.add(new LabelValuePair("Check your Internet connection.", "Error!"));
			}else{
				searchResult.add(new LabelValuePair("Unknown Error.", "Error!"));
			}
            return searchResult;
        }

        List<LabelValuePair> searchResult = parseSearch(page, "eventid");
		if(searchResult == null || searchResult.size() == 0) {
			searchResult = new ArrayList<LabelValuePair>();
			searchResult.add(new LabelValuePair("No Events...", "Error!"));
		}
        return searchResult;
    }

    public static List<LabelValuePair> searchByEvent(final String eventId, final String feedTable) throws IOException, URISyntaxException {
    	final String uri = "http://www.olbg.com/members/maketip2/markets.php";
        final String params = "e=" + URLEncoder.encode(eventId, "UTF-8").replace("+", "%20") + "&t=" + feedTable + "&sid=" + Math.random();
        final String page = Login.getRequestLogged(uri, params, false);

        if (Utils.isBlank(page)) {
            return null;
        }

        final List<LabelValuePair> searchResult = parseSearch(page, "market");
        return searchResult;
    }
	
	public static List<LabelValuePair> searchByMarket(final int marketId, final String eventId, final String feedTable) throws URISyntaxException, IOException
	{
    	final String uri = "http://www.olbg.com/members/maketip2/selections.php";
        final String params = "e=" + URLEncoder.encode(eventId, "UTF-8").replace("+", "%20") + "&t=" + feedTable + "&m=" + marketId + "&sid=" + Math.random();
        final String page = Login.getRequestLogged(uri, params, false);

        if (Utils.isBlank(page)) {
            return null;
        }

        final List<LabelValuePair> searchResult = parseSearch(page, "selection");
        return searchResult;
    }

    public static List<LabelValuePair> searchBySelection(final String selection, final String eventId, final String subSport, final String selectId) throws URISyntaxException, IOException
	{
    	final String uri = "http://www.olbg.com/members/maketip2/bettype.php";
        final String params = "e=" + URLEncoder.encode(eventId, "UTF-8").replace("+", "%20") + "&sp=" + URLEncoder.encode(subSport, "UTF-8").replace("+", "%20") + 
				"&s=" + URLEncoder.encode(selection, "UTF-8").replace("+", "%20") + "&sid=" + Math.random();
        final String page = Login.getRequestLogged(uri, params, false);

        if (Utils.isBlank(page)) {
            return null;
        }

        final List<LabelValuePair> searchResult = parseSearch(page, selectId);
        return searchResult;
    }

    public static StatusCode confirmTip(Tip tip) throws URISyntaxException, IOException {
		final String uri = "http://www.olbg.com/members/maketip2/confirm.php";
        final String params = "eventid=" + URLEncoder.encode(tip.eventId, "UTF-8").replace("+", "%20")
        			+ "&market=" + tip.marketId + "&selection=" + URLEncoder.encode(tip.selection, "UTF-8").replace("+", "%20")
        			+ "&nap=" + tip.nap + "&nb=" + tip.nb + "&stake=" + tip.stake + "&bettype=" + tip.betType
        			+ "&price=" + URLEncoder.encode(tip.price, "UTF-8").replace("+", "%20") + "&post_fb=false&post_tw=false" 
        			+ "&comment=" + URLEncoder.encode(tip.comment+"\nvia AndrOLBG", "UTF-8").replace("+", "%20")
        			+ "&sport=" + URLEncoder.encode(tip.sport, "UTF-8").replace("+", "%20") + "&subsport=" + URLEncoder.encode(tip.subSport, "UTF-8").replace("+", "%20")
        			+ "&tipsterid=" + Login.getActualUserId() + "&feedtable=" + tip.feedTable + "&sid=" + Math.random();
        final String page = Login.getRequestLogged(uri, params, false);
        if (Utils.isBlank(page)) {
            return StatusCode.NO_DATA_FROM_SERVER;
        }
        int startPos = page.indexOf("confirm_save");
        if (startPos == -1) {
            return StatusCode.TIP_POST_ERROR;
        }
        String saveFunc = page.substring(startPos);
        int conftime = Integer.parseInt(Utils.getMatch(saveFunc, OLBGConstants.PATTERN_SEARCH_CONFTIME, "-1"));
        double odds = Float.parseFloat(Utils.getMatch(saveFunc, OLBGConstants.PATTERN_SEARCH_ODDS, "-1"));
        String bookie = Utils.getMatch(saveFunc, OLBGConstants.PATTERN_SEARCH_BOOKIE, null);
        return confirmSave(tip, conftime, odds, bookie);
	}

    public static StatusCode confirmSave(Tip tip, int conftime, double odds, String bookie) throws URISyntaxException, IOException {
		final String uri = "http://www.olbg.com/members/maketip2/save.php";
        final String params = "eventid=" + URLEncoder.encode(tip.eventId, "UTF-8").replace("+", "%20")
        			+ "&market=" + tip.marketId + "&selection=" + URLEncoder.encode(tip.selection, "UTF-8").replace("+", "%20")
        			+ "&nap=" + tip.nap + "&nb=" + tip.nb + "&stake=" + tip.stake + "&bettype=" + tip.betType
        			+ "&price=" + URLEncoder.encode(tip.price, "UTF-8").replace("+", "%20") + "&post_fb=false&post_tw=false" 
        			+ "&comment=" + URLEncoder.encode(tip.comment+"\nvia AndrOLBG", "UTF-8").replace("+", "%20")
        			+ "&sport=" + URLEncoder.encode(tip.sport, "UTF-8").replace("+", "%20") + "&subsport=" + URLEncoder.encode(tip.subSport, "UTF-8").replace("+", "%20")
        			+ "&tipsterid=" + Login.getActualUserId() + "&feedtable=" + tip.feedTable + "&conftime=" + conftime
        			+ "&odds=" + odds + "&bookie=" + URLEncoder.encode(bookie, "UTF-8").replace("+", "%20") + "&sid=" + Math.random();
        final String page = Login.getRequestLogged(uri, params, false);
        if (Utils.isBlank(page)) {
            return StatusCode.NO_DATA_FROM_SERVER;
        }
        if (page.length() < 5) {
            return StatusCode.TIP_SAVED;
        }
		return StatusCode.TIP_POST_ERROR;
	}

}
