package com.technicalrj.halanxscouts.utlis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.technicalrj.halanxscouts.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileSaver {

    private Activity activity;
    private File galleryFolder;
    private File documentFolder;
    private String TAG = FileSaver.class.getSimpleName();



    public FileSaver(Activity activity, boolean isDocument, boolean isPrivateDirectory) {
        this.activity = activity;
        if(isDocument){
            createDocumentDirectory(isPrivateDirectory);
        } else {
            createImageDirectory(isPrivateDirectory);
        }
    }

    private void createImageDirectory(boolean isPrivate){
        File storageDirectory;
        if(isPrivate) {
            storageDirectory = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        galleryFolder = new File(storageDirectory, activity.getResources().getString(R.string.app_name));
        if (!galleryFolder.exists()) {
            boolean wasCreated = galleryFolder.mkdirs();
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory");
            }
        }
    }



    private void createDocumentDirectory(boolean isPrivate) {
        File storageDirectory;
        if(isPrivate) {
            storageDirectory = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        } else {
            storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        }
        documentFolder = new File(storageDirectory, activity.getResources().getString(R.string.app_name));
        if (!documentFolder.exists()) {
            boolean wasCreated = documentFolder.mkdirs();
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory");
            }
        }
    }

    public File createImageFile(File galleryFolder) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "resized_image_" + timeStamp + "_";
        return File.createTempFile(imageFileName, ".jpg", galleryFolder);
    }

    private File createDocumentFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "agreement" + timeStamp + "_";
        String imageFileName = "Halanx_agreement.pdf";
        return new File(documentFolder, imageFileName);
//        return File.createTempFile(imageFileName, ".pdf", documentFolder);
    }


    public Uri saveDocument(InputStream inputStream){
        Uri uri = null;
        FileOutputStream outputPhoto = null;
        try {
            File imageFile = createDocumentFile();
            outputPhoto = new FileOutputStream(imageFile);

            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = inputStream.read(data)) != -1) {
                total += count;
                outputPhoto.write(data, 0, count);
            }

            Log.d(TAG, "saveDocument: total: "+total);

            outputPhoto.flush();
            outputPhoto.close();
            outputPhoto.close();
            uri = Uri.fromFile(imageFile);

            Log.d(TAG, "saveDocument: ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//                    unlock();
            try {
                if (outputPhoto != null) {
                    outputPhoto.flush();
                    outputPhoto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uri;
    }

    public File saveBitmap(Bitmap bitmap){
        Uri uri = null;
        FileOutputStream outputPhoto = null;
        try {
            File imageFile = createImageFile(galleryFolder);
            outputPhoto = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputPhoto);
//            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), "");
            galleryAddPic(imageFile);
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//                    unlock();
            try {
                if (outputPhoto != null) {
                    outputPhoto.flush();
                    outputPhoto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public Uri saveBitmapTemp(Bitmap bitmap){
        Uri uri = null;
        FileOutputStream outputPhoto = null;
        try {
            File imageFile = createImageFile(galleryFolder);
            outputPhoto = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputPhoto);
//            MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), "");
            galleryAddPic(imageFile);
            uri = Uri.fromFile(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//                    unlock();
            try {
                if (outputPhoto != null) {
                    outputPhoto.flush();
                    outputPhoto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uri;
    }

    private void galleryAddPic(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public File getGalleryFolder() {
        return galleryFolder;
    }

    public void setGalleryFolder(File galleryFolder) {
        this.galleryFolder = galleryFolder;
    }

    public File getDocumentFolder() {
        return documentFolder;
    }

    public void setDocumentFolder(File documentFolder) {
        this.documentFolder = documentFolder;
    }
}