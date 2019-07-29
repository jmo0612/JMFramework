package com.thowo.jmframework.jmobarcodescannercam2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.thowo.jmframework.R;
import com.thowo.jmframework.camera2basic.Camera2BasicFragment;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class BarcodeScannerView2 extends FrameLayout implements Camera2BasicFragment.CameraPreviewHandler{

    //private CameraWrapper mCameraWrapper;
    private FrameLayout mPreview;
    private Camera2BasicFragment camera2BasicFragment;
    private IViewFinder2 mViewFinderView;
    private Rect mFramingRectInPreview;
    //private CameraHandlerThread mCameraHandlerThread;
    private Boolean mFlashState;
    private boolean mAutofocusState = true;
    private boolean mShouldScaleToFill = true;

    private boolean mIsLaserEnabled = true;
    @ColorInt private int mLaserColor = ContextCompat.getColor(getContext(),R.color.viewfinder_laser);
    @ColorInt private int mBorderColor = ContextCompat.getColor(getContext(),R.color.viewfinder_border);
    private int mMaskColor = ContextCompat.getColor(getContext(),R.color.viewfinder_mask);
    private int mBorderWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private int mBorderLength = getResources().getInteger(R.integer.viewfinder_border_length);
    private boolean mRoundedCorner = false;
    private int mCornerRadius = 0;
    private boolean mSquaredFinder = false;
    private float mBorderAlpha = 1.0f;
    private int mViewFinderOffset = 0;
    private float mAspectTolerance = 0.1f;


    private Activity mActivity;
    private int mResId;

    public BarcodeScannerView2(Context context) {
        super(context);
        //init();
    }

    public BarcodeScannerView2(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BarcodeScannerView,
                0, 0);

        try {
            setShouldScaleToFill(a.getBoolean(R.styleable.BarcodeScannerView_shouldScaleToFill, true));
            mIsLaserEnabled = a.getBoolean(R.styleable.BarcodeScannerView_laserEnabled, mIsLaserEnabled);
            mLaserColor = a.getColor(R.styleable.BarcodeScannerView_laserColor, mLaserColor);
            mBorderColor = a.getColor(R.styleable.BarcodeScannerView_borderColor, mBorderColor);
            mMaskColor = a.getColor(R.styleable.BarcodeScannerView_maskColor, mMaskColor);
            mBorderWidth = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderWidth, mBorderWidth);
            mBorderLength = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_borderLength, mBorderLength);

            mRoundedCorner = a.getBoolean(R.styleable.BarcodeScannerView_roundedCorner, mRoundedCorner);
            mCornerRadius = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_cornerRadius, mCornerRadius);
            mSquaredFinder = a.getBoolean(R.styleable.BarcodeScannerView_squaredFinder, mSquaredFinder);
            mBorderAlpha = a.getFloat(R.styleable.BarcodeScannerView_borderAlpha, mBorderAlpha);
            mViewFinderOffset = a.getDimensionPixelSize(R.styleable.BarcodeScannerView_finderOffset, mViewFinderOffset);
        } finally {
            a.recycle();
        }

        //init();
    }


    public void init(Activity activity, int resId) {
        mViewFinderView = createViewFinderView(getContext());
        camera2BasicFragment=Camera2BasicFragment.newInstance();
        mActivity=activity;
        mResId=resId;
        //startCamera();
    }

    public final void setupLayout() {
        //removeAllViews();

        if (mViewFinderView instanceof View) {
            addView((View) mViewFinderView);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by " +
                    "'createViewFinderView()' should be instance of android.view.View");
        }
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView2}
     */
    protected IViewFinder2 createViewFinderView(Context context) {
        ViewFinderView2 viewFinderView = new ViewFinderView2(context);
        viewFinderView.setBorderColor(mBorderColor);
        viewFinderView.setLaserColor(mLaserColor);
        viewFinderView.setLaserEnabled(mIsLaserEnabled);
        viewFinderView.setBorderStrokeWidth(mBorderWidth);
        viewFinderView.setBorderLineLength(mBorderLength);
        viewFinderView.setMaskColor(mMaskColor);

        viewFinderView.setBorderCornerRounded(mRoundedCorner);
        viewFinderView.setBorderCornerRadius(mCornerRadius);
        viewFinderView.setSquareViewFinder(mSquaredFinder);
        viewFinderView.setViewFinderOffset(mViewFinderOffset);
        return viewFinderView;
    }

    public void setLaserColor(int laserColor) {
        mLaserColor = laserColor;
        mViewFinderView.setLaserColor(mLaserColor);
        mViewFinderView.setupViewFinder();
    }
    public void setMaskColor(int maskColor) {
        mMaskColor = maskColor;
        mViewFinderView.setMaskColor(mMaskColor);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        mViewFinderView.setBorderColor(mBorderColor);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mBorderWidth = borderStrokeWidth;
        mViewFinderView.setBorderStrokeWidth(mBorderWidth);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderLineLength(int borderLineLength) {
        mBorderLength = borderLineLength;
        mViewFinderView.setBorderLineLength(mBorderLength);
        mViewFinderView.setupViewFinder();
    }
    public void setLaserEnabled(boolean isLaserEnabled) {
        mIsLaserEnabled = isLaserEnabled;
        mViewFinderView.setLaserEnabled(mIsLaserEnabled);
        mViewFinderView.setupViewFinder();
    }
    public void setIsBorderCornerRounded(boolean isBorderCornerRounded) {
        mRoundedCorner = isBorderCornerRounded;
        mViewFinderView.setBorderCornerRounded(mRoundedCorner);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderCornerRadius(int borderCornerRadius) {
        mCornerRadius = borderCornerRadius;
        mViewFinderView.setBorderCornerRadius(mCornerRadius);
        mViewFinderView.setupViewFinder();
    }
    public void setSquareViewFinder(boolean isSquareViewFinder) {
        mSquaredFinder = isSquareViewFinder;
        mViewFinderView.setSquareViewFinder(mSquaredFinder);
        mViewFinderView.setupViewFinder();
    }
    public void setBorderAlpha(float borderAlpha) {
        mBorderAlpha = borderAlpha;
        mViewFinderView.setBorderAlpha(mBorderAlpha);
        mViewFinderView.setupViewFinder();
    }

    public void startCamera(String cameraId) {
        // under construction
        startCamera();
    }

    public void startCamera() {
        //startCamera(CameraUtils.getDefaultCameraId());
        mActivity.getFragmentManager().beginTransaction().replace(mResId,camera2BasicFragment).commit();
        camera2BasicFragment.setCameraPreviewHandler(this);
    }

    public void stopCamera() {
        //not set yet
    }

    public void stopCameraPreview() {
        //not set yet
    }

    protected void resumeCameraPreview() {
        //not set yet
    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if (mFramingRectInPreview == null) {
            Rect framingRect = mViewFinderView.getFramingRect();
            int viewFinderViewWidth = mViewFinderView.getWidth();
            int viewFinderViewHeight = mViewFinderView.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);

            if(previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth;
                rect.right = rect.right * previewWidth / viewFinderViewWidth;
            }

            if(previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight;
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            }

            mFramingRectInPreview = rect;
        }
        return mFramingRectInPreview;
    }

    public void setFlash(boolean flag) {
        mFlashState = flag;
        //not set yet
    }

    public boolean getFlash() {
        //not set yet
        return false;
    }

    public void toggleFlash() {
        //not set yet
    }

    public void setAutoFocus(boolean state) {
        //not set yet
    }

    public void setShouldScaleToFill(boolean shouldScaleToFill) {
        mShouldScaleToFill = shouldScaleToFill;
    }

    public void setAspectTolerance(float aspectTolerance) {
        mAspectTolerance = aspectTolerance;
    }

    public byte[] getRotatedData(byte[] data, Size size) {
        int width = size.getWidth();
        int height = size.getHeight();

        int rotationCount = getRotationCount();

        if(rotationCount == 1 || rotationCount == 3) {
            for (int i = 0; i < rotationCount; i++) {
                byte[] rotatedData = new byte[data.length];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++)
                        rotatedData[x * height + height - y - 1] = data[x + y * width];
                }
                data = rotatedData;
            }
        }

        return data;
    }

    public int getRotationCount() {
        int displayOrientation = getDisplayOrientation();
        return displayOrientation / 90;
    }

    public int getDisplayOrientation() {

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result= (getCameraFacingOldVersion(camera2BasicFragment.getCameraFacing()) - degrees + 360) % 360;
        return result;
    }

    private int getCameraFacingOldVersion(int cameraFacingNewVersion){
        if(cameraFacingNewVersion==1){
            return 0;
        }else{
            return 1;
        }
    }

    public Size getPreviewSize(){
        return camera2BasicFragment.getPreviewSize();
    }

}

