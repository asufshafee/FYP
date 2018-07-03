package com.example.apple.fyp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apple.fyp.Adapters.Email_Adapter;
import com.example.apple.fyp.Database.MyApplication;
import com.example.apple.fyp.Objects.EMailObject;
import com.example.apple.fyp.Objects.ServerHandler;
import com.example.apple.fyp.Utils.AppUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

import br.vince.easysave.EasySave;


/**
 * A simple {@link Fragment} subclass.
 */
public class menu_inbox extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    List<EMailObject> list;
    private Email_Adapter mAdapter;
    MyApplication myApplication;


    ProgressDialog progressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int total;

    RecyclerView recyclerView;
    View view;


    @Override
    public void onRefresh() {
        RefreshList();
        if (!Connected) {
            try {
                getEmailTask.execute();
            } catch (Exception Ex) {

            }


        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_inbox, container, false);
        myApplication = (MyApplication) getActivity().getApplicationContext();
        recyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        list = new ArrayList<>();
        getEmailTask = new GetEmailTask();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Checking for new Messages");
        String FolderName = myApplication.getCurrentFolderName();
        list = myApplication.getEmailsWithFolderName(myApplication.getCurrentFolderName() + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());
        mSwipeRefreshLayout.setRefreshing(true);
        myApplication.setCurrentEmailMoveFolderName(myApplication.getCurrentFolderName() + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());

        Collections.sort(list, new Comparator<EMailObject>() {
            public int compare(EMailObject obj1, EMailObject obj2) {
                return obj1.getTime().compareToIgnoreCase(obj2.getTime()); // To compare string values
            }
        });

        Collections.reverse(list);


        if (list == null || list.size() == 0) {
            First = false;
            list = new ArrayList<>();
        } else {
            First = true;

        }
        ShowList();
        if (AppUtils.haveNetworkConnection(getActivity()))
            getEmailTask.execute();
        else mSwipeRefreshLayout.setRefreshing(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                RefreshList();
            }
        }, 10000);
        return view;

    }


    public void ShowList() {
        mAdapter = new Email_Adapter(list, getActivity(), myApplication.getCurrentFolderName() + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    int lastSize = 0;
    int runAsynTask = 0;

    GetEmailTask getEmailTask;

    public void RefreshList() {
        if (!AppUtils.haveNetworkConnection(getActivity()))
            return;

        if (getEmailTask.getStatus() == AsyncTask.Status.RUNNING) {
        } else {
            if (runAsynTask == 5) {
                try {
                    getEmailTask.execute();
                } catch (Exception e) {
                }
            } else {
                runAsynTask++;
            }
        }

        if (myApplication.getEmailsWithFolderName(myApplication.getCurrentFolderName() + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail()).size() > lastSize) {
            list = myApplication.getEmailsWithFolderName(myApplication.getCurrentFolderName() + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());
            lastSize = list.size();
            Collections.sort(list, new Comparator<EMailObject>() {
                public int compare(EMailObject obj1, EMailObject obj2) {
                    return obj1.getTime().compareToIgnoreCase(obj2.getTime()); // To compare string values
                }
            });
            Collections.reverse(list);

            if (list.size() < total) {
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }


            ShowList();
            mAdapter.notifyDataSetChanged();
            Log.d("FYPLogsMantains", "Loaded " + String.valueOf(list.size()));
            if (Connected) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        RefreshList();
                    }
                }, 5000);
            } else {
                mSwipeRefreshLayout.setRefreshing(false);

            }
        } else {
            mAdapter.notifyDataSetChanged();

        }


    }

    Store store = null;

    public void getALLGMailMessages() {
        //ArrayList<EMailObject> list = new ArrayList<>();

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        Session emailSession = Session.getDefaultInstance(properties);

        try {
            store = emailSession.getStore();
            String Email = myApplication.getCurrentLogin();
            String EmailMain = myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail();
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
        Connected = store.isConnected();
        getEmails();

        //   return list;

    }

    Boolean First = false;
    Boolean Connected = true;

    public void getEmails() {
        if (Connected) {
            int count = 0;
            Folder emailFolder = null;
            Folder MoveFolder = null;
            String DeletedFolderName = "";

            String FolderName = myApplication.getCurrentFolderName();
            try {
                if (myApplication.getCurrentLogin().equals("Gmail") && !FolderName.contains("INBOX")) {
                    emailFolder = store.getFolder("[Gmail]/" + FolderName);
                } else {
                    emailFolder = store.getFolder(FolderName);
                }


                if (myApplication.getCurrentLogin().equals("Gmail")) {
                    MoveFolder = store.getFolder("[Gmail]/Spam");
                    DeletedFolderName = "Spam";
                } else if (myApplication.getCurrentLogin().equals("yahoo")) {
                    MoveFolder = store.getFolder("Trash");
                    DeletedFolderName = "Trash";
                } else {
                    MoveFolder = store.getFolder("Deleted");
                    DeletedFolderName = "Deleted";

                }


                // use READ_ONLY if you don't wish the messages
                // to be marked as read after retrieving its content
                emailFolder.open(Folder.READ_WRITE);

                count = emailFolder.getMessages().length;
            } catch (MessagingException e) {
                String Mesage = e.getMessage();
                e.printStackTrace();
            }

            // search for all "unseen" messages
//            Flags seen = new Flags(Flags.Flag.SEEN);
//
//            FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
//            Flags seen = new Flags(Flags.Flag.SEEN);
//            FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
//            try {
//                emailFolder.search(unseenFlagTerm);
//            } catch (MessagingException e) {
//                e.printStackTrace();
            //HJASVDFNHHACJVk
//            }
            javax.mail.Message[] messages = new javax.mail.Message[count];

            try {
                if (list.size() >= emailFolder.getMessages().length) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);

                        }
                    });
                    return;
                }
                if (!First) {
                    First = true;
                    list = new ArrayList<>();
                    messages = emailFolder.getMessages();
                    Log.d("FYPLogsMantains", "Total  " + String.valueOf(messages.length));
                    total = messages.length;
                } else {
                    messages = emailFolder.getMessages(list.get(list.size() - 1).getId(), emailFolder.getMessages()[emailFolder.getMessageCount() - 1].getMessageNumber());
                    Log.d("FYPLogsMantains", "Total Left " + String.valueOf(messages.length));
                    total = messages.length + list.size();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < (messages.length); i++) {

                javax.mail.Message message = messages[i];
                try {
                    message.getFlags();

                    Address[] fromAddress = message.getFrom();
                    String from = fromAddress[0].toString();
                    String subject = message.getSubject();
                    String sentDate = message.getReceivedDate().toString();

                    String contentType = message.getContentType();
                    String messageContent = "";

                    // store attachment file name, separated by comma
                    String attachFiles = "";
                    if (contentType.contains("multipart")) {
                        Multipart multiPart = (Multipart) message.getContent();
                        int numberOfParts = multiPart.getCount();
                        for (int partCount = 0; partCount < numberOfParts; partCount++) {
                            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                            messageContent = part.getContent().toString();
                        }
                    } else if (contentType.contains("text/plain")
                            || contentType.contains("text/html")) {
                        Object content = message.getContent();
                        if (content != null) {
                            messageContent = content.toString();
                        }
                    }

                    // print out details of each message
                    final EMailObject eMailObject = new EMailObject();
                    eMailObject.setFrom(from);
                    eMailObject.setTime(getCurrentTimeStamp(message.getReceivedDate()));
                    eMailObject.setId(message.getMessageNumber());

                    eMailObject.setUniqueID(Calendar.getInstance().getTimeInMillis());
                    List<String> AllRecipients = new LinkedList<>();
                    for (Address address : message.getAllRecipients())
                        AllRecipients.add(address.toString());


                    List<String> From = new LinkedList<>();
                    for (Address address : message.getFrom())
                        From.add(address.toString());

                    eMailObject.setAddresses(AllRecipients);
                    eMailObject.setFromAddress(From);
                    if (!message.getFlags().contains(Flags.Flag.SEEN))
                        eMailObject.setUnRead(false);
                    eMailObject.setSubject(subject);
                    eMailObject.setSendDate(sentDate);
                    eMailObject.setAttachments(attachFiles);
                    eMailObject.setMessage(messageContent);
                    eMailObject.setMoved(myApplication.getCurrentFolderName() + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());
                    eMailObject.setEmail(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());


                    List<String> BlockEmails = myApplication.getBlockMails(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());

                    for (String Email : BlockEmails) {
                        for (Address address : message.getFrom()) {
                            if (address.toString().contains(Email)) {

                                message.getFolder().copyMessages(new Message[]{message}, MoveFolder);
                                message.setFlag(Flags.Flag.DELETED, true);
                                message.getFolder().expunge();
                                eMailObject.setMoved(DeletedFolderName + "/" + myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());


                            }
                        }
                    }


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EMailObject[] eMailObjects = new EMailObject[1];
                            eMailObjects[0] = eMailObject;
                            myApplication.setListNewEmails(eMailObjects);
                        }
                    });

                    Log.v("email", eMailObject.getFrom());
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            return;
        }

    }


    private class GetEmailTask extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            //Copy you logic to calculate progress and call
            getALLGMailMessages();
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public String getCurrentTimeStamp(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    @Override
    public void onDestroyView() {
        getEmailTask.cancel(true);
        myApplication.SaveEmails();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
