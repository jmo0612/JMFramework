package com.thowo.jmframework.jmozxing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.thowo.jmframework.jmobarcodescannercam2.BarcodeScannerView2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JmoZXingScannerViewCamera2 extends BarcodeScannerView2 {

    private static final String TAG = "ZXingScannerView";
    private boolean isQRFound=false;
    private Bitmap capturedBmp;

    public interface ResultHandler{
        void handleResult(Result rawResult);
    }

    private MultiFormatReader mMultiFormatReader;
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<>();
    private List<BarcodeFormat> mFormats;
    private ResultHandler mResultHandler;

    static {
        ALL_FORMATS.add(BarcodeFormat.AZTEC);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.MAXICODE);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.RSS_EXPANDED);
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.UPC_EAN_EXTENSION);
    }

    public JmoZXingScannerViewCamera2(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onImagePreviewed(byte[] imageData, Size imageSize) {
        //Toast.makeText(getContext(),"previewed",Toast.LENGTH_SHORT).show();
        if(mResultHandler == null) {
            return;
        }

        if(isQRFound){
            return;
        }


        int width = getPreviewSize().getWidth();
        int height = getPreviewSize().getHeight();
        int rotationCount = getRotationCount();
        if(rotationCount == 1 || rotationCount == 3) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        imageData=getRotatedData(imageData,imageSize);

        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(imageData, width, height);

        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                rawResult = mMultiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } catch (NullPointerException npe) {
                // This is terrible
            } catch (ArrayIndexOutOfBoundsException aoe) {

            } finally {
                mMultiFormatReader.reset();
            }

            if (rawResult == null) {
                LuminanceSource invertedSource = source.invert();
                bitmap = new BinaryBitmap(new HybridBinarizer(invertedSource));


                try {
                    rawResult = mMultiFormatReader.decodeWithState(bitmap);
                } catch (NotFoundException e) {
                    // continue
                } finally {
                    mMultiFormatReader.reset();
                }
            }
        }

        final Result finalRawResult = rawResult;

        if (finalRawResult != null) {
            //take picture code here......

            Toast.makeText(getContext(),"YES",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onImagePreviewed: BERHASIL");

        }else{
            Toast.makeText(getContext(),"NO",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onImagePreviewed: NULL");
        }


    }

    @Override
    public void init(Activity activity, int resId){
        super.init(activity, resId);
        super.setupLayout();
        super.startCamera();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        initMultiFormatReader();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if(mFormats == null) {
            return ALL_FORMATS;
        }
        return mFormats;
    }

    private void initMultiFormatReader() {
        Map<DecodeHintType,Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void foundQR(){
        isQRFound=true;
    }

    public void rescanQR(){
        isQRFound=false;
    }

    public void resumeCameraPreview(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
        PlanarYUVLuminanceSource source = null;

        try {
            source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                    rect.width(), rect.height(), false);
        } catch(Exception e) {
        }

        return source;
    }

    public Bitmap getCapturedBmp(){
        return capturedBmp;
    }

}