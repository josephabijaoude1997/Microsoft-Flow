package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class PlayActivity extends Activity {
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;

    Button button_back_play;
    Button button_camera_play;
    Button button_upload_play;
    ImageView imageView_photo_play;



    private void UploadImage(String path) {
        try {
            //String path = "android.resource://com.example.aidrummer/drawable/partition";
            Uri uri = Uri.parse(path);
            final InputStream imageStream = getContentResolver().openInputStream(uri);
            final int imageLength = imageStream.available();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        final String imageName = ImageManager.UploadImage(imageStream, imageLength);

                        handler.post(new Runnable() {

                            public void run() {
                                Toast.makeText(PlayActivity.this, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(PlayActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            th.start();
        } catch (Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void GetMidi() {
        try {
            final File downloadedFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "drum.mid");
            final String path = downloadedFile.getAbsolutePath();
            final long midiLength = 0;

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        MidiManager.GetMidi("drum.mid", path);

                        handler.post(new Runnable() {

                            public void run() {
                                Toast.makeText(PlayActivity.this, "Midi Downloaded Successfully. Name = " + "drum.mid at " +path, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(PlayActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            th.start();
        } catch (Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        imageView_photo_play = findViewById(R.id.imageView_photo_play);
        imageView_photo_play.setImageResource(R.drawable.placeholder_photo);

        button_back_play = findViewById(R.id.button_back_play);
        button_back_play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }});

        button_camera_play = findViewById(R.id.button_camera_play);
        button_camera_play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        button_upload_play = findViewById(R.id.button_upload_play);
        button_upload_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = "file://" + Environment.getExternalStorageDirectory().toString() + "/AI_Drummer/partition_play.jpg";
                UploadImage(path);
//                makeConnection();
                GetMidi();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/AI_Drummer");
            myDir.mkdirs();
            String fname = "partition_play" + ".jpg";
            File file = new File(myDir, fname);
            Log.i(TAG, "" + file);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            imageView_photo_play.setImageBitmap(photo);
        }
    }
}
