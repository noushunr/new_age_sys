package com.shijil.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class RxImageConverters {

   /* public static LiveData<File> uriToFile(final Context context, final Uri uri, final File file) {
        if (users == null) {
            users = new MutableLiveData<List<Users>>();
            // Asynchronous call for inserting data using service or asynctask.
            loadUsers();
        }
        // Dao live data will provide you all users information if data inserted.
        LiveData<List<User>> mUsers = mDb.userDao().getAllUsers();
        users.setValue(mUsers);
    }*/

    private static MutableLiveData<File> mutableLiveData = new MutableLiveData<>();


    public static MutableLiveData<File> uriToFileLiveData() {
        return mutableLiveData;
    }

    public static LiveData<File> uriToFile(final Context context, final Uri uri, final File file) {

        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
// do async operation to fetch users
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
// on background thread,
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    copyInputStreamToFile(inputStream, file);
                    mutableLiveData.postValue(file);
                } catch (Exception e) {
                    Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                    mutableLiveData.postValue(null);
                }
            }
        });
        return mutableLiveData;
    }
   /* public static LiveData<File> uriToFile(final FragmentActivity context, final Uri uri, final File file) {

        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        mutableLiveData.setValue(null);
        mutableLiveData.removeObservers(context);
        // do async operation to fetch users
        ExecutorService service =  Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                // on background thread,
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    copyInputStreamToFile(inputStream, file);
                    mutableLiveData.postValue(file);
                } catch (Exception e) {
                    Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                    mutableLiveData.postValue(null);
                }
            }
        });
        return mutableLiveData;
    }*/


    /*public static Observable<File> uriToFilea(final Context context, final Uri uri, final File file) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Exception {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    copyInputStreamToFile(inputStream, file);
                    emitter.onNext(file);
                    emitter.onComplete();
                } catch (Exception e) {
                    Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }*/

   /* public static Observable<Bitmap> uriToBitmap(final Context context, final Uri uri) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> emitter) throws Exception {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    emitter.onNext(bitmap);
                    emitter.onComplete();
                } catch (IOException e) {
                    Log.e(RxImageConverters.class.getSimpleName(), "Error converting uri", e);
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }*/

    private static void copyInputStreamToFile(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[10 * 1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

}
