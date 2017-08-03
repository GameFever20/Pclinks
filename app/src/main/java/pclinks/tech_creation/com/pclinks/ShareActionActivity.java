package pclinks.tech_creation.com.pclinks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import utils.CustomMessage;
import utils.Firebasehandler;

public class ShareActionActivity extends Activity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(ShareActionActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        String messageString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        sendMessage(currentUser, messageString);

        //finish();
    }

    private void sendMessage(FirebaseUser currentUser, String messageString) {
        CustomMessage customMessage = new CustomMessage();


        customMessage.setCustomMessageText(messageString);
        customMessage.setCustomMessageUserUID(currentUser.getUid());
        customMessage.setMessageTime(Calendar.getInstance().getTimeInMillis());
        customMessage.setCustomMessageDevice(android.os.Build.MODEL);
        customMessage.setMessageType(0);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending message ");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Firebasehandler().uploadCustomMessage(customMessage, new Firebasehandler.OnCustomMessageListener() {
            @Override
            public void onCustomMessageUpload(boolean isSuccessful) {
                if (isSuccessful) {
                    Toast.makeText(ShareActionActivity.this, "Message sent ", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                    finish();

                } else {
                    Toast.makeText(ShareActionActivity.this, "Failed to sent message", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
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


}
