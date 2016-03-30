package com.minismap.data;

import com.minismap.EmptyObjectException;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/03/29.
 */
public class Entry {

    public static Entry load(String line) {
        Entry rv = new Entry();
        StringTokenizer tokens = new StringTokenizer(line, Seperators.INSIDE_FOG);
        rv.abbreviation = Seperators.emptyConvert(tokens.nextToken());
        rv.lockMessage = Seperators.emptyConvert(tokens.nextToken());
        String token;
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            if(token.compareTo(Seperators.NEW_ARRAY_INDICATOR) == 0) {
                break;
            }
            rv.starts.add(GridPoint.load(token));
        }
        while(tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            if(token.compareTo(Seperators.NEW_ARRAY_INDICATOR) == 0) {
                break;
            }
            rv.ends.add(GridPoint.load(token));
        }
        return rv;
    }

    public ArrayList<GridPoint> starts;
    public ArrayList<GridPoint> ends;
    public String abbreviation;
    public String lockMessage;

    public Entry(String abbreviation, String lockMessage, GridPoint start, GridPoint end) {
        this.abbreviation = abbreviation;
        this.lockMessage = lockMessage;
        starts = new ArrayList<>();
        starts.add(start);
        ends = new ArrayList<>();
        ends.add(end);
    }

    private Entry() {
        starts = new ArrayList<>();
        ends = new ArrayList<>();
    }

    public void removeStart(GridPoint gp) throws EmptyObjectException {
        if(starts.size() == 1) {
            throw new EmptyObjectException("Cannot remove last start point of an entry.");
        }
        starts.remove(gp);
    }

    public void removeEnd(GridPoint gp) throws EmptyObjectException {
        if(ends.size() == 1) {
            throw new EmptyObjectException("Cannot remove last end point of an entry.");
        }
        ends.remove(gp);
    }

    public void addEnd(GridPoint gp) {
        ends.add(gp);
    }

    public void addStart(GridPoint gp) {
        starts.add(gp);
    }

    public boolean contains(GridPoint gp) {
        return starts.contains(gp) || ends.contains(gp);
    }

    public String save() {
        String rv = Seperators.emptyConvert(abbreviation) +Seperators.INSIDE_FOG +Seperators.emptyConvert(lockMessage);
        for(GridPoint gp : starts) {
            rv += Seperators.INSIDE_FOG +gp.x +"," +gp.y;
        }
        rv += Seperators.INSIDE_FOG +Seperators.NEW_ARRAY_INDICATOR;
        for(GridPoint gp : ends) {
            rv += Seperators.INSIDE_FOG +gp.x +"," +gp.y;
        }
        return rv;
    }
}
