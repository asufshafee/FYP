package com.example.apple.fyp;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.apple.fyp.Adapters.Email_Adapter;
import com.example.apple.fyp.Database.MyApplication;
import com.example.apple.fyp.Objects.EMailObject;
import com.example.apple.fyp.Objects.EMailsHandler;

import javax.mail.Address;

public class ReadMailActivity extends AppCompatActivity {

    TextView textViewFrom, textViewSubject, textViewMessage;
    WebView webView;

    MyApplication myApplication;
    EMailsHandler eMailsHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);

        eMailsHandler = new EMailsHandler();

        myApplication = (MyApplication) getApplicationContext();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewFrom = (TextView) findViewById(R.id.tvReadMailFrom);
        textViewSubject = (TextView) findViewById(R.id.tvReadMailSubject);
        textViewMessage = (TextView) findViewById(R.id.tvReadMailMessage);
        textViewFrom.setText(getIntent().getStringExtra("from"));
        textViewSubject.setText(getIntent().getStringExtra("subject"));
        webView = (WebView) findViewById(R.id.webView);

        String text = getIntent().getStringExtra("msg");
        webView.loadData(text, "text/html; charset=UTF-8", null);
        textViewMessage.setText(Html.fromHtml(text));


        findViewById(R.id.Forword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Forward(ReadMailActivity.this);
            }
        });
        findViewById(R.id.Replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Replay(ReadMailActivity.this);

            }
        });
        findViewById(R.id.ReplayAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplayAll(ReadMailActivity.this);
            }
        });
    }


    public void ReplayAll(final Activity activity) {
        Optiondialog = new Dialog(activity);
        Optiondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Optiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog.setCancelable(true);
        Optiondialog.setContentView(R.layout.replay_all_email_dialog);


        final EditText Message = Optiondialog.findViewById(R.id.Messsage);

        Optiondialog.findViewById(R.id.Replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EMailObject eMailObject = myApplication.getCurrentReadEmail();
                eMailObject.setMessage(Message.getText().toString());

                for (String address : eMailObject.getAddresses()) {
                    eMailObject.setTo(address.toString());
                    eMailsHandler.SendEmail(getBaseContext(), eMailObject);
                }
                Optiondialog.dismiss();

            }
        });
        Optiondialog.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Optiondialog.dismiss();
            }
        });


        Optiondialog.show();

    }

    public void Forward(final Activity activity) {
        Optiondialog = new Dialog(activity);
        Optiondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Optiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog.setCancelable(true);
        Optiondialog.setContentView(R.layout.replay_all_email_dialog);


        final EditText Email = Optiondialog.findViewById(R.id.Messsage);

        Optiondialog.findViewById(R.id.Forward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Optiondialog.dismiss();

            }
        });
        Optiondialog.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Optiondialog.dismiss();
            }
        });


        Optiondialog.show();

    }


    Dialog Optiondialog;

    public void Replay(final Activity activity) {
        Optiondialog = new Dialog(activity);
        Optiondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Optiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog.setCancelable(true);
        Optiondialog.setContentView(R.layout.replay_email_dialog);


        final EditText Message = Optiondialog.findViewById(R.id.Messsage);

        Optiondialog.findViewById(R.id.Replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EMailObject eMailObject = myApplication.getCurrentReadEmail();
                eMailObject.setMessage(Message.getText().toString());

                for (String address : eMailObject.getFromAddress()) {
                    eMailObject.setTo(address.toString());
                    eMailsHandler.SendEmail(getBaseContext(), eMailObject);
                }
                Optiondialog.dismiss();

            }
        });
        Optiondialog.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Optiondialog.dismiss();
            }
        });


        Optiondialog.show();

    }
}
