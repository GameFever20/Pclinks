package pclinks.tech_creation.com.pclinks;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        copyToClipBoard(getIntent().getStringExtra("message"));

        finish();

    }

    public void copyToClipBoard(String message) {

        ClipboardManager myclipboard;
        myclipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


        ClipData mydata = ClipData.newPlainText("text", message);

        myclipboard.setPrimaryClip(mydata);


        Toast.makeText(NotificationActivity.this, "Message copied", Toast.LENGTH_SHORT).show();


    }

}
