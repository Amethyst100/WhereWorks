package com.phloxinc.whereworks.database;

public abstract class BaseEntity {
    private long _id = 0;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
