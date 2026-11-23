package com.ornek.elfeneri;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;

public class MainActivity extends Activity {

    CameraManager cameraManager;
    String cameraId;
    boolean isOn = false;

    TextView status;
    Button button;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        cameraManager =
        (CameraManager)getSystemService(CAMERA_SERVICE);

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30,30,30,30);
        layout.setBackgroundColor(0xFF171717);

        TextView title = new TextView(this);

        title.setText("EL FENERİ");
        title.setTextSize(30);
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(Gravity.CENTER);

        layout.addView(title);

        status = new TextView(this);

        status.setText("Fener kapalı");
        status.setTextSize(20);
        status.setTextColor(0xFFFFFFFF);
        status.setGravity(Gravity.CENTER);

        layout.addView(status);

        button = new Button(this);

        button.setText("FENERİ AÇ");

        layout.addView(button,
        new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,100));

        setContentView(layout);

        button.setOnClickListener(v -> {
            setFlash(!isOn);
        });

        if(checkSelfPermission(Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
            new String[]{Manifest.permission.CAMERA},10);

        } else {
            findFlash();
        }
    }

    void findFlash() {
        try {
            for(String id:cameraManager.getCameraIdList()) {

                CameraCharacteristics c =
                cameraManager.getCameraCharacteristics(id);

                Boolean flash =
                c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

                Integer facing =
                c.get(CameraCharacteristics.LENS_FACING);

                if(Boolean.TRUE.equals(flash) &&
                (facing == null ||
                facing == CameraCharacteristics.LENS_FACING_BACK)) {

                    cameraId=id;
                    status.setText("Fener kapalı");
                    return;
                }
            }

            status.setText("Flaş bulunamadı");
            button.setEnabled(false);

        } catch(Exception e) {
            status.setText("Kamera hatası");
        }
    }

    void setFlash(boolean on) {

        if(cameraId == null) {
            findFlash();
            if(cameraId == null) return;
        }

        try {
            cameraManager.setTorchMode(cameraId,on);

            isOn=on;

            if(on) {
                status.setText("FENER AÇIK");
                button.setText("FENERİ KAPAT");
            } else {
                status.setText("Fener kapalı");
                button.setText("FENERİ AÇ");
            }

        } catch(Exception e) {
            status.setText("Fener açılamadı");
        }
    }

    @Override
    public void onRequestPermissionsResult(
    int requestCode,
    String[] permissions,
    int[] results) {

        super.onRequestPermissionsResult(
        requestCode,permissions,results);

        if(requestCode==10 &&
        results.length>0 &&
        results[0]==PackageManager.PERMISSION_GRANTED) {

            findFlash();

        } else {
            status.setText("Kamera izni gerekli");
            button.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {

        if(isOn && cameraId!=null) {
            try {
                cameraManager.setTorchMode(cameraId,false);
            } catch(Exception e) {}
        }

        super.onDestroy();
    }
}
