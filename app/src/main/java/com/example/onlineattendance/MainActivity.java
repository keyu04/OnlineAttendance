package com.example.onlineattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText editTextusername, editTextpassword;
    Button submitbutton;
    String username, password;
    String fname, fid;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager=new SessionManager(MainActivity.this);

        editTextusername = findViewById(R.id.edittextboxusername);
        editTextpassword = findViewById(R.id.edittextboxpassword);
        submitbutton = findViewById(R.id.buttonLogin);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextusername.getText().toString();
                password = editTextpassword.getText().toString();
                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username Empty", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Password Empty", Toast.LENGTH_SHORT).show();
                } else {
                    LoginCheck();
                }
            }
        });
    }

    private void LoginCheck() {
        String url = "http://10.0.2.2/onlineattendance/LoginCheck.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    String url = "http://10.0.2.2/onlineattendance/fetchname.php?username=" + username + "&password=" + password;
                    StringRequest name = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("tbl_faculty");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        fid = jsonObject1.getString("fid");
                                        fname = jsonObject1.getString("f_name");
                                        sessionManager.createSession(fname);
                                    }
                                }
                                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                intent.putExtra("fname", fname);
                                intent.putExtra("fid", fid);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue NameFetch = Volley.newRequestQueue(MainActivity.this);
                    NameFetch.add(name);

                } else if (response.equals("fail")) {
                    Toast.makeText(MainActivity.this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("username", username);
                param.put("password", password);
                return param;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

}