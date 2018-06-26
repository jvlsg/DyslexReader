package cognitiva.dyslexreader;

import android.content.SharedPreferences;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SwitchPreference switchWhiteNoise;
    private SwitchPreference switchFistLastColors;

    String currentAppTheme;




    //TODO: HOLDTIME

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(loadTheme());
        setContentView(R.layout.activity_settings);


        /**
         * Botão para retorno ao menu Principal
         */
        /*ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
        /**
         *
         */
        //switchWhiteNoise = (SwitchPreference) findPr
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
            return R.style.AppTheme_Light;
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
                currentAppTheme = getString((R.string.themeValueCustom));
            }
            setTheme(loadTheme());

            //Isso faz com que recarregue a interface corretamente, mas reseta a posição da palavra
            recreate();
        }

    }




}


