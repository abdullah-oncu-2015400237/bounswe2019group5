package com.example.languageLearning;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class TextEssayDetailTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String dummy_response = "{\"id\":4,\"type\":\"writing\",\"language\":\"english\",\"writing\":\"http:\\/\\/35.158.176.194\\/media\\/essays\\/essay5810149663315215007.txt\",\"reviewer\":\"kbozdogan\",\"author\":\"notme\",\"status\":\"created\"}";
        Essay essay;
        Intent intent = new Intent(this, TextEssayDetailActivity.class);
        try {
            JSONObject jsonEssay = new JSONObject(dummy_response);
            essay = Essay.fromJSON(jsonEssay);
        }
        catch (Exception e) {
            e.printStackTrace();
            finish();
            return ;
        }
        MyApplication app = (MyApplication) getApplication();
        app.setUsername("kbozdogan");
        intent.putExtra("essay", essay);
        startActivity(intent);
        finish();
    }
}
