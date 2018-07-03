package com.example.apple.fyp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import com.example.apple.fyp.Objects.ServerHandler;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

public class ReadMailActivity extends AppCompatActivity {

    TextView textViewFrom, textViewSubject, textViewMessage;
    WebView webView;

    MyApplication myApplication;
    EMailsHandler eMailsHandler;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending....");

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

    EditText Email;

    public void Forward(final Activity activity) {
        final Dialog Optiondialog = new Dialog(activity);
        Optiondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Optiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog.setCancelable(true);
        Optiondialog.setContentView(R.layout.forword_email_dialog);


        Email = Optiondialog.findViewById(R.id.Email);

        Optiondialog.findViewById(R.id.ForWord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                new ForWordEmail().execute();
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


        final TextView Email = Optiondialog.findViewById(R.id.Email);
        EMailObject eMailObject1 = myApplication.getCurrentReadEmail();
        for (String address : eMailObject1.getFromAddress()) {
            Email.setText(address.toString());
        }

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


    private class ForWordEmail extends AsyncTask<String, Void, Void> {


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
                    store.connect(ServerHandler.GMAIL_HOST, myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());
                if (myApplication.getCurrentLogin().toLowerCase().contains("yahoo"))
                    store.connect("imap.mail.yahoo.com", myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());
                if (myApplication.getCurrentLogin().toLowerCase().contains("hotmail"))
                    store.connect("pop3.live.com", myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            Folder emailFolder = null;

            String FolderName = myApplication.getCurrentEmailMoveFolderName();

            try {
                if (myApplication.getCurrentLogin().equals("Gmail") && !FolderName.contains("INBOX")) {
                    emailFolder = store.getFolder("[Gmail]/" + FolderName.split("/")[0]);
                } else {
                    emailFolder = store.getFolder(FolderName.split("/")[0]);
                }

                emailFolder.open(Folder.READ_WRITE);
                Message message = emailFolder.getMessage(myApplication.getCurrentReadEmail().getId());


                Transport t = emailSession.getTransport("smtp");
                try {
                    //connect to the smpt server using transport instance
                    //change the user and password accordingly
                    t.connect(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());
                    t.sendMessage(message, InternetAddress.parse(Email.getText().toString()));
                } finally {
                    t.close();
                }
            } catch (Exception e) {
                String Mesage = e.getMessage();
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();

                }
            });
        }
    }
}
