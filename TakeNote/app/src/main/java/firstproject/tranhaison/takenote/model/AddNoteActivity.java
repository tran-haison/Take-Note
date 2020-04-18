package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import firstproject.tranhaison.takenote.Note;
import firstproject.tranhaison.takenote.NotesDbAdapter;
import firstproject.tranhaison.takenote.R;

/**
 * AddNoteActivity is used to add a new note or edit/view a note
 */
public class AddNoteActivity extends AppCompatActivity {

    NotesDbAdapter myDB;
    EditText editTextTitle, editTextNote;
    Toolbar toolbarAddNote;
    FloatingActionButton floatingActionButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        myDB = new NotesDbAdapter(this);
        myDB.open();

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextNote = (EditText) findViewById(R.id.editTextNote);
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        toolbarAddNote = (Toolbar) findViewById(R.id.toolBarAddNote);
        setSupportActionBar(toolbarAddNote);

        addNewNote();
        Note editedNote = getEditedNote();
        updateNote(editedNote);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_note_photo:
                // TODO
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_add_note_delete:
                // TODO
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Add a new note to database after filling in both Title and Note
     * if at least 1 field was missed, user has to fill it
     */
    private void addNewNote() {
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTextTitle.getText().toString();
                String note_text = editTextNote.getText().toString();

                if (title.isEmpty() && note_text.isEmpty()) {
                    Toast.makeText(AddNoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                }
                else {
                    myDB.createNote(title, note_text);
                    Intent intent = new Intent(AddNoteActivity.this, ViewNoteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                }
            }
        });
    }

    /**
     * Get a note that user want to edit or view from ViewNoteActivity
     * @return
     */
    private Note getEditedNote() {
        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getBundleExtra("data");

        Note editedNote = new Note();

        if (bundle != null) {
            editedNote = (Note) bundle.getSerializable("editedNote");
        }

        if (editedNote != null) {
            editTextTitle.setText(editedNote.getTitle());
            editTextNote.setText(editedNote.getNote());
        }

        return editedNote;
    }

    /**
     * Update an old note to a new note
     * if there is no note that has been chosen, user cannot update it
     * in this case, user must add a new one
     * @param editedNote
     */
    public void updateNote(final Note editedNote) {
        toolbarAddNote.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                String title = editTextTitle.getText().toString();
                String note = editTextNote.getText().toString();

                if (title.equals("") && note.equals("")) {
                    Toast.makeText(AddNoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                }
                else {
                    boolean isEdited = myDB.updateNote(editedNote.getId(), title, note);
                    if (isEdited) {
                        intent.putExtra("isEdited", RESULT_OK);
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                    } else {
                        myDB.createNote(title, note);
                        intent = new Intent(AddNoteActivity.this, ViewNoteActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                    }
                }
            }
        });

    }
}
