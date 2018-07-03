package com.example.apple.fyp.Adapters;

/**
 * Created by GeeksEra on 4/27/2018.
 */


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.fyp.Home;
import com.example.apple.fyp.Database.MyApplication;
import com.example.apple.fyp.Objects.EMailObject;
import com.example.apple.fyp.Objects.Email;
import com.example.apple.fyp.Objects.Menu;
import com.example.apple.fyp.Objects.ServerHandler;
import com.example.apple.fyp.R;
import com.example.apple.fyp.ReadMailActivity;
import com.example.apple.fyp.Utils.AppUtils;
import com.example.apple.fyp.menu_inbox;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class Email_Adapter extends RecyclerView.Adapter<Email_Adapter.MyViewHolder> {
    List<EMailObject> list;
    Activity context;
    MyApplication myApplication;
    Dialog Optiondialog;
    String FolderName;

    ProgressDialog progressDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView Card;
        TextView tvEmail, tvEmailSubject, tvCenterWord, txtItemTime;

        public MyViewHolder(View view) {
            super(view);
            tvEmail = (TextView) view.findViewById(R.id.txtItemEmailFrom);
            tvEmailSubject = (TextView) view.findViewById(R.id.txtItemEmailSubject);
            tvCenterWord = (TextView) view.findViewById(R.id.txtItemEmailCenterWord);
            txtItemTime = view.findViewById(R.id.txtItemTime);
            Card = view.findViewById(R.id.Card);
            view.setSelected(true);
        }
    }


    public Email_Adapter(List<EMailObject> sList, Activity context, String FolerName) {
        this.list = sList;
        this.context = context;
        try {
            myApplication = (MyApplication) context.getApplicationContext();

        } catch (Exception Ex) {

        }
        this.FolderName = FolerName;
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading....");
        } catch (Exception Ex) {

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_item_design, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolderEmail, final int position) {


        if (list.get(position).getDeleted()) {
            viewHolderEmail.Card.setVisibility(View.GONE);
        }

        if (!list.get(position).getMoved().equals(FolderName)) {
            viewHolderEmail.Card.setVisibility(View.GONE);
        }


        viewHolderEmail.tvEmail.setText(list.get(position).getFrom());
        viewHolderEmail.tvEmailSubject.setText(list.get(position).getSubject());
        viewHolderEmail.tvCenterWord.setText(list.get(position).getFrom().charAt(0) + "");
        viewHolderEmail.txtItemTime.setText(list.get(position).getTime());


        if (!list.get(position).getUnRead()) {
            viewHolderEmail.Card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }

        viewHolderEmail.Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyDataSetChanged();
                myApplication.setCurrentReadEmail(list.get(position));
                Intent intent = new Intent(context, ReadMailActivity.class);
                intent.putExtra("from", list.get(position).getFrom());
                intent.putExtra("subject", list.get(position).getSubject());
                intent.putExtra("msg", list.get(position).getMessage());
                context.startActivity(intent);
                list.get(position).setUnRead(true);
                myApplication.MarkRead(list.get(position));

            }
        });

        viewHolderEmail.Card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showDialog(context, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void showDialog(final Activity activity, final int Position) {
        Optiondialog = new Dialog(activity);
        Optiondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Optiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog.setCancelable(true);
        Optiondialog.setContentView(R.layout.select_an_layout_dialog);


        Optiondialog.findViewById(R.id.Delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myApplication.setCurrentDeletedEmail(FolderName.split("/")[0]);
                myApplication.setCurrentDeletedEmailObject(list.get(Position));
                new DeleteEmail().execute();
                list.get(Position).setDeleted(true);
                Optiondialog.dismiss();
                myApplication.MarkDelete(list.get(Position));
                notifyDataSetChanged();

            }
        });
        Optiondialog.findViewById(R.id.Move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowEmailsList(activity, Position);
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


    public void showDialogForMove(Activity activity, final int EmailPosition) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.move_email_dialog_layout);


        final List<String> List = new ArrayList<>();

        List.addAll(myApplication.getOnlineManus());

        for (int i = 0; i < List.size(); i++) {
            if (List.get(i).equals(FolderName)) {
                List.remove(i);
            }
        }
        List.remove(1);
        List.remove(List.size() - 1);

        ListView my_listview = (ListView) dialog.findViewById(R.id.List);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, List);
        my_listview.setAdapter(adapter);

        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (AppUtils.haveNetworkConnection(context))
                    new MoveEmailTask().execute();
                myApplication.setCurrentEmailMoveObject(list.get(EmailPosition));
                myApplication.setCurrentEmailMoveFolderName(FolderName.split("/")[0]);
                myApplication.setCuurentEmailFolderToMove(List.get(position));
                dialog.dismiss();
                Toast.makeText(context, "Email Moved", Toast.LENGTH_SHORT).show();
                list.get(EmailPosition).setMoved(List.get(position) + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());
                myApplication.MoveEmail(list.get(EmailPosition), List.get(position) + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());
                String MovedFolder = List.get(position) + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail();
                notifyDataSetChanged();
            }
        });


        dialog.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public void ShowEmailsList(final Activity activity, final int EmailPosition) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.move_email_dialog_layout);
        final TextView Messsage = dialog.findViewById(R.id.Messsage);

        final List<String> List = new ArrayList<>();

        List<Email> Hotmail = myApplication.getEmail("Hotmail");
        List<Email> Gmail = myApplication.getEmail("Gmail");
        List<Email> Yahoo = myApplication.getEmail("Yahoo");

        if (Gmail != null)
            for (int i = 0; i < Gmail.size(); i++) {
                List.add(Gmail.get(i).getEmail());
            }
        if (Yahoo != null)
            for (int i = 0; i < Yahoo.size(); i++) {
                List.add(Yahoo.get(i).getEmail());
            }
        if (Hotmail != null)
            for (int i = 0; i < Hotmail.size(); i++) {
                List.add(Hotmail.get(i).getEmail());
            }


        if (List.size() > 1) {
            Messsage.setText("Select Email");
            ListView my_listview = (ListView) dialog.findViewById(R.id.List);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, List);
            my_listview.setAdapter(adapter);

            my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    showDialogForMove(activity, EmailPosition, List.get(position));
                    dialog.dismiss();
                }
            });


            dialog.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();


        } else {
            dialog.hide();
            showDialogForMove(activity, EmailPosition);
        }


    }


    public void showDialogForMove(Activity activity, final int EmailPosition, final String Email) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.move_email_dialog_layout);


        final List<String> List = new ArrayList<>();

        List.addAll(myApplication.getOnlineManusMyEmail(Email));

        for (int i = 0; i < List.size(); i++) {
            if (List.get(i).equals(FolderName)) {
                List.remove(i);
            }
        }

        List.remove(1);
        List.remove(List.size() - 1);
        ListView my_listview = (ListView) dialog.findViewById(R.id.List);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, List);
        my_listview.setAdapter(adapter);

        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String EmailFolderToMOve = List.get(position) + "/" + Email;
                myApplication.setCurrentEmailMoveFolderName(FolderName.split("/")[0]);
                myApplication.setCuurentEmailFolderToMove(List.get(position) + "/" + Email);
                myApplication.setCurrentEmailMoveObject(list.get(EmailPosition));
                if (AppUtils.haveNetworkConnection(context))
                    new MoveEmailToOtherTask().execute();
                Toast.makeText(context, "Email Moved", Toast.LENGTH_SHORT).show();
                list.get(EmailPosition).setMoved(List.get(position) + "/" + Email);
                myApplication.MoveEmail(list.get(EmailPosition), List.get(position) + "/" + Email);
                notifyDataSetChanged();
                dialog.dismiss();

            }
        });


        dialog.findViewById(R.id.Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private class DeleteEmail extends AsyncTask<String, Void, Void> {


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
            String FolderName = myApplication.getCurrentDeletedEmail();
            try {
                if (myApplication.getCurrentLogin().equals("Gmail") && !FolderName.contains("INBOX")) {
                    emailFolder = store.getFolder("[Gmail]/" + FolderName);
                } else {
                    emailFolder = store.getFolder(FolderName);
                }
                // use READ_ONLY if you don't wish the messages
                // to be marked as read after retrieving its content
                emailFolder.open(Folder.READ_WRITE);
                Message message = emailFolder.getMessage(myApplication.getCurrentDeletedEmailObject().getId());
                message.setFlag(Flags.Flag.DELETED, true);
                message.getFolder().expunge();

            } catch (Exception e) {
                String Mesage = e.getMessage();
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

        }
    }


    private class MoveEmailTask extends AsyncTask<String, Void, Void> {


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
            Folder MoveFolder = null;

            String FolderName = myApplication.getCurrentEmailMoveFolderName();
            String MOveFolderName = myApplication.getCuurentEmailFolderToMove();

            try {
                if (myApplication.getCurrentLogin().equals("Gmail") && !FolderName.contains("INBOX")) {
                    emailFolder = store.getFolder("[Gmail]/" + FolderName);
                } else {
                    emailFolder = store.getFolder(FolderName);
                }


                if (myApplication.getCurrentLogin().equals("Gmail") && !FolderName.contains("INBOX")) {
                    MoveFolder = store.getFolder("[Gmail]/" + MOveFolderName);
                } else {
                    MoveFolder = store.getFolder(MOveFolderName);
                }
                // use READ_ONLY if you don't wish the messages
                // to be marked as read after retrieving its content
                emailFolder.open(Folder.READ_WRITE);
                Message message = emailFolder.getMessage(myApplication.getCurrentEmailMoveObject().getId());
                message.getFolder().copyMessages(new Message[]{message}, MoveFolder);
                message.setFlag(Flags.Flag.DELETED, true);
                message.getFolder().expunge();

            } catch (Exception e) {
                String Mesage = e.getMessage();
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

        }
    }


    private class MoveEmailToOtherTask extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            //Copy you logic to calculate progress and call
            String FolderName = myApplication.getCurrentEmailMoveFolderName();
            String MOveFolderName = myApplication.getCuurentEmailFolderToMove();
            Store store = null;
            Store store2 = null;
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            Properties properties1 = new Properties();
            properties1.put("mail.store.protocol", "imaps");

            Session emailSession1 = Session.getDefaultInstance(properties1);
            Session emailSession = Session.getDefaultInstance(properties);

            try {
                store2 = emailSession1.getStore();
                if (MOveFolderName.contains("gmail")) {
                    Email email = null;
                    List<Email> Gmail = myApplication.getEmail("Gmail");
                    for (int i = 0; i < Gmail.size(); i++) {
                        if (Gmail.get(i).getEmail().equals(MOveFolderName.split("/")[1])) {
                            email = Gmail.get(i);
                        }
                    }
                    store2.connect(ServerHandler.GMAIL_HOST, email.getEmail(), email.getPassword());

                }
                if (MOveFolderName.contains("yahoo")) {
                    Email email = null;
                    List<Email> Yahoo = myApplication.getEmail("Yahoo");
                    for (int i = 0; i < Yahoo.size(); i++) {
                        if (Yahoo.get(i).getEmail().equals(MOveFolderName.split("/")[1])) {
                            email = Yahoo.get(i);
                        }
                    }
                    store2.connect("imap.mail.yahoo.com", email.getEmail(), email.getPassword());

                }
                if (MOveFolderName.contains("hotmail")) {
                    Email email = null;
                    List<Email> Hotmail = myApplication.getEmail("Hotmail");
                    for (int i = 0; i < Hotmail.size(); i++) {
                        if (Hotmail.get(i).getEmail().equals(MOveFolderName.split("/")[1])) {
                            email = Hotmail.get(i);
                        }
                    }
                    store2.connect("pop3.live.com", email.getEmail(), email.getPassword());

                }

                store = emailSession.getStore();
                if (myApplication.getCurrentLogin().toLowerCase().contains("gmail"))
                    store.connect(ServerHandler.GMAIL_HOST, myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());
                if (myApplication.getCurrentLogin().toLowerCase().contains("yahoo"))
                    store.connect("imap.mail.yahoo.com", myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());
                if (myApplication.getCurrentLogin().toLowerCase().contains("hotmail"))
                    store.connect("pop3.live.com", myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());


            } catch (Exception e) {
                e.printStackTrace();
            }

            Folder emailFolder = null;
            Folder MoveFolder = null;


            try {
                if (myApplication.getCurrentLogin().equals("Gmail") && !FolderName.contains("INBOX")) {
                    emailFolder = store.getFolder("[Gmail]/" + FolderName.split("/")[0]);
                } else {
                    emailFolder = store.getFolder(FolderName);
                }


                if (MOveFolderName.split("/")[1].toLowerCase().contains("gmail")) {
                    if (!MOveFolderName.split("/")[0].contains("INBOX"))
                    {
                        MoveFolder = store2.getFolder("[Gmail]/" + MOveFolderName.split("/")[0]);
                    }else{
                        MoveFolder = store2.getFolder(MOveFolderName.split("/")[0]);

                    }
                } else {
                    MoveFolder = store2.getFolder(MOveFolderName.split("/")[0]);
                }
                // use READ_ONLY if you don't wish the messages
                // to be marked as read after retrieving its content
                emailFolder.open(Folder.READ_WRITE);
                Message message = emailFolder.getMessage(myApplication.getCurrentEmailMoveObject().getId());
                message.getFolder().copyMessages(new Message[]{message}, MoveFolder);
                message.setFlag(Flags.Flag.DELETED, true);
                message.getFolder().expunge();

            } catch (Exception e) {
                String Mesage = e.getMessage();
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
        }
    }

}

