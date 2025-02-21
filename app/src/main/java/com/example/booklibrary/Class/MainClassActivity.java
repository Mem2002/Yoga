package com.example.booklibrary.Class;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.booklibrary.MyDatabaseHelper;
import com.example.booklibrary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainClassActivity extends AppCompatActivity {
    private static final int ADD_CLASS_REQUEST = 1;
    RecyclerView recyclerViewClass;
    FloatingActionButton add_class_detail_button;
    MyDatabaseHelper myDB;
    ArrayList<String> class_id, class_date, class_teacher, class_typeclass, class_comment;
    ClassAdapter classAdapter;
    SearchView searchView; // Khai báo SearchView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_class);

        // Khởi tạo RecyclerView và SearchView
        recyclerViewClass = findViewById(R.id.myRecyclerViewClass);
        searchView = findViewById(R.id.search_teacher);
        add_class_detail_button = findViewById(R.id.add_class_detail_button);
        add_class_detail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainClassActivity.this, AddClassActivity.class);
                startActivityForResult(intent, ADD_CLASS_REQUEST);
            }
        });

        myDB = new MyDatabaseHelper(MainClassActivity.this);
        class_id = new ArrayList<>();
        class_date = new ArrayList<>();
        class_teacher = new ArrayList<>();
        class_typeclass = new ArrayList<>();
        class_comment = new ArrayList<>();

        storeDataInArray();
        setupRecyclerView();
        setupSearchView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CLASS_REQUEST && resultCode == RESULT_OK) {
            // Khi nhận kết quả từ AddClassActivity
            class_id.clear();
            class_date.clear();
            class_teacher.clear();
            class_typeclass.clear();
            class_comment.clear();
            storeDataInArray();
            classAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        class_id.clear();
        class_date.clear();
        class_teacher.clear();
        class_typeclass.clear();
        class_comment.clear();
        storeDataInArray();
        classAdapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        classAdapter = new ClassAdapter(MainClassActivity.this, class_id, class_date, class_teacher, class_typeclass, class_comment);
        recyclerViewClass.setAdapter(classAdapter);
        recyclerViewClass.setLayoutManager(new LinearLayoutManager(MainClassActivity.this));
    }

    private void storeDataInArray() {
        Cursor cursor = myDB.readAllClassInstances();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                class_id.add(cursor.getString(0));
                class_date.add(cursor.getString(1));
                class_teacher.add(cursor.getString(2));
                class_typeclass.add(cursor.getString(3));
                class_comment.add(cursor.getString(4));
            }
        }
        cursor.close();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
    }

    private void filterData(String query) {
        Cursor cursor = myDB.searchTeacher(query);
        class_id.clear();
        class_date.clear();
        class_teacher.clear();
        class_typeclass.clear();
        class_comment.clear();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                class_id.add(cursor.getString(0));
                class_date.add(cursor.getString(1));
                class_teacher.add(cursor.getString(2));
                class_typeclass.add(cursor.getString(3));
                class_comment.add(cursor.getString(4));
            }
        }
        classAdapter.notifyDataSetChanged();
        cursor.close();
    }
}
