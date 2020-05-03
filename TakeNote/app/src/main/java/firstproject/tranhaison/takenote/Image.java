package firstproject.tranhaison.takenote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class Image {

    private byte[] image;

    public Image() {
        this.image = null;
    }

    /**
     * Get the bitmap and image view's height and width
     * Then compare the ratio between W/H of bitmap and image view
     * Finally set the new bitmap to the new height and width to let at least 1 property fit the property of image view
     * @param bitmap the bitmap of the image
     * @param imageView the view want to set new bitmap
     */
    public void rescaleBitmap(final Bitmap bitmap, final ImageView imageView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    int currentBitmapWidth = bitmap.getWidth();
                    int currentBitmapHeight = bitmap.getHeight();
                    int ivWidth = imageView.getWidth();
                    int ivHeight = imageView.getMaxHeight();

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
            }
        }, 50);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap=null;
        try {
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap ;
    }

    public String getRealPathFromURI(Uri uri, Context context) {
        String path = "";
        if (context.getContentResolver() != null) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

}
