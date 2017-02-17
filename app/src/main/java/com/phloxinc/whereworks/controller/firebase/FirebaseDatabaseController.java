package com.phloxinc.whereworks.controller.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseController {

    public static void put(String key, String value) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference(key);
        mRef.setValue(value);
    }
}
