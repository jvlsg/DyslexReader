package cognitiva.dyslexreader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    //Button to ReaderActivity
    Button btnReader;
    Button btnFile;
    Button btnPaste;
    Button btnCamera;
    Button btnSettings;

    TextView tvPreview;

    ConstraintLayout constraintLayout;

    String currentAppTheme;


    /***
     * TODO: Declaration of Parser Classes
     *
     * ImageParser imageParser
     * HTTPParser httpParser
     * EPUBParser epubParser
     * PDFParser pdfParser
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * O textView será muito usado, então já coloca ele aqui
         */
        tvPreview = (TextView)findViewById(R.id.tvPreview);
        btnReader = (Button)findViewById(R.id.btnReader);
        btnFile = (Button)findViewById(R.id.btnFile);
        btnPaste = (Button)findViewById(R.id.btnPaste);
        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnSettings = (Button)findViewById(R.id.btnSettings);
        constraintLayout = (ConstraintLayout)findViewById(R.id.mainConstraintLayout);
        loadTheme();

        tvPreview.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void createToast(String text)
    {
        Toast t = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        TextView v = (TextView) t.getView().findViewById(android.R.id.message);
        v.setBackgroundColor(Color.TRANSPARENT);
        t.show();
    }

    public void setTextViewBackground()
    {
        if(currentAppTheme.equals(getString(R.string.themeValueLight)))
        {
            tvPreview.setBackgroundColor(getResources().getColor(R.color.colorTextView_light));
            //tvPreview.setBackgroundColor(getResources().getColor(R.color.colorTextSuffix_dark));
        }
        else if(currentAppTheme.equals(getString(R.string.themeValueDark)))
        {
            tvPreview.setBackgroundColor(getResources().getColor(R.color.colorTextView_dark));
        }
    }


    public void loadTheme()
    {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        preferences.registerOnSharedPreferenceChangeListener(this);
        String currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));

        if(currentAppTheme.equals(getString(R.string.themeValueLight)))
        {
            setTheme(R.style.AppTheme_Light);
            setTextViewBackground();
        }
        else if(currentAppTheme.equals(getString(R.string.themeValueDark)))
        {
            setTheme(R.style.AppTheme_Dark);
            setTextViewBackground();
        }
        else
        {
            setCustomTheme(preferences, this,
                    new View[]{constraintLayout},
                    new Button[] {btnReader, btnFile, btnPaste, btnCamera, btnSettings},
                    new TextView[] {tvPreview});
        }

    }

    /**
     * Usado para carregar o Tema custom pegando as cores das Preferences.
     * O método deve ser chamado por cada atividade conforme é necessário aplicar o tema customizado.
     * Argumentos podem ser nulos caso não haja um conjunto especifico para modificar
     * @param preferences referencia ao SharedPreferences para carregar as cores
     * @param context Contexto
     * @param views Vetor com todas as views que tem de ser customizadas
     * @param buttons Vetor com todos os botões que tem de ser customizados
     * @param textViews Vetor com todos as TextViews que tem de ser customizados
     */
    public static void setCustomTheme(SharedPreferences preferences, Context context, View views[], Button buttons[], TextView textViews[]){

        int customPrimaryText = preferences.getInt(context.getString(R.string.themeCustomPrimaryTextKey), R.color.colorTextPrimary_light);
        int customSecondaryText = preferences.getInt(context.getString(R.string.themeCustomSecondaryTextKey), R.color.colorTextPrimary_light);
        int customBackground = preferences.getInt(context.getString(R.string.themeCustomBackgroundKey), R.color.colorBackground_light);
        int customTextViewBackground = preferences.getInt(context.getString(R.string.themeCustomTextviewBackgroundKey), R.color.colorBackground_light);

        if(views != null) {
            for (int i = 0; i < views.length; i++) {
                if(views[i]!=null)
                    views[i].setBackgroundColor(customBackground);
            }
        }
        if (buttons != null) {
            for (int i = 0; i < buttons.length; i++) {
                if(buttons[i]!=null) {
                    buttons[i].setTextColor(customPrimaryText);
                    buttons[i].setBackgroundColor(customTextViewBackground);
                }
            }
        }
        if(textViews != null) {
            for (int i = 0; i < textViews.length; i++) {
                if(textViews[i]!=null) {
                    textViews[i].setTextColor(customSecondaryText);
                    textViews[i].setBackgroundColor(customTextViewBackground);
                }
            }
        }
    }

    /***
     * Button Callback for btnReader
     * Pega a string da caixa e manda pra nova Activity
     */
    public void onClickReader(View v){
        String text;
        text = tvPreview.getText().toString();
        if (text != "")
        {
            Intent intent = new Intent(getBaseContext(), ReaderActivity.class);
            intent.putExtra("text", text);
            startActivity(intent);
        }
        else
        {
            createToast(getResources().getString(R.string.toastSendError));
            //makeText(this, R.string.toastSendError, Toast.LENGTH_SHORT).show();
        }

    }

    /***
     * Button Callback for btnFile
     * Abre o gerenciador de arquivos, mas ainda não faz nada se selecionar o arquivo
     */
    public void onClickFile(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivity(intent);

    }

    /***
     * Button Callback for btnPaste
     * Pega o texto do clip board. Se tiver algo, coloca na caixa
     * Caso contrário, faz nada
     */
    public void onClickPaste(View v){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(clipboardManager.hasPrimaryClip())
        {
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            if (item.getText() != null)
            {
                tvPreview.setText(item.getText());
            }
            else
            {
                return;
            }
        }
    }

    /**
     *
     */
    public void onClickCamera(View v){}

    /***
     * Button Callback for btnSettings
     */
    public void onClickSettings(View v){
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.themeKey)))
        {
            String mode = sharedPreferences.getString(key, "light");

            if(mode.equals(getString(R.string.themeValueDark)))
            {
                currentAppTheme = getString(R.string.themeValueDark);
            }
            else if(mode.equals(getString(R.string.themeValueLight)))
            {
                currentAppTheme = getString(R.string.themeValueLight);
            }
            else if(mode.equals(getString(R.string.themeValueCustom)))
            {
                currentAppTheme = getString(R.string.themeValueCustom);
            }
            loadTheme();

            //Isso faz com que recarregue a interface corretamente, mas reseta a posição da palavra
            recreate();
        }


        if(key.equals(getString(R.string.themeCustomPrimaryTextKey)) ||
            key.equals(getString(R.string.themeCustomSecondaryTextKey)) ||
            key.equals(getString(R.string.themeCustomBackgroundKey)) ||
            key.equals(getString(R.string.themeCustomTextviewBackgroundKey)) ||
            key.equals(getString(R.string.themeCustomHighlightKey)) ||
            key.equals(getString(R.string.themeCustomPrefixKey)) ||
            key.equals(getString(R.string.themeCustomSuffixKey))
            ){
            System.out.println("CHAMOU");

            loadTheme();
            recreate();
        }
    }
}
