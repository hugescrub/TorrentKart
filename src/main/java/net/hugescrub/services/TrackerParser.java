package net.hugescrub.services;

import net.hugescrub.models.GamesResults;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackerParser {
    private static final String BASE_URL = "https://it.itorrents-igruha.org";

    public static void main(String[] args) throws UnsupportedEncodingException {
        // JSONObject jsonObject = searchGames();
        TrackerParser.searchGames("monster hunter");
    }

    public static GamesResults searchGames(String searchString) {
        final String URL_PATH = "/index.php?";
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36 OPR/86.0.4363.70";
        URL url = null;
        String response = null;

        HashMap<String, String> urlEncodedMap = new HashMap<>();
        urlEncodedMap.put("do", "search");
        urlEncodedMap.put("subaction", "search");
        urlEncodedMap.put("search_start", "1");
        urlEncodedMap.put("full_search", "0");
        urlEncodedMap.put("result_from", "1");
        urlEncodedMap.put("story", searchString);

        List<String> gameLinks = null;
        List<String> gameNames = null;
        try {
            // get url-encoded string
            String requestBody = getDataString(urlEncodedMap);
            url = new URL(String.format("%s/%s%s", BASE_URL, URL_PATH, requestBody));

            var document = Jsoup.connect(url.toString())
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", USER_AGENT)
                    .header("charset", "utf-8")
                    .timeout(10000)
                    .get();

            var gameData = document.select("div.article-film-title > a");
            System.out.println(gameData);

            System.out.println();

            gameLinks = new ArrayList<>();
            System.out.println("Links:\n");
            for (var elementLink : gameData) {
                gameLinks.add(elementLink.attr("href"));
                System.out.println(elementLink.attr("href"));
            }

            System.out.println();

            gameNames = new ArrayList<>();
            System.out.println("Game names:\n");
            for (var elementName : gameData) {
                gameNames.add(elementName.text());
                System.out.println(elementName.text());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GamesResults gamesResults = new GamesResults();
        gamesResults.setGameLinks(gameLinks);
        gamesResults.setGameNames(gameNames);
        return gamesResults;
    }

    public static String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
