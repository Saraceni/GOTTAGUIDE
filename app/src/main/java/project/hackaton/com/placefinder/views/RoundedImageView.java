package project.hackaton.com.placefinder.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by rafaelgontijo on 7/5/15.
 */
public class RoundedImageView extends ImageView {

    private Paint paint;
    private Bitmap outputLogoBitmap;

    public RoundedImageView(Context context) {
        super(context);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        //Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int offset = getPaddingTop();
        int w = getWidth() - offset;
        Rect dest = new Rect(offset, offset, w, w);

        outputLogoBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas outputCanvas = new Canvas(outputLogoBitmap);
        Bitmap croppedBitmap = getCircularCroppedBitmap(b, w);
        //bitmap.recycle();
        //int color = getResources().getColor(R.color.blue_background);
        Drawable background = getBackground();
        if(background != null)
        {
            background.setBounds(0, 0, getWidth(), getHeight());
            background.draw(outputCanvas);
        }
        //outputCanvas.drawARGB(255, Color.red(color), Color.green(color), Color.blue(color));
        outputCanvas.drawBitmap(croppedBitmap, null, dest, paint);
        canvas.drawBitmap(outputLogoBitmap, 0, 0, null);
        croppedBitmap.recycle();
    }

    public Bitmap getLogoBitmap()
    {
        return outputLogoBitmap;
    }

    public static Bitmap fitAndCropBitmap(Bitmap originalImage, int width, int height)
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        float bitmapScaleParameter = 1;
        float originalScaleParameter = 1;

        int widthDiff = originalImage.getWidth() - width;
        int heightDiff = originalImage.getHeight() - height;

        // one image size is smaller than view size
        if(widthDiff < 0 || heightDiff < 0)
        {
            if(widthDiff < heightDiff)
            {
                bitmapScaleParameter = originalImage.getWidth();
                originalScaleParameter = width;
            }
            else
            {
                bitmapScaleParameter = originalImage.getHeight();
                originalScaleParameter = height;
            }
        }
        else
        {
            if(widthDiff > heightDiff)
            {
                bitmapScaleParameter = originalImage.getHeight();
                originalScaleParameter = height;
            }
            else
            {
                bitmapScaleParameter = originalImage.getWidth();
                originalScaleParameter = width;
            }
        }

        float factor = originalScaleParameter / bitmapScaleParameter;

        int factoredImageWidth = (int) (originalImage.getWidth() * factor);
        int factoredImageHeight = (int) (originalImage.getHeight() * factor);

        int widthOffset = (factoredImageWidth - width) / 2;
        widthOffset /= factor;

        int heightOffset = (factoredImageHeight - height) / 2;
        heightOffset /= factor;

        int left = widthOffset, top = heightOffset;
        int right = originalImage.getWidth() - widthOffset, bottom = originalImage.getHeight() - heightOffset;

        Rect srcRect = new Rect(left, top, right, bottom);
        Rect dstRect = new Rect(0,0, width, height);

        canvas.drawBitmap(originalImage, srcRect, dstRect, paint);
        return  output;
    }

    public static Bitmap getCircularCroppedBitmap(Bitmap src, int diameter)
    {
        Bitmap sbmp;

        int radius = diameter/2;

        sbmp = fitAndCropBitmap(src, diameter, diameter);

        Bitmap result = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.parseColor("#BAB399"));

        //canvas.drawRect(0, 0, diameter, diameter, paint);
        canvas.drawCircle(radius,
                radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas.drawBitmap(sbmp, srcRect, rect, paint);
        canvas.drawBitmap(sbmp, null, new Rect(0,0, diameter, diameter), paint);
        paint.setXfermode(null);

        return result;

    }


}












