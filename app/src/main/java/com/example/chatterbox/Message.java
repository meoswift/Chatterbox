package com.example.chatterbox;

import com.parse.ParseClassName;
import com.parse.ParseObject;

// Message model provides data to ChatAdapter to bind to RecyclerView
@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";

    // Get the user ID of current Message Parse object
    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    // Get the body message of current Message Parse object
    public String getBody() {
        return getString(BODY_KEY);
    }

    // Set the user ID of current Message Parse object
    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    // Set the body message of current Message Parse object
    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}

