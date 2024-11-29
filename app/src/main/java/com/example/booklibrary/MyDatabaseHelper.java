package com.example.booklibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    private static final String DATABASE_NAME = "CW.db";
    private static final int DATABASE_VERSION = 1; // Cập nhật phiên bản

    // Library Table
    private static final String TABLE_NAME = "my_library";
    public static final String COLUM_ID = "_id";
    private static final String COLUM_DAYOFWEEK = "yoga_dayofWeek";
    private static final String COLUMN_TIMEOFCOURSE = "yoga_timeofCourse";
    private static final String COLUMN_CAPACITY = "yoga_capacity";
    private static final String COLUMN_DURATION = "yoga_duration";
    private static final String COLUMN_PRICE_PER_CLASS = "yoga_pricePerClass";
    public static final String COLUMN_TYPE_OF_CLASS = "yoga_typeOfClass";
    private static final String COLUMN_DESCRIPTION = "yoga_description";

    // Class Schedule Table
    private static final String TABLE_SCHEDULE = "class_schedule";
    private static final String COLUMN_SCHEDULE_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TEACHER = "teacher";
    private static final String COLUMN_COMMENTS = "comments";
    public static final String COLUMN_TYPE_OF_CLASS_SCHEDULE = "yoga_typeOfClass";
    private static final String COLUMN_FIRESTORE_ID = "firestore_id"; // Thêm trường mới

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createLibraryTable = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUM_DAYOFWEEK + " TEXT, " +
                COLUMN_TIMEOFCOURSE + " TEXT, " +
                COLUMN_CAPACITY + " INTEGER, " +
                COLUMN_DURATION + " TEXT, " +
                COLUMN_PRICE_PER_CLASS + " TEXT, " +
                COLUMN_TYPE_OF_CLASS + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT);";
        db.execSQL(createLibraryTable);

        String createScheduleTable = "CREATE TABLE " + TABLE_SCHEDULE + " (" +
                COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_TEACHER + " TEXT NOT NULL, " +
                COLUMN_COMMENTS + " TEXT, " +
                COLUMN_TYPE_OF_CLASS_SCHEDULE + " TEXT, " +
                COLUMN_FIRESTORE_ID + " TEXT);"; // Thêm trường mới
        db.execSQL(createScheduleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Library Methods
    void addBook(String dayofWeek, String timeofCourse, int capacity, String duration, String pricePerClass, String typeOfClass, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUM_DAYOFWEEK, dayofWeek);
        cv.put(COLUMN_TIMEOFCOURSE, timeofCourse);
        cv.put(COLUMN_CAPACITY, capacity);
        cv.put(COLUMN_DURATION, duration);
        cv.put(COLUMN_PRICE_PER_CLASS, pricePerClass);
        cv.put(COLUMN_TYPE_OF_CLASS, typeOfClass);
        cv.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_NAME, null, cv);
        Toast.makeText(context, result == -1 ? "Failed" : "Added Successfully!", Toast.LENGTH_SHORT).show();
    }

    Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, null);
    }

    void updateData(String row_id, String dayofWeek, String timeofCourse, String capacity, String duration, String pricePerClass, String typeOfClass, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUM_DAYOFWEEK, dayofWeek);
        cv.put(COLUMN_TIMEOFCOURSE, timeofCourse);
        cv.put(COLUMN_CAPACITY, capacity);
        cv.put(COLUMN_DURATION, duration);
        cv.put(COLUMN_PRICE_PER_CLASS, pricePerClass);
        cv.put(COLUMN_TYPE_OF_CLASS, typeOfClass);
        cv.put(COLUMN_DESCRIPTION, description);
        long result = db.update(TABLE_NAME, cv, COLUM_ID + "=?", new String[]{row_id});
        Toast.makeText(context, result == -1 ? "Failed to Update." : "Successfully Updated!", Toast.LENGTH_SHORT).show();
    }

    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, COLUM_ID + "=?", new String[]{row_id});
        Toast.makeText(context, result == -1 ? "Failed to Delete" : "Successfully Deleted", Toast.LENGTH_SHORT).show();
    }

    // Class Schedule Methods
    public void addClassInstance(String date, String teacher, String comments, String typeOfClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TEACHER, teacher);
        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_TYPE_OF_CLASS_SCHEDULE, typeOfClass);

        long result = db.insert(TABLE_SCHEDULE, null, cv);
        Toast.makeText(context, result == -1 ? "Failed to add class instance" : "Class instance added successfully!", Toast.LENGTH_SHORT).show();

        // Đồng bộ hóa với Firestore
        if (result != -1) {
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("date", date);
            scheduleData.put("teacher", teacher);
            scheduleData.put("comments", comments);
            scheduleData.put("yoga_typeOfClass", typeOfClass);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("class_schedule").add(scheduleData).addOnSuccessListener(documentReference -> {
                String firestoreId = documentReference.getId();
                Log.d("Firestore ID", "Firestore ID: " + firestoreId); // Log firestore_id
                updateFirestoreId(result, firestoreId);
            }).addOnFailureListener(e -> {
                Log.e("Firestore Error", "Error adding document", e);
            });
        }
    }

    private void updateFirestoreId(long id, String firestoreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FIRESTORE_ID, firestoreId);
        db.update(TABLE_SCHEDULE, cv, COLUMN_SCHEDULE_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Method to get all class types
    public ArrayList<String> getAllClassTypes() {
        ArrayList<String> classTypes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_TYPE_OF_CLASS}, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_TYPE_OF_CLASS);
                if (columnIndex != -1) {
                    String classType = cursor.getString(columnIndex);
                    classTypes.add(classType);
                }
            }
            cursor.close();
        }
        return classTypes;
    }

    public Cursor readAllClassInstances() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SCHEDULE, null);
    }

    public void updateClassInstance(int instanceId, String date, String teacher, String comments, String typeOfClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TEACHER, teacher);
        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_TYPE_OF_CLASS_SCHEDULE, typeOfClass);

        long result = db.update(TABLE_SCHEDULE, cv, COLUMN_SCHEDULE_ID + "=?", new String[]{String.valueOf(instanceId)});
        Toast.makeText(context, result == -1 ? "Failed to update class instance" : "Class instance updated successfully!", Toast.LENGTH_SHORT).show();
    }

    public void deleteClassInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_SCHEDULE, COLUMN_SCHEDULE_ID + "=?", new String[]{String.valueOf(instanceId)});
        Toast.makeText(context, result == -1 ? "Failed to delete class instance" : "Class instance deleted successfully!", Toast.LENGTH_SHORT).show();
    }

    public Cursor searchTeacher(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_TEACHER + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};
        return db.query(TABLE_SCHEDULE, null, selection, selectionArgs, null, null, null);
    }

    // Get Class Instance by ID
    public ClassInstance getClassInstanceById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + COLUMN_SCHEDULE_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy dữ liệu từ cursor với kiểm tra
            int classIdIndex = cursor.getColumnIndex(COLUMN_SCHEDULE_ID);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int teacherIndex = cursor.getColumnIndex(COLUMN_TEACHER);
            int commentsIndex = cursor.getColumnIndex(COLUMN_COMMENTS);
            int typeOfClassIndex = cursor.getColumnIndex(COLUMN_TYPE_OF_CLASS_SCHEDULE);
            int firestoreIdIndex = cursor.getColumnIndex(COLUMN_FIRESTORE_ID);

            if (classIdIndex != -1 && dateIndex != -1 && teacherIndex != -1 && commentsIndex != -1 && typeOfClassIndex != -1 && firestoreIdIndex != -1) {
                int classId = cursor.getInt(classIdIndex);
                String date = cursor.getString(dateIndex);
                String teacher = cursor.getString(teacherIndex);
                String comments = cursor.getString(commentsIndex);
                String typeOfClass = cursor.getString(typeOfClassIndex);
                String firestoreId = cursor.getString(firestoreIdIndex); // Cột firestore_id

                cursor.close();
                return new ClassInstance(classId, date, teacher, comments, typeOfClass, firestoreId);
            } else {
                cursor.close();
                Log.e("DB Error", "One or more columns do not exist in the cursor.");
                return null; // Không tìm thấy lớp
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return null; // Không tìm thấy lớp
        }
    }

    // Class Instance Model
    public static class ClassInstance {
        private int id;
        private String date;
        private String teacher;
        private String comments;
        private String typeOfClass;
        private String firestoreId;

        public ClassInstance(int id, String date, String teacher, String comments, String typeOfClass, String firestoreId) {
            this.id = id;
            this.date = date;
            this.teacher = teacher;
            this.comments = comments;
            this.typeOfClass = typeOfClass;
            this.firestoreId = firestoreId;
        }

        // Getter methods
        public int getId() { return id; }
        public String getDate() { return date; }
        public String getTeacher() { return teacher; }
        public String getComments() { return comments; }
        public String getTypeOfClass() { return typeOfClass; }
        public String getFirestoreId() { return firestoreId; }
    }
}
