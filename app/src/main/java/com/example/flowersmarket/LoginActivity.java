package com.example.flowersmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsario;
    private EditText txtContrasena;
    private Button btnIngresa;
    private Button btnSalir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUsario = (EditText) findViewById(R.id.txtUsuario);
        txtContrasena = (EditText) findViewById(R.id.txtContraseña);
        btnIngresa = findViewById(R.id.btnIngresa);
        btnSalir = findViewById(R.id.btnSalir);

        btnIngresa.setOnClickListener(view -> {
            if(!txtUsario.getText().toString().isEmpty() && !txtContrasena.getText().toString().isEmpty()){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(txtUsario.getText().toString(), txtContrasena.getText().toString()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"Datos correctos",Toast.LENGTH_SHORT).show();
                        Intent listProducts = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(listProducts);
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Datos incorrectos",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSalir.setOnClickListener(view -> finish());
    }
}