package vn.opdo.model;


import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.opdo.quanlythethanhvien.MainFragment;
import vn.opdo.quanlythethanhvien.ListFragment;

public class FirebaseDB {
    public static boolean IsSave = false;
    public static FirebaseDatabase database;
    public static String account;
    public static MainFragment fragmenMain;
    public static ListFragment fragmentList;
    public static DatabaseReference cardList;
    public static void unloadData()
    {
        if (cardList != null) cardList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void loadData(boolean flag)
    {
        if (flag) loadData();
        else  unloadData();
        IsSave = flag;
    }

    public static void loadData()
    {

        cardList = database.getReference(account + "/card");
        cardList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Card.emptySqlite();
                CardType.emptySqlite();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Card post = item.getValue(Card.class);
                    CardType type = item.child("type").getValue(CardType.class);
                    post.setType(type);
                    type.saveToSqlite(false);
                    post.saveToSqlite(false);
                }
                fragmenMain.addData();
                fragmentList.addData();
                fragmenMain.adapter.notifyDataSetChanged();
                fragmentList.adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }


        });
    }
}
