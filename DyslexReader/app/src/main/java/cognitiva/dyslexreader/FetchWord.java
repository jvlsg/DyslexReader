package cognitiva.dyslexreader;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchWord extends AsyncTask<String, Void, Void> {

    private static final String API_KEY = "33f1899bae9a7328fd0020ed3710587667a3c3fa92cade914";

    @Override
    protected Void doInBackground(String... strings) {
        ArrayList<Pair <String, String>> definitions = getDefinitions(strings[0]);
        Pair<String[], Integer> hyphenation = getHyphenation(strings[0]);

        return null;
    }

    private static  ArrayList<Pair <String, String>> getDefinitions(String word) {
        Response response = null;

        String url = "https://api.wordnik.com/v4/word.json/" + word.toLowerCase() +
                "/definitions?limit=10&includeRelated=false&sourceDictionaries=wiktionary" +
                "&useCanonical=false&includeTags=false&api_key=" + API_KEY;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                String json = response.body().string();
                Log.d("FetchWord-definition", json);
                return getDefinitionsJSON(json);
            } catch (IOException e) {
                Log.d("FetchWord-definition", "erro");
                e.printStackTrace();
            }
        } else {
            Log.d("FetchWord-definition", "erro");
        }

        return null;
    }

    private static ArrayList<Pair <String, String>> getDefinitionsJSON(String jsonString) {
        JSONArray json = null;
        JSONObject def = null;
        int arraySize = 0;
        ArrayList<Pair <String, String>> definitions = new ArrayList<>();

        try {
            json = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (json != null) {
            arraySize = json.length();
            for (int i = 0; i < arraySize; ++i) {
                try {
                    def = json.getJSONObject(i);
                    definitions.add(Pair.create(def.getString("partOfSpeech"), def.getString("text")));
                    Log.d("FetchWord-partOfSpeech", def.getString("partOfSpeech"));
                    Log.d("FetchWord-text", def.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        } else {
            Log.d("FetchWord-definition", "erro json");
            return null;
        }

        return definitions;
    }

    private static Pair<String[], Integer> getHyphenation(String word) {
        Response response = null;

        String url = "https://api.wordnik.com/v4/word.json/" + word.toLowerCase() +
                "/hyphenation?useCanonical=false&limit=50&api_key=" + API_KEY;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                String json = response.body().string();
                Log.d("FetchWord-hyphenation", json);
                return getHyphenationJSON(json);
            } catch (IOException e) {
                Log.d("FetchWord-hyphenation", "erro");
                e.printStackTrace();
            }
        } else {
            Log.d("FetchWord-hyphenation", "erro");
        }

        return null;
    }

    private static Pair<String[], Integer> getHyphenationJSON(String jsonString) {
        JSONArray json = null;
        JSONObject sy = null;
        int arraySize = 0;
        Integer stress = 0;
        String[] syllable = null;

        try {
            json = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (json != null) {
            arraySize = json.length();
            syllable = new String[arraySize];
            for (int i = 0; i < arraySize; ++i) {
                try {
                    sy = json.getJSONObject(i);
                    syllable[i] = sy.getString("text");
                    if (sy.optString("type") == "stress") {
                        stress = i;
                    }
                    Log.d("hyphenation-syllable", syllable[i]);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            Log.d("hyphenation-stress", String.valueOf(stress));
        } else {
            Log.d("FetchWord-hyphenation", "erro json");
            return null;
        }

        return Pair.create(syllable, stress);
    }
}
