package com.minismap.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/03/24.
 */
public class FogOfWar {

    private static final String sep = ""+((char)31);

    public static FogOfWar load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, sep);
        FogOfWar rv = new FogOfWar(Map.emptyConvert(tokens.nextToken()));
        while(tokens.hasMoreTokens()) {
            rv.points.add(GridPoint.load(tokens.nextToken()));
        }
        return rv;
    }

    public String name;
    private ArrayList<GridPoint> points;
    private ArrayList<Entry> entries;

    private FogOfWar(String name) {
        this.name = name;
        points = new ArrayList<>();
        entries = new ArrayList<>();
    }

    public FogOfWar(String name, int x, int y) {
        this.name = name;
        points = new ArrayList<>();
        points.add(new GridPoint(x, y));
        entries = new ArrayList<>();
    }

    public FogOfWar(String name, int startx, int starty, int endx, int endy) {
        this.name = name;
        points = new ArrayList<>();
        for(int x = startx; x <= endx; x++) {
            for(int y = starty; y <= endy; y++) {
                points.add(new GridPoint(x, y));
            }
        }
        entries = new ArrayList<>();
    }

    public boolean addPoint(int x, int y) {
        GridPoint gp = new GridPoint(x, y);
        if(!points.contains(gp)) {
            points.add(gp);
            return true;
        }
        return false;
    }

    public boolean addPoint(GridPoint gp) {
        if(!points.contains(gp)) {
            points.add(gp);
            return true;
        }
        return false;
    }

    public boolean removePoint(int x, int y) {
        GridPoint gp = new GridPoint(x, y);
        return points.remove(gp);
    }

    public boolean removePoint(GridPoint gp) {
        return points.remove(gp);
    }

    public boolean contains(GridPoint gp) {
        return points.contains(gp);
    }

    public boolean contains(int x, int y) {
        return points.contains(new GridPoint(x, y));
    }

    public void addRectangle(int startx, int starty, int endx, int endy) {
        GridPoint gp;
        for(int x = startx; x <= endx; x++) {
            for(int y = starty; y <= endy; y++) {
                gp = new GridPoint(x, y);
                if(!points.contains(gp)) {
                    points.add(gp);
                }
            }
        }
    }

    public void removeRectangle(int startx, int starty, int endx, int endy) {
        GridPoint gp;
        for(int i = 0; i < points.size(); i++) {
            gp = points.get(i);
            if(gp.x >= startx && gp.x <= endx && gp.y >= starty && gp.y <= endy) {
                points.remove(i);
                i--;
            }
        }
    }

    public int pointCount() {
        return points.size();
    }

    public GridPoint getPoint(int index) {
        return points.get(index);
    }

    public float[] getOutline(int x0, int y0, int boxSize) {
        ArrayList<FloatLine> points = new ArrayList<>();
        FloatLine fp;
        for(GridPoint pt : this.points) {
            //top
            fp = new FloatLine(x0 + pt.x*boxSize, y0 + pt.y*boxSize, x0 + (pt.x+1)*boxSize, y0 + pt.y*boxSize);
            if(points.contains(fp)) {
                points.remove(fp);
            } else {
                points.add(fp);
            }
            //right
            fp = new FloatLine(x0 + (pt.x+1)*boxSize, y0 + pt.y*boxSize, x0 + (pt.x+1)*boxSize, y0 + (pt.y+1)*boxSize);
            if(points.contains(fp)) {
                points.remove(fp);
            } else {
                points.add(fp);
            }
            //bottom
            fp = new FloatLine(x0 + pt.x*boxSize, y0 + (pt.y+1)*boxSize, x0 + (pt.x+1)*boxSize, y0 + (pt.y+1)*boxSize);
            if(points.contains(fp)) {
                points.remove(fp);
            } else {
                points.add(fp);
            }
            //left
            fp = new FloatLine(x0 + pt.x*boxSize, y0 + pt.y*boxSize, x0 + pt.x*boxSize, y0 + (pt.y+1)*boxSize);
            if(points.contains(fp)) {
                points.remove(fp);
            } else {
                points.add(fp);
            }
        }
        float[] rv = new float[points.size()*4];
        for(int i = 0; i < rv.length; i+=4) {
            fp = points.get(i/4);
            rv[i] = fp.x1;
            rv[i+1] = fp.y1;
            rv[i+2] = fp.x2;
            rv[i+3] = fp.y2;
        }
        return rv;
    }

    public boolean overlaps(FogOfWar fog) {
        for(GridPoint gp : fog.points) {
            if(points.contains(gp)) {
                return true;
            }
        }
        return false;
    }

    public void removeOverlap(FogOfWar fog) {
        for(GridPoint gp : fog.points) {
            if(points.contains(gp)) {
                points.remove(gp);
            }
        }
    }

    public String save() {
        String rv = Map.emptyConvert(name);
        for(GridPoint gp : points) {
            rv += sep +gp.x +"," +gp.y;
        }
        return rv;
    }

    public class Entry {

        public ArrayList<GridPoint> starts;
        public ArrayList<GridPoint> ends;
        public String description;
        public String abbreviation;
        public String lockMessage;

        public Entry(String abbreviation, String description, String lockMessage, GridPoint start, GridPoint end) {
            this.abbreviation = abbreviation;
            this.description = description;
            this.lockMessage = lockMessage;
            starts = new ArrayList<>();
            starts.add(start);
            ends = new ArrayList<>();
            ends.add(end);
        }

    }

}
