package cognitiva.dyslexreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ReaderActivity extends AppCompatActivity {

    /**
     * Tudo que será usado da interface está aqui
     */
    Button btnNextWord;
    Button btnPreviousWord;
    Button btnSettings;
    Button btnAnalyzeWord;
    ProgressBar pbReadingProgress;
    TextView tvMainText;

    //O texto que foi mandado da MainActivity
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        //Pega o texto da MainActivity
        text = getIntent().getStringExtra("text");
    }

    /**
     * Para iniciar os settings
     *
     * @param v
     * Você não usa
     */
    public void onClickSettings (View v)
    {

    }

    /**
     * Quando clicar, ir pra próxima palavra
     * @param v
     */
    public void onClickNextWord (View v)
    {

    }

    /**
     * Ir para a palavra anterior
     * @param v
     */
    public void onClickPreviousWord (View v)
    {

    }

    /**
     * Analisa a palavra atual
     * @param v
     */
    public void onClickAnalyze (View v)
    {
        Intent intent = new Intent(getBaseContext(), ReaderActivity.class);
        /**
         * Inserir aqui que devemos pegar a palavra atual e mandar pra próxima activity
         */
        startActivity(intent);
    }


}
