package cognitiva.dyslexreader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import java.net.MalformedURLException;
import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class MainActivity extends AppCompatActivity {

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
        tvPreview.setMovementMethod(new ScrollingMovementMethod());

        //utilizado no httpParser
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
            Toast.makeText(this, R.string.toastSendError, Toast.LENGTH_SHORT).show();
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
     * Button Callback for btnSettings
     */
    public void onClickSettings(View v){
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }
    /*
     * Button Callback fot btnHttpParser
     * */
    public void onClickHttpParser(View v){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popup_http_parser,null);
        ConstraintLayout mConstraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        Button popUpPaste = (Button) customView.findViewById(R.id.btnPastePopUpHttp);
        Button getUrl = (Button) customView.findViewById(R.id.btnGetText);
        final EditText textUrl = (EditText) customView.findViewById(R.id.editLink);

        popUpPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if(clipboardManager.hasPrimaryClip())
                {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    if (item.getText() != null)
                    {
                        textUrl.setText(item.getText());
                    }
                    else
                    {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BoilerpipeProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        mPopupWindow.showAtLocation(mConstraintLayout, Gravity.CENTER,0,0);


    }
}