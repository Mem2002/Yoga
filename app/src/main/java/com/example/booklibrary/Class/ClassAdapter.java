package com.example.booklibrary.Class;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.booklibrary.R;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.MyViewHolder> {
    private  Context context;
    private ArrayList<String> class_id, class_date, class_teacher, class_typeclass, class_comment;

    ClassAdapter(Context context, ArrayList class_id, ArrayList class_date, ArrayList class_teacher, ArrayList class_typeclass, ArrayList class_comment ){
        this.context = context;
        this.class_id = class_id;
        this.class_date = class_date;
        this.class_teacher = class_teacher;
        this.class_typeclass = class_typeclass;
        this.class_comment = class_comment;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.class_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.class_id_txt.setText(String.valueOf(class_id.get(position)));
        holder.class_date_txt.setText(String.valueOf(class_date.get(position)));
        holder.class_teacher_txt.setText(String.valueOf(class_teacher.get(position)));
        holder.class_type_txt.setText(String.valueOf(class_typeclass.get(position)));
        holder.class_comments_txt.setText(String.valueOf(class_comment.get(position)));
        holder.mainLayout_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateClassActivity.class);
                intent.putExtra("id", String.valueOf(class_id.get(position)));
                intent.putExtra("date", String.valueOf(class_date.get(position)));
                intent.putExtra("teacher", String.valueOf(class_teacher.get(position)));
                intent.putExtra("typeclass", String.valueOf(class_typeclass.get(position)));
                intent.putExtra("comment", String.valueOf(class_comment.get(position)));
                context.startActivity(intent); // Thêm dòng này
            }
        });
    }

    @Override
    public int getItemCount() {
        return class_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView class_id_txt, class_date_txt, class_teacher_txt, class_type_txt, class_comments_txt;
            LinearLayout mainLayout_class;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            class_id_txt = itemView.findViewById(R.id.class_id_txt);
            class_date_txt = itemView.findViewById(R.id.class_date_txt);
            class_teacher_txt = itemView.findViewById(R.id.class_teacher_txt);
            class_type_txt = itemView.findViewById(R.id.class_type_txt);
            class_comments_txt = itemView.findViewById(R.id.class_comments_txt);
            mainLayout_class = itemView.findViewById(R.id.mainLayout_class);

        }
    }
}
