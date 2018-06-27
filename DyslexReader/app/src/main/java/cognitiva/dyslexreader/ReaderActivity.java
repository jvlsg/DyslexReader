package cognitiva.dyslexreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReaderActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    /**
     * Tudo que será usado da interface está aqui
     */
    Button btnNextWord;
    Button btnPreviousWord;
    Button btnSettings;
    Button btnAnalyzeWord;
    ProgressBar pbReadingProgress;
    TextView tvMainText;

    ConstraintLayout constraintLayout;

    //O texto que foi mandado da MainActivity
    String text;

    //Palavra atual, tanto no modo highlight quanto PPP
    String selectedWord;

    //Posição no ArrayList da palavra atual
    int wordPosition = -1;

    //Array que contém todas as palavras em uma lista
    ArrayList<String> list = new ArrayList<String>();

    /**
     * Array que contém a posição em caracteres da palavra no texto original.
     * Usado para setar as posições de Spannable para alterar a cor de highlight / prefixo / sufixo
     * da palavra atual.
    */
    ArrayList<Integer> listCoordnate = new ArrayList<Integer>();

    /**
     * Se for 0, é por highlight, se for 1 é PPP
     */
    Boolean ReadingType = false;


    //Se o programa via pintar a primeira e última letra da palavra selecionada
    Boolean swicthFirstLastColors = true;

    //Tempo que cada palavra aparece no modo de segurar, em milisegundos
    int holdTime;

    // Handler necessário para fazer as funções de segurar os botões
    android.os.Handler handler = new android.os.Handler();

    Boolean switchWhiteNoise;

    //Usado para carregar o tema atual do aplicativo
    String currentAppTheme;

    //Usado para tocar o ruído branco
    MediaPlayer mp;

    //Usadas para colorir o texto
    int currentHighlightColor;
    int currentPrefixColor;
    int currentSuffixColor;


    @Override
    protected  void onPause()
    {
        super.onPause();
        if(mp != null)
        {
            mp.pause();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUserPreferences();
        themeStyle();
        setContentView(R.layout.activity_reader);

        btnNextWord = (Button) findViewById(R.id.btnNextWord);
        btnPreviousWord = (Button) findViewById(R.id.btnPreviousWord);
        btnAnalyzeWord = (Button) findViewById(R.id.btnAnalyzeWord);
        pbReadingProgress = (ProgressBar) findViewById(R.id.pbReadingProgress);

        constraintLayout = (ConstraintLayout) findViewById(R.id.readerConstraintLayout);


        tvMainText = (TextView) findViewById(R.id.tvMainText);
        //Aplica o tema atual à activity
        themeStyle();
        tvMainText.setMovementMethod(new ScrollingMovementMethod());

        //Pega o texto da MainActivity
        text = getIntent().getStringExtra("text");

        StringTokenizer st = new StringTokenizer(text);
        while(st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }

        getListCoordinates();
        updateCurrentColors();
        initializeText();




        mp = MediaPlayer.create(this, R.raw.whitenoise);
        mp.setLooping(true);
        playWhiteNoise();


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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mp = MediaPlayer.create(this, R.raw.whitenoise);
        mp.setLooping(true);
        playWhiteNoise();

    }

    /**
     *Função que coloca o tema na interface da Activity
     */
    public void themeStyle()
    {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        preferences.registerOnSharedPreferenceChangeListener(this);

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
                    getSupportActionBar(),
                    new View[]{constraintLayout},
                    new Button[] {btnPreviousWord, btnNextWord, btnAnalyzeWord, btnSettings},
                    new TextView[] {tvMainText});
        }
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
     * Usada para carregar as preferencias de modo de leitura do usuário
     */
    void loadUserPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Listener para mudanças de Configurações
        preferences.registerOnSharedPreferenceChangeListener(this);

        String mode = preferences.getString(getString(R.string.readingModeKey), getString(R.string.readingModeValueWordByWord));
        if(mode.equals(getString(R.string.readingModeValueHighlight)))
            ReadingType = false;
        else
            ReadingType = true;

        currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));

        swicthFirstLastColors = preferences.getBoolean(getString(R.string.firstLastColorsKey), false);

        holdTime = 100 * preferences.getInt(getString(R.string.holdTimeKey),R.integer.holdTimeDefault);

        switchWhiteNoise = preferences.getBoolean(getString(R.string.whiteNoiseKey), false);
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
            tvMainText.setGravity(Gravity.LEFT);
            tvMainText.setText(text, TextView.BufferType.SPANNABLE);
            Spannable s = (Spannable) tvMainText.getText();
            tvMainText.setTextSize(20);
            wordPosition++;
            s.setSpan(new ForegroundColorSpan(currentHighlightColor), listCoordnate.get(wordPosition), listCoordnate.get(wordPosition) + list.get(wordPosition).length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            checkFirstLastColors(s);
            selectedWord = list.get(wordPosition);

        }
        else
        {
            tvMainText.setTextSize(50);
            wordPosition++;
            tvMainText.setGravity(Gravity.LEFT);
            selectedWord = list.get(wordPosition);
            tvMainText.setText(selectedWord, TextView.BufferType.SPANNABLE);
            checkFirstLastColors();
        }
    }



    /***
     * Atualiza as variáveis de cor baseado no tema atual.
     * O tema atual está armazenado em currentAppTheme
     */
    void updateCurrentColors()
    {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        if(currentAppTheme.equals(getString(R.string.themeValueLight))){
            currentHighlightColor = getResources().getColor(R.color.colorTextHighlight_light);
            currentPrefixColor = getResources().getColor(R.color.colorTextPrefix_light);
            currentSuffixColor = getResources().getColor(R.color.colorTextSuffix_light);
            tvMainText.setBackgroundColor(getResources().getColor(R.color.colorTextView_light));
        }
        if(currentAppTheme.equals(getString(R.string.themeValueDark))){
            currentHighlightColor = getResources().getColor(R.color.colorTextHighlight_dark);
            currentPrefixColor = getResources().getColor(R.color.colorTextPrefix_dark);
            currentSuffixColor = getResources().getColor(R.color.colorTextSuffix_dark);
            tvMainText.setBackgroundColor(getResources().getColor(R.color.colorTextView_dark));
        }
        if(currentAppTheme.equals(getString(R.string.themeValueCustom)))
        {
            currentHighlightColor = preferences.getInt(this.getString(R.string.themeCustomHighlightKey), R.color.colorTextPrimary_light);
            currentPrefixColor = preferences.getInt(this.getString(R.string.themeCustomPrefixKey), R.color.colorTextPrimary_light);
            currentSuffixColor = preferences.getInt(this.getString(R.string.themeCustomSuffixKey), R.color.colorTextPrimary_light);
        }

    }

    /***
     * Usada para tocar o Ruído branco quando a coniguração está habilitada
     */
    public void playWhiteNoise()
    {
        System.out.println("playWhiteNoise");
        if(switchWhiteNoise == true)
        {
            System.out.println("PLAY");
            mp.start();
        }
        else
        {
            System.out.println("STOP");
            mp.stop();
        }
    }


    /**
     * Função que coloca os valores na listCoordnates
     */
    public void getListCoordinates()
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
     * Verifica se o switch pra colorir a primeira e última letra está como true
     * Se sim, então colore
     * Apenas para o modo de Highlight
     * @param s
     */
    public void checkFirstLastColors(Spannable s)
    {
        if(swicthFirstLastColors == true)
        {
            s.setSpan(new ForegroundColorSpan(currentPrefixColor), listCoordnate.get(wordPosition), listCoordnate.get(wordPosition)+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(currentSuffixColor), listCoordnate.get(wordPosition) + list.get(wordPosition).length() - 1, listCoordnate.get(wordPosition) + list.get(wordPosition).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            s.setSpan(new ForegroundColorSpan(currentPrefixColor), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(currentSuffixColor), selectedWord.length()-1, selectedWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                tvMainText.setText(selectedWord, TextView.BufferType.SPANNABLE);
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
                s.setSpan(new ForegroundColorSpan(currentHighlightColor),
                        listCoordnate.get(wordPosition),
                        listCoordnate.get(wordPosition) + list.get(wordPosition).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                tvMainText.setText(selectedWord, TextView.BufferType.SPANNABLE);
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
                s.setSpan(new ForegroundColorSpan(currentHighlightColor),
                        listCoordnate.get(wordPosition),
                        listCoordnate.get(wordPosition) + list.get(wordPosition).length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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


    /***
     * Atualiza a UI baseado em mudanças nas configuarações de preferencias dos usuarios
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ///FIRST LAST COLORS
        if(key.equals(getString(R.string.firstLastColorsKey))){
            swicthFirstLastColors = sharedPreferences.getBoolean(key,false);

            if(wordPosition == list.size()-1){
                onClickPeviousWord();
                onClickNextWord();
            }else {
                onClickNextWord();
                onClickPeviousWord();
            }
        }
        ///WHITE NOISE
        if(key.equals(getString(R.string.whiteNoiseKey))){
            switchWhiteNoise = sharedPreferences.getBoolean(key,false);
            playWhiteNoise();
        }
        ///HOLD TIME
        if(key.equals(getString(R.string.holdTimeKey))){
            holdTime =  100 * sharedPreferences.getInt(key, R.integer.holdTimeDefault);
        }
        ///READING MODE
        if(key.equals(getString(R.string.readingModeKey))){
            String mode = sharedPreferences.getString(key,getString(R.string.readingModeValueWordByWord));
            if(mode.equals(getString(R.string.readingModeValueHighlight)))
                ReadingType = false;
            else
                ReadingType = true;
            initializeText();
        }

        //THEME
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

            themeStyle();
            updateCurrentColors();
            //Isso faz com que recarregue a interface corretamente, mas reseta a posição da palavra
            recreate();
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
