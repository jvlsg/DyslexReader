package cognitiva.dyslexreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    //Array que contém a posição da palavra no texto original
    ArrayList<Integer> listCoordnate = new ArrayList<Integer>();

    /**
     * Se for 0, é por highlight, se for 1 é PPP
     */
    Boolean ReadingType = false;


    //Se o programa via pintar a primeira e última letra da palavra selecionada
    Boolean swicthFirstLastColors = true;

    //Tempo que cada palavra aparece no modo de segurar
    int holdTime = 500;

    // Handler necessário para fazer as funções de segurar os botões
    android.os.Handler handler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        tvMainText = (TextView) findViewById(R.id.tvMainText);
        tvMainText.setMovementMethod(new ScrollingMovementMethod());

        //Pega o texto da MainActivity
        text = getIntent().getStringExtra("text");
        //tvMainText.setText(text);

        StringTokenizer st = new StringTokenizer(text);
        while(st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }
        getListCooridinates();
        loadUserPreferences();
        initializeText();

        btnNextWord = (Button) findViewById(R.id.btnNextWord);
        btnPreviousWord = (Button) findViewById(R.id.btnPreviousWord);

        //Seta Touch listeners, para que os botões de next e previous funcionem tanto pra hold quanto pra normal
        btnNextWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    handler.postDelayed(longNext, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    handler.removeCallbacks(longNext);
                }
                return false;
            }
        });


        btnPreviousWord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    handler.postDelayed(longPrevious, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    handler.removeCallbacks(longPrevious);
                }
                return false;
            }
        });
    }

    //Função para chamar uma ou várias vezes o nextWord
    private Runnable longNext = new Runnable() {
        @Override
        public void run() {
            onClickNextWord();
            handler.postDelayed(this, holdTime);
        }
    };

    //Função para chamar uma ou várias vezes o previousWord
    private Runnable longPrevious = new Runnable() {
        @Override
        public void run() {
            onClickPeviousWord();
            handler.postDelayed(this, holdTime);
        }
    };



    /**
     * Função que coloca os valores na listCoordnates
     */
    public void getListCooridinates()
    {
        int start = 0;
        Boolean flag = false;
        if (text.charAt(start) != ' ' && text.charAt(start) != '\n' && text.charAt(start) != '\t' && flag == false)
        {
            listCoordnate.add(start);
        }
        for (; start < text.length(); ++start)
        {
            if (text.charAt(start) == ' ' || text.charAt(start) == '\n' || text.charAt(start) == '\t')
            {
                flag = true;
            }
            else
            {
                if(flag == true)
                {
                    flag = false;
                    listCoordnate.add(start);
                }
            }
        }
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
            s.setSpan(new ForegroundColorSpan(0xFFFF0000), listCoordnate.get(wordPosition), listCoordnate.get(wordPosition+1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            checkFirstLastColors(s);
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
            s.setSpan(new ForegroundColorSpan(0xFF00FF00), listCoordnate.get(wordPosition), listCoordnate.get(wordPosition)+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(0xFF0000FF), listCoordnate.get(wordPosition) + list.get(wordPosition).length() - 1, listCoordnate.get(wordPosition) + list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
     */
    public void onClickNextWord()
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
                s.setSpan(new ForegroundColorSpan(0xFFFF0000), listCoordnate.get(wordPosition), listCoordnate.get(wordPosition) + list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                checkFirstLastColors(s);
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
     */
    public void onClickPeviousWord()
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
                wordPosition--;
                s.setSpan(new ForegroundColorSpan(0xFFFF0000), listCoordnate.get(wordPosition), listCoordnate.get(wordPosition) + list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                checkFirstLastColors(s);
                selectedWord = list.get(wordPosition);
            }
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
