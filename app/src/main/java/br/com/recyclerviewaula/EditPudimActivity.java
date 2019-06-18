package br.com.recyclerviewaula;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class EditPudimActivity extends AppCompatActivity {

    EditText saborEditText;
    EditText coberturaEditText;
    ImageView imagem;
    EditText url;

    Pudim pudim;
    int position;
    Button galeria;
    Button camera;
    Button atualizarCamera;



    public void getFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    public void getFromCamera(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("String", "antes do setContent");
        setContentView(R.layout.activity_edit_pudim);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("String", "antes do componente");
        saborEditText = (EditText) findViewById(R.id.saborEditText);
        coberturaEditText = (EditText) findViewById(R.id.coberturaEditText);
        imagem = (ImageView) findViewById(R.id.imagem);
        url = (EditText) findViewById(R.id.url);
        galeria = (Button) findViewById(R.id.galeria);
        Log.d("String", "antes do getBundle");
        Bundle bundle = getIntent().getExtras();
        Log.d("String", "depois do getBundle");
        final int requestCode = bundle.getInt("request_code");
        Log.d("String", "request_code="+requestCode);


        if (requestCode == 1) {
            Log.d("String", "1");
            pudim = (Pudim) bundle.getParcelable("pudim");
            Log.d("String", "2");
            position = bundle.getInt("position");
            Log.d("String", "3");
            saborEditText.setText(pudim.getSabor());
            Log.d("String", "4");
            coberturaEditText.setText(pudim.getCobertura());
            Log.d("String", "antes do setBitmap");
            imagem.setImageBitmap(pudim.getFoto());
            Log.d("String", "depois do setBitmap");
        }else
            pudim = new Pudim();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pudim.setSabor(saborEditText.getText().toString());
                pudim.setCobertura(coberturaEditText.getText().toString());
                //pudim.setFoto(new MainActivity().changeSize(((BitmapDrawable)imagem.getDrawable()).getBitmap()));
                pudim.setFoto(((BitmapDrawable) imagem.getDrawable()).getBitmap());

                Intent returnIntent = new Intent();
                Bundle returnBundle = new Bundle();
                returnBundle.putParcelable("pudim", pudim);

                if (requestCode == 1)
                    returnBundle.putInt("position",position);

                returnIntent.putExtras(returnBundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0)
            if (resultCode == Activity.RESULT_OK){
                Uri targetUri = data.getData();
                //uriTextView.setText(targetUri.toString());
                Bitmap bitmap;
                try{
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    imagem.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagem.setImageBitmap(imageBitmap);
            }
    }

}