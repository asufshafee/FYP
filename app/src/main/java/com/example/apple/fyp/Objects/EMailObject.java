package com.example.apple.fyp.Objects;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;

/**
 * Created by Malik on 4/23/2018.
 */

public class EMailObject {

    String to, from, subject, sendDate, message, attachments, cc="", bcc="";
    ArrayList<String> mails;
    String Time;
    Boolean UnRead = true;
    Boolean Deleted = false;
    String Moved = "";
    String Email;
    List<String> addresses;
    List<String> FromAddress;


    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public List<String> getFromAddress() {
        return FromAddress;
    }

    public void setFromAddress(List<String> fromAddress) {
        FromAddress = fromAddress;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Boolean getDeleted() {
        return Deleted;
    }

    public void setDeleted(Boolean deleted) {
        Deleted = deleted;
    }

    public String getMoved() {
        return Moved;
    }

    public void setMoved(String moved) {
        Moved = moved;
    }

    public Boolean getUnRead() {
        return UnRead;
    }

    public void setUnRead(Boolean unRead) {
        UnRead = unRead;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTo() {
        return to;

    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public ArrayList<String> getMails() {
        return mails;
    }

    public void setMails(ArrayList<String> mails) {
        this.mails = mails;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public EMailObject() {
        mails = new ArrayList<>();
    }

    public void addEmail(String email) {
        mails.add(email);
    }

    public ArrayList<String> getListOfMails() {
        return mails;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }
}
