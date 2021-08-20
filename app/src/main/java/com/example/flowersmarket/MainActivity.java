package com.example.flowersmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //---------- inflate menu ---------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //---------- set functionality ---------
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.option1){
            Intent listProducts = new Intent(MainActivity.this, ListActivity.class);
            startActivityForResult(listProducts,0);
        } else if (id == R.id.option2) {
            Intent newProduct = new Intent(MainActivity.this, NewItemActivity.class);
            startActivityForResult(newProduct,0);
        }
        return true;
    }
}