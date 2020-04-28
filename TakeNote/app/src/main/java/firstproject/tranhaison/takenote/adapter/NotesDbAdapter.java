package firstproject.tranhaison.takenote.adapter;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import firstproject.tranhaison.takenote.Note;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 */
public class NotesDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGE = "image";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL, body TEXT NOT NULL, date VARCHAR(30) NOT NULL, image BLOB DEFAULT NULL);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
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
    public long createNote(String title, String body, String date, byte[] image) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_IMAGE, image);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return an ArrayList which contain all the notes of the database
     * @return ArrayList of all notes
     */
    public ArrayList<Note> fetchAllNotes() {
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_IMAGE},
                null, null, null, null, null);

        ArrayList<Note> noteArrayList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setNote(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setImage(cursor.getBlob(4));
                noteArrayList.add(note);
            } while (cursor.moveToNext());
        }

        return noteArrayList;
    }

    /**
     * Return a Note which has the rowId
     * @param rowId id of note to retrieve
     * @return Note of that id, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Note fetchNote(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_IMAGE},
                KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        Note note = new Note();
        note.setId(Long.parseLong(mCursor.getString(0)));
        note.setTitle(mCursor.getString(1));
        note.setNote(mCursor.getString(2));
        note.setDate(mCursor.getString(3));
        note.setImage(mCursor.getBlob(4));
        return note;
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
    public boolean updateNote(long rowId, String title, String body, String date, byte[] image) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE, date);
        args.put(KEY_IMAGE, image);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Check to see if a note exists or not
     * If exists -> return true
     * otherwise return false
     * @param rowId ID of the note
     * @return
     */
    public boolean findNote(long rowId) {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_IMAGE},
                KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (mCursor == null)
            return false;
        else
            return true;
    }
}
