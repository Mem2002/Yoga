package com.example.booklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> _id;
    private ArrayList<String> yoga_dayofWeek;
    private ArrayList<String> yoga_timeofCourse;
    private ArrayList<String> yoga_capacity;
    private ArrayList<String> yoga_duration;
    private ArrayList<String> yoga_pricePerClass;
    private ArrayList<String> yoga_typeOfClass; // Thêm thuộc tính này
    private ArrayList<String> yoga_description;

    Activity activity;

    int position;

    public CustomAdapter(Activity activity,  Context context,
                         ArrayList<String> _id,
                         ArrayList<String> yoga_dayofWeek,
                         ArrayList<String> yoga_timeofCourse,
                         ArrayList<String> yoga_capacity,
                         ArrayList<String> yoga_duration,
                         ArrayList<String> yoga_pricePerClass,
                         ArrayList<String> yoga_typeOfClass,
                         ArrayList<String> yoga_description) {
        this.activity = activity;
        this.context = context;
        this._id = _id;
        this.yoga_dayofWeek = yoga_dayofWeek;
        this.yoga_timeofCourse = yoga_timeofCourse;
        this.yoga_capacity = yoga_capacity;
        this.yoga_duration = yoga_duration;
        this.yoga_pricePerClass = yoga_pricePerClass;
        this.yoga_typeOfClass = yoga_typeOfClass; // Gán giá trị
        this.yoga_description = yoga_description;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.position = position;
        holder._id_txt.setText(_id.get(position));
        holder.yoga_dayofWeek_txt.setText(yoga_dayofWeek.get(position));
        holder.yoga_timeofCourse_txt.setText(yoga_timeofCourse.get(position));
        holder.yoga_capacity_txt.setText(yoga_capacity.get(position));
        holder.yoga_duration_txt.setText(yoga_duration.get(position));
        holder.yoga_pricePerClass_txt.setText(yoga_pricePerClass.get(position));
        holder.yoga_typeOfClass_txt.setText(yoga_typeOfClass.get(position)); // Sửa lại
        holder.yoga_description_txt.setText(yoga_description.get(position));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(_id.get(position)));
                intent.putExtra("dayofWeek", String.valueOf(yoga_dayofWeek.get(position)));
                intent.putExtra("timeofCourse", String.valueOf(yoga_timeofCourse.get(position)));
                intent.putExtra("capacity", String.valueOf(yoga_capacity.get(position)));
                intent.putExtra("duration", String.valueOf(yoga_duration.get(position)));
                intent.putExtra("pricePerClass", String.valueOf(yoga_pricePerClass.get(position)));
                intent.putExtra("typeOfClass", String.valueOf(yoga_typeOfClass.get(position)));
                intent.putExtra("description", String.valueOf(yoga_description.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _id.size(); // Trả về kích thước của danh sách
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView _id_txt, yoga_dayofWeek_txt, yoga_timeofCourse_txt, yoga_capacity_txt, yoga_duration_txt, yoga_pricePerClass_txt, yoga_typeOfClass_txt, yoga_description_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            _id_txt = itemView.findViewById(R.id._id_txt);
            yoga_dayofWeek_txt = itemView.findViewById(R.id.yoga_dayofWeek_txt);
            yoga_timeofCourse_txt = itemView.findViewById(R.id.yoga_timeofCourse_txt);
            yoga_capacity_txt = itemView.findViewById(R.id.yoga_capacity_txt);
            yoga_duration_txt = itemView.findViewById(R.id.yoga_duration_txt);
            yoga_pricePerClass_txt = itemView.findViewById(R.id.yoga_pricePerClass_txt);
            yoga_typeOfClass_txt = itemView.findViewById(R.id.yoga_typeOfClass_txt);
            yoga_description_txt = itemView.findViewById(R.id.yoga_description_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
