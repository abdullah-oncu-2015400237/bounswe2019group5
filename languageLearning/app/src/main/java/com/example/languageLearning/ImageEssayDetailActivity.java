package com.example.languageLearning;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;

public class ImageEssayDetailActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    Essay essay;
    ConstraintLayout reviewerInfoLayout;
    TextView reviewerInfoTextView;
    ImageButton reviewerProfileButton;
    PhotoView essayPhotoView;
    Button annotateButton, rejectButton, completedButton;
    MyApplication app;
    ProgressBar progressBar;
    Bitmap essayImage;
    CropImageView cropImageView;
    ArrayList<AnnotationForImageEssay> annotations = new ArrayList<>();
    boolean currentlyCroppingForAnnotation=false;

    Bitmap getOverlayBitmap() {
        Bitmap bmp=Bitmap.createBitmap(essayImage.getWidth(), essayImage.getHeight(),essayImage.getConfig());
        Canvas cnvs=new Canvas(bmp);
        Paint paint=new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(64);

        for (AnnotationForImageEssay ann : annotations) {
            int x = (int)(ann.x*essayImage.getWidth()/100);
            int y = (int)(ann.y*essayImage.getHeight()/100);
            int right = (int)((ann.x+ann.w)*essayImage.getWidth()/100);
            int bottom = (int)((ann.y+ann.h)*essayImage.getHeight()/100);
            cnvs.drawRect(x, y,right,bottom , paint);
        }

        return bmp;
    }

    private void drawAnnotations() {
        Bitmap overlayBitmap = getOverlayBitmap();
        Bitmap bmOverlay = Bitmap.createBitmap(essayImage.getWidth(), essayImage.getHeight(), essayImage.getConfig());
        Canvas canvas = new Canvas();
        canvas.setBitmap(bmOverlay);
        canvas.drawBitmap(essayImage, new Matrix(), null);
        canvas.drawBitmap(overlayBitmap, new Matrix(), null);
        essayPhotoView.setImageBitmap(bmOverlay);
        essayPhotoView.setZoomable(true);
        //essayPhotoView.setScaleType(ImageView.ScaleType.CENTER);
    }

    private AnnotationForImageEssay getAnnotationFromXYPosition(int x, int y) {
        final int RADIUS = 10;
        AnnotationForImageEssay closestAnnotation = null;
        double closestAnnotationDist = 1e9;

        for (int curX = x-RADIUS; curX <= x+RADIUS; curX++) {
            if (curX < 0 || curX >= essayImage.getWidth())
                continue;
            for (int curY = y - RADIUS; curY <= y + RADIUS; curY++) {
                if (curY < 0 || curY >= essayImage.getHeight())
                    continue;
                for (AnnotationForImageEssay ann : annotations) {
                    int left = (int)(ann.x*essayImage.getWidth()/100);
                    int top = (int)(ann.y*essayImage.getHeight()/100);
                    int right = (int)((ann.x+ann.w)*essayImage.getWidth()/100);
                    int bottom = (int)((ann.y+ann.h)*essayImage.getHeight()/100);

                    if (left <= curX && right > curX && top <= curY && bottom > curY) {
                        double dist = (curX - left) * (curX - left) + (curY - top) * (curY - top); //TODO: Here and in TextEssayDetailsActivity, calculate distance to the middle point of annotations, not to their top-left corner.
                        if (dist < closestAnnotationDist) {
                            closestAnnotation = ann;
                            closestAnnotationDist = dist;
                        }
                    }
                }
            }
        }

        return closestAnnotation;
    }

    private void patchStatus(final String status, Response.Listener<JSONObject> callback) {
        essay.status = status;
        JSONObject data = new JSONObject();
        try {
            data.put("status", status);
        }
        catch (JSONException e) {
            e.printStackTrace();
            finish();
            return ;
        }
        app.initiateAPICall(Request.Method.PATCH, "essay/" + essay.id + "/", data, callback, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
            }
        });
    }

    private void reject() {
        patchStatus("rejected", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(ImageEssayDetailActivity.this, "Essay rejected", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void accept() {
        rejectButton.setVisibility(View.INVISIBLE);
        completedButton.setVisibility(View.VISIBLE);
        patchStatus("accepted", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        });
    }

    private void complete() {
        annotateButton.setVisibility(View.INVISIBLE);
        completedButton.setVisibility(View.INVISIBLE);
        patchStatus("completed", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(ImageEssayDetailActivity.this, "Essay completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean essayPhotoViewOnTouch(View v, float x, float y) {
        int x_pix = (int)(x*essayImage.getWidth());
        int y_pix = (int)(y*essayImage.getHeight());
        AnnotationForImageEssay ann = getAnnotationFromXYPosition(x_pix, y_pix);
        if (ann != null)
            AnnotationDialogHelper.showAnnotationDialog(this, ann.annotationText, ann.creator);
        return true;
    }

    private void reviewerProfileButtonClicked() {
        Intent intent = new Intent(this, ProfilePageActivity.class);
        intent.putExtra("username", essay.reviewer);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essay_detail_loading);
        app = (MyApplication) getApplication();
        essay = (Essay)getIntent().getSerializableExtra("essay");
        progressBar = findViewById(R.id.downloadProgressBar);

        app.rawHTTPGetRequest(essay.fileUri, new InputStreamFunction() {
            @Override
            public void invoke(InputStream s) {
                essayImage = BitmapFactory.decodeStream(s);
                if (essayImage == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ImageEssayDetailActivity.this, "Failed to download image", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    return ;
                }

                app.initiateAPICall(Request.Method.GET, "annotation/?source=" + essay.id, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++)
                                annotations.add(AnnotationForImageEssay.fromJSON(response.getJSONObject(i)));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            finish();
                            return ;
                        }

                        ImageEssayDetailActivity.this.setContentView(R.layout.activity_image_essay_detail_loaded);
                        reviewerInfoLayout = findViewById(R.id.detail_header_layout_include);
                        reviewerInfoTextView = reviewerInfoLayout.findViewById(R.id.reviewerInfoTextView);
                        reviewerProfileButton = reviewerInfoLayout.findViewById(R.id.reviewerProfileButton);
                        essayPhotoView = findViewById(R.id.essayPhotoView);
                        annotateButton = findViewById(R.id.annotateButton);
                        rejectButton = findViewById(R.id.rejectButton);
                        completedButton = findViewById(R.id.completedButton);
                        cropImageView = findViewById(R.id.cropImageView);
                        progressBar = null;
                        drawAnnotations();

                        if (app.getUsername().equals(essay.author)) { // We are the author
                            annotateButton.setVisibility(View.VISIBLE);
                            rejectButton.setVisibility(View.INVISIBLE);
                            completedButton.setVisibility(View.INVISIBLE);
                            reviewerInfoTextView.setText("Reviewer is @" + essay.reviewer);
                            reviewerProfileButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reviewerProfileButtonClicked();
                                }
                            });
                        }
                        else { // We are the reviewer
                            reviewerInfoLayout.setVisibility(View.GONE);
                            if (essay.status.equals("accepted")) {
                                rejectButton.setVisibility(View.INVISIBLE);
                                completedButton.setVisibility(View.VISIBLE);
                                annotateButton.setVisibility(View.VISIBLE);
                            }
                            else if (essay.status.equals("pending")){
                                rejectButton.setVisibility(View.VISIBLE);
                                completedButton.setVisibility(View.INVISIBLE);
                                annotateButton.setVisibility(View.VISIBLE);
                            }
                            else if (essay.status.equals("completed")) {
                                rejectButton.setVisibility(View.INVISIBLE);
                                completedButton.setVisibility(View.INVISIBLE);
                                annotateButton.setVisibility(View.INVISIBLE);
                            }
                            else if (essay.status.equals("rejected")) {
                                rejectButton.setVisibility(View.INVISIBLE);
                                completedButton.setVisibility(View.INVISIBLE);
                                annotateButton.setVisibility(View.INVISIBLE);
                            }
                            completedButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    complete();
                                }
                            });
                            rejectButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reject();
                                }
                            });
                        }

                        annotateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (currentlyCroppingForAnnotation == false) {
                                    essayPhotoView.setVisibility(View.INVISIBLE);

                                    cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
                                    cropImageView.setImageBitmap(essayImage);
                                    //cropImageView.setCropRect(cropRect);
                                    cropImageView.setVisibility(View.VISIBLE);
                                    currentlyCroppingForAnnotation = true;
                                }
                                else {
                                    final Rect rect = cropImageView.getCropRect();
                                    AlertDialog.Builder alert = new AlertDialog.Builder(ImageEssayDetailActivity.this);
                                    final EditText edittext = new EditText(ImageEssayDetailActivity.this);
                                    alert.setTitle("Enter Your Annotation");
                                    alert.setView(edittext);
                                    alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            essayPhotoView.setVisibility(View.VISIBLE);
                                            cropImageView.setVisibility(View.INVISIBLE);
                                            currentlyCroppingForAnnotation = false;

                                            final AnnotationForImageEssay ann = new AnnotationForImageEssay();

                                            ann.x = (float)rect.left / essayImage.getWidth() * 100;
                                            ann.y = (float)rect.top / essayImage.getHeight() * 100;
                                            ann.w = (float)(rect.right-rect.left) / essayImage.getWidth() * 100;
                                            ann.h = (float)(rect.bottom-rect.top) / essayImage.getHeight() * 100;
                                            ann.annotationText = edittext.getText().toString();
                                            ann.essayId = String.valueOf(essay.id);
                                            ann.id = "";
                                            ann.creator = app.getUsername();
                                            JSONObject data;
                                            try {
                                                data = ann.toJSON();
                                                data.remove("id");
                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                                return ;
                                            }
                                            if (essay.reviewer.equals(app.getUsername())) { // Reviewers implicitly accept essays by creating annotations
                                                if (essay.status.equals("accepted") == false) {
                                                    accept();
                                                }
                                            }
                                            app.initiateAPICall(Request.Method.POST, "annotation/", data, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    annotations.add(ann);
                                                    drawAnnotations();
                                                }
                                            }, null);
                                        }
                                    });
                                    alert.show();
                                }
                            }
                        });
                        essayPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(ImageView v, float x, float y) {
                                essayPhotoViewOnTouch(v, x, y);
                            }
                        });
                    }
                }, null);
            }
        }, null);
    }
}