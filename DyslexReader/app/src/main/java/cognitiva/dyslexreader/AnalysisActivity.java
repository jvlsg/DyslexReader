package cognitiva.dyslexreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

public class AnalysisActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    String currentAppTheme;

    TextView tvWord;
    TextView tvMeaning;
    TextView tvPhonetics;
    Button btnPrinunciation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(loadTheme());
        setContentView(R.layout.activity_analysis);

        tvWord = (TextView) findViewById(R.id.tvSyllable);
        tvWord.setMovementMethod(new ScrollingMovementMethod());

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
        else
        {
            //TODO: colocar o bacckground custom aqui
        }
    }

    public int loadTheme()
    {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        preferences.registerOnSharedPreferenceChangeListener(this);
        String currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        if(currentAppTheme.equals(getString(R.string.themeValueLight)))
        {
            return R.style.AppTheme_Light;
        }
        else if(currentAppTheme.equals(getString(R.string.themeValueDark)))
        {
            return R.style.AppTheme_Dark;
        }
        else
        {
            //TODO: Colocar o tema custom aqui
            return R.style.AppTheme_Dark;
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
            setTheme(loadTheme());
            setBackground();

            //Isso faz com que recarregue a interface corretamente, mas reseta a posição da palavra
            recreate();
        }

    }


}
