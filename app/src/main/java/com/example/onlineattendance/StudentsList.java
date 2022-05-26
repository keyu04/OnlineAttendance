package com.example.onlineattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentsList extends AppCompatActivity {
    TextView textViewyear, textViewdept, textViewsem, textViewsub;
    String year, course, sem, subject,fid;
    ArrayList<StudentModel> Students = new ArrayList<>();
    ArrayList<String> pAttendanceList=new ArrayList<>();
    RecyclerView recyclerView;
    CheckBox checkBox;
    ImageView backarrow, sendarrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);




        textViewyear = findViewById(R.id.Batch);
        textViewdept = findViewById(R.id.dept);
        textViewsem = findViewById(R.id.sem);
        textViewsub = findViewById(R.id.subject);

        backarrow = findViewById(R.id.left_icon);
        sendarrow = findViewById(R.id.right_icon);

        Intent intent = getIntent();
        year = intent.getStringExtra("SelectedYear");
        course = intent.getStringExtra("selectedCourse");
        sem = intent.getStringExtra("selectedsem");
        subject = intent.getStringExtra("selectedsubject");
        fid = intent.getStringExtra("fid");

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sendarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (StudentModel s : Students) {
                    if (s.isPresent()) {
                        pAttendanceList.add(String.valueOf(s.getSt_id()));
                    }
                }
                    InsertAttendanceToDatabase();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        StudentAdapter adapter = new StudentAdapter(StudentsList.this, Students);
        recyclerView.setLayoutManager(new GridLayoutManager(StudentsList.this, 1, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickLickListener(new StudentAdapter.onItemClickListener() {
            @Override
            public void onclick(int position) {

                StudentModel student;

                if (Students.get(position).isPresent()) {
                    student = new StudentModel(Students.get(position).getSt_id(),Students.get(position).getSt_Name(),Students.get(position).getSt_Enrollment(), false);
                } else {
                    student = new StudentModel(Students.get(position).getSt_id(),Students.get(position).getSt_Name(),Students.get(position).getSt_Enrollment(), true);
                }
                Students.set(position, student);
                adapter.notifyDataSetChanged();

            }
        });


        String url = "http://10.0.2.2/onlineattendance/fetchstudentList.php?subject=" + subject + "&year=" + year + "&sem=" + sem;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("tbl_student");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Integer St_id = jsonObject1.getInt("sid");
                            String St_name = jsonObject1.getString("st_name");
                            String St_enroll = jsonObject1.getString("st_enroll");
                            if (St_enroll.isEmpty()) {
                                String St_code = jsonObject1.getString("st_code");
                                Students.add(new StudentModel(St_id, St_name, St_code,false));
                            } else {
                                Students.add(new StudentModel(St_id, St_name, St_enroll,false));
                            }

                        }

                        adapter.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(StudentsList.this);
        queue.add(stringRequest);

        textViewyear.setText(year);
        textViewdept.setText(course);
        textViewsem.setText(sem);
        textViewsub.setText(subject);

    }

    private void InsertAttendanceToDatabase() {

        String url = "http://10.0.2.2/onlineattendance/Attendance.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(StudentsList.this,response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentsList.this, "Fail To Response" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("fid",fid);
                param.put("pAttendanceList", String.valueOf(pAttendanceList));
                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(StudentsList.this);
        queue.add(request);
    }
}