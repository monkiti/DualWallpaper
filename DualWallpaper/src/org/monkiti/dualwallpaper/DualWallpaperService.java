package org.monkiti.dualwallpaper;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class DualWallpaperService extends WallpaperService {
	
    private final Handler handler = new Handler();

	public Engine onCreateEngine() {
		return new LiveEngine();
	}

    public class LiveEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

        private Bitmap landImage;
        private Bitmap portImage;
        
        private final Runnable drawRunnable = new Runnable() {
            public void run() {
                drawFrame();
            }
        };

        public LiveEngine() {
        	SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            setting.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(setting, "landscape");
            onSharedPreferenceChanged(setting, "portrait");
        }
        
        public void onCreate(SurfaceHolder surfaceHolder) {
        	super.onCreate(surfaceHolder);
        }

        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawRunnable);
        }

        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                drawFrame();
            } else {
                handler.removeCallbacks(drawRunnable);
            }
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
        	super.onSurfaceCreated(surfaceHolder);
        }

        public void onSurfaceChanged(SurfaceHolder holder,int format, int width , int height) {
            super.onSurfaceChanged(holder, format, width, height);

            drawFrame();
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            
            handler.removeCallbacks(drawRunnable);
        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
        	drawFrame();
        }

        private void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            
            Canvas c = null;
            try{
                c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(Color.WHITE);
                    
                    Resources resources = getResources();
                    Configuration config = resources.getConfiguration();
                    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    	c.drawBitmap(landImage, 0, 0, null);
                    else
                    	c.drawBitmap(portImage, 0, 0, null);
                }
                
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

    	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    		String path = sharedPreferences.getString(key, "");
    		
    		if (key == "landscape") {
    			if (landImage != null) {
    				landImage.recycle();
    			    landImage = null;
    			}

    			System.gc();
        		Bitmap bitmap = BitmapFactory.decodeFile(path);
        		
    			if (bitmap != null)
    				landImage = bitmap;
    			else
    				landImage = BitmapFactory.decodeResource(getResources(), R.drawable.landscape);
    		} else {
    			if (portImage != null) {
    				portImage.recycle();
    				portImage = null;
    			}

    			System.gc();
        		Bitmap bitmap = BitmapFactory.decodeFile(path);
        		
    			if (bitmap != null)
    				portImage = bitmap;
    			else
    				portImage = BitmapFactory.decodeResource(getResources(), R.drawable.portrait);
    		}

            drawFrame();
    	}
    }
}

