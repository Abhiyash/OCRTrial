package com.example.abhiyash.ocrtrial;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
        private static int RESULT_LOAD_IMG = 1;
        String imgDecodableString;

        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button);

        //init image
        //image = BitmapFactory.decodeResource(getResources(),R.drawable.ani);

        //initialize Tesseract API
        String language = "eng";
        datapath = getFilesDir() + "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);

        b1.setOnClickListener(this);
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";
            Log.d("12345", filepath);
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
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    public void processImage(View view)
    {
        if(image!=null) {
            mTess.setImage(image);
            OCRresult = mTess.getUTF8Text().toString();

            OCRTextView = (TextView) findViewById(R.id.OCRTextView);
            //EditText e=(EditText)findViewById(R.id.editText);
            //Toast.makeText(this, "Ocr ans=" + OCRresult, Toast.LENGTH_SHORT).show();
            OCRTextView.setText(OCRresult);
        }
        else
        {
            Toast.makeText(this, "Please load a picture", Toast.LENGTH_SHORT).show();
        }
        //e.setText("asd"+OCRresult);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Inside onclick", Toast.LENGTH_SHORT).show();
        //if(b1.isSelected()){

        try {

            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            //String term = editTextInput.getText().toString();

          ;

                intent.putExtra(SearchManager.QUERY, OCRTextView.getText().toString());
                startActivity(intent);




            } catch (Exception e) {
            Toast.makeText(this, "Error Occurred"+e, Toast.LENGTH_SHORT).show();
        }
        //}
    }

    public void loadImagefromGallery(View view) {
        // Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
        // Create intent to Open Image applications like Gallery, Google Photos
        System.out.println("1");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
        // Start the Intent
        System.out.println("2");
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        System.out.println("3");
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("4");
        super.onActivityResult(requestCode, resultCode, data);

        try {
            System.out.println("5");
            // When an Image is picked

            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                System.out.println("6");
                // Get the Image from data
                Uri selectedImage = data.getData();
                System.out.println("7");
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                System.out.println("8");
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                System.out.println("9");
                // Move to first row

                cursor.moveToFirst();
                System.out.println("10");
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                System.out.println("11");
                imgDecodableString = cursor.getString(columnIndex);
                System.out.println("12");
                cursor.close();
                System.out.println("13");
                ImageView imgView = (ImageView) findViewById(R.id.imageView);
                System.out.println("14");
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                imgView.setImageBitmap(convertImageViewToBitmap(imgView));
                System.out.println("15");

            } else {

                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong" + e, Toast.LENGTH_LONG).show();
        }
    }



    public Bitmap convertImageViewToBitmap(ImageView v) {

        image = ((BitmapDrawable) v.getDrawable()).getBitmap();
        return image;
    }

        }
