package pclinks.tech_creation.com.pclinks;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import utils.ClickListener;
import utils.CustomMessage;
import utils.CustomMessageAdapter;
import utils.Firebasehandler;
import utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    final int messageLimit = 10;

    ArrayList<CustomMessage> customMessageArrayList = new ArrayList<>();

    RecyclerView recyclerView;

    CustomMessageAdapter customMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.mainActivity_message_recyclerView);

        initializeActivity();
    }

    private void initializeActivity() {
        //initializeEditText();
        downloadCustomMessage();

    }

    private void initializeEditText() {
        EditText editText = (EditText) findViewById(R.id.mainActivity_messageText_EditText);
        editText.setText(pasteFromClipboard());
    }

    private void downloadCustomMessage() {

        new Firebasehandler().downloadCustomMessageList(getUserUID(), messageLimit, new Firebasehandler.OnCustomMessageListener() {
            @Override
            public void onCustomMessageUpload(boolean isSuccessful) {

            }

            @Override
            public void onCustomMessageListDownLoad(ArrayList<CustomMessage> messageArrayList, boolean isSuccessful) {

                if (isSuccessful) {
                    // Collections.reverse(messageArrayList);

                    customMessageArrayList = messageArrayList;
                    initializeRecyclerView();

                    checkforHelpDialogue();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCustomMessageDownLoad(CustomMessage customMessage, boolean isSuccessful) {

            }
        });
    }

    private void checkforHelpDialogue() {

        if (customMessageArrayList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    openHelpActivity();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });


            String helpdialogText = getString(R.string.help_dialog_text);
            builder.setTitle("Help");
            builder.setMessage(helpdialogText);

// Create the AlertDialog
            AlertDialog dialog = builder.create();

            dialog.show();
        }

    }


    private void checkforRateUsDialog() {
        if (customMessageArrayList.size() == (messageLimit - 1)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
            builder.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button

                    rateUs();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });


            String helpdialogText = getString(R.string.rate_dialog_text);
            builder.setTitle("Rate us");
            builder.setMessage(helpdialogText);

// Create the AlertDialog
            AlertDialog dialog = builder.create();

            dialog.show();
        }
    }


    public void uploadCustomMessage(final CustomMessage customMessage) {

        new Firebasehandler().uploadCustomMessage(customMessage, new Firebasehandler.OnCustomMessageListener() {
            @Override
            public void onCustomMessageUpload(boolean isSuccessful) {
                if (isSuccessful) {
                    customMessageArrayList.add(customMessage);
                    scrollRecyclerViewToLast();
                    checkforRateUsDialog();
                }
            }

            @Override
            public void onCustomMessageListDownLoad(ArrayList<CustomMessage> messageArrayList, boolean isSuccessful) {

            }

            @Override
            public void onCustomMessageDownLoad(CustomMessage customMessage, boolean isSuccessful) {

            }
        });

    }


    private void initializeRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.mainActivity_message_recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        customMessageAdapter = new CustomMessageAdapter(customMessageArrayList, this);

        recyclerView.setAdapter(customMessageAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                copyToClipBoard(customMessageArrayList.get(position).getCustomMessageText());
                Toast.makeText(MainActivity.this, "Message copied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        scrollRecyclerViewToLast();


    }

    public CustomMessage createCustomMessage() {
        CustomMessage customMessage = new CustomMessage();

        EditText editText = (EditText) findViewById(R.id.mainActivity_messageText_EditText);
        String messageString = editText.getText().toString().trim();
        if (!validateForm(messageString)) {
            return null;
        } else {
            customMessage.setCustomMessageText(messageString);
        }

        if (customMessage.getCustomMessageText() == null) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
            return null;
        }

        customMessage.setCustomMessageUserUID(getUserUID());
        customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
        customMessage.setCustomMessageDevice(android.os.Build.MODEL);


        return customMessage;
    }

    private boolean validateForm(String messageString) {
        if (messageString != null) {
            if (messageString.isEmpty()) {

                //  CoordinatorLayout coordinatorLayout =(CoordinatorLayout) findViewById(R.id.coordinateLayout);

                Toast.makeText(this, "Message text empty", Toast.LENGTH_SHORT).show();
                /*Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Paste text from clipboard", Snackbar.LENGTH_LONG)
                        .setAction("paste", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initializeEditText();
                            }
                        });

                snackbar.show();*/
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    public void copyToClipBoard(String message) {

        ClipboardManager myclipboard;
        myclipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


        ClipData mydata = ClipData.newPlainText("text", message);

        myclipboard.setPrimaryClip(mydata);


        Toast.makeText(MainActivity.this, "string entered in clipboard", Toast.LENGTH_SHORT).show();


    }

    public String pasteFromClipboard() {
        ClipboardManager myclipboard;
        myclipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData mydata = myclipboard.getPrimaryClip();
        ClipData.Item mydata_item = mydata.getItemAt(0);


        String s = mydata_item.getText().toString() + "";

        return s;

    }


    public void sendButtonClick(View view) {

        CustomMessage customMessage = createCustomMessage();
        if (customMessage != null) {
            uploadCustomMessage(customMessage);
        } else {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            // Handle the help action
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send_me_link) {
            sendHelpText();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_rate_us) {
            rateUs();
        } else if (id == R.id.nav_share) {
            shareApp();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateUs() {


        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void shareApp() {
        int applicationNameId = this.getApplicationInfo().labelRes;
        final String appPackageName = this.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(applicationNameId));
        String text = "Install this cool application: ";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.putExtra(Intent.EXTRA_TEXT, text + "\n " + link);
        startActivity(Intent.createChooser(i, "Share link:"));
    }

    private void sendHelpText() {

        String helpText = getString(R.string.pclink_help_text);

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pc links App Help");

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, helpText);


        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "No email clients installed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openHelpActivity() {
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    public String getUserUID() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        return currentUser.getUid();
    }


    public void scrollRecyclerViewToLast() {

        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        customMessageAdapter.notifyDataSetChanged();

    }

}
