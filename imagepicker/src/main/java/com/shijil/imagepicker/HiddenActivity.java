package com.shijil.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HiddenActivity extends Activity {

    private static final String KEY_CAMERA_PICTURE_URL = "cameraPictureUrl";

    public static final String IMAGE_SOURCE = "image_source";
    public static final String ALLOW_MULTIPLE_IMAGES = "allow_multiple_images";

    private static final int SELECT_PHOTO = 100;
    private static final int TAKE_PHOTO = 101;

    private Uri cameraPictureUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_CAMERA_PICTURE_URL, cameraPictureUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cameraPictureUrl = savedInstanceState.getParcelable(KEY_CAMERA_PICTURE_URL);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleIntent(getIntent());
        } else {
            RxImagePicker.with(this).isPermissionDenied(true);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PHOTO:
                    handleGalleryResult(data);
                    break;
                case TAKE_PHOTO:
                    RxImagePicker.with(this).onImagePicked(cameraPictureUrl);
                    break;
            }
        }
        finish();
    }

    private void handleGalleryResult(Intent data) {
        if (getIntent().getBooleanExtra(ALLOW_MULTIPLE_IMAGES, false)) {
            ArrayList<Uri> imageUris = new ArrayList<>();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    imageUris.add(clipData.getItemAt(i).getUri());
                }
            } else {
                imageUris.add(data.getData());
            }
            RxImagePicker.with(this).onImagesPicked(imageUris);
        } else {
            RxImagePicker.with(this).onImagePicked(data.getData());
        }
    }

    private void handleIntent(Intent intent) {
        if (!checkPermission()) {
            return;
        }

        Sources sourceType = Sources.values()[intent.getIntExtra(IMAGE_SOURCE, 0)];
        int chooseCode = 0;
        Intent pictureChooseIntent = null;

        switch (sourceType) {
            case CAMERA:
                cameraPictureUrl = createImageUri();

                pictureChooseIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl);
                pictureChooseIntent.putExtra("return-data", true);
                chooseCode = TAKE_PHOTO;


                break;
            case GALLERY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    pictureChooseIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    pictureChooseIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,
                            getIntent().getBooleanExtra(ALLOW_MULTIPLE_IMAGES, false));
                    pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    pictureChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pictureChooseIntent.setType("image/*");
                chooseCode = SELECT_PHOTO;
                break;
        }

        startActivityForResult(pictureChooseIntent, chooseCode);
    }

    private boolean checkPermission() {
        String[] permissions = new String[]{};
        boolean isStoragePermission = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            isStoragePermission =  (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED);
        }
        if (isStoragePermission ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
            if (isStoragePermission){
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
                    permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
                }else {
                    permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                }
            }else {
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)){
                    permissions = new String[]{Manifest.permission.CAMERA};
                }
            }
            ActivityCompat.requestPermissions(this, permissions,
                    100);

            return false;
        } else {
            return true;
        }
    }

    private Uri createImageUri() {
        ContentResolver contentResolver = getContentResolver();
        ContentValues cv = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        cv.put(MediaStore.Images.Media.TITLE, timeStamp);
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
    }

}
