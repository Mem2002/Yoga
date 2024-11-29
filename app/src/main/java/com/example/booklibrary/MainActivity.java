package com.example.booklibrary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booklibrary.Class.MainClassActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton add_button, add_class_button;

    MyDatabaseHelper myDB;
    ArrayList<String> _id, yoga_dayofWeek, yoga_timeofCourse, yoga_capacity, yoga_duration, yoga_pricePerClass, yoga_typeOfClass, yoga_description;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.add_button);
        add_class_button = findViewById(R.id.add_class_button);

        myDB = new MyDatabaseHelper(MainActivity.this);
        _id = new ArrayList<>();
        yoga_dayofWeek = new ArrayList<>();
        yoga_timeofCourse = new ArrayList<>();
        yoga_capacity = new ArrayList<>();
        yoga_duration = new ArrayList<>();
        yoga_pricePerClass = new ArrayList<>();
        yoga_typeOfClass = new ArrayList<>();
        yoga_description = new ArrayList<>();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        add_class_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainClassActivity.class);
                startActivity(intent);
            }
        });


        loadClasses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loadClasses();
        }
    }

    private void loadClasses() {

        _id.clear();
        yoga_dayofWeek.clear();
        yoga_timeofCourse.clear();
        yoga_capacity.clear();
        yoga_duration.clear();
        yoga_pricePerClass.clear();
        yoga_typeOfClass.clear();
        yoga_description.clear();


        storeDataInArray();


        customAdapter = new CustomAdapter(MainActivity.this, this, _id, yoga_dayofWeek, yoga_timeofCourse, yoga_capacity, yoga_duration, yoga_pricePerClass, yoga_typeOfClass, yoga_description);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    void storeDataInArray() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                _id.add(cursor.getString(0));
                yoga_dayofWeek.add(cursor.getString(1));
                yoga_timeofCourse.add(cursor.getString(2));
                yoga_capacity.add(cursor.getString(3));
                yoga_duration.add(cursor.getString(4));
                yoga_pricePerClass.add(cursor.getString(5));
                yoga_typeOfClass.add(cursor.getString(6));
                yoga_description.add(cursor.getString(7));
            }
        }
    }
}
