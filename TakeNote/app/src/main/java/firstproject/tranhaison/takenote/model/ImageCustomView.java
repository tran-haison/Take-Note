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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageCustomView extends AppCompatActivity {

    Toolbar toolbarImageCustom;
    ImageView imageViewImageCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_custom_view);

        imageViewImageCustom = (ImageView) findViewById(R.id.imageViewImageCustom);
        toolbarImageCustom = (Toolbar) findViewById(R.id.toolBarImageCustomView);
        setSupportActionBar(toolbarImageCustom);

        getImageCustom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_custom_view_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_image_custom_grab_text:
                Toast.makeText(this, "Grab text", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_image_custom_delete:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getImageCustom() {
        Intent intent = getIntent();
        String imageCustom = intent.getStringExtra("imageCustom");

        Image image = new Image();
        Bitmap bitmap = image.getBitmap(imageCustom);

        if (bitmap != null) {
            rescaleBitmap(bitmap, imageViewImageCustom);
        }
    }

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
