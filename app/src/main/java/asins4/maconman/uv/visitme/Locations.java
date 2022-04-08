package asins4.maconman.uv.visitme;

import java.io.Serializable;

public class Locations implements Serializable {
    String location;
    String extra;
    int visited;

    public Locations(String location, String extra, int visited) {
        this.location = location;
        this.extra = extra;
        this.visited = visited;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }
}
