package com.htwberlin.geolist.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.htwberlin.geolist.data.models.MarkerLocation;
import com.htwberlin.geolist.data.models.SharedUser;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.data.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "geoList";

    // Table Names
    private static final String TABLE_LISTS = "lists";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SHARED_LISTS = "sharedLists";
    private static final String TABLE_GPS = "gpsLocation";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_CHANGED_AT = "changed_at";
    private static final String KEY_UUID = "uuid";

    // Task Table - column names
    private static final String KEY_TASK_LIST_ID = "list_id";
    private static final String KEY_TASK_DESCRIPTION = "description";
    private static final String KEY_TASK_COMPLETED = "completed";
    private static final String KEY_TASK_DELETED = "isDeleted";
    private static final String KEY_TASK_COMPLETED_DATE = "completedDate";

    // User Table - column names
    private static final String KEY_USER_DISPLAY_NAME = "displayName";
    private static final String KEY_USER_IDENTIFIER = "identifier";
    private static final String KEY_USER_LAST_SYNC = "lastSync";

    // Location Table - column names
    private static final String KEY_LOCATION_LONGITUDE = "longitude";
    private static final String KEY_LOCATION_LATITUDE = "latitude";
    private static final String KEY_LOCATION_NOTIFY_DATE = "notify";

    // LIST Table - column names
    private static final String KEY_LIST_DISPLAY_NAME = "displayName";
    private static final String KEY_LIST_OWNED = "isOwned";
    private static final String KEY_LIST_DELETED = "isDeleted";
    private static final String KEY_LIST_REMEMBER_DATE = "rememberDate";
    private static final String KEY_LIST_REMEMBER_LOCATION = "rememberLocationId";

    // SHARED LISTS Table - column names
    private static final String KEY_SHARED_USER_ID = "userId";
    private static final String KEY_SHARED_LIST_ID = "listId";

    // GPS Location Table - column names
    private static final String KEY_GPS_LAT = "glat";
    private static final String KEY_GPS_LON = "glon";


    // Table Create Statements
    private static final String CREATE_TABLE_LISTS = "CREATE TABLE "
            + TABLE_LISTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UUID + " TEXT,"
            + KEY_LIST_DISPLAY_NAME + " TEXT,"
            + KEY_LIST_OWNED + " BOOLEAN,"
            + KEY_LIST_DELETED + " BOOLEAN,"
            + KEY_LIST_REMEMBER_DATE + " DATETIME,"
            + KEY_LIST_REMEMBER_LOCATION + " TEXT,"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_CHANGED_AT + " DATETIME"
            + ")";
    // Table Create Statements

    private static final String CREATE_GPS_LOCATION = "CREATE TABLE "
            + TABLE_GPS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_GPS_LAT + " TEXT,"
            + KEY_GPS_LON + " TEXT,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "
            + TABLE_LOCATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UUID + " TEXT,"
            + KEY_LOCATION_LONGITUDE + " TEXT,"
            + KEY_LOCATION_LATITUDE + " TEXT,"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_LOCATION_NOTIFY_DATE + " TEXT"
            + ")";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE "
            + TABLE_TASKS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UUID + " TEXT,"
            + KEY_TASK_LIST_ID + " TEXT,"
            + KEY_TASK_DESCRIPTION + " TEXT,"
            + KEY_TASK_COMPLETED + " INTEGER,"
            + KEY_TASK_COMPLETED_DATE + " TEXT,"
            + KEY_TASK_DELETED + " BOOLEAN,"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_CHANGED_AT + " DATETIME"
            + ")";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UUID + " TEXT,"
            + KEY_USER_DISPLAY_NAME + " TEXT,"
            + KEY_USER_IDENTIFIER + " TEXT,"
            + KEY_USER_LAST_SYNC + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    private static final String CREATE_TABLE_SHARED_LISTS = "CREATE TABLE "
            + TABLE_SHARED_LISTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UUID + " TEXT,"
            + KEY_SHARED_USER_ID + " TEXT,"
            + KEY_SHARED_LIST_ID + " TEXT,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_LISTS);
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_SHARED_LISTS);
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_GPS_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHARED_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS);

        // create new tables
        onCreate(db);
    }


    public void clearAllTables() {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("DELETE FROM " + TABLE_SHARED_LISTS);
        db.execSQL("DELETE FROM " + TABLE_TASKS);
        db.execSQL("DELETE FROM " + TABLE_LOCATIONS);
        db.execSQL("DELETE FROM " + TABLE_USERS);
        db.execSQL("DELETE FROM " + TABLE_LISTS);
        db.execSQL("DELETE FROM " + TABLE_GPS);
    }

    // #######################################
    // ################ LISTS ################
    // #######################################

    public long createList(TaskList list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, list.getUuid().toString());
        values.put(KEY_LIST_DISPLAY_NAME, list.getDisplayName().trim());
        if (list.getRememberByDate() != null) {
            values.put(KEY_LIST_REMEMBER_DATE, getDateTimeStringFromDate(list.getRememberByDate()));
        }
        if (list.getRememberByDate() != null) {
            values.put(KEY_LIST_REMEMBER_LOCATION, list.getRememberByLocation().getId());
        }

        values.put(KEY_LIST_OWNED, list.isOwned());
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_CHANGED_AT, getDateTime());

        // insert row
        long newID = db.insert(TABLE_LISTS, null, values);

        if (list.getTasks() != null) {
            for (Task task : list.getTasks()) {
                createTask(task, newID);
            }
        }

        return newID;
    }

    public TaskList getList(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_LISTS + " WHERE "
                + KEY_ID + " = " + id + "";

        Cursor c = db.rawQuery(selectQuery, null);
        TaskList item = null;
        if (c != null && c.moveToFirst()) {
            item = new TaskList(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
            item.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            item.setDisplayName((c.getString(c.getColumnIndex(KEY_LIST_DISPLAY_NAME))));
            item.setOwned(c.getInt(c.getColumnIndex(KEY_LIST_OWNED)) > 0);
            item.setDeleted(c.getInt(c.getColumnIndex(KEY_LIST_DELETED)) > 0);
            item.setChangesAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CHANGED_AT))));
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
            if (c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)) == null || c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)).equals("")) {
                item.setRememberByLocation(null);
            } else {
                item.setRememberByLocation(getLocation(c.getLong(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION))));
            }
            item.setTasks(getAllTasksFromListWithDeleted(id));
        }
        c.close();
        return item;
    }

    public TaskList getListByUUID(UUID uuid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_LISTS + " WHERE "
                + KEY_UUID + " = '" + uuid.toString() + "'";

        Cursor c = db.rawQuery(selectQuery, null);
        TaskList item = null;
        if (c != null && c.moveToFirst()) {
            item = new TaskList(uuid);
            item.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            item.setDisplayName((c.getString(c.getColumnIndex(KEY_LIST_DISPLAY_NAME))));
            item.setOwned(c.getInt(c.getColumnIndex(KEY_LIST_OWNED)) > 0);
            item.setDeleted(c.getInt(c.getColumnIndex(KEY_LIST_DELETED)) > 0);
            item.setChangesAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CHANGED_AT))));
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
            if (c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)) == null || c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)).equals("")) {
                item.setRememberByLocation(null);
            } else {
                item.setRememberByLocation(getLocation(c.getLong(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION))));
            }
            item.setTasks(getAllTasksFromListWithDeleted(item.getId()));
        }
        c.close();

        return item;
    }

    public ArrayList<TaskList> getAllLists() {
        ArrayList<TaskList> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                TaskList item = new TaskList(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setDisplayName((c.getString(c.getColumnIndex(KEY_USER_DISPLAY_NAME))));
                item.setOwned(c.getInt(c.getColumnIndex(KEY_LIST_OWNED)) > 0);
                item.setDeleted(c.getInt(c.getColumnIndex(KEY_LIST_DELETED)) > 0);
                item.setTasks(getAllTasksFromList((c.getLong(c.getColumnIndex(KEY_ID)))));
                if (c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)) == null || c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)).equals("")) {
                    item.setRememberByLocation(null);
                } else {
                    item.setRememberByLocation(getLocation(c.getLong(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION))));
                }
                if (c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_DATE)) == null || c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_DATE)).equals("")) {
                    item.setRememberByDate(null);
                } else {
                    item.setRememberByDate(getDateFromString(c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_DATE))));
                }
                item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
                if (!item.isDeleted())
                    items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public ArrayList<TaskList> getAllListsWithDeleted() {
        ArrayList<TaskList> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                TaskList item = new TaskList(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setDisplayName((c.getString(c.getColumnIndex(KEY_USER_DISPLAY_NAME))));
                item.setOwned(c.getInt(c.getColumnIndex(KEY_LIST_OWNED)) > 0);
                item.setDeleted(c.getInt(c.getColumnIndex(KEY_LIST_DELETED)) > 0);
                item.setTasks(getAllTasksFromList((c.getLong(c.getColumnIndex(KEY_ID)))));
                if (c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)) == null || c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION)).equals("")) {
                    item.setRememberByLocation(null);
                } else {
                    item.setRememberByLocation(getLocation(c.getLong(c.getColumnIndex(KEY_LIST_REMEMBER_LOCATION))));
                }
                if (c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_DATE)) == null || c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_DATE)).equals("")) {
                    item.setRememberByDate(null);
                } else {
                    item.setRememberByDate(getDateFromString(c.getString(c.getColumnIndex(KEY_LIST_REMEMBER_DATE))));
                }
                item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public int updateList(TaskList item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST_DISPLAY_NAME, item.getDisplayName());
        values.put(KEY_CHANGED_AT, getDateTime());
        values.put(KEY_LIST_OWNED, item.isOwned());
        values.put(KEY_LIST_DELETED, item.isDeleted());

        if (item.getRememberByDate() != null) {
            values.put(KEY_LIST_REMEMBER_DATE, getDateTimeStringFromDate(item.getRememberByDate()));
        } else {
            values.put(KEY_LIST_REMEMBER_DATE, "");
        }

        if (item.getRememberByLocation() != null) {
            if (item.getRememberByLocation().getId() > 0) {
                values.put(KEY_LIST_REMEMBER_LOCATION, item.getRememberByLocation().getId());
            } else {
                long id = this.createLocation(item.getRememberByLocation().getUuid(), item.getRememberByLocation().getLongitude(), item.getRememberByLocation().getLatitude());
                values.put(KEY_LIST_REMEMBER_LOCATION, id);
            }

        } else {
            values.put(KEY_LIST_REMEMBER_LOCATION, "");
        }

        // updating row
        return db.update(TABLE_LISTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }


    public void deleteList(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        TaskList item = getList(id);
        values.put(KEY_CHANGED_AT, getDateTime());
        values.put(KEY_LIST_DELETED, true);

        // updating row
        db.update(TABLE_LISTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // #######################################
    // ################ USERS ################
    // #######################################

    public long createUser(String signature, String displayName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, signature);
        values.put(KEY_USER_DISPLAY_NAME, displayName.trim());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        return db.insert(TABLE_USERS, null, values);
    }

    public User getUser(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        User item = null;

        if (c != null && c.moveToFirst()) {
            item = new User(c.getString(c.getColumnIndex(KEY_UUID)));
            item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            item.setDisplayName((c.getString(c.getColumnIndex(KEY_USER_DISPLAY_NAME))));
            item.setLastSync(c.getLong(c.getColumnIndex(KEY_USER_LAST_SYNC)));
        }
        c.close();
        return item;
    }

    public User getUser(String signature) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_UUID + " = '" + signature + "'";

        Cursor c = db.rawQuery(selectQuery, null);
        User item = null;

        if (c != null && c.moveToFirst()) {
            item = new User(c.getString(c.getColumnIndex(KEY_UUID)));
            item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            item.setDisplayName((c.getString(c.getColumnIndex(KEY_USER_DISPLAY_NAME))));
            item.setLastSync(c.getLong(c.getColumnIndex(KEY_USER_LAST_SYNC)));
        }
        c.close();
        return item;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                User item = new User(c.getString(c.getColumnIndex(KEY_UUID)));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setDisplayName((c.getString(c.getColumnIndex(KEY_USER_DISPLAY_NAME))));
                item.setLastSync(c.getLong(c.getColumnIndex(KEY_USER_LAST_SYNC)));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }


    public int updateUser(User item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_DISPLAY_NAME, item.getDisplayName());

        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public void deleteUser(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }


    // #######################################
    // ############## LOCATIONS ##############
    // #######################################

    public long createLocation(UUID uuid, double longitude, double latitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, uuid.toString());
        values.put(KEY_LOCATION_LONGITUDE, longitude);
        values.put(KEY_LOCATION_LATITUDE, latitude);
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        return db.insert(TABLE_LOCATIONS, null, values);
    }

    public MarkerLocation getLocation(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        MarkerLocation item = null;
        if (c != null && c.moveToFirst()) {
            item = new MarkerLocation(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
            item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            item.setLongitude((c.getDouble(c.getColumnIndex(KEY_LOCATION_LONGITUDE))));
            item.setLatitude((c.getDouble(c.getColumnIndex(KEY_LOCATION_LATITUDE))));
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
            item.setLastNotification(getDateFromString(c.getString(c.getColumnIndex(KEY_LOCATION_NOTIFY_DATE))));
            c.close();
        }
        return item;
    }

    public MarkerLocation getLocation(UUID id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS + " WHERE "
                + KEY_UUID + " = '" + id.toString() + "'";

        Cursor c = db.rawQuery(selectQuery, null);

        MarkerLocation item = null;
        if (c != null && c.moveToFirst()) {
            item = new MarkerLocation(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
            item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            item.setLongitude((c.getDouble(c.getColumnIndex(KEY_LOCATION_LONGITUDE))));
            item.setLatitude((c.getDouble(c.getColumnIndex(KEY_LOCATION_LATITUDE))));
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
            item.setLastNotification(getDateFromString(c.getString(c.getColumnIndex(KEY_LOCATION_NOTIFY_DATE))));
            c.close();
        }
        return item;
    }

    public ArrayList<MarkerLocation> getAllLocations() {
        ArrayList<MarkerLocation> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                MarkerLocation item = new MarkerLocation(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setLongitude((c.getDouble(c.getColumnIndex(KEY_LOCATION_LONGITUDE))));
                item.setLatitude((c.getDouble(c.getColumnIndex(KEY_LOCATION_LATITUDE))));
                item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
                item.setLastNotification(getDateFromString(c.getString(c.getColumnIndex(KEY_LOCATION_NOTIFY_DATE))));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public int updateLocation(MarkerLocation item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_LONGITUDE, item.getLongitude());
        values.put(KEY_LOCATION_LATITUDE, item.getLatitude());
        if (item.getLastNotification() != null) {
            values.put(KEY_LOCATION_NOTIFY_DATE, getDateTime());
        } else {
            values.put(KEY_LOCATION_NOTIFY_DATE, "");
        }

        // updating row
        return db.update(TABLE_LOCATIONS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public void deleteLocation(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }


    // #######################################
    // ################ TASKS ################
    // #######################################

    public long createTask(Task item, long listID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, item.getUuid().toString());
        values.put(KEY_TASK_LIST_ID, listID);
        values.put(KEY_TASK_DESCRIPTION, item.getDescription());
        values.put(KEY_TASK_COMPLETED, item.isCompleted());
        values.put(KEY_TASK_COMPLETED_DATE, "");
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        return db.insert(TABLE_TASKS, null, values);
    }

    public Task getTask(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        Task item = null;
        if (c != null && c.moveToFirst()) {
            item = new Task(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
            item.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            item.setTaskListId(c.getLong(c.getColumnIndex(KEY_TASK_LIST_ID)));
            item.setCompleted(intToBool((c.getInt(c.getColumnIndex(KEY_TASK_COMPLETED)))));
            item.setCompletedDate(getDateFromString((c.getString(c.getColumnIndex(KEY_TASK_COMPLETED_DATE)))));
            item.setDescription((c.getString(c.getColumnIndex(KEY_TASK_DESCRIPTION))));
            item.setDeleted(c.getInt(c.getColumnIndex(KEY_TASK_DELETED)) > 0);
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
        }
        c.close();
        return item;
    }

    public Task getTaskByUUID(UUID uuid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " WHERE "
                + KEY_UUID + " = '" + uuid.toString() + "'";

        Cursor c = db.rawQuery(selectQuery, null);
        Task item = null;
        if (c != null && c.moveToFirst()) {
            item = new Task(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
            item.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            item.setTaskListId(c.getLong(c.getColumnIndex(KEY_TASK_LIST_ID)));
            item.setCompleted(intToBool((c.getInt(c.getColumnIndex(KEY_TASK_COMPLETED)))));
            item.setCompletedDate(getDateFromString((c.getString(c.getColumnIndex(KEY_TASK_COMPLETED_DATE)))));
            item.setDescription((c.getString(c.getColumnIndex(KEY_TASK_DESCRIPTION))));
            item.setDeleted(c.getInt(c.getColumnIndex(KEY_TASK_DELETED)) > 0);
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
        }
        c.close();
        return item;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task item = new Task(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setTaskListId(c.getLong(c.getColumnIndex(KEY_TASK_LIST_ID)));
                item.setCompleted(intToBool((c.getInt(c.getColumnIndex(KEY_TASK_COMPLETED)))));
                item.setCompletedDate(getDateFromString((c.getString(c.getColumnIndex(KEY_TASK_COMPLETED_DATE)))));
                item.setDescription((c.getString(c.getColumnIndex(KEY_TASK_DESCRIPTION))));
                item.setDeleted(c.getInt(c.getColumnIndex(KEY_TASK_DELETED)) > 0);
                item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
                if (!item.isDeleted())
                    items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public ArrayList<Task> getAllTasksFromList(long listID) {
        ArrayList<Task> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_LIST_ID + " = " + listID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task item = new Task(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setTaskListId(c.getLong(c.getColumnIndex(KEY_TASK_LIST_ID)));
                item.setCompleted(intToBool((c.getInt(c.getColumnIndex(KEY_TASK_COMPLETED)))));
                item.setCompletedDate(getDateFromString((c.getString(c.getColumnIndex(KEY_TASK_COMPLETED_DATE)))));
                item.setDescription((c.getString(c.getColumnIndex(KEY_TASK_DESCRIPTION))));
                item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
                item.setDeleted(c.getInt(c.getColumnIndex(KEY_TASK_DELETED)) > 0);
                if (!item.isDeleted())
                    items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public ArrayList<Task> getAllTasksFromListWithDeleted(long listID) {
        ArrayList<Task> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS + " WHERE " + KEY_TASK_LIST_ID + " = " + listID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Task item = new Task(UUID.fromString(c.getString(c.getColumnIndex(KEY_UUID))));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setTaskListId(c.getLong(c.getColumnIndex(KEY_TASK_LIST_ID)));
                item.setCompleted(intToBool((c.getInt(c.getColumnIndex(KEY_TASK_COMPLETED)))));
                item.setCompletedDate(getDateFromString((c.getString(c.getColumnIndex(KEY_TASK_COMPLETED_DATE)))));
                item.setDescription((c.getString(c.getColumnIndex(KEY_TASK_DESCRIPTION))));
                item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
                item.setDeleted(c.getInt(c.getColumnIndex(KEY_TASK_DELETED)) > 0);
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public int updateTask(Task item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_DESCRIPTION, item.getDescription());
        values.put(KEY_TASK_COMPLETED, item.isCompleted());
        values.put(KEY_TASK_DELETED, item.isDeleted());
        if (item.isCompleted()) {
            values.put(KEY_TASK_COMPLETED, true);
            values.put(KEY_TASK_COMPLETED_DATE, getDateTime());
        } else {
            values.put(KEY_TASK_COMPLETED, false);
            values.put(KEY_TASK_COMPLETED_DATE, "");
        }

        values.put(KEY_CHANGED_AT, getDateTime());

        // updating row
        return db.update(TABLE_TASKS, values, KEY_UUID + " = ?",
                new String[]{String.valueOf(item.getUuid().toString())});
    }


    public int updateTask(Task item, UUID id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_DESCRIPTION, item.getDescription());
        values.put(KEY_TASK_COMPLETED, item.isCompleted());
        values.put(KEY_TASK_DELETED, item.isDeleted());
        if (item.isCompleted()) {
            values.put(KEY_TASK_COMPLETED, true);
            values.put(KEY_TASK_COMPLETED_DATE, getDateTime());
        } else {
            values.put(KEY_TASK_COMPLETED, false);
            values.put(KEY_TASK_COMPLETED_DATE, "");
        }

        values.put(KEY_CHANGED_AT, getDateTime());

        // updating row
        return db.update(TABLE_TASKS, values, KEY_UUID + " = ?",
                new String[]{item.getUuid().toString()});
    }

    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Task item = getTask(id);
        values.put(KEY_CHANGED_AT, getDateTime());
        values.put(KEY_TASK_DELETED, true);

        // updating row
        db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }


    // #######################################
    // ############### SHARED ################
    // #######################################

    public long saveShare(TaskList list, User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, user.getSignature());
        values.put(KEY_SHARED_LIST_ID, list.getId());
        values.put(KEY_SHARED_USER_ID, user.getId());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        return db.insert(TABLE_SHARED_LISTS, null, values);
    }

    public void removeShare(TaskList list, User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SHARED_LISTS, KEY_SHARED_LIST_ID + " = ? AND " + KEY_SHARED_USER_ID + " = ?",
                new String[]{String.valueOf(list.getId()), String.valueOf(user.getId())});
    }

    public ArrayList<SharedUser> getSharedUsers() {
        ArrayList<SharedUser> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHARED_LISTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                SharedUser item = new SharedUser(c.getString(c.getColumnIndex(KEY_UUID)));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setListId(c.getLong(c.getColumnIndex(KEY_SHARED_LIST_ID)));
                item.setUserId(c.getLong(c.getColumnIndex(KEY_SHARED_USER_ID)));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public ArrayList<SharedUser> getSharedUsersInList(UUID listId) {
        ArrayList<SharedUser> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHARED_LISTS + " WHERE " + KEY_SHARED_LIST_ID + " = '" + listId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                SharedUser item = new SharedUser(c.getString(c.getColumnIndex(KEY_UUID)));
                item.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                item.setListId(c.getLong(c.getColumnIndex(KEY_SHARED_LIST_ID)));
                item.setUserId(c.getLong(c.getColumnIndex(KEY_SHARED_USER_ID)));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return items;
    }

    public void setCurrentLocation(double lat, double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        cleanGPSTable();
        ContentValues values = new ContentValues();
        values.put(KEY_GPS_LAT, String.valueOf(lat));
        values.put(KEY_GPS_LON, String.valueOf(lon));
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        db.insert(TABLE_GPS, null, values);
    }

    public MarkerLocation getCurrentLocation() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_GPS + " LIMIT 1";

        Cursor c = db.rawQuery(selectQuery, null);
        MarkerLocation item = new MarkerLocation(UUID.randomUUID());
        item.setLongitude(0);
        item.setLatitude(0);
        if (c != null && c.moveToFirst()) {
            item.setId(c.getLong(c.getColumnIndex(KEY_ID)));
            item.setLatitude(c.getDouble(c.getColumnIndex(KEY_GPS_LAT)));
            item.setLongitude(c.getDouble(c.getColumnIndex(KEY_GPS_LON)));
            item.setCreatedAt(getDateFromString(c.getString(c.getColumnIndex(KEY_CREATED_AT))));
        }
        c.close();
        return item;
    }

    public void cleanGPSTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GPS);
    }

    private boolean intToBool(int integer) {
        return integer > 0;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTimeStringFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date getDateFromString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException | NullPointerException e) {
            //e.printStackTrace();
        }
        return null;
    }

}