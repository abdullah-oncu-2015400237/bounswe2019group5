package com.example.languageLearning;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Exercise {
    int id; // As returned by the backend
    Question[] questions;

    public static Exercise fromJSON(JSONObject object) throws JSONException {
        Exercise exercise = new Exercise();
        exercise.id = object.getInt("id");
        JSONArray jquestions = object.getJSONArray("questions");
        exercise.questions = new Question[jquestions.length()];
        for (int i=0; i<jquestions.length(); i++)
            exercise.questions[i] = Question.fromJSON(jquestions.getJSONObject(i));
        return exercise;
    }
}
