package firstproject.tranhaison.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

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
    ImageButton imageButtonAdd2;
    NoteAdapter noteAdapter;

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
        imageButtonAdd2 = (ImageButton) findViewById(R.id.imageButtonAdd2);

        noteArrayList = new ArrayList<>();

        /**
         * noteAdapter is used to connect between notes (which are stored in noteArrayList) and ListView
         */
        noteAdapter = new NoteAdapter(myDB,ViewNoteActivity.this, R.layout.note_layout, noteArrayList);
        listViewNote.setAdapter(noteAdapter);

        addNewNote();
        editNote();
        getNotes();
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
        imageButtonAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right); // Add transition for both Activities
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

                startActivity(intent);
            }
        });
    }
}
