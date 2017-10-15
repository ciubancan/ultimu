package com.example.nesty.theapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import static android.R.attr.data;
import static android.R.attr.value;
import static com.example.nesty.theapp.R.id.captured_photo;

public class textView extends AppCompatActivity {

    private ImageView imageHolder;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        ImageView image = (ImageView) findViewById(captured_photo);
        image.setRotation(90.0f);
        /*final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        imageCursor.moveToFirst();
        do {
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            if (fullPath.contains("DCIM")) {
                //--last image from camera --
                Bitmap bMap = BitmapFactory.decodeFile("");
                image.setImageBitmap(bMap);
                return;
            }
        }
        while (imageCursor.moveToNext());*/

        //private ImageView mImageView;
        //mImageView = (ImageView) findViewById(R.id.imageViewId);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/NewVision", options);
        image.setImageBitmap(bitmap);









    }



}

