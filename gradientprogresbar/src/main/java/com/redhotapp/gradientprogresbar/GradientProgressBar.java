package com.redhotapp.gradientprogresbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

public class GradientProgressBar extends View {
    private final String TAG = "ColoredProgressView";
    final private int SCALE_POINTS = 5;
    final private float BIG_CIRCLE_SCALE = 0.92f; // space for shadow

    private int[] colors = null;
    private Pair<Integer, Float> colorAndWeight[] = null;

    private float positions[];

    private ColoredLine line;
    private ColoredCircle circle;

    private int w;
    private int h;

    // additional settings

    private int progress = 0;
    private int pointsNumber = 20;

    private int centralCircleColor = Color.WHITE;
    private int textColor = Color.DKGRAY;
    private int dividersColor = Color.WHITE;
    private int textSize = 0;
    private int dividerWidthPx = 2;

    private boolean circleVisible = true;
    private boolean smallCircleVisible = true;
    private boolean textVisible = true;
    private boolean dividersVisible = true;

    private STYLE style = STYLE.ROUND;

    public enum STYLE {ROUND, PIMP, NO_POINTER}

    public GradientProgressBar(Context context) {
        super(context);
        init();
    }

    public GradientProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        {
            this.colorAndWeight = new Pair[colors.length];
            for (int i = 0; i < colors.length; i++) {
                colorAndWeight[i] = new Pair<>(colors[i], 1f / colors.length);
            }
        }
        this.invalidate();
    }

    /**
     * Set colors and corresponding weights
     *
     * @param colorsAndWeights array of colors and their weights sum of weights should be 1
     */
    public void setColorAndWeight(Pair<Integer, Float>[] colorsAndWeights) {
        this.colorAndWeight = colorsAndWeights;
        this.invalidate();
    }


    public void setStyle(STYLE style) {
        this.style = style;
        if (style == STYLE.NO_POINTER || style == STYLE.PIMP) {
            setCircleVisible(false);
            setSmallCircleVisible(false);
        }
    }

    public void setProgress(int percent) {
        this.progress = percent;
    }

    public void setPointsNumber(int pointsNumber) {
        this.pointsNumber = pointsNumber;
    }

    public void setCentralCircleColor(int centralCircleColor) {
        this.centralCircleColor = centralCircleColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setDividersColor(int dividersColor) {
        this.dividersColor = dividersColor;
    }

    public void setDividerWidthPx(int dividerWidthPx) {
        this.dividerWidthPx = dividerWidthPx;
    }

    public void setSmallCircleVisible(boolean smallCircleVisible) {
        this.smallCircleVisible = smallCircleVisible;
    }

    public void setTextVisible(boolean textVisible) {
        this.textVisible = textVisible;
    }

    public void setDividersVisible(boolean dividersVisible) {
        this.dividersVisible = dividersVisible;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public void setCircleVisible(boolean circleVisible) {
        this.circleVisible = circleVisible;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        setPositions();
        canvas.drawColor(Color.TRANSPARENT);
        this.w = getWidth();
        this.h = getHeight();
        setShapes(w, h);


        Bitmap b = Bitmap.createBitmap(w, h / SCALE_POINTS, Bitmap.Config.ARGB_8888);
        paint.setColor(Color.GREEN);

        Shader mShader;
        if (colorAndWeight != null) {
            colors = new int[colorAndWeight.length];
            for (int i = 0; i < colorAndWeight.length; i++) {
                colors[i] = colorAndWeight[i].first;
            }
            mShader = new LinearGradient(0, 0, getW(), 0, colors, positions, Shader.TileMode.CLAMP);
        } else {
            throw new IllegalArgumentException("colors must be provided");
        }

        //line with gradient inside bitmap
        Canvas c = new Canvas(b);
        paint.setShader(mShader);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(line.height);
        paint.setAntiAlias(true);
        c.drawLine(line.startX, line.height / 2, line.endX, line.height / 2, paint);

        canvas.drawBitmap(b, 0, h / SCALE_POINTS, paint);

        if (textVisible) drawTextUnderLine(canvas);
        if (dividersVisible) drawDividers(canvas);

        if (style == STYLE.ROUND) {
            try {
                circle.color = b.getPixel(b.getWidth() / 100 * progress, b.getHeight() - 2);
                circle.startColor = b.getPixel(b.getWidth() / 100 * progress - circle.radius * 2, b.getHeight() - 2);
                circle.endColor = b.getPixel(b.getWidth() / 100 * progress + circle.radius * 2, b.getHeight() - 2);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (circleVisible == true) {
                Paint bigCirclePaint = new Paint();
                bigCirclePaint.setDither(true);
                LinearGradient gradient = new LinearGradient(circle.cx - circle.radius, circle.cy, circle.cx + circle.radius, circle.cy, new int[]{circle.startColor, circle.endColor}, null, Shader.TileMode.MIRROR);
                bigCirclePaint.setStyle(Paint.Style.FILL);
                bigCirclePaint.setAntiAlias(true);
                bigCirclePaint.setShader(gradient);
                canvas.drawCircle(circle.cx, circle.cy, circle.radius, bigCirclePaint);
            }

            if (progress != 0 && smallCircleVisible) {
                Paint smallCirclePaint = new Paint();
                smallCirclePaint.setStyle(Paint.Style.FILL);
                smallCirclePaint.setAntiAlias(true);
                smallCirclePaint.setColor(centralCircleColor);
                canvas.drawCircle(circle.cx, circle.cy, circle.radius / 2, smallCirclePaint);

            }
        } else if (style == STYLE.PIMP) {
            int circleColor = Color.RED;
            try {
                circleColor = b.getPixel(b.getWidth() / 100 * progress, b.getHeight() - 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Paint paintCircle = new Paint();
            paintCircle.setStyle(Paint.Style.FILL);
            paintCircle.setColor(circleColor);
            paintCircle.setAntiAlias(true);

            float radius = getH() / SCALE_POINTS / 2;
            float cx = getW() * progress / 100 + dividerWidthPx;
            canvas.drawCircle(cx, radius, radius, paintCircle);
        }

    }


    private void setShapes(int w, int h) {
        line = new ColoredLine();
        line.height = h / SCALE_POINTS;

        line.startX = 0 + line.height / 2;
        line.endX = w - line.height / 2;
        if (style == STYLE.ROUND) {
            circle = new ColoredCircle();
            circle.radius = (int) (getH() / SCALE_POINTS / 2 * 3 * BIG_CIRCLE_SCALE);
            circle.cx = getW() * progress / 100;
        }
    }

    private void drawDividers(Canvas canvas) {
        if (colorAndWeight != null && colorAndWeight.length > 0) {
            Paint linesPaint = new Paint();
            linesPaint.setDither(true);
            linesPaint.setAntiAlias(true);
            linesPaint.setColor(dividersColor);
            for (int i = 0; i < colorAndWeight.length; i++) {
                if (positions[i] != 0)
                    canvas.drawRect(getW() * positions[i], h / SCALE_POINTS, getW() * positions[i] + dividerWidthPx, h / SCALE_POINTS * 2, linesPaint);
            }
        }

    }

    private void drawTextUnderLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        if (textSize == 0) textSize = (int) ((getHeight() / SCALE_POINTS) * 2f);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        if (colorAndWeight != null && colorAndWeight.length > 0) {
            String text;
            for (int i = 0; i < colorAndWeight.length; i++) {
                text = (int) (positions[i] * pointsNumber) + "";
                //  text = String.format("%.1f", positions[i] * pointsNumber);

                int margin = textSize / 2 * text.length();
                if (positions[i] == 0) {
                    canvas.drawText(text, getW() * positions[i], getHeight() / SCALE_POINTS * 4, paint);
                } else if (positions[i] > 0.95) {
                    canvas.drawText(text, getW() * positions[i] - margin - line.startX, getHeight() / SCALE_POINTS * 4, paint);
                } else {
                    canvas.drawText(text, getW() * positions[i] - margin / 2, getHeight() / SCALE_POINTS * 4, paint);
                }
            }
        }
    }

    private void setPositions() {
        if (colorAndWeight != null && colorAndWeight.length > 0) {
            positions = new float[colorAndWeight.length];
            float b;
            for (int i = 0; i < colorAndWeight.length; i++) {
                if (i == 0) positions[i] = colorAndWeight[i].second;
                else {
                    b = colorAndWeight[i].second + positions[i - 1];
                    positions[i] = Math.round(b * 10000f) / 10000f;
                }

                Log.e(TAG, positions[i] + " = positions[i]");
            }
        }
    }


    private class ColoredLine {
        float startX;
        float endX;
        int height;
    }

    private class ColoredCircle {
        int color = Color.RED;
        int startColor = Color.RED;
        int endColor = Color.RED;
        int radius = (int) (getH() / SCALE_POINTS / 2 * 3 * BIG_CIRCLE_SCALE);
        float cx;
        float cy = radius * 1f / BIG_CIRCLE_SCALE;
    }
}
