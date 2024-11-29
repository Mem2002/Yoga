package com.example.booklibrary.Class;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog; // Thêm import cho AlertDialog
import androidx.appcompat.app.AppCompatActivity;

import com.example.booklibrary.MyDatabaseHelper;
import com.example.booklibrary.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateClassActivity extends AppCompatActivity {
    EditText date_input, teacher_input, comment_input;
    Spinner typeclass_input;
    Button update_class_button, delete_class_button;
    String id, date, teacher, typeclass, comment;
    MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_class);

        date_input = findViewById(R.id.date_class_input2);
        teacher_input = findViewById(R.id.teacher_class_input2);
        typeclass_input = findViewById(R.id.typeOfClassSpinner2);
        comment_input = findViewById(R.id.comments_class_input2);
        update_class_button = findViewById(R.id.update_class_button);
        delete_class_button = findViewById(R.id.delete_class_button);
        myDB = new MyDatabaseHelper(UpdateClassActivity.this);

        setUpSpinner();

        update_class_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClassData();
            }
        });

        delete_class_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(UpdateClassActivity.this)
                        .setTitle("Confirm deletion")
                        .setMessage("Are you sure you want to delete this class?")
                        .setPositiveButton("Yes", (dialog, which) -> deleteClassData())
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        getAndSetIntentData();
    }

    void setUpSpinner() {
        ArrayList<String> classTypes = myDB.getAllClassTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, classTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeclass_input.setAdapter(adapter);
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("date") && getIntent().hasExtra("teacher") &&
                getIntent().hasExtra("typeclass") && getIntent().hasExtra("comment")) {
            id = getIntent().getStringExtra("id");
            date = getIntent().getStringExtra("date");
            teacher = getIntent().getStringExtra("teacher");
            typeclass = getIntent().getStringExtra("typeclass");
            comment = getIntent().getStringExtra("comment");

            date_input.setText(date);
            teacher_input.setText(teacher);
            comment_input.setText(comment);

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) typeclass_input.getAdapter();
            int spinnerPosition = adapter.getPosition(typeclass);
            if (spinnerPosition >= 0) {
                typeclass_input.setSelection(spinnerPosition);
            } else {
                Log.e("UpdateClassActivity", "Type class not found in spinner: " + typeclass);
            }
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    void updateClassData() {
        date = date_input.getText().toString().trim();
        teacher = teacher_input.getText().toString().trim();
        typeclass = typeclass_input.getSelectedItem().toString();
        comment = comment_input.getText().toString().trim();

        if (date.isEmpty() || teacher.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MyDatabaseHelper.ClassInstance classInstance = myDB.getClassInstanceById(Integer.parseInt(id));

            if (classInstance != null) {
                String firestoreId = classInstance.getFirestoreId();
                Log.d("EditClassActivity", "Firestore ID: " + firestoreId);

                myDB.updateClassInstance(Integer.parseInt(id), date, teacher, comment, typeclass);
                Toast.makeText(this, "Class updated successfully in SQLite", Toast.LENGTH_SHORT).show();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("date", date);
                updatedData.put("teacher", teacher);
                updatedData.put("comments", comment);
                updatedData.put("yoga_typeOfClass", typeclass);

                firestore.collection("class_schedule").document(firestoreId)
                        .update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Class updated successfully in Firestore", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update class in Firestore", Toast.LENGTH_SHORT).show();
                            Log.e("UpdateClassActivity", "Error updating Firestore document", e);
                        });
            } else {
                Toast.makeText(this, "Class instance not found", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ID format", Toast.LENGTH_SHORT).show();
            Log.e("UpdateClassActivity", "Error parsing ID: " + id, e);
        }
    }

    void deleteClassData() {
        try {
            MyDatabaseHelper.ClassInstance classInstance = myDB.getClassInstanceById(Integer.parseInt(id));

            if (classInstance != null) {
                String firestoreId = classInstance.getFirestoreId();
                Log.d("DeleteClassActivity", "Firestore ID: " + firestoreId);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("class_schedule").document(firestoreId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            myDB.deleteClassInstance(Integer.parseInt(id));
                            Toast.makeText(this, "Class deleted successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UpdateClassActivity.this, MainClassActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to delete from Firestore", Toast.LENGTH_SHORT).show();
                            Log.e("DeleteClassActivity", "Error deleting Firestore document", e);
                        });
            } else {
                Toast.makeText(this, "Class instance not found", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ID format", Toast.LENGTH_SHORT).show();
            Log.e("UpdateClassActivity", "Error parsing ID: " + id, e);
        }
    }
}
