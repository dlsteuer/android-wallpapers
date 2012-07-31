package com.steuernol.android.wallpaper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

public class ParticlesWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new ParticleWallpaperEngine();
    }

    private class ParticleWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private List<Point> circles;
        private Paint paint = new Paint();
        private int width;
        private int height;
        private boolean visible = true;
        private int maxNumber;
        private boolean touchEnabled;

        public ParticleWallpaperEngine() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ParticlesWallpaperService.this);
            maxNumber = Integer.valueOf(prefs.getString("numberOfCircles", "4"));
            touchEnabled = prefs.getBoolean("touch", false);
            circles = new ArrayList<Point>();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(10f);
            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (!touchEnabled) {
                return;
            }

            float x = event.getX();
            float y = event.getY();

            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    circles.add(new Point(String.valueOf(circles.size() + 1), (int)x, (int)y));
                    drawCircles(canvas, circles);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            super.onTouchEvent(event);
        }



        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    if (circles.size() >= maxNumber) {
                        circles.clear();
                    }
                    int x = (int)(width * Math.random());
                    int y = (int)(height * Math.random());

                    circles.add(new Point(String.valueOf(circles.size() + 1), x, y));
                    drawCircles(canvas, circles);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 5000);
            }
        }

        private void drawCircles(Canvas canvas, List<Point> circles) {
            canvas.drawColor(Color.BLACK);
            for(Point point:circles) {
                Path path = new Path();
                path.moveTo(point.x, point.y);
                path.lineTo(point.x - 100f, point.y);
                path.lineTo(point.x, point.y - 100f);
                path.lineTo(point.x + 100f, point.y);
                path.close();
                canvas.drawPath(path, paint);
            }
        }
    }
}
