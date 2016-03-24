package com.minismap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.minismap.data.Enemy;
import com.minismap.data.FogOfWar;
import com.minismap.data.GridPoint;
import com.minismap.data.Map;
import com.minismap.data.MapArray;

/**
 * Created by nbp184 on 2016/03/21.
 */
public class MapView extends ImageView implements GestureDetector.OnGestureListener {

    public static final int MODE_LAYOUT = 0;
    public static final int MODE_ENEMY = 1;
    public static final int MODE_FOG = 2;

    public static final int FOG_RECTANGLE = 1;
    public static final int FOG_SINGLE = 0;

    public interface OnTapListener {
        void onAddEnemy(int x, int y);
        void onChangeEnemy(int enemyIndex);
        void onMoveTapError(String message);
        void onAddFog(int x, int y);
        void onAddFog(int startx, int starty, int endx, int endy);
        void onChangeFog(int fogIndex);
    }

    private Map map;
    private int drawingMode;
    private int fogMode;
    private int gridColour;
    private int enemyColour;
    private int textColour;
    private int fogColour;
    private int fogOutlineColour;
    private int fogRColour;
    private boolean showGrid;
    private GestureDetector mDetector;
    private OnTapListener listener;
    private Enemy selectedEnemy;
    private FogOfWar selectedFog;
    private GridPoint start;

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        map = null;
        drawingMode = MODE_LAYOUT;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gridColour = getContext().getColor(R.color.colourLayout);
            enemyColour = getContext().getColor(R.color.enemy);
            fogColour = getContext().getColor(R.color.colorTransFog);
            fogOutlineColour = getContext().getColor(R.color.colorFog);
            fogRColour = getContext().getColor(R.color.colorFogRect);
        } else {
            gridColour = getResources().getColor(R.color.colourLayout);
            enemyColour = getResources().getColor(R.color.enemy);
            fogColour = getResources().getColor(R.color.colorTransFog);
            fogOutlineColour = getResources().getColor(R.color.colorFog);
            fogRColour = getResources().getColor(R.color.colorFogRect);
        }
        textColour = 0xFFFFFFFF;
        showGrid = true;
        float dpToPx = getContext().getResources().getDisplayMetrics().densityDpi/160f;
        float fingerWidth = 100f/4f*dpToPx;
        mDetector = new GestureDetector(getContext(), this);
        listener = null;
        selectedEnemy = null;
        selectedFog = null;
        fogMode = FOG_RECTANGLE;
        start = null;
    }

    public void setMap(Map map) {
        this.map = map;
        this.setImageBitmap(map.getBackground());
    }

    public void setMapBackground(Bitmap image) {
        map.setBackground(image);
        this.setImageBitmap(image);
    }

    public void setMapName(String name) {
        map.name = name;
    }

    public String getMapName() {
        return map.name;
    }

    public void setDrawingMode(int mode) {
        drawingMode = mode;
        selectedEnemy = null;
        selectedFog = null;
        start = null;
        invalidate();
    }

    public int getDrawingMode() {
        return drawingMode;
    }

    public void setFogMode(int mode) {
        fogMode = mode;
        start = null;
    }

    public void deselectFog() {
        selectedFog = null;
        start = null;
        invalidate();
    }

    public void setMapX0(int x0) {
        map.x0 = x0;
        invalidate();
    }

    public void setMapY0(int y0) {
        map.y0 = y0;
        invalidate();
    }

    public void setMapBoxSize(int boxSize) {
        map.boxSize = boxSize;
        invalidate();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        invalidate();
    }

    public void setMapWidth(int width) {
        map.width = width;
        invalidate();
    }

    public void setMapHeight(int height) {
        map.height = height;
        invalidate();
    }

    public int getMapIndex() {
        return MapArray.getInstance().indexOf(map);
    }

    public void addEnemy(String name, String abbreviation, int x, int y) {
        map.enemies.add(new Enemy(name, abbreviation, x, y));
        invalidate();
    }

    public void editEnemy(int index, String name, String abbreviation) {
        Enemy enemy = map.enemies.get(index);
        enemy.name = name;
        enemy.abbreviation = abbreviation;
        invalidate();
    }

    public void addFog(String name, int x, int y) {
        map.addFog(new FogOfWar(name, x, y));
        invalidate();
    }

    public void addFog(String name, int x1, int y1, int x2, int y2) {
        map.addFog(new FogOfWar(name, x1, y1, x2, y2));
        invalidate();
    }

    public String getFogName(int index) {
        return map.getFog(index).name;
    }

    public void setFogName(int index, String name) {
        map.getFog(index).name = name;
    }

    public void deleteSelectedEnemy() {
        if(selectedEnemy != null) {
            map.enemies.remove(selectedEnemy);
            selectedEnemy = null;
            invalidate();
        }
    }

    public void deleteSelectedFog() {
        if(selectedFog != null) {
            map.removeFog(selectedFog);
            selectedFog = null;
            invalidate();
        }
    }

    public void setOnTapListener(OnTapListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        if(map != null && map.getBackground() != null) {
            switch(drawingMode) {
                case MODE_LAYOUT:
                    drawLayout(canvas, width, height);
                    break;
                case MODE_ENEMY:
                    if(showGrid) {
                        drawGrid(canvas, width, height);
                    }
                    drawEnemies(canvas, width, height);
                    break;
                case MODE_FOG:
                    if(showGrid) {
                        drawGrid(canvas, width, height);
                    }
                    drawFog(canvas, width, height);
                    break;
            }
        }
    }

    private void drawFog(Canvas canvas, int width, int height) {
        Paint fog = new Paint();
        fog.setColor(fogColour);
        Paint selected = new Paint();
        selected.setColor(gridColour);
        selected.setStyle(Paint.Style.STROKE);
        selected.setStrokeWidth(0.1f * map.boxSize - 1);
        Paint highlight = new Paint();
        highlight.setColor(fogRColour);
        highlight.setStyle(Paint.Style.STROKE);
        highlight.setStrokeWidth(0.1f * map.boxSize - 1);
        Paint outline = new Paint();
        outline.setColor(fogOutlineColour);
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(0.05f * map.boxSize);
        FogOfWar current;
        GridPoint gp;
        float x, y;
        for(int i = 0; i < map.fogCount(); i++) {
            current = map.getFog(i);
            for(int j = 0; j < current.pointCount(); j++) {
                gp = current.getPoint(j);
                x = map.x0 + gp.x*map.boxSize;
                y = map.y0 + gp.y*map.boxSize;
                canvas.drawRect(x, y, x + map.boxSize, y + map.boxSize, fog);
                if(current != selectedFog) {
                    canvas.drawLines(current.getOutline(map.x0, map.y0, map.boxSize), outline);
                }
            }
        }
        if(selectedFog != null) {
            canvas.drawLines(selectedFog.getOutline(map.x0, map.y0, map.boxSize), selected);
        }
        if(start != null) {
            x = map.x0 + start.x*map.boxSize;
            y = map.y0 + start.y*map.boxSize;
            canvas.drawRect(x, y, x + map.boxSize, y + map.boxSize, highlight);
        }
    }

    private void drawEnemies(Canvas canvas, int width, int height) {
        Paint circle = new Paint();
        circle.setColor(enemyColour);
        Paint text = new Paint();
        text.setColor(textColour);
        text.setTextSize(map.boxSize / 2f);
        Paint selected = new Paint();
        selected.setColor(gridColour);
        selected.setStyle(Paint.Style.STROKE);
        selected.setStrokeWidth(0.1f * map.boxSize - 1);
        Rect bounds = new Rect();
        float x, y;
        for(Enemy enemy : map.enemies) {
            x = map.x0 + enemy.location.x*map.boxSize;
            y = map.y0 + enemy.location.y*map.boxSize;
            canvas.drawCircle(x + map.boxSize / 2f, y + map.boxSize / 2f, 0.8f * map.boxSize / 2f, circle);
            text.getTextBounds(enemy.abbreviation, 0, enemy.abbreviation.length(), bounds);
            canvas.drawText(enemy.abbreviation, x + map.boxSize / 2f - bounds.width() / 2f, y + map.boxSize / 2f + bounds.height() / 2f, text);
            if(enemy == selectedEnemy) {
                canvas.drawCircle(x + map.boxSize / 2f, y + map.boxSize / 2f, 0.9f * map.boxSize / 2f - 1, selected);
            }
        }
    }

    private void drawGrid(Canvas canvas, int width, int height) {
        int gWidth;
        if(map.width < 0) {
            gWidth = width - map.x0;
        } else {
            gWidth = map.width*map.boxSize + 1;
        }
        int gHeight;
        if(map.height < 0) {
            gHeight = height - map.y0;
        } else {
            gHeight = map.height*map.boxSize + 1;
        }
        Paint p = new Paint();
        p.setColor(gridColour);
        for(int i = map.x0; i <= gWidth; i += map.boxSize) {
            canvas.drawLine(i, map.y0, i, map.y0 + gHeight, p);
        }
        for(int j = map.y0; j <= gHeight; j += map.boxSize) {
            canvas.drawLine(map.x0, j, map.x0 + gWidth, j, p);
        }
    }

    private void drawLayout(Canvas canvas, int width, int height) {
        Paint p = new Paint();
        p.setColor(gridColour);
        canvas.drawLine(map.x0, map.y0 - map.boxSize / 2f, map.x0, map.y0 + map.boxSize, p);
        canvas.drawLine(map.x0, map.y0 + map.boxSize, map.x0 + map.boxSize, map.y0 + map.boxSize, p);
        canvas.drawLine(map.x0 + map.boxSize, map.y0 + map.boxSize, map.x0 + map.boxSize, map.y0, p);
        canvas.drawLine(map.x0 + map.boxSize, map.y0, map.x0 - map.boxSize / 2f, map.y0, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(listener != null) {
            switch(drawingMode) {
                case MODE_ENEMY:
                    return singleTapEnemy(e.getX(), e.getY());
                case MODE_FOG:
                    return singleTapFog(e.getX(), e.getY());
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (listener != null) {
            switch (drawingMode) {
                case MODE_ENEMY:
                    longPressEnemy(e.getX(), e.getY());
                    break;
                case MODE_FOG:
                    longPressFog(e.getX(), e.getY());
                    break;
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private boolean singleTapEnemy(float ex, float ey) {
        int x = (int)(ex - map.x0)/map.boxSize;
        int y = (int)(ey - map.y0)/map.boxSize;
        if(x >= 0 && y >= 0 && (map.width < 0 || x < map.width) && (map.height < 0 || y < map.height)) {
            if(selectedEnemy != null) {
                Enemy enemy = map.getEnemyAt(x, y);
                if(enemy == selectedEnemy) {
                    selectedEnemy = null;
                    invalidate();
                } else if(enemy == null) {
                    selectedEnemy.location.x = x;
                    selectedEnemy.location.y = y;
                    selectedEnemy = null;
                    invalidate();
                } else {
                    listener.onMoveTapError("Can't place \"" + selectedEnemy.abbreviation +"\" on top of \"" +enemy.abbreviation +"\".");
                }
            } else {
                selectedEnemy = map.getEnemyAt(x, y);
                if(selectedEnemy != null) {
                    invalidate();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void longPressEnemy(float ex, float ey) {
        int x = (int)(ex - map.x0)/map.boxSize;
        int y = (int)(ey - map.y0)/map.boxSize;
        if(x >= 0 && y >= 0 && (map.width < 0 || x < map.width) && (map.height < 0 || y < map.height)) {
            int enemyIndex = map.getEnemyIndexAt(x, y);
            if(enemyIndex < 0) {
                listener.onAddEnemy(x, y);
            } else {
                listener.onChangeEnemy(enemyIndex);
            }
        }
    }

    /*Fog of War
        single tap in fog: if fog selected then remove square else select fog
        single tap outside fog: if fog selected then add to fog
        long press in fog: edit fog
        long press outside fog: create fog
        add method for deselect
     */

    private boolean singleTapFog(float ex, float ey) {
        int x = (int) (ex - map.x0) / map.boxSize;
        int y = (int) (ey - map.y0) / map.boxSize;
        if (x >= 0 && y >= 0 && (map.width < 0 || x < map.width) && (map.height < 0 || y < map.height)) {
            GridPoint gp = new GridPoint(x, y);
            if(selectedFog == null) {
                selectedFog = map.getFogAt(gp);
                start = null;
                invalidate();
            } else if(fogMode == FOG_RECTANGLE) {
                if(start == null) {
                    start = gp;
                    invalidate();
                } else if(selectedFog.contains(start)) {
                    selectedFog.removeRectangle(Math.min(start.x, gp.x), Math.min(start.y, gp.y), Math.max(start.x, gp.x), Math.max(start.y, gp.y));
                    start = null;
                    invalidate();
                } else {
                    selectedFog.addRectangle(Math.min(start.x, gp.x), Math.min(start.y, gp.y), Math.max(start.x, gp.x), Math.max(start.y, gp.y));
                    map.removeOverlapFrom(selectedFog);
                    start = null;
                    invalidate();
                }
            } else {
                if(selectedFog.contains(gp)) {
                    selectedFog.removePoint(gp);
                    invalidate();
                } else if(map.getFogAt(gp) == null) {
                    selectedFog.addPoint(gp);
                    invalidate();
                } else {
                    selectedFog = map.getFogAt(gp);
                    invalidate();
                }
                start = null;
            }
        }
        return false;
    }

    private void longPressFog(float ex, float ey) {
        int x = (int)(ex - map.x0)/map.boxSize;
        int y = (int)(ey - map.y0)/map.boxSize;
        if(x >= 0 && y >= 0 && (map.width < 0 || x < map.width) && (map.height < 0 || y < map.height)) {
            int fogIndex = map.getFogIndexAt(x, y);
            if(fogIndex < 0) {
                if(fogMode == FOG_RECTANGLE) {
                    if(start == null) {
                        start = new GridPoint(x, y);
                        invalidate();
                    } else {
                        listener.onAddFog(Math.min(start.x, x), Math.min(start.y, y), Math.max(start.x, x), Math.max(start.y, y));
                        start = null;
                    }
                } else {
                    start = null;
                    listener.onAddFog(x, y);
                }
            } else {
                start = null;
                listener.onChangeFog(fogIndex);
            }
        }
    }

}
