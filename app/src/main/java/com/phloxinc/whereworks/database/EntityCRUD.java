package com.phloxinc.whereworks.database;

import java.util.ArrayList;

public interface EntityCRUD {

    void delete(BaseEntity entity);

    void update(BaseEntity entity);

    void insert(BaseEntity entity);

    ArrayList<? extends BaseEntity> fetch(BaseEntity entity);

    ArrayList<? extends BaseEntity> fetchAll(Class<? extends BaseEntity> clazz);
}
