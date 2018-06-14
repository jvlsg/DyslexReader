package cognitiva.dyslexreader;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AnalysisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        /*Intent intent = getIntent();
        String wordToAnalyze = intent.getExtras().getString(getString(R.string.wordToAnalyzeKey));
        */
        //wordToAnalyze = "Duck";

        /**
         * Botão para retorno à Reader ACtivity
         */
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
