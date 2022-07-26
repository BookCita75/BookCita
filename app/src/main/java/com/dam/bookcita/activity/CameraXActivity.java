package com.dam.bookcita.activity;

import static com.dam.bookcita.common.Constantes.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dam.bookcita.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraXActivity extends AppCompatActivity {

    private static final String TAG = "CameraXActivity";
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;

    private String scannedImgAbsolutePath;
    private String isbnScanne;

    private String resultTextOCR;

    private String type_ISBN_or_OCR;

    private String id_BD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);

        Intent intent = getIntent();
        type_ISBN_or_OCR = intent.getStringExtra(TYPE_ISBN_OR_OCR);
        id_BD = intent.getStringExtra(ID_BD);

        textureView = findViewById(R.id.view_finder);

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {

        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen


        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetResolution(screen)
                //.setLensFacing(CameraX.LensFacing.FRONT)
                .build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + ".png");
                //Environment.getExternalStorageDirectory()
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String msg = "Pic captured at " + file.getAbsolutePath();
                        scannedImgAbsolutePath = file.getAbsolutePath();
                        Log.i(TAG, "onImageSaved: file.getPath() : " + file.getPath());
                        Log.i(TAG, "onImageSaved: msg: " + msg);
                        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        if (type_ISBN_or_OCR.equals("ISBN")) {
                            extractISBN();
                        } else if (type_ISBN_or_OCR.equals("OCR")) {
                            Toast.makeText(CameraXActivity.this, "Scan OCR", Toast.LENGTH_LONG).show();
                            scanOCR();
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed : " + message;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        if (cause != null) {
                            cause.printStackTrace();
                            Log.i(TAG, "onError: cause Message" + cause.getMessage());
                        }
                    }
                });
            }
        });

        //bind to lifecycle:
        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imgCap);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void extractISBN() {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_EAN_13
                                )
                        .build();


        InputImage imageScannee=null;
        try {
            Uri uriImgScannee = Uri.fromFile(new File(scannedImgAbsolutePath));
            imageScannee = InputImage.fromFilePath(getApplicationContext(), uriImgScannee);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BarcodeScanner scannerBarcode = BarcodeScanning.getClient(options);

        Task<List<Barcode>> result = scannerBarcode.process(imageScannee)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        Log.i(TAG, "onSuccess: Task completed successfully " + barcodes.toString());
                        if (barcodes.isEmpty()) {
                            Toast.makeText(CameraXActivity.this, "ISBN non reconnu", Toast.LENGTH_LONG).show();
                        } else {
                            for (Barcode barcode: barcodes) {
                                Rect bounds = barcode.getBoundingBox();
                                Point[] corners = barcode.getCornerPoints();
                                // on suppose qu'on a scanné qu'un seul isbn;
                                isbnScanne = barcode.getRawValue();
                                Log.i(TAG, "onSuccess: isbnScanne : " + isbnScanne);
                                Toast.makeText(CameraXActivity.this, "ISBN scanné : " + isbnScanne, Toast.LENGTH_LONG).show();
                                int valueType = barcode.getValueType();
                                Log.i(TAG, "onSuccess: valueType : " + valueType);

                                Intent detailIntent = new Intent(getApplicationContext(), DetailsLivreISBN.class);

                                detailIntent.putExtra(ISBN, isbnScanne);
                                detailIntent.putExtra(ID_GOOGLE_BOOKS, "");

                                startActivity(detailIntent);

                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Log.i(TAG, "onFailure: e:" + e);
                    }
                });
    }

    private void scanOCR(){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        InputImage imageScannee=null;
        try {
            Uri uriImgScannee = Uri.fromFile(new File(scannedImgAbsolutePath));
            imageScannee = InputImage.fromFilePath(getApplicationContext(), uriImgScannee);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Task<Text> result =
                recognizer.process(imageScannee)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                Toast.makeText(CameraXActivity.this, "OCR réussie!", Toast.LENGTH_LONG).show();
                                resultTextOCR = text.getText();
                                Log.i(TAG, "onSuccess: resultTextOCR : " + resultTextOCR);
                                Intent selectionCitationIntent = new Intent(CameraXActivity.this, ScanOCR_SelectionnerCitationActivity.class);
                                selectionCitationIntent.putExtra(ID_BD, id_BD);
                                selectionCitationIntent.putExtra(RESULT_TEXT_OCR, resultTextOCR);
                                startActivity(selectionCitationIntent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CameraXActivity.this, "OCR échouée!", Toast.LENGTH_LONG).show();
                                Log.i(TAG, "onFailure: e.getMessage() : " + e.getMessage());
                            }
                        });

    }

}