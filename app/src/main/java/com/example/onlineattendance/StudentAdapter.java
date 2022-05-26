package com.example.onlineattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    ArrayList<StudentModel> Student_List;
    Context context;
    private onItemClickListener mlistener;

    public interface onItemClickListener {
        void onclick(int position);
    }

    public void setOnItemClickLickListener(onItemClickListener listener) {
        mlistener = listener;
    }


    public StudentAdapter(Context context, ArrayList<StudentModel> Student_List) {
        this.context=context;
        this.Student_List=Student_List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_detail,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.St_Name.setText(Student_List.get(position).getSt_Name());
            holder.St_Enrollment.setText(Student_List.get(position).getSt_Enrollment());
            holder.checkBox.setChecked(Student_List.get(position).isPresent());
    }

    @Override
    public int getItemCount() {
        return Student_List.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {
    TextView St_Name,St_Enrollment;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            St_Name=itemView.findViewById(R.id.textviewname);
            St_Enrollment=itemView.findViewById(R.id.textviewenrollment);

            checkBox = itemView.findViewById(R.id.check);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onclick(position);
                        }
                    }
                }
            });

        }
    }

}
