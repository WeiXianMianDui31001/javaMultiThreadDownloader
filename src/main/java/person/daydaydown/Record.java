package person.daydaydown;

import java.io.Serializable;

public class Record implements Serializable {
    public long start;
    public long end;
    public long current;
    public Stat state;
    public String url;
    Record(long start, long end, long current, Stat state,String url) {
        this.start = start;
        this.end = end;
        this.current = current;
        this.state = state;
        this.url = url;
    }
}

enum Stat {
    DONE,NOT_DONE;
}

