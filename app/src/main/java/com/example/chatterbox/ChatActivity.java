package com.example.chatterbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

// Activity that handles user login, send message, and maintain chat persistence.
public class ChatActivity extends AppCompatActivity {

    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText mMessage;
    Button mSendBtn;

    RecyclerView mChatRV;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    boolean mFirstLoad;

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
    // Sets up
    private void setupMessagePosting() {
        // Find the text field, button, and recycler view
        mMessage = findViewById(R.id.etMessage);
        mSendBtn = findViewById(R.id.btSend);
        mChatRV = findViewById(R.id.chatRv);

        mMessages = new ArrayList<>();
        mFirstLoad = true;

        final String userId = ParseUser.getCurrentUser().getObjectId();
        // initialize an adapter and sets adapter to Chat recycler view
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        mChatRV.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mChatRV.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the message that user writes
                String data = mMessage.getText().toString();
                // Create a Parse-backed Message model
                Message message = new Message();
                // Set body and user ID to message Parse object
                message.setBody(data);
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                // save properties of this object to Parse database
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {// no exception thrown, message is created on Parse
                            Log.d("ChatActivity", "Successfully created message on Parse!");
                            updateMessages();
                        } else
                            Log.e("ChatActivity", "Failed to save message", e);
                    }
                });
            }
        });
        mMessage.setText(null); // clears the EditText message box
    }

    private void updateMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(50);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        mChatRV.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

}