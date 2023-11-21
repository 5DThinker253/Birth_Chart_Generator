package com.ejs.birthchart.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Base64Utils {
    public static Bitmap decodeBase64ToBitmap(String base64String) {
        // Eliminar el encabezado "data:image/png;base64,"
        String encodedImage = base64String.substring(base64String.indexOf(",") + 1);
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static byte[] decodeBase64(String base64String) {
        String encodedImage = base64String.substring(base64String.indexOf(",") + 1);
        return Base64.decode(encodedImage, Base64.DEFAULT);
    }
}