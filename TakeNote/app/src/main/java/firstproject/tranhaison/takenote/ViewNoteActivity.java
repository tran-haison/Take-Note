package firstproject.tranhaison.takenote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewNoteActivity extends AppCompatActivity {

    NotesDbAdapter myDB;

    ListView listViewNote;
    ArrayList<Note> noteArrayList;
    ImageButton imageButtonAdd2;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        myDB = new NotesDbAdapter(this);
        myDB.open();

        listViewNote = (ListView) findViewById(R.id.listViewNote);
        imageButtonAdd2 = (ImageButton) findViewById(R.id.imageButtonAdd2);
        noteArrayList = new ArrayList<>();

        noteAdapter = new NoteAdapter(myDB,ViewNoteActivity.this, R.layout.note_layout, noteArrayList);
        listViewNote.setAdapter(noteAdapter);

        addNewNote();
        editNote();
        getNotes();
    }

    // Display the note in ListView on screen
    private void getNotes() {
        noteArrayList.clear();
        noteArrayList = myDB.fetchAllNotes();
        noteAdapter.notifyDataSetChanged();
    }

    private void addNewNote () {
        imageButtonAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
            }
        });
    }

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
