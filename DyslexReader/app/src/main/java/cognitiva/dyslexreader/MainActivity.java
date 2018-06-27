package cognitiva.dyslexreader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.StrictMode;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import java.net.MalformedURLException;
import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Button to ReaderActivity
    Button btnReader;
    Button btnFile;
    Button btnPaste;
    Button btnCamera;
    Button btnSettings;
    Button btnHttpParser;

    TextView tvPreview;
    private PopupWindow mPopupWindow;
    private static final int CAMERA_ACTIVITY_CODE = 311;

    ConstraintLayout constraintLayout;

    String currentAppTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadTheme();
        setContentView(R.layout.activity_main);

        /**
         * O textView será muito usado, então já coloca ele aqui
         */
        tvPreview = (TextView)findViewById(R.id.tvPreview);
        btnReader = (Button)findViewById(R.id.btnReader);
        btnFile = (Button)findViewById(R.id.btnHttpParser);
        btnPaste = (Button)findViewById(R.id.btnPaste);
        btnCamera = (Button)findViewById(R.id.btnCamera);
        constraintLayout = (ConstraintLayout)findViewById(R.id.mainConstraintLayout);

        loadTheme();
        setTextViewBackground();

        tvPreview.setMovementMethod(new ScrollingMovementMethod());

        //utilizado no httpParser
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public void createToast(String text)
    {
        Toast t = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        TextView v = (TextView) t.getView().findViewById(android.R.id.message);
        v.setBackgroundColor(Color.TRANSPARENT);
        t.show();
    }

    /**
     * Método usado para colorir o fundo de Textviews da Activity
     */
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

    /***
     * Método usado para carregar os temas do aplicativo.
     * Sendo eles os defaults e o customizado
     */
    public void loadTheme()
    {
        SharedPreferences preferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));
        preferences.registerOnSharedPreferenceChangeListener(this);
        //String currentAppTheme = preferences.getString(getString(R.string.themeKey), getString(R.string.themeValueLight));

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
            setCustomTheme(preferences, this,
                    getSupportActionBar(),
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
    public static void setCustomTheme(SharedPreferences preferences, Context context, ActionBar actionBar, View views[], Button buttons[], TextView textViews[]){

        int customPrimaryText = preferences.getInt(context.getString(R.string.themeCustomPrimaryTextKey), R.color.colorTextPrimary_light);
        int customSecondaryText = preferences.getInt(context.getString(R.string.themeCustomSecondaryTextKey), R.color.colorTextPrimary_light);
        int customBackground = preferences.getInt(context.getString(R.string.themeCustomBackgroundKey), R.color.colorBackground_light);
        int customTextViewBackground = preferences.getInt(context.getString(R.string.themeCustomTextviewBackgroundKey), R.color.colorBackground_light);

        if(actionBar != null){
            System.out.println("NOTNULL");
            SpannableString title = new SpannableString(context.getString(R.string.app_name));
            title.setSpan(new ForegroundColorSpan(customPrimaryText), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            
            actionBar.setTitle(title);
            actionBar.setDisplayShowTitleEnabled(true);
        }

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
            createToast(getString(R.string.toastSendError));
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
    public void onClickCamera(View v){
        Intent intent = new Intent(getBaseContext(), CameraActivity.class);
        startActivityForResult(intent,CAMERA_ACTIVITY_CODE);
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == CAMERA_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                String returnString = data.getStringExtra("TEXT_CAMERA");

                // set text view with string
                tvPreview.setText(returnString);
            }
        }
    }


    /***
    *Callback do HTTP parser
    */
    public void onClickHttpParser(View v) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view

        View customView = inflater.inflate(R.layout.popup_http_parser, null);
        ConstraintLayout mConstraintLayout = (ConstraintLayout) findViewById(R.id.mainConstraintLayout);
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        Button btnClose = (Button) customView.findViewById(R.id.btnClosePopUp);
        Button popUpPaste = (Button) customView.findViewById(R.id.btnPastePopUpHttp);
        Button getUrl = (Button) customView.findViewById(R.id.btnGetText);
        final EditText textUrl = (EditText) customView.findViewById(R.id.editLink);


        btnClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        popUpPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (clipboardManager.hasPrimaryClip()) {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    if (item.getText() != null) {
                        textUrl.setText(item.getText());
                    } else {
                        return;
                    }
                }
            }
        });

        getUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL url;
                try {
                    url = new URL(textUrl.getText().toString());
                    String text = ArticleExtractor.INSTANCE.getText(url);
                    tvPreview.setText(text);
                    mPopupWindow.dismiss();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (BoilerpipeProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        mPopupWindow.showAtLocation(mConstraintLayout, Gravity.CENTER,0,0);


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
            else if(mode.equals(getString(R.string.themeValueCustom)))
            {
                currentAppTheme = getString(R.string.themeValueCustom);
            }
            loadTheme();
            setTextViewBackground();

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
            setTextViewBackground();
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