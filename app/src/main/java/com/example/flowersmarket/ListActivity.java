package com.example.flowersmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.flowersmarket.models.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerViewItems;
    FlowersAdapter flowersAdapter;
    FirebaseFirestore firestore;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerViewItems = findViewById(R.id.recyclerView);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        firestore = FirebaseFirestore.getInstance();

        Query query = firestore.collection("flowers");

        //----- Select all registers in the collection ----
        FirestoreRecyclerOptions<Item> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Item>()
                    .setQuery(query, Item.class)
                    .build();

        //----- Set the adapter to recyclerView -------
        flowersAdapter = new FlowersAdapter(firestoreRecyclerOptions, this, this);
        recyclerViewItems.setAdapter(flowersAdapter);
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


    @Override
    protected void onStart() {
        super.onStart();
        flowersAdapter.startListening();
        flowersAdapter.notifyDataSetChanged();
    }

    //----- If the user isnt in the app, stop listening for updates ------
    @Override
    protected void onStop() {
        super.onStop();
        flowersAdapter.stopListening();
    }
}