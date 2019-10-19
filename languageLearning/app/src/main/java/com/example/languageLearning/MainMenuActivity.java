package com.example.languageLearning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {
    private final String TAG = "TEST";
    private MyApplication app;
    TextView welcomeMessage;
    ImageButton profileButton, logoutButton;
    Dialog popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        app = (MyApplication)getApplication();

        welcomeMessage = findViewById(R.id.welcomeMessage);
        profileButton = findViewById(R.id.profileButton);
        logoutButton = findViewById(R.id.logoutButton);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, ProfilePage.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutPopup();
            }
        });

        Bundle b = getIntent().getExtras();
        String username = b.getString("USERNAME");
        welcomeMessage.setText("Hello " + username + "!");

    }

    public void showLogoutPopup(){
        popup = new Dialog(this);
        Button logoutYesButton, logoutNoButton;
        popup.setContentView(R.layout.logout_popup);
        logoutYesButton = popup.findViewById(R.id.logoutYesButton);
        logoutYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.setToken("");
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        logoutNoButton = popup.findViewById(R.id.logoutNoButton);
        logoutNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.dismiss();
            }
        });
        popup.show();
    }
}