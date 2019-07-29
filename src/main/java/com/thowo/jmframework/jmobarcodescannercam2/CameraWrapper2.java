package com.thowo.jmframework.jmobarcodescannercam2;


import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;

public class CameraWrapper2 {
    public final CameraDevice mCamera;
    public final String mCameraId;

    private CameraWrapper2(@NonNull CameraDevice camera, String cameraId) {
        if (camera == null) {
            throw new NullPointerException("Camera cannot be null");
        }
        this.mCamera = camera;
        this.mCameraId = cameraId;
    }

    public static CameraWrapper2 getWrapper(CameraDevice camera, String cameraId) {
        if (camera == null) {
            return null;
        } else {
            return new CameraWrapper2(camera, cameraId);
        }
    }
}
