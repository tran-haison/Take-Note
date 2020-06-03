package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import firstproject.tranhaison.takenote.Image;
import firstproject.tranhaison.takenote.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class ImageActivity extends AppCompatActivity {

    Toolbar toolbarImageCustom;
    ImageView imageViewImageCustom;

    Bitmap selectedImage = null;

    final int RESULT_DELETE = 5;
    final int RESULT_TEXT_RECOGNITION = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageViewImageCustom = (ImageView) findViewById(R.id.imageViewImageCustom);
        toolbarImageCustom = (Toolbar) findViewById(R.id.toolBarImageCustomView);
        setSupportActionBar(toolbarImageCustom);

        getImageCustom();
        returnPreviousActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_image_custom_grab_text:
                runTextRecognition(selectedImage);
                break;
            case R.id.menu_image_custom_delete:
                intentFinish(RESULT_DELETE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intentFinish(RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void returnPreviousActivity() {
        toolbarImageCustom.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentFinish(RESULT_OK);
            }
        });
    }

    /**
     * Get image from AddNoteActivity then display in ImageView
     */
    private void getImageCustom() {
        Intent intent = getIntent();
        String imageCustom = intent.getStringExtra("imageCustom");

        Image image = new Image();
        selectedImage = image.getBitmap(imageCustom);

        if (selectedImage != null) {
            rescaleBitmap(selectedImage, imageViewImageCustom);
        }
    }

    /**
     * Get the ML text recognition model to detect text from an image
     * @param selectedImage
     */
    private void runTextRecognition (Bitmap selectedImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(selectedImage);
        final FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        textRecognitionResult(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * If there is no text in image -> Toast to inform user
     * otherwise store in String variable to put into intent and return back to AddNoteActivity
     * @param result
     */
    private void textRecognitionResult(FirebaseVisionText result) {
        String resultText = result.getText();
        if (resultText.isEmpty())
            Toast.makeText(this, "No text detected!", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent();
            intent.putExtra("textDetect", resultText);
            setResult(RESULT_TEXT_RECOGNITION, intent);
            finish();
            overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
        }
    }

    /**
     * Finish an activity, return to previous (AddNoteActivity)
     * @param result_code
     */
    private void intentFinish(int result_code) {
        Intent intent = new Intent();
        setResult(result_code, intent);
        finish();
        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
    }

    /**
     * Rescale the image to fit at least either width or height of the ImageView
     * @param bitmap
     * @param imageView
     */
    public void rescaleBitmap(final Bitmap bitmap, final ImageView imageView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentBitmapWidth = bitmap.getWidth();
                int currentBitmapHeight = bitmap.getHeight();
                int ivWidth = imageView.getWidth();
                int ivHeight = imageView.getHeight();

                if ((ivWidth/ivHeight) < (currentBitmapWidth/currentBitmapHeight)) {
                    int newWidth = ivWidth;
                    int newHeight = (int) Math.floor((double)currentBitmapHeight * ((double)newWidth / (double)currentBitmapWidth));
                    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                    imageView.setImageBitmap(newBitmap);
                } else {
                    int newHeight = ivHeight;
                    int newWidth = (int) Math.floor((double)currentBitmapWidth * ((double)newHeight / (double)currentBitmapHeight));
                    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                    imageView.setImageBitmap(newBitmap);
                }
            }
        }, 50);
    }
}
