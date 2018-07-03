package com.example.apple.fyp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apple.fyp.Database.MyApplication;
import com.example.apple.fyp.Objects.Email;
import com.example.apple.fyp.Objects.ServerHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;

public class configureActivity extends AppCompatActivity {

    Session session;
    EditText txtmail;
    EditText txtPassWord;
    MyApplication myApplication;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait....");


        txtmail = (EditText) findViewById(R.id.txtEmail);
        txtPassWord = (EditText) findViewById(R.id.txtPass);


        myApplication = (MyApplication) getApplicationContext();
        if (myApplication.getIsLogin()) {
            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }


        findViewById(R.id.Hotmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                myApplication.setCurentLogin("Hotmail");
                new LoginTask().execute();


            }
        });
        findViewById(R.id.Yahoo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApplication.setCurentLogin("Yahoo");
                progressDialog.show();
                new LoginTask().execute();


            }
        });

        findViewById(R.id.Gmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApplication.setCurentLogin("Gmail");
                progressDialog.show();
                new LoginTask().execute();


            }
        });


    }


    private class LoginTask extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            //Copy you logic to calculate progress and call

            Store store = null;
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");

            Session emailSession = Session.getDefaultInstance(properties);

            try {
                store = emailSession.getStore();
                if (myApplication.getCurrentLogin().toLowerCase().contains("gmail"))
                    store.connect(ServerHandler.GMAIL_HOST, txtmail.getText().toString(),txtPassWord.getText().toString());
                if (myApplication.getCurrentLogin().toLowerCase().contains("yahoo"))
                    store.connect("imap.mail.yahoo.com",  txtmail.getText().toString(),txtPassWord.getText().toString());
                if (myApplication.getCurrentLogin().toLowerCase().contains("hotmail"))
                    store.connect("pop3.live.com", txtmail.getText().toString(),txtPassWord.getText().toString());

            } catch (final NoSuchProviderException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.dismiss();
                e.printStackTrace();
            } catch (final MessagingException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.dismiss();
                e.printStackTrace();
            }

            if (store.isConnected()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        Email email = new Email();
                        email.setEmail(txtmail.getText().toString());
                        myApplication.setIsLogin(true);
                        email.setPassword(txtPassWord.getText().toString());
                        myApplication.setEmail(myApplication.getCurrentLogin(), email);
                        startActivity(intent);
                        finish();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
        }
    }


}
