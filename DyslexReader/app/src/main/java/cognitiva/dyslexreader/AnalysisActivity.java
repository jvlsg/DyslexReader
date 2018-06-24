package cognitiva.dyslexreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        /**
         * Botão para retorno à Reader ACtivity
         */
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
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
        return R.style.AppTheme_Dark;

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
            setTheme(loadTheme());
            setBackground();

            //Isso faz com que recarregue a interface corretamente, mas reseta a posição da palavra
            recreate();
        }

    }


}
