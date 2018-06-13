package cognitiva.dyslexreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

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

    //Palavra atual, tanto no modo highlight quanto PPP
    String selectedWord;

    //Posição no ArrayList da palavra atual
    int wordPosition = -1;

    //Array que contém todas as palavras em uma lista
    ArrayList<String> list = new ArrayList<String>();

    /**
     * Se for 0, é por highlight, se for 1 é PPP
     */
    Boolean ReadingType = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        tvMainText = (TextView) findViewById(R.id.tvMainText);
        //Pega o texto da MainActivity
        text = getIntent().getStringExtra("text");
        //tvMainText.setText(text);

        StringTokenizer st = new StringTokenizer(text);
        while(st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }
        initializeText();
    }

    /**
     * Quando o texto for incializado pela primeira vez
     */
    public void initializeText()
    {
        if(ReadingType == false)
        {
            //Botar o texto inteiro aqui
        }
        else
        {
            tvMainText.setTextSize(50);
            wordPosition++;
            selectedWord = list.get(wordPosition);
            tvMainText.setText(selectedWord);
        }
    }

    /**
     * Quando você apertar o botão de próximo, ele pega o próximo do array
     * @param v
     */
    public void nextWord(View v)
    {
        if (wordPosition < list.size() - 1)
        {
            if (ReadingType == true)
            {
                //Se for PPP...
                wordPosition++;
                selectedWord = list.get(wordPosition);
                tvMainText.setText(selectedWord);
            }
            else
            {
                //Se for highlight...
            }
        }
        else
        {
            return;
        }
    }


    /**
     * Quando você apertar pra voltar palavra, ele pega o anterior
     * @param v
     */
    public void previousWord(View v)
    {
        if(wordPosition >= 0)
        {
            if (ReadingType == true)
            {
                //Se for por PPP
                wordPosition--;
                selectedWord = list.get(wordPosition);
                tvMainText.setText(selectedWord);
            }
            else
            {
                //Se for por highlight...

            }
        }
        else
        {

        }
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
        Intent intent = new Intent(getBaseContext(), AnalysisActivity.class);
        /**
         * Inserir aqui que devemos pegar a palavra atual e mandar pra próxima activity
         */
        startActivity(intent);
    }


}
