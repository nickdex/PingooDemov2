package com.nick.pingoodemov2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
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
    private static final String TAG = "Processor";
    private static boolean firstRun = false;

    private static final String CONTACT_NAME = "NAME";
    private final String CONTACT_TABLE = "CONTACT";

    private final String MUSIC_TABLE = "MUSIC";
    private final String MUSIC_TITLE = "TITLE";
    private final String IS_NEW_MUSIC = "IS_NEW";

    public final static int OLD = 0;
    public final static int NEW = 1;
    public final static int DELETED = 2;
    public final static int CHANGED = 3;

    private SQLiteDatabase database;
    private Context context;

    public DatabaseUtility(Context context)
    {
        super(context, NAME, null, VERSION);
        this.context = context;

    }

    public static DatabaseUtility newInstance(Context context)
    {
        return new DatabaseUtility(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MUSIC_TABLE + " ( " + MUSIC_TITLE + " TEXT PRIMARY KEY, " + IS_NEW_MUSIC + " INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE + " ( " + CONTACT_NAME + " TEXT PRIMARY KEY, " + IS_NEW_MUSIC + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + MUSIC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
        onCreate(db);
    }


    public void insertList(List<CustomItem> fresh)
    {
        AsyncTask<List<CustomItem>, Void, Void> task = new AsyncTask<List<CustomItem>, Void, Void>()
        {
            @Override
            protected Void doInBackground(List<CustomItem>... params)
            {
                List<CustomItem> fresh = params[0];
                SQLiteDatabase db = getWritableDatabase();
                Cursor cursor = db.query(MUSIC_TABLE, new String[]{MUSIC_TITLE, IS_NEW_MUSIC}, null, null, null, null, null);
                if (cursor != null)
                {
                    if (cursor.getCount() == 0)
                    {
                        for (CustomItem item : fresh)
                        {
                            item.setInfo(NEW);
                            insertItem(item, db);
                        }
                    } else
                    {
                        List<CustomItem> updatedList = getUpdatedItemList(getListFromDatabaseCursorToInsert(cursor), fresh);
                        if (updatedList != null)
                        {
                            for (CustomItem item : updatedList)
                            {
                                insertItem(item, db);
                            }
                        }
                    }
                }

                db.close();
                return null;
            }
        };

        task.execute(fresh);

    }

    public List<CustomItem> getItemListFromDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(MUSIC_TABLE, new String[]{MUSIC_TITLE, IS_NEW_MUSIC}, null, null, null, null, null);
        if (cursor != null)
        {
            return getSortedList(getListFromDatabaseCursorToShow(cursor, db));
        }
        return null;
    }

    private List<CustomItem> getSortedList(List<CustomItem> list) {
        List<CustomItem> tempList = new ArrayList<>();
        for(CustomItem item : list)
        {
            if (item.getInfo() == OLD)
            {
                tempList.add(item);
            }
        }
        if(!tempList.isEmpty())
        {
            list.removeAll(tempList);
            list.addAll(tempList);
        }
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
        List<CustomItem> tempList = new ArrayList<>();
        List<CustomItem> readdedList = new ArrayList<>();
        int flag;
        //New Item
        if (fresh.size() > old.size())
        {
            flag = NEW;
            tempList.addAll(fresh);
            for (CustomItem itemFresh : fresh)
            {
                for (CustomItem itemOld : old)
                {
                    if (itemFresh.getContent().equals(itemOld.getContent()))
                    {
                        tempList.remove(itemFresh);
                        break;
                    }
                }
            }

            for (CustomItem item : tempList)
            {
                item.setInfo(flag);
            }
        }
        //Deleted Item
        else if (fresh.size() < old.size())
        {

            flag = DELETED;
            for (CustomItem itemOld : old)
            {
                for (CustomItem itemFresh : fresh)
                {
                    if (itemFresh.getContent().equals(itemOld.getContent()))
                    {
                        tempList.add(itemOld);
                        break;
                    }
                }
            }
            old.removeAll(tempList);
            for (CustomItem item : old)
            {
                item.setInfo(flag);
            }
            for (CustomItem item : tempList)
            {
                item.setInfo(OLD);
            }
            tempList.addAll(0, old);

        }
//        Changed Item
        else
        {
            flag = CHANGED;
            tempList.addAll(fresh);
            for (CustomItem itemFresh : fresh)
            {
                for (CustomItem itemOld : old)
                {
                    if (itemFresh.getContent().equals(itemOld.getContent()))
                    {
                        if (itemFresh.getInfo() == itemOld.getInfo())
                        {
                            tempList.remove(itemFresh);
                        } else
                        {
                            readdedList.add(itemFresh);
                        }
                        break;
                    }
                }
            }
            if (tempList.isEmpty())
            {
                return null;
            } else
            {
                fresh.removeAll(tempList);
                for (CustomItem item : tempList)
                {
                    item.setInfo(flag);
                }

                if(!readdedList.isEmpty())
                {
                    fresh.removeAll(readdedList);
                    tempList.removeAll(readdedList);
                    for(CustomItem item : readdedList)
                    {
                        item.setInfo(NEW);
                    }
                    tempList.addAll(readdedList);
                }

                for (CustomItem item : fresh)
                {
                    item.setInfo(OLD);
                }
                tempList.addAll(fresh);
            }
        }


        return tempList;
    }

    public List<CustomItem> getListFromDatabaseCursorToInsert(Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            List<CustomItem> list = new ArrayList<>();
            do
            {
                cursor.moveToNext();
                String title = cursor.getString(0);
                int info = cursor.getInt(1);
                CustomItem item = new CustomItem(title, info);
                list.add(item);

            } while (!cursor.isLast());

            return list;
        } else
        {
            return null;
        }
    }


    public List<CustomItem> getListFromDatabaseCursorToShow(Cursor cursor, SQLiteDatabase db)
    {
        if (cursor != null && cursor.getCount() > 0)
        {
            List<CustomItem> list = new ArrayList<>();
            do
            {
                cursor.moveToNext();
                String title = cursor.getString(0);
                int info = cursor.getInt(1);
                CustomItem item = new CustomItem(title, info);
                list.add(item);

                if(item.getInfo() == DELETED) {
                    db.delete(MUSIC_TABLE, IS_NEW_MUSIC +" = ?", new String[]{String.valueOf(item.getInfo())});
                }

            } while (!cursor.isLast());

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
            do
            {
                cursor.moveToNext();
                String title = cursor.getString(0);
                //OLD doesn't matter it will be replaced later when interacting with database
                CustomItem item = new CustomItem(title, OLD);
                list.add(item);
            } while (!cursor.isLast());

            return list;
        } else
        {
            return null;
        }
    }

    private void insertItem(CustomItem item, SQLiteDatabase database)
    {

        ContentValues values = new ContentValues();
        values.put(MUSIC_TITLE, item.getContent());
        values.put(IS_NEW_MUSIC, item.getInfo());

        try
        {
            database.insertOrThrow(MUSIC_TABLE, null, values);
        } catch (SQLiteConstraintException e)
        {
            if(item.getInfo() == NEW){
                values.put(IS_NEW_MUSIC, OLD);
            }
            database.updateWithOnConflict(MUSIC_TABLE, values, MUSIC_TITLE + " = ?", new String[]{item.getContent()}, SQLiteDatabase.CONFLICT_REPLACE);
        }
        Log.i(TAG, item.getContent() + " # " + item.getInfo());
    }
}
