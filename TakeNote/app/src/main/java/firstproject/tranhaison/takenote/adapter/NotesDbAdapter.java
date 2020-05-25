package firstproject.tranhaison.takenote.adapter;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import firstproject.tranhaison.takenote.helper.Folder;
import firstproject.tranhaison.takenote.helper.Note;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 */
public class NotesDbAdapter {

    // Database name and version
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 2;

    // Tables name
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_FOLDERS = "folders";
    private static final String TABLE_NOTE_FOLDER = "note_folder";

    // NOTES table - column name
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGE = "image";

    // FOLDERS table - column name
    public static final String KEY_FOLDER_ID = "folder_id";
    public static final String KEY_FOLDER_NAME = "folder_name";

    // NOTE_FOLDER table - column name
    public static final String KEY_NOTE_FOLDER_ID = "note_folder_id";

    // Init new instance
    private static final String TAG = "NotesDbAdapter";
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    // NOTES table create statement
    private static final String CREATE_TABLE_NOTES =
            "CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, body TEXT NOT NULL, date VARCHAR(30) NOT NULL, image TEXT NOT NULL);";

    // FOLDERS table create statement
    private static final String CREATE_TABLE_FOLDERS =
            "CREATE TABLE " + TABLE_FOLDERS + "("
            + KEY_FOLDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_FOLDER_NAME + " TEXT NOT NULL" + ")";

    // NOTE_FOLDER table create statement
    private static final String CREATE_TABLE_NOTE_FOLDER =
            "CREATE TABLE " + TABLE_NOTE_FOLDER + "("
            + KEY_NOTE_FOLDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ROWID + " INTEGER NOT NULL, "
            + KEY_FOLDER_ID + " INTEGER NOT NULL" + ")";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_NOTES);
            db.execSQL(CREATE_TABLE_FOLDERS);
            db.execSQL(CREATE_TABLE_NOTE_FOLDER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            // onUpgrade drop old tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_FOLDER);
            // create new ones
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body the body of the note
     * @param date the date of the note
     * @param image the image of the note (if exists)
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body, String date, String image) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_IMAGE, image);
        return mDb.insert(TABLE_NOTES, null, initialValues);
    }

    /**
     * Put the value into table FOLDERS
     * @param folder_name
     * @return
     */
    public long createFolder(String folder_name) {
        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_NAME, folder_name);
        return mDb.insert(TABLE_FOLDERS, null, values);
    }

    /**
     * Put the id of note and id of folder into table NOTE_FOLDER
     * @param note_id
     * @param folder_id
     * @return
     */
    public long createNoteFolder(long note_id, long folder_id) {
        ContentValues values = new ContentValues();
        values.put(KEY_ROWID, note_id);
        values.put(KEY_FOLDER_ID, folder_id);
        return mDb.insert(TABLE_NOTE_FOLDER, null, values);
    }

    /**
     * Delete the note with the given rowId
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {
        return mDb.delete(TABLE_NOTES, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Delete the folder and all the notes in it (or not)
     * @param folder
     * @param deleteAllNotesInFolder
     * @return
     */
    public boolean deleteFolder(Folder folder, boolean deleteAllNotesInFolder) {
        if (deleteAllNotesInFolder) {
            ArrayList<Note> noteArrayList = fetchAllNotesInFolder(folder.getName());
            for (Note note : noteArrayList) {
                deleteNote(note.getId());
            }
        }
        return mDb.delete(TABLE_FOLDERS, KEY_FOLDER_ID + "=" + folder.getId(), null) > 0;
    }

    /**
     * Delete the connection between note and folder when the note is not belonged to a folder anymore
     * @param note_folder_id
     * @return
     */
    public boolean deleteNoteFolder(long note_folder_id) {
        return mDb.delete(TABLE_NOTE_FOLDER, KEY_NOTE_FOLDER_ID + "=" + note_folder_id, null) > 0;
    }

    /**
     * Return a Note which has the rowId
     * @param rowId id of note to retrieve
     * @return Note of that id, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Note fetchNote(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, TABLE_NOTES, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_IMAGE},
                KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        Note note = new Note();
        note.setId(Long.parseLong(mCursor.getString(0)));
        note.setTitle(mCursor.getString(1));
        note.setNote(mCursor.getString(2));
        note.setDate(mCursor.getString(3));
        note.setImage(mCursor.getString(4));
        return note;
    }

    /**
     * Return an ArrayList which contain all the notes of the database
     * @return ArrayList of all notes
     */
    public ArrayList<Note> fetchAllNotes() {
        Cursor cursor = mDb.query(TABLE_NOTES, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_IMAGE},
                null, null, null, null, null);

        ArrayList<Note> noteArrayList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setNote(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setImage(cursor.getString(4));
                noteArrayList.add(note);
            } while (cursor.moveToNext());
        }

        return noteArrayList;
    }

    /**
     * Get all notes in one specific folder
     * @param folder_name
     * @return
     */
    public ArrayList<Note> fetchAllNotesInFolder(String folder_name) {
        String query = "SELECT * FROM "
                + TABLE_NOTES + " tn, "
                + TABLE_FOLDERS + " tf, "
                + TABLE_NOTE_FOLDER + " tnf "
                + "WHERE " + "tn." + KEY_ROWID + "=" + "tnf." + KEY_ROWID
                + " AND " + "tf." + KEY_FOLDER_ID + "=" + "tnf." + KEY_FOLDER_ID
                + " AND " + "tf." + KEY_FOLDER_NAME + "= '" + folder_name + "'";

        Cursor cursor = mDb.rawQuery(query, null);

        ArrayList<Note> noteArrayList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setNote(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setImage(cursor.getString(4));
                noteArrayList.add(note);
            } while (cursor.moveToNext());
        }

        return noteArrayList;
    }

    /**
     * Get all folders
     * @return
     */
    public ArrayList<Folder> fetchAllFolders() {
        Cursor cursor = mDb.query(TABLE_FOLDERS, new String[] {KEY_FOLDER_ID, KEY_FOLDER_NAME},
                null, null, null, null, null);

        ArrayList<Folder> folderArrayList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Folder folder = new Folder();
                folder.setId(Integer.parseInt(cursor.getString(0)));
                folder.setName(cursor.getString(1));
                folderArrayList.add(folder);
            } while (cursor.moveToNext());
        }
        return folderArrayList;
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title, body and date
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @param date value to set note date to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body, String date, String image) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE, date);
        args.put(KEY_IMAGE, image);
        return mDb.update(TABLE_NOTES, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Update the folder with the folder's ID
     * @param folder_id
     * @param folder_name
     * @return
     */
    public boolean updateFolder(long folder_id, String folder_name) {
        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_NAME, folder_name);
        return mDb.update(TABLE_FOLDERS, values, KEY_FOLDER_ID + "=" + folder_id, null) > 0;
    }

    /**
     * Update the
     * @param note_folder_id
     * @param folder_id
     * @return
     */
    public boolean updateNoteFolder(long note_folder_id, long folder_id) {
        ContentValues values = new ContentValues();
        values.put(KEY_FOLDER_ID, folder_id);
        return mDb.update(TABLE_NOTE_FOLDER, values, KEY_NOTE_FOLDER_ID + "=" + note_folder_id, null) > 0;
    }
}
