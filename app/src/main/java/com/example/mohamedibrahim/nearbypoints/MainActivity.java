package com.example.mohamedibrahim.nearbypoints;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SQLiteController controller = new SQLiteController(this);
    List<PlaceItem> rowItems = new ArrayList<PlaceItem>();
    ListView favList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchPlaces.class);
                startActivity(intent);
            }
        });

        favList=(ListView) findViewById(R.id.favList);
        rowItems.addAll(controller.getAllPlaces());
        if (rowItems.isEmpty()) {
            //Hint for user how to use application
            Intent intent = new Intent(MainActivity.this, PopActivity.class);
            startActivity(intent);

        } else {
            //transfer fav list to the main activity
            MyAdapter adapter = new MyAdapter(this,
                    R.layout.place_list, rowItems);
            favList.setAdapter(adapter);
        }

    }
}
