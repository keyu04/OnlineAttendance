package com.example.onlineattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    SessionManager sessionManager;
    Spinner spinnerbatch, spinnercourse, spinnersem, spinnersubject;
    EditText editTexttopic;
    TextView textViewname;
    ArrayList<String> BatchList = new ArrayList<>();
    ArrayList<String> DeptList = new ArrayList<>();
    ArrayList<String> SemList = new ArrayList<>();
    ArrayList<String> SubjectList = new ArrayList<>();
    ArrayAdapter BatchAdapter, DeptAdapter, SemAdapter, SubjectAdapter;
    String selectedYear, selectedCourse, selectedSem, selectedSubject;
    String Faculty;
    String bid, did, sem_id, fid, sub_id, topic;
    Button submit;
    Date dateobj;
    DateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager=new SessionManager(Dashboard.this);
        sessionManager.checkLogin();

        Intent intent = getIntent();
        Faculty = intent.getStringExtra("fname");
        fid = intent.getStringExtra("fid");

        spinnerbatch = findViewById(R.id.batch);
        spinnercourse = findViewById(R.id.course);
        spinnersem = findViewById(R.id.sem);
        spinnersubject = findViewById(R.id.subject);
        editTexttopic = findViewById(R.id.topic);
//        textViewname=findViewById(R.id.FacultyName);//name for session
        submit = findViewById(R.id.submit);


        //session store
        HashMap<String,String> F=sessionManager.FacultyDetail();
        String fname=F.get(sessionManager.NAME);
//        textViewname.setText(fname);


        //current date
        dateobj = new Date();
        dateFormat = new SimpleDateFormat("dd-MM-yy");



        //menu
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {

                    case R.id.nav_home:
                        Toast.makeText(Dashboard.this, "Home is Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_login:
                        sessionManager.logout();
                        break;
                    default:
                        return true;

                }
                return true;
            }
        });


        String url1 = "http://10.0.2.2/onlineattendance/fechbatch.php";
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("tbl_batch");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String year = jsonObject1.getString("byear");
                                BatchList.add(year);
                                BatchAdapter = new ArrayAdapter<>(Dashboard.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, BatchList);
                                spinnerbatch.setAdapter(BatchAdapter);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue1 = Volley.newRequestQueue(Dashboard.this);
        queue1.add(stringRequest1);
        spinnerbatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getId() == R.id.batch) {

                    selectedYear = adapterView.getSelectedItem().toString();
                    batchid(selectedYear);//find batch id

                    if (selectedYear.equals("--Select Year--")) {
                        DeptList.clear();
                        spinnercourse.setEnabled(false);
                    } else {
                        spinnercourse.setEnabled(true);
                        String url2 = "http://10.0.2.2/onlineattendance/fetchdept.php?batch=" + selectedYear;
                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("tbl_dept");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String d_name = jsonObject1.getString("d_name");
                                        DeptList.add(d_name);
                                        DeptAdapter = new ArrayAdapter<>(Dashboard.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, DeptList);
                                        spinnercourse.setAdapter(DeptAdapter);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Dashboard.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        RequestQueue queue2 = Volley.newRequestQueue(Dashboard.this);
                        queue2.add(stringRequest2);
                        spinnercourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                if (adapterView.getId() == R.id.course) {
                                    SemList.clear();
                                    selectedCourse = adapterView.getSelectedItem().toString();
                                    deptid(selectedCourse);
                                    String url3 = "http://10.0.2.2/onlineattendance/fetchsems.php?course=" + selectedCourse;
                                    StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url3, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                JSONArray jsonArray = jsonObject.getJSONArray("tbl_sems");
                                                if(jsonArray.length() > 0)
                                                {
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                        String sem_name = jsonObject1.getString("sem_name");
                                                        SemList.add(sem_name);
                                                        SemAdapter = new ArrayAdapter<>(Dashboard.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, SemList);
                                                        spinnersem.setAdapter(SemAdapter);
                                                    }
                                                }
                                                else
                                                {
                                                    SemList.clear();
                                                    String[] Empty_list = new String[3];
                                                    Empty_list[0] = "";
                                                    SemAdapter = new ArrayAdapter<>(Dashboard.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Empty_list);
                                                    spinnersem.setAdapter(SemAdapter);
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
                                    RequestQueue queue3 = Volley.newRequestQueue(Dashboard.this);
                                    queue3.add(stringRequest3);
                                    spinnersem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            if (adapterView.getId() == R.id.sem) {
                                                SubjectList.clear();
                                                selectedSem = adapterView.getSelectedItem().toString();
                                                semid(selectedSem);
                                                String url4 = "http://10.0.2.2/onlineattendance/fetchsubject.php?faculty=" + Faculty + "&sem=" + selectedSem + "&dept=" + selectedCourse;
                                                StringRequest stringRequest4 = new StringRequest(Request.Method.GET, url4, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            JSONArray jsonArray = jsonObject.getJSONArray("tbl_subject");
                                                            if (jsonArray.length() > 0) {
                                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                    String sub_name = jsonObject1.getString("sub_name");
                                                                    SubjectList.add(sub_name);
                                                                    SubjectAdapter = new ArrayAdapter<>(Dashboard.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, SubjectList);
                                                                    spinnersubject.setAdapter(SubjectAdapter);
                                                                }
                                                            } else {
                                                                SubjectList.clear();
                                                                String[] Empty_list = new String[3];
                                                                Empty_list[0] = "";
                                                                SubjectAdapter = new ArrayAdapter<>(Dashboard.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Empty_list);
                                                                spinnersubject.setAdapter(SubjectAdapter);
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
                                                RequestQueue queue4 = Volley.newRequestQueue(Dashboard.this);
                                                queue4.add(stringRequest4);
                                                spinnersubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                        if (adapterView.getId() == R.id.subject) {
                                                            selectedSubject = adapterView.getSelectedItem().toString();
                                                            String url = "http://10.0.2.2/onlineattendance/topics/subjectid.php?subject=" + selectedSubject;
                                                            StringRequest subject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    try {

                                                                        JSONObject jsonObject = new JSONObject(response);
                                                                        JSONArray jsonArray = jsonObject.getJSONArray("tbl_subject");
                                                                        if (jsonArray.length() > 0) {
                                                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                                                                sub_id = jsonObject1.getString("sub_id");
                                                                            }
                                                                        }

                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Toast.makeText(Dashboard.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                            RequestQueue subjectid = Volley.newRequestQueue(Dashboard.this);
                                                            subjectid.add(subject);
                                                        }
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topic = editTexttopic.getText().toString();
                if (selectedSubject.isEmpty() || selectedSem.isEmpty() || selectedYear.isEmpty() || selectedCourse.isEmpty() || topic.isEmpty()) {
                    Toast.makeText(Dashboard.this, "Select/Filled the Field...", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);

                    builder.setMessage("Do you want to Submit The Data.. ?");
                    builder.setTitle("Alert !");
                    builder.setCancelable(false);


                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StudentList();
                            InsertTopicDetail();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void StudentList() {

        Intent intent = new Intent(Dashboard.this, StudentsList.class);
        intent.putExtra("SelectedYear", selectedYear);
        intent.putExtra("selectedCourse", selectedCourse);
        intent.putExtra("selectedsem", selectedSem);
        intent.putExtra("selectedsubject", selectedSubject);
        intent.putExtra("fid",fid);
        startActivity(intent);

    }

    private void InsertTopicDetail() {
        String url = "http://10.0.2.2/onlineattendance/topics/topicInserted.php";
        StringRequest topicrequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Dashboard.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, "Fail To Response" + error, Toast.LENGTH_SHORT).show();
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
                param.put("bid", bid);
                param.put("did", did);
                param.put("sem_id", sem_id);
                param.put("fid", fid);
                param.put("sub_id", sub_id);
                param.put("topic", topic);
                param.put("date", dateFormat.format(dateobj));

                return param;
            }
        };

        RequestQueue topicqueue = Volley.newRequestQueue(Dashboard.this);
        topicqueue.add(topicrequest);
    }

    private void semid(String selectedSem) {
        String url = "http://10.0.2.2/onlineattendance/topics/semid.php?sem_name=" + selectedSem;
        StringRequest sem = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("tbl_sems");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            sem_id = jsonObject1.getString("sem_id");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue semid = Volley.newRequestQueue(Dashboard.this);
        semid.add(sem);
    }

    private void deptid(String selectedCourse) {
        String url = "http://10.0.2.2/onlineattendance/topics/did.php?dept=" + selectedCourse;
        StringRequest dept = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("tbl_dept");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            did = jsonObject1.getString("did");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue deptid = Volley.newRequestQueue(Dashboard.this);
        deptid.add(dept);
    }

    private void batchid(String selectedYear) {
        String url = "http://10.0.2.2/onlineattendance/topics/bid.php?year=" + selectedYear;
        StringRequest batch = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("tbl_batch");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            bid = jsonObject1.getString("bid");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue Batchid = Volley.newRequestQueue(Dashboard.this);
        Batchid.add(batch);
    }
}