package com.example.nesty.theapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatSwitcher;
import io.fotoapparat.error.CameraErrorCallback;
import io.fotoapparat.hardware.CameraException;
import io.fotoapparat.parameter.LensPosition;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.AspectRatioSelectors.standardRatio;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.lensPosition;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static io.fotoapparat.result.transformer.SizeTransformers.scaled;


public class Meniu extends AppCompatActivity {


        private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate(this);
        private boolean hasCameraPermission;
        private CameraView cameraView;

        private FotoapparatSwitcher fotoapparatSwitcher;
        private Fotoapparat frontFotoapparat;
        private Fotoapparat backFotoapparat;
        public static String mCurrentPhotoPath;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_meniu);

            cameraView = (CameraView) findViewById(R.id.camera_view);
            hasCameraPermission = permissionsDelegate.hasCameraPermission();

            if (hasCameraPermission) {
                cameraView.setVisibility(View.VISIBLE);
            } else {
                permissionsDelegate.requestCameraPermission();
            }

            setupFotoapparat();

            focusOnLongClick();
            takePictureOnClick();

        }

        private void setupFotoapparat() {
            frontFotoapparat = createFotoapparat(LensPosition.FRONT);
            backFotoapparat = createFotoapparat(LensPosition.BACK);
            fotoapparatSwitcher = FotoapparatSwitcher.withDefault(backFotoapparat);
        }







        private void focusOnLongClick() {
            cameraView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    fotoapparatSwitcher.getCurrentFotoapparat().autoFocus();

                    return true;
                }
            });
        }

        private void takePictureOnClick() {
            cameraView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePicture();


                }
            });
        }

        private boolean canSwitchCameras() {
            return frontFotoapparat.isAvailable() == backFotoapparat.isAvailable();
        }

        private Fotoapparat createFotoapparat(LensPosition position) {
            return Fotoapparat
                    .with(this)
                    .into(cameraView)
                    .previewScaleType(ScaleType.CENTER_CROP)
                    .photoSize(standardRatio(biggestSize()))
                    .lensPosition(lensPosition(position))
                    .focusMode(firstAvailable(
                            continuousFocus(),
                            autoFocus(),
                            fixed()
                    ))
                    .flash(firstAvailable(
                            autoRedEye(),
                            autoFlash(),
                            torch(),
                            off()
                    ))
                    .frameProcessor(new SampleFrameProcessor())
                    .logger(loggers(
                            logcat(),
                            fileLogger(this)
                    ))
                    .cameraErrorCallback(new CameraErrorCallback() {
                        @Override
                        public void onError(CameraException e) {
                            Toast.makeText(Meniu.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .build();
        }
        private String getPicture() { // genereaza un nume unic
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_hh:mm:ss"); // data si ora
        String timestamp = sdf.format(new Date());
        return timestamp+".JPG";
    }

        private void takePicture() {
            PhotoResult photoResult = fotoapparatSwitcher.getCurrentFotoapparat().takePicture();
            //photoResult.saveToFile(new File(getExternalFilesDir("cam_app"), getPicture()));
            //File file = new File(dir, NameOfFile +CurrentDateAndTime+ ".jpg");
            //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


            // Get the directory for the user's public pictures directory.
            File file=new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "NewVision");
            file.mkdir();
            String getDirectoryPath = file.getAbsolutePath();
            File file_fin=new File(getDirectoryPath,"a.JPG");//unde e salvata poza
            photoResult.saveToFile(file_fin);
            mCurrentPhotoPath=file_fin.getAbsolutePath();





                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);








            final Handler handler = new Handler();// functie pentru delay de 2,5 secunde
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Meniu.this,textView.class);
                    startActivity(i);//si aici
                }
            }, 2500);//aici setezi timpul cat sa fie delayul



        }



        @Override
        protected void onStart() {
            super.onStart();
            if (hasCameraPermission) {
                fotoapparatSwitcher.start();
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (hasCameraPermission) {
                fotoapparatSwitcher.stop();
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
                fotoapparatSwitcher.start();
                cameraView.setVisibility(View.VISIBLE);
            }
        }

        private class SampleFrameProcessor implements FrameProcessor {

            @Override
            public void processFrame(Frame frame) {
                // Perform frame processing, if needed
            }

        }




}