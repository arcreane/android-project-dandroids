package com.dandroids.studytracker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class TimerProgressView extends View {

    private final Paint backgroundRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressRingPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerTextPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelTextPaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcRect             = new RectF();

    private float progress    = 1f;
    private String centerText = "25:00";
    private String labelText  = "Focus";

    public TimerProgressView(Context context) {
        super(context); init();
    }
    public TimerProgressView(Context context, AttributeSet attrs) {
        super(context, attrs); init();
    }
    public TimerProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init();
    }

    private void init() {
        backgroundRingPaint.setColor(Color.parseColor("#2D325A"));
        backgroundRingPaint.setStyle(Paint.Style.STROKE);
        backgroundRingPaint.setStrokeWidth(24f);
        backgroundRingPaint.setStrokeCap(Paint.Cap.ROUND);

        progressRingPaint.setColor(Color.parseColor("#5C6BC0"));
        progressRingPaint.setStyle(Paint.Style.STROKE);
        progressRingPaint.setStrokeWidth(24f);
        progressRingPaint.setStrokeCap(Paint.Cap.ROUND);

        centerTextPaint.setColor(Color.parseColor("#E8EAF6"));
        centerTextPaint.setTextSize(72f);
        centerTextPaint.setTextAlign(Paint.Align.CENTER);
        centerTextPaint.setFakeBoldText(true);

        labelTextPaint.setColor(Color.parseColor("#9FA8DA"));
        labelTextPaint.setTextSize(36f);
        labelTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setProgress(long remainingMillis, long totalMillis) {
        this.progress = totalMillis > 0
                ? (float) remainingMillis / totalMillis : 0f;

        long minutes = (remainingMillis / 1000) / 60;
        long seconds = (remainingMillis / 1000) % 60;
        this.centerText = String.format("%02d:%02d", minutes, seconds);

        if (progress > 0.5f) {
            progressRingPaint.setColor(Color.parseColor("#5C6BC0"));
        } else if (progress > 0.2f) {
            progressRingPaint.setColor(Color.parseColor("#FF9800"));
        } else {
            progressRingPaint.setColor(Color.parseColor("#F44336"));
        }
        invalidate();
    }

    public void setLabelText(String label) {
        this.labelText = label;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx     = getWidth()  / 2f;
        float cy     = getHeight() / 2f;
        float radius = Math.min(cx, cy) - 40f;

        arcRect.set(cx - radius, cy - radius, cx + radius, cy + radius);

        canvas.drawCircle(cx, cy, radius, backgroundRingPaint);
        canvas.drawArc(arcRect, -90f, 360f * progress, false, progressRingPaint);

        float textY = cy - ((centerTextPaint.descent() + centerTextPaint.ascent()) / 2f);
        canvas.drawText(centerText, cx, textY, centerTextPaint);
        canvas.drawText(labelText, cx, cy + radius * 0.5f, labelTextPaint);
    }
}
