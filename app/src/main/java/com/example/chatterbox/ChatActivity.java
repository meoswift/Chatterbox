package com.example.chatterbox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

// Activity that handles user login, send message, and maintain chat persistence.
public class ChatActivity extends AppCompatActivity {

    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText mMessage;
    Button mSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // User login - either as a current user or an new anonymous user
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d("ChatActivity", "Anonymous login failed.");
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse
    private void setupMessagePosting() {
        // Find the text field and button
        mMessage = findViewById(R.id.etMessage);
        mSendBtn = findViewById(R.id.btSend);
        // When send button is clicked, create message object on Parse
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the message that user writes
                String data = mMessage.getText().toString();
                // create a parse object by class name "Message"
                ParseObject message = ParseObject.create("Message");
                // stores the data and user id with the object
                message.put(BODY_KEY, data);
                message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
                // save properties of this object to Parse database
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            Toast.makeText(ChatActivity.this,
                                    "Successfully created message on Parse!",
                                    Toast.LENGTH_SHORT).show();
                        else
                            Log.e("ChatActivity", "Failed to save message", e);
                    }
                });
            }
        });
    }

}