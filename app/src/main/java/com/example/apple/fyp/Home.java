package com.example.apple.fyp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.fyp.Database.MyApplication;
import com.example.apple.fyp.Objects.BlockMail;
import com.example.apple.fyp.Objects.Email;
import com.example.apple.fyp.Objects.ServerHandler;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    MyApplication myApplication;
    Menu menu;
    Fragment CurrentFragment;
    ProgressDialog progressDialog;
    String NewLogin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myApplication = (MyApplication) getApplicationContext();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait....");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplication(), compose_email.class);
                startActivity(i);
                finish();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        List<String> MTemp = myApplication.getOnlineManus();
        getSupportActionBar().setTitle("INBOX");


        View header = navigationView.getHeaderView(0);
        TextView Email = (TextView) header.findViewById(R.id.Email);
        Email.setText(myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail());

        if (myApplication.getOnlineManus().size() == 0) {
            progressDialog.show();
            new GetALLFolders().execute();
        } else {
            for (String Menus : myApplication.getOnlineManus()) {
                menu.add(Menus);
            }
            CurrentFragment = new menu_inbox();
            getSupportFragmentManager().beginTransaction().add(R.id.container1, CurrentFragment).addToBackStack(null).commit();

        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    List<String> AccountRegistered = new LinkedList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);


        List<Email> Hotmail = myApplication.getEmail("Hotmail");
        if (Hotmail != null) {
            for (Email email : Hotmail) {
                AccountRegistered.add(email.getEmail());
                menu.add(Menu.NONE, 0, Menu.NONE, email.getEmail());

            }
        }
        List<Email> Yahoo = myApplication.getEmail("Yahoo");

        if (Yahoo != null) {
            for (Email email : Yahoo) {
                AccountRegistered.add(email.getEmail());
                menu.add(Menu.NONE, 0, Menu.NONE, email.getEmail());

            }

        }
        List<Email> Gmail = myApplication.getEmail("Gmail");

        if (Gmail != null) {
            for (Email email : Gmail) {
                AccountRegistered.add(email.getEmail());
                menu.add(Menu.NONE, 0, Menu.NONE, email.getEmail());

            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.NewAccount) {
            AddAccount(Home.this);
            return true;
        }

        if (id == 0) {
            if (item.getTitle().toString().contains("gmail")) {
                List<Email> Gmail = myApplication.getEmail("Gmail");
                int index = 0;
                for (int i = 0; i < Gmail.size(); i++) {
                    if (Gmail.get(i).getEmail().equals(item.getTitle().toString())) {
                        index = i;
                    }
                }

                myApplication.setCurrentLoginEmailIndex(index);
                myApplication.setCurentLogin("Gmail");

            }
            if (item.getTitle().toString().contains("hotmail")) {
                List<Email> Gmail = myApplication.getEmail("Hotmail");
                int index = 0;
                for (int i = 0; i < Gmail.size(); i++) {
                    if (Gmail.get(i).getEmail().equals(item.getTitle().toString())) {
                        index = i;
                    }
                }

                myApplication.setCurrentLoginEmailIndex(index);
                myApplication.setCurentLogin("Hotmail");

            }
            if (item.getTitle().toString().contains("yahoo")) {
                List<Email> Gmail = myApplication.getEmail("Yahoo");
                int index = 0;
                for (int i = 0; i < Gmail.size(); i++) {
                    if (Gmail.get(i).getEmail().equals(item.getTitle().toString())) {
                        index = i;
                    }
                }

                myApplication.setCurrentLoginEmailIndex(index);
                myApplication.setCurentLogin("Yahoo");
            }


            String Email = myApplication.getEmail(myApplication.getCurrentLogin()).get(myApplication.getCurrentLoginEmailIndex()).getEmail();

            startActivity(new Intent(getApplicationContext(), Home.class));
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (item.getTitle().toString().equals("Block Email")) {
            BlockEmailsDialog(Home.this);
        } else if (item.getTitle().equals("INBOX")) {
            getSupportActionBar().setTitle(item.getTitle().toString());
            myApplication.setCurrentFolderName(item.getTitle().toString());
            CurrentFragment = new menu_inbox();
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, CurrentFragment).commit();
        } else if (item.getTitle().toString().contains("Create Folder")) {
            CreateFolder(Home.this);
        } else if (item.getTitle().toString().contains("Logout")) {
            myApplication.Lagout();
            Intent i = new Intent(getApplicationContext(), configureActivity.class);
            startActivity(i);
            finish();
        } else {
            getSupportActionBar().setTitle(item.getTitle().toString());
            myApplication.setCurrentFolderName(item.getTitle().toString());
            CurrentFragment = new menu_inbox();
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, CurrentFragment).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void BlockEmailsDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.block_email_dialog);
        final EditText Email = dialog.findViewById(R.id.Email);
        final NiceSpinner Account = dialog.findViewById(R.id.Account);
        Account.attachDataSource(AccountRegistered);
        final ListView BlockEmails = dialog.findViewById(R.id.BlockEmails);

        List<String> BlockEmailsStrings = new LinkedList<>();
        for (BlockMail blockEmails : myApplication.getBlockMails())
            BlockEmailsStrings.add(blockEmails.getEmail());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_list, R.id.text_view_spinner, BlockEmailsStrings);
        BlockEmails.setAdapter(adapter);


        BlockEmails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        dialog.findViewById(R.id.Block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BlockMail blockMail = new BlockMail();
                blockMail.setAccount(AccountRegistered.get(Account.getSelectedIndex()));
                blockMail.setEmail(Email.getText().toString());
                myApplication.setBlockMails(blockMail);
                Toast.makeText(getApplicationContext(), "Blocked", Toast.LENGTH_SHORT).show();
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


    public void CreateFolder(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.create_folder_dialog_layout);
        final EditText FolderName = dialog.findViewById(R.id.FolderName);
        dialog.findViewById(R.id.CreateFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new CreateFolderTask().execute(FolderName.getText().toString());
                List<String> menusList = myApplication.getOnlineManus();
                menusList.add(FolderName.getText().toString());
                menusList = new ArrayList<>(new LinkedHashSet<>(menusList));
                menusList.remove(menusList.size() - 1);
                menusList.add(FolderName.getText().toString());
                menusList.add("Logout");
                myApplication.AddMenus(menusList);
                menu.clear();
                for (String Menus : menusList)
                    menu.add(Menus);
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.SaveEmails();

    }

    @Override
    protected void onResume() {
        super.onResume();
        myApplication.SaveEmails();

    }

    public void RefreshFragment() {
        onBackPressed();
        getSupportFragmentManager().beginTransaction().add(R.id.container1, CurrentFragment).addToBackStack(null).commit();


    }

    EditText txtmail;
    EditText txtPassWord;


    public void AddAccount(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_new_account);


        txtmail = (EditText) dialog.findViewById(R.id.txtEmail);
        txtPassWord = (EditText) dialog.findViewById(R.id.txtPass);


        dialog.findViewById(R.id.Hotmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewLogin = "Hotmail";
                new LoginTask().execute();
                progressDialog.show();


            }
        });
        dialog.findViewById(R.id.Yahoo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewLogin = "Yahoo";
                new LoginTask().execute();
                progressDialog.show();


            }
        });

        dialog.findViewById(R.id.Gmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewLogin = "Gmail";
                new LoginTask().execute();
                progressDialog.show();


            }
        });

        dialog.show();

    }

    private boolean createFolder(Folder parent, String folderName) {
        boolean isCreated = true;

        try {
            Folder newFolder = parent.getFolder(folderName);
            isCreated = newFolder.create(Folder.HOLDS_MESSAGES);
            System.out.println("created: " + isCreated);

        } catch (Exception e) {
            System.out.println("Error creating folder: " + e.getMessage());
            e.printStackTrace();
            isCreated = false;
        }
        return isCreated;
    }


    private class CreateFolderTask extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            //Copy you logic to calculate progress and call

            ShowProgress();
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
                HideProgress(e.getMessage());
                e.printStackTrace();
            } catch (MessagingException e) {
                HideProgress(e.getMessage());

                e.printStackTrace();
            }

            if (store.isConnected()) {
                try {
                    Folder defaultFolder = store.getDefaultFolder();
                    if (createFolder(defaultFolder, params[0])) {

                    } else {
                    }

                } catch (MessagingException e) {
                    HideProgress(e.getMessage());
                    e.printStackTrace();
                }

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            HideProgress("Folder Created");

        }
    }


    private class GetALLFolders extends AsyncTask<String, Void, Void> {


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
                List<String> menusList = new LinkedList<>();
                Folder[] folders = new Folder[0];
                try {
                    folders = store.getDefaultFolder().list("*");

                } catch (MessagingException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                menusList.add("INBOX");
                menusList.add("Create Folder");
                menusList.add("Block Email");
                for (final javax.mail.Folder folder : folders) {

                    try {
                        Folder[] sub = folder.list();
                        if (!menusList.contains(folder.getName()) && !folder.getName().toLowerCase().contains("gmail"))
                            menusList.add(folder.getName());
                        Log.d("Folder", folder.getName());


                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }

                menusList.add("Logout");
                menusList = new ArrayList<>(new LinkedHashSet<>(menusList));
                myApplication.AddMenus(menusList);
                for (final String Menus : menusList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            menu.add(Menus);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CurrentFragment = new menu_inbox();
                        getSupportFragmentManager().beginTransaction().add(R.id.container1, CurrentFragment).addToBackStack(null).commit();

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
                if (NewLogin.toLowerCase().contains("gmail"))
                    store.connect(ServerHandler.GMAIL_HOST, txtmail.getText().toString(), txtPassWord.getText().toString());
                if (NewLogin.toLowerCase().contains("yahoo"))
                    store.connect("imap.mail.yahoo.com", txtmail.getText().toString(), txtPassWord.getText().toString());
                if (NewLogin.toLowerCase().contains("hotmail"))
                    store.connect("pop3.live.com", txtmail.getText().toString(), txtPassWord.getText().toString());

            } catch (final NoSuchProviderException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                progressDialog.dismiss();
                e.printStackTrace();
            } catch (final MessagingException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                        myApplication.setEmail(NewLogin, email);
                        myApplication.setCurentLogin(NewLogin);
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


    public void ShowProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();

            }
        });
    }

    public void HideProgress(final String Message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
