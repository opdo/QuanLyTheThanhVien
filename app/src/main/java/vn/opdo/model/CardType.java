package vn.opdo.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;

public class CardType implements Serializable {
    private long id;
    private String typeName;


    public CardType() {}
    public CardType(long id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static ArrayList<CardType> getList()
    {
        ArrayList<CardType> lst = new ArrayList<>();
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARDTYPE", null);
        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            CardType kh = new CardType(id, name);
            lst.add(kh);
        }
        cursor.close();

        return lst;
    }
    public static void emptySqlite()
    {
        sqlitedb.database.delete("CARDTYPE", "1=1", null);
    }

    public boolean saveToSqlite()
    {
        return saveToSqlite(true);
    }

    public boolean saveToSqlite(boolean detectNew)
    {
        ContentValues value = new ContentValues();
        value.put("name", toTitleCase(typeName));

        if (!isExistID(getId()))
        {
            // add
            if (id < 1) id =  System.nanoTime();
            value.put("id", id);
            try
            {
                long ketQua = sqlitedb.database.insert("CARDTYPE", null, value);
                if(FirebaseDB.IsSave && detectNew) FirebaseDB.database.getReference(FirebaseDB.account + "/cardType/" + this.getId()).setValue(this);
                if (ketQua != -1) {

                    return true;
                }
            }
            catch (Exception e)
            {
                return  false;
            }
        }
        else
        {

            // save
            value.put("id", id);
            long ketQua = sqlitedb.database.update("CARDTYPE", value, "id="+id, null);
            if(FirebaseDB.IsSave && detectNew) FirebaseDB.database.getReference(FirebaseDB.account + "/cardType/" + this.getId()).setValue(this);
            if (ketQua > 0) return true;
        }



        return  false;
    }

    private boolean isExistID(long id) {
        int count = 0;
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARDTYPE where id = "+ id, null);
        while (cursor.moveToNext())
        {
            count++;
            break;
        }
        cursor.close();
        return count > 0;
    }

    private boolean isExistID(String id) {
        int count = 0;
        Cursor cursor = sqlitedb.database.rawQuery("select * from CARDTYPE where name = '"+ id + "'", null);
        while (cursor.moveToNext())
        {
            count++;
            break;
        }
        cursor.close();
        return count > 0;
    }

    public static CardType getCardTypeByName(String id) {
        for (CardType item: getList()) {
            if (item.getTypeName().toLowerCase().equalsIgnoreCase(id.toLowerCase()))
            {
                return item;
            }
        }

        // ko có, tạo mới
        CardType ctNew = new CardType(0, id);
        ctNew.saveToSqlite();
        return ctNew;
    }

    public String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public void deleteFromSqlite()
    {
        sqlitedb.database.delete("CARDTYPE", "id="+id, null);
        if(FirebaseDB.IsSave) FirebaseDB.database.getReference(FirebaseDB.account + "/cardType/" + this.getId()).setValue(null);

    }
}
