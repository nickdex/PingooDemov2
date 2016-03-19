package com.nick.pingoodemov2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 18/3/16.
 */
public class DatabaseUtility extends SQLiteOpenHelper
{
    private static final String NAME = "ping.db";

    private static final int VERSION = 1;
    private static final String TAG = "DatabaseUtility";

    private final String CONTACT_TABLE = "CONTACT";
    private final String MUSIC_TABLE = "MUSIC";

    private final String ID = "_ID";
    private final String TITLE = "NAME";
    private final String IS_NEW = "IS_NEW";

    public final static int OLD = 0;
    public final static int NEW = 1;
    public final static int DELETED = 2;
    public final static int CHANGED = 3;

    public DatabaseUtility(Context context)
    {
        super(context, NAME, null, VERSION);

    }

    public static DatabaseUtility newInstance(Context context)
    {
        return new DatabaseUtility(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MUSIC_TABLE + " ( " + ID + " TEXT PRIMARY KEY, " + TITLE + " TEXT, " + IS_NEW + " INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE + " ( " + ID + " TEXT PRIMARY KEY, " + TITLE + " TEXT, " + IS_NEW + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + MUSIC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
        onCreate(db);
    }

    public void insertContactList(List<CustomItem> fresh)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(CONTACT_TABLE, new String[]{ID, TITLE, IS_NEW}, null, null, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() == 0)
            {
                for (CustomItem item : fresh)
                {
                    item.setInfo(NEW);
                    insertContactItem(item, db);
                }
            } else
            {
                List<CustomItem> updatedList = getUpdatedItemList(getListFromDatabaseCursorToInsert(cursor), fresh);
                if (updatedList != null)
                {
                    for (CustomItem item : updatedList)
                    {
                        insertContactItem(item, db);
                    }
                }
            }
        }

        db.close();
    }


    public void insertMusicList(List<CustomItem> fresh)
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(MUSIC_TABLE, new String[]{ID, TITLE, IS_NEW}, null, null, null, null, null);

        if (cursor != null)
        {
            if (cursor.getCount() == 0)
            {
                for (CustomItem item : fresh)
                {
                    item.setInfo(NEW);
                    insertMusicItem(item, db);
                }
            } else
            {
                List<CustomItem> updatedList = getUpdatedItemList(getListFromDatabaseCursorToInsert(cursor), fresh);
                if (updatedList != null)
                {
                    for (CustomItem item : updatedList)
                    {
                        insertMusicItem(item, db);
                    }
                }
            }
        }

        db.close();
    }

    public List<CustomItem> getItemListFromDatabase(String Tag)
    {
        SQLiteDatabase db = getWritableDatabase();
        String table;
        Cursor cursor;
        switch (Tag)
        {
            case MusicFragment.TAG:
                table = MUSIC_TABLE;
                break;
            case ContactFragment.TAG:
                table = CONTACT_TABLE;
                break;
            default:
                table = null;
        }
        cursor = db.query(table, new String[]{ID, TITLE, IS_NEW}, null, null, null, null, null);

        if (cursor != null)
        {
            return getSortedList(getListFromDatabaseCursorToShow(cursor, db, table));
        }

        db.close();
        return null;
    }

    private List<CustomItem> getSortedList(List<CustomItem> list)
    {
        List<CustomItem> tempList = new ArrayList<>();
        for (CustomItem item : list)
        {
            if (item.getInfo() == OLD)
            {
                tempList.add(item);
            }
        }

        list.removeAll(tempList);
        list.addAll(tempList);

        return list;
    }

    /**
     * Heart of logic
     *
     * @param old
     * @param fresh
     * @return
     */
    public List<CustomItem> getUpdatedItemList(List<CustomItem> old, List<CustomItem> fresh)
    {
        List<CustomItem> oldItems = new ArrayList<>();
        List<CustomItem> changedItems = new ArrayList<>();

        //New Item or Changed
        if (fresh.size() >= old.size())
        {
//            flag = NEW;
            for (CustomItem itemFresh : fresh)
            {
                for (CustomItem itemOld : old)
                {
                    if (itemFresh.getId().equals(itemOld.getId()))
                    {
                        if (itemFresh.getContent().equals(itemOld.getContent()))
                        {
                            oldItems.add(itemFresh);
                        } else
                        {
                            changedItems.add(itemFresh);
                        }
                        break;
                    }
                }
            }

            fresh.removeAll(oldItems);
            fresh.removeAll(changedItems);
            //Now fresh contains only new Items
            for (CustomItem item : oldItems)
            {
                item.setInfo(OLD);
            }
            for (CustomItem item : fresh)
            {
                item.setInfo(NEW);
            }
            for (CustomItem item : changedItems)
            {
                item.setInfo(CHANGED);
            }

            oldItems.addAll(0, changedItems);
            oldItems.addAll(0, fresh);
        }
        //Deleted Item
        else if (fresh.size() < old.size())
        {
            for (CustomItem itemOld : old)
            {
                for (CustomItem itemFresh : fresh)
                {
                    if (itemFresh.getId().equals(itemOld.getId()))
                    {
                        oldItems.add(itemOld);
                        break;
                    }
                }
            }

            old.removeAll(oldItems);

            for (CustomItem item : old)
            {
                item.setInfo(DELETED);
            }
            for (CustomItem item : oldItems)
            {
                item.setInfo(OLD);
            }
            oldItems.addAll(0, old);

        }

        return oldItems;
    }

    public List<CustomItem> getListFromDatabaseCursorToInsert(Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            List<CustomItem> list = new ArrayList<>();
            while (cursor.moveToNext())
            {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                int info = cursor.getInt(2);
                CustomItem item = new CustomItem(id, title, info);
                list.add(item);
            }

            return list;
        } else
        {
            return null;
        }
    }


    public List<CustomItem> getListFromDatabaseCursorToShow(Cursor cursor, SQLiteDatabase db, String table)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            List<CustomItem> list = new ArrayList<>();
            while (cursor.moveToNext())
            {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                int info = cursor.getInt(2);
                CustomItem item = new CustomItem(id, title, info);
                list.add(item);

                if (info == DELETED)
                {
                    db.delete(table, IS_NEW + " = ?", new String[]{String.valueOf(info)});
                }
            }

            return list;
        } else
        {
            return null;
        }
    }

    public List<CustomItem> getListFromCursor(Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            List<CustomItem> list = new ArrayList<>();
            while (cursor.moveToNext())
            {

                String id = cursor.getString(0);
                String title = cursor.getString(1);
                //OLD doesn't matter it will be replaced later when interacting with database
                CustomItem item = new CustomItem(id, title, OLD);
                list.add(item);
            }

            if (!list.isEmpty())
            {
                return list;
            }

            cursor.close();
        }

        return null;

    }

    private void insertContactItem(CustomItem item, SQLiteDatabase database)
    {

        ContentValues values = new ContentValues();
        values.put(ID, item.getId());
        values.put(TITLE, item.getContent());
        values.put(IS_NEW, item.getInfo());

        try
        {
            database.insertOrThrow(CONTACT_TABLE, null, values);
        } catch (SQLiteConstraintException e)
        {
            //Redundancy check
            if (item.getInfo() == NEW)
            {
                values.put(IS_NEW, OLD);
                Log.w(TAG, "Please check " + item.toString());
            }
            database.updateWithOnConflict(CONTACT_TABLE, values, ID + " = ?", new String[]{item.getId()}, SQLiteDatabase.CONFLICT_REPLACE);
        }
        Log.i(TAG, item.toString());
    }

    private void insertMusicItem(CustomItem item, SQLiteDatabase database)
    {

        ContentValues values = new ContentValues();
        values.put(ID, item.getId());
        values.put(TITLE, item.getContent());
        values.put(IS_NEW, item.getInfo());

        try
        {
            database.insertOrThrow(MUSIC_TABLE, null, values);
        } catch (SQLiteConstraintException e)
        {
            //Redundancy check
            if (item.getInfo() == NEW)
            {
                values.put(IS_NEW, OLD);
                Log.w(TAG, "Please check " + item.toString());
            }
            database.updateWithOnConflict(MUSIC_TABLE, values, ID + " = ?", new String[]{item.getId()}, SQLiteDatabase.CONFLICT_REPLACE);
        }
        Log.i(TAG, item.toString());
    }
}
