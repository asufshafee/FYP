package com.example.apple.fyp.Objects;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.apple.fyp.Database.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

/**
 * Created by Malik on 4/23/2018.
 */

public class EMailsHandler {


    MyApplication myApplication;

    public Properties getGmailProperties() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        return props;
    }

    public Properties getYahoo() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.mail.yahoo.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.port", "587");
        return props;
    }

    public Properties getHotmail() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.live.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return props;
    }

    Session session;

    public Session getSession() {
        if (myApplication.getCurrentLogin().equals("Gmail")) {
            session = Session.getInstance(getGmailProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());

                }
            });
        }
        if (myApplication.getCurrentLogin().equals("Yahoo")) {
            session = Session.getInstance(getGmailProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());

                }
            });
        }
        if (myApplication.getCurrentLogin().equals("Hotmail")) {
            session = Session.getInstance(getHotmail(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail(), myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getPassword());

                }
            });
        }

        return session;
    }

    public boolean SendEmail(final Context context, EMailObject eMailObject) {
        try {
            myApplication = (MyApplication) context.getApplicationContext();
            final Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail()));
            message.setSubject(eMailObject.getSubject());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(eMailObject.getTo()));
            if (!eMailObject.getCc().equals(""))
                message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(eMailObject.getCc()));
            if (!eMailObject.getBcc().equals(""))
                message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(eMailObject.getBcc()));
            message.setText(eMailObject.getMessage());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            Toast.makeText(context, "Email Sent", Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    public static ArrayList<EMailObject> getALLGMailMessages() {
        ArrayList<EMailObject> list = new ArrayList<>();

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        Session emailSession = Session.getDefaultInstance(properties);
        Store store = null;
        try {
            store = emailSession.getStore();
            store.connect(ServerHandler.GMAIL_HOST, ServerHandler.EMAIL_USER, ServerHandler.EMAIL_PASSWORD);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        int count = 0;
        Folder emailFolder = null;
        try {
            emailFolder = store.getFolder("INBOX");
            // use READ_ONLY if you don't wish the messages
            // to be marked as read after retrieving its content
            emailFolder.open(Folder.READ_WRITE);
            count = emailFolder.getNewMessageCount();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // search for all "unseen" messages
        Flags seen = new Flags(Flags.Flag.SEEN);

        FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
        javax.mail.Message[] messages = new javax.mail.Message[count];

        try {

            messages = emailFolder.search(unseenFlagTerm);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < messages.length; i++) {

            javax.mail.Message message = messages[i];
            try {
                message.getFlags();

                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                String sentDate = message.getSentDate().toString();

                String contentType = message.getContentType();
                String messageContent = "";

                // store attachment file name, separated by comma
                String attachFiles = "";
                if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }

                // print out details of each message
                EMailObject eMailObject = new EMailObject();
                eMailObject.setFrom(from);
                eMailObject.setSubject(subject);
                eMailObject.setSendDate(sentDate);
                eMailObject.setAttachments(attachFiles);
                eMailObject.setMessage(messageContent);
                list.add(eMailObject);
                // Log.v("email",eMailObject.getFrom());
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return list;

    }
}
