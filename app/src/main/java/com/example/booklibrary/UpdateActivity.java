package com.example.booklibrary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {
    EditText day_input, timeofCourse_input, capacity_input, duration_input, pricePerClass_input, typeofClass_input, description_input;
    Button update_button, delete_button;

    String id, dayofWeek, timeofCourse, capacity, duration, pricePerClass, typeOfClass, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        day_input = findViewById(R.id.day_input2);
        timeofCourse_input = findViewById(R.id.timeofCourse_input2);
        capacity_input = findViewById(R.id.capacity_input2);
        duration_input = findViewById(R.id.duration_input2);
        pricePerClass_input = findViewById(R.id.pricePerClass_input2);
        typeofClass_input = findViewById(R.id.typeofClass_input2);
        description_input = findViewById(R.id.description_input2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);


        getAndIntentData();


        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
    }

    void getAndIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("dayofWeek") &&
                getIntent().hasExtra("timeofCourse") && getIntent().hasExtra("capacity") &&
                getIntent().hasExtra("duration") && getIntent().hasExtra("pricePerClass") &&
                getIntent().hasExtra("typeOfClass") && getIntent().hasExtra("description")) {

            id = getIntent().getStringExtra("id");
            dayofWeek = getIntent().getStringExtra("dayofWeek");
            timeofCourse = getIntent().getStringExtra("timeofCourse");
            capacity = getIntent().getStringExtra("capacity");
            duration = getIntent().getStringExtra("duration");
            pricePerClass = getIntent().getStringExtra("pricePerClass");
            typeOfClass = getIntent().getStringExtra("typeOfClass");
            description = getIntent().getStringExtra("description");

            day_input.setText(dayofWeek);
            timeofCourse_input.setText(timeofCourse);
            capacity_input.setText(capacity);
            duration_input.setText(duration);
            pricePerClass_input.setText(pricePerClass);
            typeofClass_input.setText(typeOfClass);
            description_input.setText(description);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    void updateData() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
        dayofWeek = day_input.getText().toString().trim();
        timeofCourse = timeofCourse_input.getText().toString().trim();
        capacity = capacity_input.getText().toString().trim();
        duration = duration_input.getText().toString().trim();
        pricePerClass = pricePerClass_input.getText().toString().trim();
        typeOfClass = typeofClass_input.getText().toString().trim();
        description = description_input.getText().toString().trim();


        myDB.updateData(id, dayofWeek, timeofCourse, capacity, duration, pricePerClass, typeOfClass, description);
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show(); // Thông báo cập nhật thành công
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + dayofWeek + " ?");
        builder.setMessage("Are you sure you want to delete " + dayofWeek + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);
                Toast.makeText(UpdateActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show(); // Thông báo đã xóa
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
