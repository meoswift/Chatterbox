package com.example.chatterbox;

import android.app.Application;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

//  Purpose: Set up connection to Parse backend server
public class ChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register message class as part of Application
        ParseObject.registerSubclass(Message.class);

        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId and server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("simplechat-client") // should correspond to APP_ID env variable
                .clientBuilder(builder)
                .server("https://codepath-chat-lab.herokuapp.com/parse/").build());
    }

}
