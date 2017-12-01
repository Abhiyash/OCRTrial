package com.example.abhiyash.ocrtrial;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView OCRTextView;
    Bitmap image; //our image
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "";
    Button b1;
    String OCRresult = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1= (Button)findViewById(R.id.button);

        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.ani);

        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);

        b1.setOnClickListener(this);
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";
            Log.d("12345",filepath);
            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    public void processImage(View view){

        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text().toString();

        OCRTextView = (TextView) findViewById(R.id.OCRTextView);
        //EditText e=(EditText)findViewById(R.id.editText);
        Toast.makeText(this, "Ocr ans="+OCRresult, Toast.LENGTH_SHORT).show();
        OCRTextView.setText(OCRresult);
        //e.setText("asd"+OCRresult);
    }

    @Override
    public void onClick(View v) {
        //if(b1.isSelected()){
            try {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                //String term = editTextInput.getText().toString();
                intent.putExtra(SearchManager.QUERY, OCRTextView.getText().toString());
                startActivity(intent);
            } catch (Exception e) {
                // TODO: handle exception
            }
        //}
    }
}
