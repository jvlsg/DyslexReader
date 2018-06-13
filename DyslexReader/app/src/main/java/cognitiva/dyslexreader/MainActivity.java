package cognitiva.dyslexreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Button to ReaderActivity
    Button btnReader;
    Button btnFile;
    Button btnPaste;
    Button btnCamera;
    Button btnSettings;

    TextView tvPreview;

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

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /***
     * Button Callback for btnReader
     */
    public void onClickReader(){

    }

    /***
     * Button Callback for btnFile
     */
    public void onClickFile(){

    }

    /***
     * Button Callback for btnPaste
     */
    public void onClickPaste(){

    }

    /**
     *
     */
    public void onClickCamera(){}

    /***
     * Button Callback for btnSettings
     */
    public void onClickSettings(){

    }
}
