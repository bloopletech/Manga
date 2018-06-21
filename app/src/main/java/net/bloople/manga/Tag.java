package net.bloople.manga;

public class Tag {
    private int _id;
    private String tag;
    private int popularity = 1;

    Tag(int _id, String tag) {
        this._id = _id;
        this.tag = tag;
    }

    public int id() {
        return _id;
    }

    public String tag() {
        return tag;
    }

    public int popularity() {
        return popularity;
    }

    public void popularity(int popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return tag;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Tag)) return super.equals(other);
        return _id == ((Tag)other).id();
    }
}
