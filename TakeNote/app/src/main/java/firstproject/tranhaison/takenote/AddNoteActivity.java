package firstproject.tranhaison.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * AddNoteActivity is used to add a new note or edit/view a note
 */
public class AddNoteActivity extends AppCompatActivity {

    NotesDbAdapter myDB;

    EditText editTextTitle, editTextNote;
    ImageButton imageButtonCheck, imageButtonAddMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        myDB = new NotesDbAdapter(this);
        myDB.open();

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextNote = (EditText) findViewById(R.id.editTextNote);
        imageButtonCheck = (ImageButton) findViewById(R.id.imageButtonCheck);
        imageButtonAddMain = (ImageButton) findViewById(R.id.imageButtonAddMain);

        addNewNote();

        Note editedNote = getEditedNote();
        updateNote(editedNote);
    }

    /**
     * Add a new note to database after filling in both Title and Note
     * if at least 1 field was missed, user has to fill it
     */
    private void addNewNote() {
        imageButtonAddMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTextTitle.getText().toString();
                String note_text = editTextNote.getText().toString();

                if (title.isEmpty() && note_text.isEmpty())
                    Toast.makeText(AddNoteActivity.this, "Please fill in both title and note", Toast.LENGTH_SHORT).show();
                else if (title.isEmpty())
                    Toast.makeText(AddNoteActivity.this, "Please fill in the title", Toast.LENGTH_SHORT).show();
                else if (note_text.isEmpty())
                    Toast.makeText(AddNoteActivity.this, "Please fill in the note", Toast.LENGTH_SHORT).show();
                else {
                    long id = myDB.createNote(title, note_text);

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
        imageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                String title = editTextTitle.getText().toString();
                String note = editTextNote.getText().toString();

                if (title.equals("") && note.equals(""))
                    Toast.makeText(AddNoteActivity.this, "Please fill in both title and note", Toast.LENGTH_SHORT).show();
                else if (title.equals(""))
                    Toast.makeText(AddNoteActivity.this, "Please fill in the title", Toast.LENGTH_SHORT).show();
                else if (note.equals(""))
                    Toast.makeText(AddNoteActivity.this, "Please fill in the note", Toast.LENGTH_SHORT).show();
                else {
                    boolean isEdited = myDB.updateNote(editedNote.getId(), title, note);
                    if (isEdited) {
                        intent.putExtra("isEdited", RESULT_OK);
                        setResult(RESULT_OK, intent);
                        finish();

                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                    } else {
                        Toast.makeText(AddNoteActivity.this, "You should add a new note", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
