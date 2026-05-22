package com.dandroids.studytracker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.dandroids.studytracker.model.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BarChartView extends View {

    private final Paint barPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float[] dayMinutes = new float[7]; // Mon–Sun
    private final String[] dayLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint.setColor(Color.parseColor("#5C6BC0"));
        barPaint.setStyle(Paint.Style.FILL);

        labelPaint.setColor(Color.parseColor("#E8EAF6"));
        labelPaint.setTextSize(28f);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        axisPaint.setColor(Color.parseColor("#9FA8DA"));
        axisPaint.setStrokeWidth(2f);
        axisPaint.setStyle(Paint.Style.STROKE);
    }

    public void setSessions(List<Session> sessions) {
        // Reset
        for (int i = 0; i < 7; i++) dayMinutes[i] = 0;

        Calendar now = Calendar.getInstance();
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);

        for (Session s : sessions) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(s.startTime);
            if (c.get(Calendar.WEEK_OF_YEAR) == currentWeek
                    && c.get(Calendar.YEAR) == currentYear) {
                int day = c.get(Calendar.DAY_OF_WEEK); // 1=Sun, 2=Mon...
                int index = (day == Calendar.SUNDAY) ? 6 : day - 2;
                if (index >= 0 && index < 7) {
                    dayMinutes[index] += s.durationMinutes;
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width   = getWidth();
        float height  = getHeight();
        float padding = 40f;
        float chartH  = height - padding * 2;
        float chartW  = width  - padding * 2;

        // Find max for scaling
        float maxVal = 1f;
        for (float v : dayMinutes) if (v > maxVal) maxVal = v;

        float barWidth = chartW / 7f * 0.6f;
        float gap      = chartW / 7f;

        // Draw axis line
        canvas.drawLine(padding, height - padding,
                width - padding, height - padding, axisPaint);

        for (int i = 0; i < 7; i++) {
            float barH    = (dayMinutes[i] / maxVal) * chartH;
            float left    = padding + i * gap + (gap - barWidth) / 2f;
            float top     = height - padding - barH;
            float right   = left + barWidth;
            float bottom  = height - padding;

            // Alternate color for weekend
            barPaint.setColor(i >= 5
                    ? Color.parseColor("#9FA8DA")
                    : Color.parseColor("#5C6BC0"));

            canvas.drawRoundRect(new RectF(left, top, right, bottom), 8, 8, barPaint);

            // Day label
            canvas.drawText(dayLabels[i], left + barWidth / 2f,
                    height - padding + 30f, labelPaint);

            // Minutes label on top of bar
            if (dayMinutes[i] > 0) {
                canvas.drawText((int) dayMinutes[i] + "m",
                        left + barWidth / 2f, top - 8f, labelPaint);
            }
        }
    }
}
