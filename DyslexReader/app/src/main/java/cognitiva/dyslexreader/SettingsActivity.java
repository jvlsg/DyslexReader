package cognitiva.dyslexreader;

import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    private SwitchPreference switchWhiteNoise;
    private SwitchPreference switchFistLastColors;


    //TODO: HOLDTIME

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        /**
         * Botão para retorno ao menu Principal
         */
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /**
         *
         */
        //switchWhiteNoise = (SwitchPreference) findPr
    }
}
