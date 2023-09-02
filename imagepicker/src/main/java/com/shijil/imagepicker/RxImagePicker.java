package com.shijil.imagepicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class RxImagePicker {

    private static RxImagePicker instance;

    public static synchronized RxImagePicker with(Context context) {
        if (instance == null) {
            instance = new RxImagePicker(context);
        }else {
            instance.context = context;
        }
        return instance;
    }

    private Context context;
    private MutableLiveData<Uri> publishSubject;
    private MutableLiveData<List<Uri>> publishSubjectMultipleImages;
    private MutableLiveData<Boolean> isPermissionDenied;

    private RxImagePicker(Context context) {
        this.context = context;
    }


    public LiveData<Uri> requestImageLiveData() {
        //if (publishSubject == null){
        publishSubject =  new MutableLiveData<>();
        //}

       // publishSubject.removeObservers(owner);
        return publishSubject;
    }

   /* public LiveData<Uri> requestImage(Sources imageSource) {
        publishSubject =  new MutableLiveData<>();
        startImagePickHiddenActivity(imageSource.ordinal(), false);
        return publishSubject;
    }*/

   public void requestImage() {

        String[] item = {"Take Photo", "Photo Gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Upload Image").setItems(item, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        startImagePickHiddenActivity(Sources.CAMERA.ordinal(), false);
                        break;
                    case 1:
                        startImagePickHiddenActivity(Sources.GALLERY.ordinal(), false);
                        break;
                    default:
                        break;
                }

            }

        });
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public LiveData<List<Uri>> requestMultipleImages() {
        publishSubjectMultipleImages = new MutableLiveData<>();
        startImagePickHiddenActivity(Sources.GALLERY.ordinal(), true);
        return publishSubjectMultipleImages;
    }

    void onImagePicked(Uri uri) {
        if (publishSubject != null) {
            //publishSubject.onNext(uri);
            //publishSubject.onComplete();
            publishSubject.postValue(uri);
        }
    }

    void onImagesPicked(List<Uri> uris) {
        if (publishSubjectMultipleImages != null) {
            //publishSubjectMultipleImages.onNext(uris);
            //publishSubjectMultipleImages.onComplete();
            publishSubjectMultipleImages.postValue(uris);
        }
    }

    private void startImagePickHiddenActivity(int imageSource, boolean allowMultipleImages) {
        Intent intent = new Intent(context, HiddenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(HiddenActivity.ALLOW_MULTIPLE_IMAGES, allowMultipleImages);
        intent.putExtra(HiddenActivity.IMAGE_SOURCE, imageSource);
        context.startActivity(intent);
    }
    public void isPermissionDenied(boolean isPermission){
        isPermissionDenied.postValue(isPermission);
    }

    public LiveData<Boolean> getPermissionStatus(){
        isPermissionDenied = new MutableLiveData<>();
        return isPermissionDenied;
    }
}

