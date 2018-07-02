package com.example.apple.fyp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.apple.fyp.Objects.EMailObject;
import com.example.apple.fyp.Objects.EMailsHandler;

import java.io.File;
import java.util.List;

import easyfilepickerdialog.kingfisher.com.library.model .DialogConfig;
import easyfilepickerdialog.kingfisher.com.library.model.SupportFile;
import easyfilepickerdialog.kingfisher.com.library.view.FilePickerDialogFragment;


public class compose_email extends AppCompatActivity {

    ImageButton btnAttach;
    EditText editTextTo,editTextCC,editTextBCC, editTextSubject, editTextMessage, editTextAttacments;

    EMailsHandler eMailsHandler;
    public static String TAG = "file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_email);
        editTextTo = (EditText) findViewById(R.id.edtComposeMailTo);
        editTextSubject = (EditText) findViewById(R.id.edtComposeMailSubject);
        editTextMessage = (EditText) findViewById(R.id.edtComposeMailMessageBody);
        editTextAttacments = (EditText) findViewById(R.id.edtComposeMailAttachment);
        editTextBCC = (EditText) findViewById(R.id.edtComposeMailToBCC);
        editTextCC = (EditText) findViewById(R.id.edtComposeMailToCC);

        getSupportActionBar().setTitle("Compose an Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        eMailsHandler = new EMailsHandler();


        btnAttach = (ImageButton) findViewById(R.id.btnComposeMailAddAttachment);
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(compose_email.this,
                        "Working Successfully", Toast.LENGTH_LONG).show();
                DialogConfig dialogConfig = new DialogConfig.Builder()
                        .enableMultipleSelect(true) // default is false
                        .enableFolderSelect(true) // default is false
                        .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android") // default is sdcard
                        .build();

                new FilePickerDialogFragment.Builder()
                        .configs(dialogConfig)
                        .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                            @Override
                            public void onFileSelected(List<File> list) {
                                Log.e(TAG, "total Selected file: " + list.size());
                                for (File file : list) {
                                    Log.e(TAG, "Selected file: " + file.getAbsolutePath());
                                }
                            }
                        }).onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                    @Override
                    public void onFileSelected(List<File> list) {
                        if (list.size() > 0) {
                            editTextAttacments.setText(list.get(0).getAbsolutePath());
                        }
                    }
                })
                        .build()
                        .show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.action_send) {
            Toast.makeText(compose_email.this,
                    "Send Button Working Successfully", Toast.LENGTH_LONG).show();
            EMailObject eMailObject = new EMailObject();
            eMailObject.setMessage(editTextMessage.getText().toString());
            //eMailObject.setTo(editTextTo.getText().toString());
            eMailObject.setTo(editTextTo.getText().toString());
            eMailObject.setCc(editTextCC.getText().toString());
            eMailObject.setBcc(editTextBCC.getText().toString());
            eMailObject.setSubject(editTextSubject.getText().toString());

            if (eMailsHandler.SendEmail(getBaseContext(), eMailObject)) {
                //Toast.makeText(this, "Email Send Successfully", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Email No Send", Toast.LENGTH_SHORT).show();

            }
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(compose_email.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}




