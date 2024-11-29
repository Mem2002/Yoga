package com.example.booklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    EditText day_input, timeofCourse_input, capacity_input, duration_input, pricePerClass_input, typeofClass_input, description_input;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        // Khởi tạo các EditText
        day_input = findViewById(R.id.day_input);
        timeofCourse_input = findViewById(R.id.timeofCourse_input);
        capacity_input = findViewById(R.id.capacity_input);
        duration_input = findViewById(R.id.duration_input);
        pricePerClass_input = findViewById(R.id.pricePerClass_input);
        typeofClass_input = findViewById(R.id.typeofClass_input);
        description_input = findViewById(R.id.description_input);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các trường
                String dayOfWeek = day_input.getText().toString().trim();
                String timeOfCourse = timeofCourse_input.getText().toString().trim();
                String capacityStr = capacity_input.getText().toString().trim();
                String duration = duration_input.getText().toString().trim();
                String pricePerClass = pricePerClass_input.getText().toString().trim();
                String typeOfClass = typeofClass_input.getText().toString().trim();
                String description = description_input.getText().toString().trim();

                // Kiểm tra đầu vào
                if (dayOfWeek.isEmpty() || timeOfCourse.isEmpty() || capacityStr.isEmpty() ||
                        duration.isEmpty() || pricePerClass.isEmpty() || typeOfClass.isEmpty()) {
                    Toast.makeText(AddActivity.this, "All fields except description are required!", Toast.LENGTH_SHORT).show();
                    return; 
                }

                int capacity;
                try {
                    capacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddActivity.this, "Capacity must be a valid number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                myDB.addBook(dayOfWeek, timeOfCourse, capacity, duration, pricePerClass, typeOfClass, description);


                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                intent.putExtra("new_class_added", true); // Gửi thông báo rằng lớp mới đã được thêm
                startActivity(intent);
                finish();
            }
        });
    }
}
