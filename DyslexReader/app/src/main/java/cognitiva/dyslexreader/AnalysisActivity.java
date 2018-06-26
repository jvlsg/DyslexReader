package cognitiva.dyslexreader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

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

public class AnalysisActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    String currentAppTheme;

    TextView tvWord;
    TextView tvMeaning;
    TextView tvPhonetics;
    Button btnPronunciation;
    //Usada para Toast de erro caso nao haja audio para tocar
    String audioErrMsg;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTheme();
        setContentView(R.layout.activity_analysis);


        tvWord = (TextView) findViewById(R.id.tvSyllable);
        tvWord.setMovementMethod(new ScrollingMovementMethod());
        tvMeaning = (TextView) findViewById(R.id.tvDefinition);
        tvMeaning.setMovementMethod(new ScrollingMovementMethod());
        btnPronunciation = findViewById(R.id.btnPronunciation);
        audioErrMsg = getResources().getString(R.string.errAudioNotAvailable);
        constraintLayout = (ConstraintLayout) findViewById(R.id.analyzeConstraintLayout);
        loadTheme();
        setBackground();

        Intent intent = getIntent();
        String wordToAnalyze = intent.getExtras().getString(getString(R.string.wordToAnalyzeKey));

        requestPermission();


        AsyncTask fetch = new FetchWord().execute(wordToAnalyze);


        /**
         * Botão para retorno à Reader ACtivity
         */
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(AnalysisActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(AnalysisActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(AnalysisActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void setBackground()
    {
        if(currentAppTheme.equals(getString(R.string.themeValueLight)))
        {
            tvWord.setBackgroundColor(getResources().getColor(R.color.colorTextView_light));
        }
        else if(currentAppTheme.equals(getString(R.string.themeValueDark)))
        {
            tvWord.setBackgroundColor(getResources().getColor(R.color.colorTextView_dark));
        }
    }

    public void loadTheme()
    {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        preferences.registerOnSharedPreferenceChangeListener(this);
        String currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        if(currentAppTheme.equals(getString(R.string.themeValueLight)))
        {
            setTheme(R.style.AppTheme_Light);
        }
        else if(currentAppTheme.equals(getString(R.string.themeValueDark)))
        {
            setTheme(R.style.AppTheme_Dark);
        }
        else
        {
            MainActivity.setCustomTheme(preferences, this,
                    new View[]{constraintLayout},
                    new Button[] {btnPronunciation},
                    new TextView[] {tvWord, tvMeaning, tvPhonetics});
            //TODO: Colocar o tema custom aqui
            //return R.style.AppTheme_Dark;
        }


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.themeKey)))
        {
            String mode = sharedPreferences.getString(key, getString(R.string.themeValueLight));
            if(mode.equals(getString(R.string.themeValueDark)))
            {
                currentAppTheme = getString(R.string.themeValueDark);
            }
            else if(mode.equals(getString(R.string.themeValueLight)))
            {
                currentAppTheme = getString(R.string.themeValueLight);
            }
            else
            {
                currentAppTheme = getString(R.string.themeValueCustom);
            }
            loadTheme();
            setBackground();

            //Isso faz com que recarregue a interface corretamente, mas reseta a posição da palavra
            recreate();
        }

    }

    public class FetchWord extends AsyncTask<String, Void, Word> {

        private static final String API_KEY = "33f1899bae9a7328fd0020ed3710587667a3c3fa92cade914";

        @Override
        protected Word doInBackground(String... strings) {

            ArrayList<Pair<String, String>> definitions = getDefinitions(strings[0]);
            Pair<String[], Integer> hyphenation = getHyphenation(strings[0]);
            String url = getAudioURL(strings[0]);
            Word word = null;

            if ((url != null)
                    && (downloadAudioFile(strings[0], url))
                    && (definitions != null)
                    && (hyphenation != null)) {
                word = new Word(strings[0].toLowerCase(), hyphenation, definitions);
            }

            return word;
        }

        @Override
        protected void onPostExecute(final Word word) {
            super.onPostExecute(word);
            if (word == null) {
                Log.d("Word", "word null");
                tvWord.setText(getString(R.string.errHyphenationNotAvailable));
                tvMeaning.setText(getString(R.string.errDefinitionNotAvailable));
                return;
            }

            //Concatena silabas com espaco entre elas
            Pair<String[], Integer> hyphenation = word.getHyphenation();
            StringBuilder builderHyph = new StringBuilder();
            String sAux;
            if(hyphenation!=null && hyphenation.first.length>0) {
                builderHyph.append(hyphenation.first[0]);
                for (int i = 1; i < hyphenation.first.length; ++i) {
                    sAux = " · " + hyphenation.first[i];
                    builderHyph.append(sAux);
                }
                tvWord.setText(builderHyph.toString());
            }
            else{
                tvWord.setText(getString(R.string.errHyphenationNotAvailable));
            }

            //Concatenas todas as definicoes em 1 string
            ArrayList<Pair<String, String>> definitions = word.getDefinitions();
            if(definitions != null && definitions.size()>0){
                StringBuilder builderDef = new StringBuilder();
                String type, def;
                for (int i = 0; i < definitions.size(); ++i) {
                    type = definitions.get(i).first;
                    def = definitions.get(i).second;
                    sAux = (i+1)+". ("+type+")\n"+def+"\n";
                    builderDef.append(sAux);
                }
                tvMeaning.setText(builderDef.toString());
            }
            else{
                tvMeaning.setText(getString(R.string.errDefinitionNotAvailable));
            }

            btnPronunciation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mp = MediaPlayer.create(v.getContext(), Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/dictionary/" + word.getWord() + ".mp3"));
                    mp.setLooping(false);
                    mp.start();

                }
            });
        }

        private ArrayList<Pair <String, String>> getDefinitions(String word) {
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

        private ArrayList<Pair <String, String>> getDefinitionsJSON(String jsonString) {
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

        private Pair<String[], Integer> getHyphenation(String word) {
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

        private Pair<String[], Integer> getHyphenationJSON(String jsonString) {
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

        private String getAudioURL(String word) {
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
                    Toast.makeText(getApplicationContext(), audioErrMsg, Toast.LENGTH_LONG);
                    Log.d("FetchWord-audio", "erro");
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), audioErrMsg, Toast.LENGTH_LONG);
                Log.d("FetchWord-audio", "erro");
            }

            return null;
        }

        private String getAudioUrlJson(String jsonString) {
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

        private boolean downloadAudioFile(String word, String urlString) {
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
                return true;
            } catch (Exception e) {
                Log.d("FetchWord-audioFile", "erro download");
                e.printStackTrace();
            }

            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_setting:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
