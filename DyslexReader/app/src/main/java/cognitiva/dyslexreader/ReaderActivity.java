package cognitiva.dyslexreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
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
    Boolean ReadingType = false;

    //Onde começa o texto pro highlight
    int startHightlight = 0;

    //Se o programa via pintar a primeira e última letra da palavra selecionada
    Boolean swicthFirstLastColors = true;





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
        loadUserPreferences();
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
            /**
             *  Cria um spannable, que é necessário
             * Incrementa pra pegar a próxima palavra
             * Colore a palavra atual, pegando o início dela com o startHighlight até a palavra
             * Adciona o startHighlight o tamanho da paavra pra ser reunitlizado depois
             */
            tvMainText.setText(text, TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) tvMainText.getText();
            tvMainText.setTextSize(20);
            wordPosition++;
            s.setSpan(new ForegroundColorSpan(0xFFFF0000), 0, list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            checkFirstLastColors(s);
            startHightlight = startHightlight + list.get(wordPosition).length();
            selectedWord = list.get(wordPosition);

        }
        else
        {
            tvMainText.setTextSize(50);
            wordPosition++;
            tvMainText.setGravity(Gravity.CENTER);
            selectedWord = list.get(wordPosition);
            checkFirstLastColors();
        }

    }


    /**
     * Usada para carregar as preferencias de modo de leitura do usuário
     */
    void loadUserPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String mode = preferences.getString(getString(R.string.readingModeKey), getString(R.string.readingModeValueWordByWord));
        if(mode.equals(getString(R.string.readingModeValueHighlight)))
            ReadingType = false;
        else
            ReadingType = true;

        swicthFirstLastColors = preferences.getBoolean(getString(R.string.firstLastColorsKey), false);
    }

    /**
     * Verifica se o switch pra colorir a primeira e última letra está como true
     * Se sim, então colore
     * Apenas para o modo de Highlight
     * @param s
     */
    public void checkFirstLastColors(Spannable s)
    {
        if(swicthFirstLastColors == true)
        {
            if(wordPosition == 0)
            {
                s.setSpan(new ForegroundColorSpan(0xFF00FF00), startHightlight, startHightlight + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(0xFF0000FF), startHightlight + list.get(wordPosition).length() - 1, startHightlight + list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else
            {
                s.setSpan(new ForegroundColorSpan(0xFF00FF00), startHightlight + 1, startHightlight + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(0xFF0000FF), startHightlight + list.get(wordPosition).length(), startHightlight + list.get(wordPosition).length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

    }

    public void checkPreviousFirstLastColors(Spannable s)
    {
        if(swicthFirstLastColors == true)
        {
            if(wordPosition == 0)
            {
                s.setSpan(new ForegroundColorSpan(0xFF00FF00), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(0xFF0000FF), list.get(wordPosition).length() - 1, list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else
            {
                s.setSpan(new ForegroundColorSpan(0xFF00FF00), startHightlight - list.get(wordPosition).length(), startHightlight - list.get(wordPosition).length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(0xFF0000FF), startHightlight - 1, startHightlight, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

    }


    /**
     * Pinta a primeira e última letra no modo PPP
     */
    public void checkFirstLastColors()
    {
        if(swicthFirstLastColors == true)
        {
            tvMainText.setText(selectedWord, TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) tvMainText.getText();
            s.setSpan(new ForegroundColorSpan(0xFF00FF00), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(0xFF0000FF), selectedWord.length()-1, selectedWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    /**
     * Quando você apertar o botão de próximo, ele pega o próximo do array
     * @param v
     */
    public void onClickNextWord(View v)
    {
        if (wordPosition < list.size() - 1)
        {
            if (ReadingType == true)
            {
                //Se for PPP...
                wordPosition++;
                selectedWord = list.get(wordPosition);
                checkFirstLastColors();
            }
            else
            {
                /**
                 *  Cria um spannable, que é necessário
                 * Incrementa pra pegar a próxima palavra
                 * Colore a palavra atual, pegando o início dela com o startHighlight até a palavra
                 * Adciona o startHighlight o tamanho da paavra pra ser reunitlizado depois
                 */
                tvMainText.setText(text, TextView.BufferType.SPANNABLE);
                Spannable s = (Spannable) tvMainText.getText();
                wordPosition++;
                s.setSpan(new ForegroundColorSpan(0xFFFF0000), startHightlight, startHightlight + list.get(wordPosition).length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                checkFirstLastColors(s);
                startHightlight = startHightlight + list.get(wordPosition).length() + 1;
                selectedWord = list.get(wordPosition);
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
    public void onClickPeviousWord(View v)
    {
        if(wordPosition > 0)
        {
            if (ReadingType == true)
            {
                //Se for por PPP
                wordPosition--;
                selectedWord = list.get(wordPosition);
                checkFirstLastColors();
            }
            else
            {
                /**
                 *  Cria um spannable, que é necessário
                 * Incrementa pra pegar a próxima palavra
                 * Colore a palavra atual, pegando o início dela com o startHighlight até a palavra
                 * Subtrai o startHighlight o tamanho da palavra pra ser reutilizado depois
                 */
                tvMainText.setText(text, TextView.BufferType.SPANNABLE);
                Spannable s = (Spannable) tvMainText.getText();
                startHightlight = startHightlight - list.get(wordPosition).length() - 1;
                wordPosition--;
                s.setSpan(new ForegroundColorSpan(0xFFFF0000), startHightlight - list.get(wordPosition).length(), startHightlight, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                checkPreviousFirstLastColors(s);
                selectedWord = list.get(wordPosition);
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
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * Analisa a palavra atual
     * @param v
     */
    public void onClickAnalyze (View v)
    {
        Intent intent = new Intent(getBaseContext(), AnalysisActivity.class);

        intent.putExtra(getString(R.string.wordToAnalyzeKey),selectedWord);
        /**
         * Inserir aqui que devemos pegar a palavra atual e mandar pra próxima activity
         */
        startActivity(intent);
    }


}
