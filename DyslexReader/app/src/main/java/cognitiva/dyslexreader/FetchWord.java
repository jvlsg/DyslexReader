package cognitiva.dyslexreader;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
        String url = getAudioURL(strings[0]);
        if (url != null) downloadAudioFile(strings[0], url);
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

    private static String getAudioURL(String word) {
        Response response = null;

        String url = "https://api.wordnik.com/v4/word.json/" + word.toLowerCase() +
                "/audio?useCanonical=false&limit=1&api_key=" + API_KEY;
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
                Log.d("FetchWord-audio", json);
                return getAudioUrlJson(json);
            } catch (IOException e) {
                Log.d("FetchWord-audio", "erro");
                e.printStackTrace();
            }
        } else {
            Log.d("FetchWord-audio", "erro");
        }

        return null;
    }

    private static String getAudioUrlJson(String jsonString) {
        JSONArray json = null;
        JSONObject def = null;
        String url;

        try {
            json = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        if (json != null) {

            try {
                def = json.getJSONObject(0);
                url = def.getString("fileUrl");
                Log.d("FetchWord-audioURL", url);
            } catch (JSONException e) {
                Log.d("FetchWord-audioURL", "erro json");
                e.printStackTrace();
                return null;
            }

        } else {
            Log.d("FetchWord-audioURL", "erro json");
            return null;
        }

        return url;
    }

    private void downloadAudioFile(String word, String urlString) {
        int count;
        try {
            URL url = new URL(urlString);
            URLConnection conection = url.openConnection();
            conection.connect();

            InputStream input = new BufferedInputStream(url.openStream());
            File folder = new File(Environment.getExternalStorageDirectory()+"/dictionary/");
            Log.d("folderexists" , ""+folder.exists());
            if (!folder.exists()) Log.d("mkdirs" , ""+folder.mkdirs());
            File file = new File(folder, word.toLowerCase()+".mp3");
            FileOutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            Log.d("FetchWord-audioFile", "arquivo mp3 baixado");
        } catch (Exception e) {
            Log.d("FetchWord-audioFile", "erro download");
            e.printStackTrace();
        }
    }
}
