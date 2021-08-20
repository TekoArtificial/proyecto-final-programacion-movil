package com.example.flowersmarket;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.flowersmarket.models.Image;
import com.example.flowersmarket.models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NewItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    Toolbar toolbar;
    EditText txtTituloArticle;
    EditText txtDecripcionArticle;
    EditText txtPrecioArticle;
    ImageView imgProductoArticle;
    Button btnRegistrarArticle;
    Button btnSeleccionarImageArticle;

    Uri selectedImgUri;
    FirebaseFirestore firestore;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtTituloArticle = findViewById(R.id.txtTituloArticle);
        txtDecripcionArticle = findViewById(R.id.txtDescripcionArticle);
        txtPrecioArticle = findViewById(R.id.txtPrecioArticle);
        imgProductoArticle = findViewById(R.id.imgProductoArticle);
        btnRegistrarArticle = findViewById(R.id.btnRegistarArticle);
        btnSeleccionarImageArticle = findViewById(R.id.btnSeleccionarArticle);

        //Instanciamos a la raiz de las coleccines
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        btnRegistrarArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btnSeleccionarImageArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 openFileChoser();
            }
        });
    }


    //---------- inflate menu ---------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    //---------- set functionality ---------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.optionMenu1){
            finish();
        }
        return true;
    }

    //------ create a new product with randomly id -------
    private void createNewProduct(String image){

        String titulo = txtTituloArticle.getText().toString();
        String descripcion = txtDecripcionArticle.getText().toString();
        long precio = Long.parseLong(txtPrecioArticle.getText().toString());
        DocumentReference idReference = firestore.collection("flowers").document();
        String id = idReference.getId();

        Item item = new Item(descripcion, id, precio, titulo, image);

        Task task = idReference.set(item).addOnSuccessListener(
                unused -> Toast.makeText(getApplicationContext(),
                        "El articulo se creo correctamente",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                        "El articulo no pudo ser registrado",
                        Toast.LENGTH_SHORT).show());
        finish();
    }

    //---------- Open file choser (img) ------
    private void openFileChoser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImgUri = data.getData();

            Picasso.with(this).load(selectedImgUri).into(imgProductoArticle);

        }
    }

    //------Metodo para subir imagen seleccionada y a la vez llamar al metodo createNewProduct con la url de la imagen
    private void uploadFile() {
        if(selectedImgUri != null) {
            StorageReference fileReference = storage.getReference().child("images/" + System.currentTimeMillis() + "." + getFileExtension(selectedImgUri));
            fileReference.putFile(selectedImgUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getApplicationContext(), "La imagen no pudo registrarse no pudo ser registrado", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Image image = new Image(uri.toString());
                            createNewProduct(image.getUrl());
                        }
                    });
                }
            });
        }
        //--------Si no se selecciono una imagen se agrega una por default
        else {
            StorageReference fileReference = storage.getReference().child("home/flower.jpg");
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Image image = new Image(uri.toString());
                    createNewProduct(image.getUrl());
                }
            });
        }
    }

    //------Obtiene la extension de la imagen seleccionada
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}