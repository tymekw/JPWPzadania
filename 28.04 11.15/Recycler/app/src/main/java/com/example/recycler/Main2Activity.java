package com.example.recycler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;

public class Main2Activity extends AppCompatActivity {
    private Button ok;
    private Button anuluj;
    private EditText login;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        login = (EditText)findViewById(R.id.login);
        password = (EditText)findViewById(R.id.password);
        ok = (Button)findViewById(R.id.ok);
        anuluj = (Button)findViewById(R.id.anuluj);

        anuluj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(login.getText().toString(),password.getText().toString());
            }
        });
    }


    private void validate(String usrLogin, String usrPass){
        if(usrLogin.length()>=5 && usrLogin.length()<=200){
            if (usrPass.length()>=5 && usrPass.length()<=200) {
                Intent intent = new Intent(this, Main3Activity.class);
                startActivity(intent);
            }
        }
    }
}
