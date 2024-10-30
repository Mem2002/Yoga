package com.example.booklibrary.Class;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
    Button update_class_button, delete_class_button; // Thêm nút xóa
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
        delete_class_button = findViewById(R.id.delete_class_button); // Khởi tạo nút xóa
        myDB = new MyDatabaseHelper(UpdateClassActivity.this);

        // Thiết lập Spinner với dữ liệu từ cơ sở dữ liệu
        setUpSpinner();

        update_class_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClassData(); // Kích hoạt cập nhật khi nút được nhấn
            }
        });

        delete_class_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClassData(); // Kích hoạt xóa khi nút được nhấn
            }
        });

        getAndSetIntentData();
    }

    void setUpSpinner() {
        ArrayList<String> classTypes = myDB.getAllClassTypes(); // Lấy dữ liệu từ DB
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

            // Setting Intent Data
            date_input.setText(date);
            teacher_input.setText(teacher);
            comment_input.setText(comment);

            // Thiết lập vị trí Spinner
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

//    void updateClassData() {
//        date = date_input.getText().toString().trim();
//        teacher = teacher_input.getText().toString().trim();
//        typeclass = typeclass_input.getSelectedItem().toString();
//        comment = comment_input.getText().toString().trim();
//
//        if (date.isEmpty() || teacher.isEmpty() || comment.isEmpty()) {
//            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            myDB.updateClassInstance(Integer.parseInt(id), date, teacher, comment, typeclass);
//            Toast.makeText(this, "Class updated successfully", Toast.LENGTH_SHORT).show();
//            finish();
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Invalid ID format", Toast.LENGTH_SHORT).show();
//            Log.e("UpdateClassActivity", "Error parsing ID: " + id, e);
//        }
//    }
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
        // Lấy thông tin lớp theo ID
        MyDatabaseHelper.ClassInstance classInstance = myDB.getClassInstanceById(Integer.parseInt(id));

        if (classInstance != null) {
            String firestoreId = classInstance.getFirestoreId(); // Giả định bạn đã có phương thức này
            Log.d("EditClassActivity", "Firestore ID: " + firestoreId);


            // Cập nhật dữ liệu trong SQLite
            myDB.updateClassInstance(Integer.parseInt(id), date, teacher, comment, typeclass);
            Toast.makeText(this, "Class updated successfully in SQLite", Toast.LENGTH_SHORT).show();

            // Cập nhật Firestore
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
//    void deleteClassData() {
//        try {
//            myDB.deleteClassInstance(Integer.parseInt(id));
//            finish(); // Kết thúc Activity và trở về Activity trước đó
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Invalid ID format", Toast.LENGTH_SHORT).show();
//            Log.e("UpdateClassActivity", "Error parsing ID: " + id, e);
//        }
//    }

    void deleteClassData() {
        try {
            // Lấy thông tin lớp theo ID
            MyDatabaseHelper.ClassInstance classInstance = myDB.getClassInstanceById(Integer.parseInt(id));

            if (classInstance != null) {
                String firestoreId = classInstance.getFirestoreId(); // Giả định bạn đã có phương thức này

                // Log firestore_id để kiểm tra
                Log.d("DeleteClassActivity", "Firestore ID: " + firestoreId);

                // Xóa tài liệu từ Firestore
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("class_schedule").document(firestoreId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Sau khi xóa thành công trong Firestore, xóa trong SQLite
                            myDB.deleteClassInstance(Integer.parseInt(id));
                            finish(); // Kết thúc Activity và trở về Activity trước đó
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
