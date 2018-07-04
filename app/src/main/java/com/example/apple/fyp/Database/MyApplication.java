package com.example.apple.fyp.Database;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.apple.fyp.Objects.Archive;
import com.example.apple.fyp.Objects.BlockMail;
import com.example.apple.fyp.Objects.EMailObject;
import com.example.apple.fyp.Objects.Email;
import com.example.apple.fyp.Objects.Menu;
import com.example.apple.fyp.Objects.ServerHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import br.vince.easysave.EasySave;

public class MyApplication extends Application {


    List<EMailObject> listNewEmails;
    HashMap<String, List<EMailObject>> ALlFolderEmails;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    HashMap<String, List<Email>> Emails;
    Gson gson;
    int CurrentLoginEmailIndex = 0;
    List<String> OnlineManus;
    HashMap<String, List<String>> ALlEmailMenusOnline;
    String CurrentFolderName = "INBOX";
    String CurrentDeletedEmailFolder = "";
    EMailObject CurrentDeletedEmailObject = null;
    String CurrentEmailMoveFolderName = "";
    EMailObject CurrentEmailMoveObject = null;
    String CuurentEmailFolderToMove = "";
    EMailObject CurrentReadEmail = null;
    List<BlockMail> blockMails;
    List<Archive> archiveList;


    @Override
    public void onCreate() {

        gson = new Gson();
        pref = getApplicationContext().getSharedPreferences("FYP", MODE_PRIVATE);
        editor = pref.edit();


        listNewEmails = new EasySave(getApplicationContext()).retrieveList("Emails", EMailObject[].class);
        blockMails = new EasySave(getApplicationContext()).retrieveList("Block", BlockMail[].class);
        archiveList = new EasySave(getApplicationContext()).retrieveList("archiveList", Archive[].class);

        if (blockMails == null) {
            blockMails = new LinkedList<>();
        }
        if (archiveList == null) {
            archiveList = new LinkedList<>();
        }

        ALlEmailMenusOnline = new LinkedHashMap<>();
        if (listNewEmails == null) {
            listNewEmails = new LinkedList<>();
        }


        if (getEmailsJsonData().equals("")) {
            Emails = new HashMap<>();
        } else {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, List<Email>>>() {
            }.getType();
            Emails = gson.fromJson(getEmailsJsonData(), type);
        }


        ALlEmailMenusOnline = new HashMap<>();
//        if (getAllEmailMenusOnline().equals("")) {
//            ALlEmailMenusOnline = new HashMap<>();
//            OnlineManus = new LinkedList<>();
//
//        } else {
//            java.lang.reflect.Type type = new TypeToken<HashMap<String, List<String>>>() {
//            }.getType();
//            ALlEmailMenusOnline = gson.fromJson(getAllEmailMenusOnline(), type);
//            OnlineManus = ALlEmailMenusOnline.get(getEmail(getCurrentLogin()).get(getCurrentLoginEmailIndex()).getEmail());
//            if (OnlineManus == null)
//                OnlineManus = new LinkedList<>();
//        }

        super.onCreate();
    }


    public List<EMailObject> getEmailsWithFolderName(String FolderName) {

        List<EMailObject> ReturenEmail = new ArrayList<>();
        for (EMailObject eMailObject : listNewEmails) {

            if (eMailObject.getEmail().equals(getEmail(getCurrentLogin()).get(CurrentLoginEmailIndex).getEmail())) {
                if (FolderName.equals(eMailObject.getMoved()))
                    ReturenEmail.add(eMailObject);
            }
        }
        return ReturenEmail;

    }

    public void setListNewEmails(EMailObject[] eMailObject) {
        List<EMailObject> Temp = new LinkedList<>();
        Temp.addAll(listNewEmails);
        Temp.addAll(Arrays.asList(eMailObject));
        this.listNewEmails = Temp;
        SaveEmails();
    }

    public void MarkRead(EMailObject eMailObject) {
        for (EMailObject eMailObject1 : listNewEmails) {
            if (eMailObject1.getUniqueID() == eMailObject.getUniqueID()) {
                eMailObject1.setUnRead(true);
            }
        }
        SaveEmails();
    }

    public void MarkDelete(EMailObject eMailObject) {
        for (EMailObject eMailObject1 : listNewEmails) {
            if (eMailObject1.getUniqueID() == eMailObject.getUniqueID()) {
                eMailObject1.setDeleted(true);
            }
        }
        SaveEmails();
    }

    public void MoveEmail(EMailObject eMailObject, String FolderName) {
        for (EMailObject eMailObject1 : listNewEmails) {
            if (eMailObject1.getUniqueID() == eMailObject.getUniqueID()) {
                eMailObject1.setMoved(FolderName);
                eMailObject1.setEmail(FolderName.split("/")[1]);
            }
        }
        SaveEmails();
    }

    public void SaveEmails() {
        try {
            new EasySave(getApplicationContext()).saveModel("Emails", listNewEmails);
        } catch (Exception Ex) {

        }

    }

    public Boolean getIsLogin() {
        return pref.getBoolean("ISLOGIN", false);  // getting boolean
    }

    public void setIsLogin(Boolean ISLOGIN) {
        editor.putBoolean("ISLOGIN", ISLOGIN);  // Saving string
        editor.commit(); // commit changes
        editor.apply();
    }


    public String getCurrentLogin() {
        return pref.getString("CurentLogin", "");  // getting boolean
    }

    public void setCurentLogin(String login) {
        editor.putString("CurentLogin", login);  // Saving string
        editor.commit(); // commit changes
        editor.apply();
    }


    public String getEmailsJsonData() {
        return pref.getString("CurentLoginEmails", "");  // getting boolean
    }

    public void setEmailJsonData(String CurentLoginEmails) {
        editor.putString("CurentLoginEmails", CurentLoginEmails);  // Saving string
        editor.commit(); // commit changes
        editor.apply();
    }

    public String getAllEmailMenusOnline() {
        return pref.getString("AllEmailMenusOnline", "");  // getting boolean
    }

    public void setALlEmailMenusOnline(String AllEmailMenusOnline) {
        editor.putString("AllEmailMenusOnline", AllEmailMenusOnline);  // Saving string
        editor.commit(); // commit changes
        editor.apply();
    }


    public List<Email> getEmail(String ID) {
        try {
            return Emails.get(ID);
        } catch (Exception Ex) {
            return null;
        }
    }

    public void setEmail(String ID, Email email) {
        List<Email> emailList = new ArrayList<>();
        emailList.add(email);
        if (Emails.get(ID) == null) {
            Emails.put(ID, emailList);
        } else {
            emailList.addAll(Emails.get(ID));
        }
        Emails.put(ID, emailList);
        String hashMapString = gson.toJson(Emails);
        setEmailJsonData(hashMapString);

    }


    public void Lagout() {
        Emails = new HashMap<>();
        setEmailJsonData("");
        setIsLogin(false);
        CurrentLoginEmailIndex = 0;
        listNewEmails = null;
        new EasySave(getApplicationContext()).saveModel("Emails", listNewEmails);


    }

    public int getCurrentLoginEmailIndex() {
        return CurrentLoginEmailIndex;
    }

    public void setCurrentLoginEmailIndex(int currentLoginEmailIndex) {
        CurrentLoginEmailIndex = currentLoginEmailIndex;
    }


    public void AddMenus(List<String> Menu) {
        OnlineManus = new LinkedList<>();
        OnlineManus.addAll(Menu);
        String Email = getEmail(getCurrentLogin()).get(getCurrentLoginEmailIndex()).getEmail();
        ALlEmailMenusOnline.put(getEmail(getCurrentLogin()).get(getCurrentLoginEmailIndex()).getEmail(), OnlineManus);
        String Json = gson.toJson(ALlEmailMenusOnline);
        setALlEmailMenusOnline(Json);
    }


    public List<String> getOnlineManus() {
        List<String> Menus = ALlEmailMenusOnline.get(getEmail(getCurrentLogin()).get(getCurrentLoginEmailIndex()).getEmail());
        if (Menus == null)
            Menus = new LinkedList<>();
        return Menus;
    }


    public List<String> getOnlineManusMyEmail(String Email) {
        List<String> Menus = ALlEmailMenusOnline.get(Email);
        if (Menus == null)
            Menus = new LinkedList<>();
        return Menus;
    }


    public String getCurrentFolderName() {
        return CurrentFolderName;
    }

    public void setCurrentFolderName(String currentFolderName) {
        CurrentFolderName = currentFolderName;
    }


    public String getCurrentDeletedEmail() {
        return CurrentDeletedEmailFolder;
    }

    public void setCurrentDeletedEmail(String currentDeletedEmail) {
        CurrentDeletedEmailFolder = currentDeletedEmail;
    }

    public EMailObject getCurrentDeletedEmailObject() {
        return CurrentDeletedEmailObject;
    }

    public void setCurrentDeletedEmailObject(EMailObject currentDeletedEmailObject) {
        CurrentDeletedEmailObject = currentDeletedEmailObject;
    }


    public String getCurrentEmailMoveFolderName() {
        return CurrentEmailMoveFolderName;
    }

    public void setCurrentEmailMoveFolderName(String currentEmailMoveFolderName) {
        CurrentEmailMoveFolderName = currentEmailMoveFolderName;
    }

    public EMailObject getCurrentEmailMoveObject() {
        return CurrentEmailMoveObject;
    }

    public void setCurrentEmailMoveObject(EMailObject currentEmailMoveObject) {
        CurrentEmailMoveObject = currentEmailMoveObject;
    }

    public String getCuurentEmailFolderToMove() {
        return CuurentEmailFolderToMove;
    }

    public void setCuurentEmailFolderToMove(String cuurentEmailFolderToMove) {
        CuurentEmailFolderToMove = cuurentEmailFolderToMove;
    }


    public EMailObject getCurrentReadEmail() {
        return CurrentReadEmail;
    }

    public void setCurrentReadEmail(EMailObject currentReadEmail) {
        CurrentReadEmail = currentReadEmail;
    }


    public List<BlockMail> getBlockMails() {
        return blockMails;
    }

    public void setBlockMails(BlockMail blockMail) {
        this.blockMails.add(blockMail);
        new EasySave(getApplicationContext()).saveList("Block", blockMails);
    }


    public List<String> getBlockMails(String Account) {

        List<String> BlockEmails = new LinkedList<>();
        for (BlockMail blockMail : getBlockMails()) {
            if (blockMail.getAccount().equals(Account)) {
                BlockEmails.add(blockMail.getEmail());
            }
        }
        return BlockEmails;
    }


    public Archive getArchive(String Email) {
        for (Archive archive : archiveList) {
            if (archive.getEmail().equals(Email))
                return archive;
        }
        return new Archive();
    }

    public void setArchive(Archive archive) {
        for (Archive archive1 : archiveList) {
            if (archive1.getEmail().equals(archive.getEmail())) {
                if (archive1.getCheck()) {
                    Toast.makeText(getApplicationContext(), "Already Archived", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        List<Archive> archiveList1 = new LinkedList<>(archiveList);
        archiveList1.add(archive);
        this.archiveList = new LinkedList<>(archiveList1);
        new EasySave(getApplicationContext()).saveList("archiveList", archiveList);
    }
}
