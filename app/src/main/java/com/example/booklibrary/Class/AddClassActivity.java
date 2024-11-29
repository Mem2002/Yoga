package com.example.booklibrary.Class;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booklibrary.MyDatabaseHelper;
import com.example.booklibrary.R;

import java.util.ArrayList;

public class AddClassActivity extends AppCompatActivity {
    private EditText dateClassInput, teacherClassInput, commentsClassInput;
    private Spinner typeOfClassSpinner;
    private Button addClassButton;
    private MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);


        dateClassInput = findViewById(R.id.date_class_input);
        teacherClassInput = findViewById(R.id.teacher_class_input);
        typeOfClassSpinner = findViewById(R.id.typeOfClassSpinner);
        commentsClassInput = findViewById(R.id.comments_class_input);
        addClassButton = findViewById(R.id.add_class_button);

        myDB = new MyDatabaseHelper(this);
        populateClassTypeSpinner();

        addClassButton.setOnClickListener(v -> {
            String date = dateClassInput.getText().toString().trim();
            String teacher = teacherClassInput.getText().toString().trim();
            String typeOfClass = (String) typeOfClassSpinner.getSelectedItem();
            String comments = commentsClassInput.getText().toString().trim();


            if (date.isEmpty() || teacher.isEmpty()) {
                Toast.makeText(AddClassActivity.this, "Date and Teacher fields are required", Toast.LENGTH_SHORT).show();
                return;
            }


            myDB.addClassInstance(date, teacher, comments, typeOfClass);
            Log.d("AddClassActivity", "Class added: Date=" + date + ", Teacher=" + teacher);


            setResult(RESULT_OK);
            finish();
        });
    }

    private void populateClassTypeSpinner() {
        ArrayList<String> classTypes = myDB.getAllClassTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfClassSpinner.setAdapter(adapter);
    }

    private void clearInputFields() {
        dateClassInput.setText("");
        teacherClassInput.setText("");
        commentsClassInput.setText("");
        typeOfClassSpinner.setSelection(0);
    }
}
