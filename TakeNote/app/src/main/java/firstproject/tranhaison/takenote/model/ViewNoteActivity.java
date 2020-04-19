package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import firstproject.tranhaison.takenote.Note;
import firstproject.tranhaison.takenote.NoteAdapter;
import firstproject.tranhaison.takenote.NotesDbAdapter;
import firstproject.tranhaison.takenote.R;

/**
 * ViewNoteActivity displays all the Notes from database in a ListView
 * in which each item can be clicked to be viewed, edited and deleted.
 * You can also add a new one.
 */
public class ViewNoteActivity extends AppCompatActivity {

    /**
     * Initialize objects for Database, Views (List View, Button), Array List and Array Adapter
     */
    NotesDbAdapter myDB;
    ListView listViewNote;
    ArrayList<Note> noteArrayList;
    Toolbar toolbar;
    FloatingActionButton floatingActionButtonAdd;
    NoteAdapter noteAdapter;

    final int REQUEST_CODE_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        /**
         * Create new database and access it as getWritable
          */
        myDB = new NotesDbAdapter(this);
        myDB.open();

        /**
         * Mapping Views
         */
        listViewNote = (ListView) findViewById(R.id.listViewNote);
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        noteArrayList = new ArrayList<>();

        /**
         * noteAdapter is used to connect between notes (which are stored in noteArrayList) and ListView
         */
        noteAdapter = new NoteAdapter(myDB,ViewNoteActivity.this, R.layout.note_layout, noteArrayList);
        listViewNote.setAdapter(noteAdapter);

        getNotes();
        addNewNote();
        editNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_view_note_search:
                // TODO
                Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_view_note_photo:
                // TODO
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_view_note_delete:
                // TODO
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get all the notes from database and put into an ArrayList
     * then refresh the Adapter to display on screen
     */
    private void getNotes() {
        noteArrayList.clear();
        noteArrayList = myDB.fetchAllNotes();
        noteAdapter.notifyDataSetChanged();
    }

    /**
     * Add a new note when clicking the Add button
     * Move from ViewNoteActivity to another Activity called AddNoteActivity
     */
    private void addNewNote () {
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
            }
        });
    }

    /**
     * Edit a note when clicking the specific note
     * or just see it
     */
    private void editNote() {
        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);

                Note editedNote = myDB.fetchNote(noteArrayList.get(position).getId());

                Bundle bundle = new Bundle();
                bundle.putSerializable("editedNote", editedNote);
                intent.putExtra("data", bundle);

                startActivityForResult(intent, REQUEST_CODE_EDIT);
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
            }
        });
    }

    /**
     * Check to see if the note has been updated or not
     * if "yes" -> Toast on screen
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
