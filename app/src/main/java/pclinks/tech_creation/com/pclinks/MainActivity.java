package pclinks.tech_creation.com.pclinks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

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


    final int messageLimit = 14;

    ArrayList<CustomMessage> customMessageArrayList = new ArrayList<>();

    RecyclerView recyclerView;

    CustomMessageAdapter customMessageAdapter;

    private AdView mAdView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeads();

        progressDialog = new ProgressDialog(this);

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

        FirebaseMessaging.getInstance().subscribeToTopic("user_" + getUserUID());
        FirebaseMessaging.getInstance().subscribeToTopic("offer");
    }

    private void initializeads() {

        AdView mAdView = (AdView) findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
                //Toast.makeText(MainActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
                Crashlytics.log("Ad loading failed");
                //Toast.makeText(MainActivity.this, "failed Loaded - " + errorCode, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
                //Toast.makeText(MainActivity.this, " close Loaded", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initializeActivity() {
        //initializeEditText();
        showProgressDialog("Fetching ...");
        downloadCustomMessage();

        setNavigationGmailID();

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
                    hideProgressDialog();
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

    /*    if (customMessageArrayList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

            AlertDialog dialog = builder.create();

            dialog.show();

        }*/


        if (customMessageArrayList.size() == 0) {

            //create and show different custom messge to user for easy navigation

            FirebaseUser firebaseUser = getCurrentUser();

            CustomMessage customMessage = new CustomMessage();
            customMessage.setCustomMessageText("Hello " + firebaseUser.getDisplayName() +
                    ",\n\nWe have sent you chrome extension link to your Email address - " +
                    firebaseUser.getEmail() +
                    "\nDownload extension to start using Pc links " +
                    "\nExchange text message or link between pc and mobile using Pc links  ");
            customMessage.setCustomMessageUserUID("Admin");
            customMessage.setCustomMessageDevice("");
            customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
            customMessage.setMessageType(100);

            customMessageArrayList.add(customMessage);

/*
            customMessage = new CustomMessage();
            customMessage.setCustomMessageText("We have sent you chrome extension link to your Email address - " + firebaseUser.getEmail() + "\nDownload extension to star using Pc links ");

            customMessage.setCustomMessageUserUID("Admin");
            customMessage.setCustomMessageDevice("");
            customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
            customMessage.setMessageType(100);

            customMessageArrayList.add(customMessage);


            customMessage = new CustomMessage();
            customMessage.setCustomMessageText("Exchange text message or link between pc and mobile using Pc links  ");
            customMessage.setCustomMessageUserUID("Admin");
            customMessage.setCustomMessageDevice("");
            customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
            customMessage.setMessageType(100);

            customMessageArrayList.add(customMessage);*/


            customMessage = new CustomMessage();
            customMessage.setCustomMessageText("For instruction on how to use Pc link \n Click here  ");
            customMessage.setCustomMessageUserUID("Admin");
            customMessage.setCustomMessageDevice("");
            customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
            customMessage.setMessageType(101);

            customMessageArrayList.add(customMessage);

            customMessageAdapter.notifyDataSetChanged();

        }

        try {
            for (CustomMessage customMessage : customMessageArrayList) {
                if (customMessage.getCustomMessageDevice().equalsIgnoreCase("Chrome")) {

                    Answers.getInstance().logContentView(new ContentViewEvent().putContentName("messaege from chrome extension").putContentId(getCurrentUser().getEmail()));
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void checkforRateUsDialog() {
        if (customMessageArrayList.size() == (messageLimit - 7)) {
           /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

            dialog.show();*/

            CustomMessage customMessage;
            customMessage = new CustomMessage();
            customMessage.setCustomMessageText("Like Pc links ?" +
                    "\nRate us and share your thoughts ");
            customMessage.setCustomMessageUserUID("Admin");
            customMessage.setCustomMessageDevice("click to rate");
            customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
            customMessage.setMessageType(102);

            customMessageArrayList.add(customMessageArrayList.size() - 1, customMessage);

            customMessageAdapter.notifyDataSetChanged();
        }else if (customMessageArrayList.size() ==(messageLimit - 1)){

            CustomMessage customMessage;
            customMessage = new CustomMessage();
            customMessage.setCustomMessageText("Like Pc links ?" +
                    "\nShare Pc link app with your friends ");
            customMessage.setCustomMessageUserUID("Admin");
            customMessage.setCustomMessageDevice("click to share");
            customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
            customMessage.setMessageType(103);

            customMessageArrayList.add(customMessageArrayList.size() - 1, customMessage);
        }



    }


    public void uploadCustomMessage(final CustomMessage customMessage) {

        showProgressDialog("Sending message..");

        new Firebasehandler().uploadCustomMessage(customMessage, new Firebasehandler.OnCustomMessageListener() {
            @Override
            public void onCustomMessageUpload(boolean isSuccessful) {
                if (isSuccessful) {
                    hideProgressDialog();
                    customMessageArrayList.add(customMessage);
                    scrollRecyclerViewToLast();
                    checkforRateUsDialog();
                    EditText editText = (EditText) findViewById(R.id.mainActivity_messageText_EditText);
                    editText.setText(null);

                    try {
                        InputMethodManager inputManager =
                                (InputMethodManager) MainActivity.this.
                                        getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                MainActivity.this.getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        editText.clearFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


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

    private void createNotification(String customMessageText) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("message", customMessageText);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);


        Notification notification = new NotificationCompat.Builder(this)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_menu_camera)
                .setContentIntent(pIntent)
                // Add media control buttons that invoke intents in your media service
                .addAction(R.drawable.ic_action_contact, "Previous", pIntent) // #0
                .addAction(R.mipmap.ic_launcher, "Pause", pIntent)  // #1
                // Apply the media style template

                .setContentTitle(customMessageText)
                .setContentText("My Awesome Band")
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

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

                CustomMessage customMessage = customMessageArrayList.get(position);


                if (customMessage.getMessageType() == 0) {
                    copyToClipBoard(customMessageArrayList.get(position).getCustomMessageText());
                    Toast.makeText(MainActivity.this, "Message copied", Toast.LENGTH_SHORT).show();
                } else if (customMessage.getMessageType() == 101) {

                    openHelpActivity();

                }else if (customMessage.getMessageType() == 102){
                    rateUs();
                }else if(customMessage.getMessageType() == 103){
                    shareApp();
                }


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

        Answers.getInstance().logRating(new RatingEvent().putContentName(getCurrentUser().getEmail()));
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

        String helpText = getString(R.string.pclink_help_downloadlink_text);

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pc links Chrome extension");

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

    public FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        return mAuth.getCurrentUser();
    }


    public void scrollRecyclerViewToLast() {
        try {
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            customMessageAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showProgressDialog(String message) {

        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void setNavigationGmailID() {


    }
}
