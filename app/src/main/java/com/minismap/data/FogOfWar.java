package com.minismap.data;

import com.minismap.EmptyObjectException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/03/24.
 */
public class FogOfWar {

    public static FogOfWar load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, Seperators.INSIDE_MAP);
        FogOfWar rv = new FogOfWar(Seperators.emptyConvert(tokens.nextToken()));
        String token;
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            if(token.compareTo(Seperators.NEW_ARRAY_INDICATOR) == 0) {
                break;
            }
            rv.points.add(GridPoint.load(token));
        }
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            if(token.compareTo(Seperators.NEW_ARRAY_INDICATOR) == 0) {
                break;
            }
            rv.entries.add(Entry.load(token));
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

    public boolean removePoint(GridPoint gp) throws EmptyObjectException {
        if(points.size() == 1) {
            throw new EmptyObjectException("Cannot remove last point from a fog. Press the delete button to delete a fog.");
        }
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

    public void removeRectangle(int startx, int starty, int endx, int endy) throws EmptyObjectException {
        GridPoint gp;
        int count = 0;
        for(int i = 0; i < points.size(); i++) {
            gp = points.get(i);
            if(gp.x >= startx && gp.x <= endx && gp.y >= starty && gp.y <= endy) {
                count++;
            }
        }
        if(count == points.size()) {
            throw new EmptyObjectException("Cannot remove all points from a fog. Press the delete button to delete a fog.");
        }
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
        String rv = Seperators.emptyConvert(name);
        for(GridPoint gp : points) {
            rv += Seperators.INSIDE_MAP +gp.x +"," +gp.y;
        }
        rv += Seperators.INSIDE_MAP +Seperators.NEW_ARRAY_INDICATOR;
        for(Entry entry : entries) {
            rv += Seperators.INSIDE_MAP +entry.save();
        }
        return rv;
    }

    public Entry getEntryAt(GridPoint gp) {
        for(Entry entry : entries) {
            if(entry.starts.contains(gp) || entry.ends.contains(gp)) {
                return entry;
            }
        }
        return null;
    }

    public int getEntryIndexAt(int x, int y) {
        GridPoint gp = new GridPoint(x, y);
        for(int i = 0; i < entries.size(); i++) {
            if(entries.get(i).contains(gp)) {
                return i;
            }
        }
        return -1;
    }

    public int entryCount() {
        return entries.size();
    }

    public Entry getEntry(int i) {
        return entries.get(i);
    }

    public void addEntry(String abbreviation, String lockMessage, int startx, int starty, int endx, int endy) {
        entries.add(new Entry(abbreviation, lockMessage, new GridPoint(startx, starty), new GridPoint(endx, endy)));
    }

    public void removeEntry(Entry entry) {
        entries.remove(entry);
    }

}
