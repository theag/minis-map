package com.minismap.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/03/15.
 */
public class Map {

    private static final Random rand = new Random();
    public static File dir = null;


    public static Map load(File dir, String line) {
        Map.dir = dir;
        StringTokenizer tokens = new StringTokenizer(line, Seperators.MAP);
        Map rv = new Map(tokens.nextToken());
        rv.filename = Seperators.nullConvert(tokens.nextToken());
        if(rv.filename != null) {
            rv.background = BitmapFactory.decodeFile(dir.getAbsolutePath() +File.separator +rv.filename);
        }
        rv.x0 = Integer.parseInt(tokens.nextToken());
        rv.y0 = Integer.parseInt(tokens.nextToken());
        rv.boxSize = Integer.parseInt(tokens.nextToken());
        rv.width = Integer.parseInt(tokens.nextToken());
        rv.height = Integer.parseInt(tokens.nextToken());
        String token;
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            if(token.compareTo(Seperators.NEW_ARRAY_INDICATOR) == 0) {
                break;
            }
            rv.enemies.add(Enemy.load(token));
        }
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            if(token.compareTo(Seperators.NEW_ARRAY_INDICATOR) == 0) {
                break;
            }
            rv.fogs.add(FogOfWar.load(token));
        }
        return rv;
    }

    public String name;
    private Bitmap background;
    private String filename;
    private String oldFilename;
    public int x0;
    public int y0;
    public int boxSize;
    public int width;
    public int height;
    public ArrayList<Enemy> enemies;
    private ArrayList<FogOfWar> fogs;

    public Map(String name) {
        this.name = name;
        background = null;
        enemies = new ArrayList<>();
        fogs = new ArrayList<>();
        x0 = 0;
        y0 = 0;
        boxSize = 10;
        width = -1;
        height = -1;
        filename = null;
        oldFilename = null;
    }

    @Override
    public String toString() {
        return name;
    }

    public ArrayList<String> getUniqueEnemyNames() {
        ArrayList<String> rv = new ArrayList<>();
        for(Enemy enemy : enemies) {
            if(!rv.contains(enemy.name)) {
                rv.add(enemy.name);
            }
        }
        return rv;
    }

    public Enemy getEnemyAt(int x, int y) {
        for(Enemy enemy : enemies) {
            if(enemy.location.equals(x, y)) {
                return enemy;
            }
        }
        return null;
    }

    public int getEnemyIndexAt(int x, int y) {
        Enemy enemy;
        for(int i = 0; i < enemies.size(); i++) {
            enemy = enemies.get(i);
            if(enemy.location.equals(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public String save(File dir) {
        Map.dir = dir;
        if(oldFilename != null) {
            (new File(dir, oldFilename)).delete();
            oldFilename = null;
        }
        if(background == null) {
            filename = null;
        } else {
            File image;
            if(filename == null) {
                do {
                    filename = "";
                    for (int i = 0; i < 5; i++) {
                        char chr = (char) (rand.nextInt(26) + 'a');
                        filename += chr;
                    }
                    filename += ".jpg";
                    image = new File(dir, filename);
                } while (image.exists());
                try {
                    FileOutputStream fOut = new FileOutputStream(image);
                    background.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                    filename = null;
                }
            }
            background.recycle();
        }
        String rv = name +Seperators.MAP +Seperators.nullConvert(filename) +Seperators.MAP +x0 +Seperators.MAP +y0 +Seperators.MAP +boxSize +Seperators.MAP +width +Seperators.MAP +height;
        for(Enemy enemy : enemies) {
            rv += Seperators.MAP +enemy.save();
        }
        rv += Seperators.MAP +Seperators.NEW_ARRAY_INDICATOR;
        for(FogOfWar fog : fogs) {
            rv += Seperators.MAP +fog.save();
        }
        return rv;
    }

    public void deleteImage(File dir) {
        if(filename != null) {
            File file = new File(dir, filename);
            file.delete();
        }
    }

    public FogOfWar getFogAt(int x, int y) {
        GridPoint gp = new GridPoint(x, y);
        for(FogOfWar fog : fogs) {
            if(fog.contains(gp)) {
                return fog;
            }
        }
        return null;
    }

    public FogOfWar getFogAt(GridPoint gp) {
        for(FogOfWar fog : fogs) {
            if(fog.contains(gp)) {
                return fog;
            }
        }
        return null;
    }

    public int getFogIndexAt(int x, int y) {
        for(int i = 0; i < fogs.size(); i++) {
            if(fogs.get(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public int fogCount() {
        return fogs.size();
    }

    public FogOfWar getFog(int i) {
        return fogs.get(i);
    }

    public boolean addFog(FogOfWar newFog) {
        for(FogOfWar fog : fogs) {
            if(newFog.overlaps(fog)) {
                newFog.removeOverlap(fog);
                if(newFog.pointCount() > 0) {
                    fogs.add(newFog);
                    return true;
                } else {
                    return false;
                }
            }
        }
        fogs.add(newFog);
        return true;
    }

    public void removeFog(FogOfWar fog) {
        fogs.remove(fog);
    }

    public void removeOverlapFrom(FogOfWar selected) {
        for(FogOfWar fog : fogs) {
            if(fog != selected && selected.overlaps(fog)) {
                selected.removeOverlap(fog);
            }
        }
    }

    public Bitmap getBackground() {
        if(background == null && filename != null) {
            try {
                background = BitmapFactory.decodeFile(dir.getAbsolutePath() + File.separator + filename);
            } catch(OutOfMemoryError ex) {
                MapArray.getInstance().unloadOtherBackgounds(this);
                background = BitmapFactory.decodeFile(dir.getAbsolutePath() + File.separator + filename);
            }
        }
        return background;
    }

    public void setBackground(Bitmap background) {
        if(this.background != null) {
            this.background.recycle();
        }
        this.background = background;
        if(filename != null) {
            oldFilename = filename;
            filename = null;
        }
    }

    public void unloadBackground() {
        if(background != null) {
            background.recycle();
            background = null;
        }
    }

    public String[] getFogNames() {
        String[] rv = new String[fogs.size()];
        for(int i = 0; i < rv.length; i++) {
            rv[i] = fogs.get(i).name;
        }
        return rv;
    }
}
