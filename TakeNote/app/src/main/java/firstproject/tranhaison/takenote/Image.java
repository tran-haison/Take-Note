package firstproject.tranhaison.takenote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Image {

    private byte[] image;

    public Image() {
        this.image = null;
    }

    public Image(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * Convert from byte array to bitmap
     * @return
     */
    public Bitmap convertToBitmap() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image);
        Bitmap bitmapImage = BitmapFactory.decodeStream(byteArrayInputStream);
        return bitmapImage;
    }

    /**
     * Convert from bitmap to byte array
     * @param bitmap
     * @return
     */
    public byte[] convertFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        image = byteArrayOutputStream.toByteArray();
        return image;
    }

    /**
     * Get the bitmap and image view's height and width
     * Then compare the ratio between W/H of bitmap and image view
     * Finally set the new bitmap to the new height and width to let at least 1 property fit the property of image view
     * @param bitmap the bitmap of the image
     * @param imageView the view want to set new bitmap
     */
    public void rescaleBitmap(Bitmap bitmap, ImageView imageView) {
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

    /**
     * Get byte[] from Input Stream
     * @param inputStream
     * @return byte[] of image
     * @throws IOException
     */
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        image = byteBuffer.toByteArray();
        return image;
    }

}
