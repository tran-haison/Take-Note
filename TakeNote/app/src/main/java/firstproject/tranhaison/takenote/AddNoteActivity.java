package firstproject.tranhaison.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
                    startActivity(intent);

                    overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                }
            }
        });
    }

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

    public void updateNote(final Note editedNote) {
        imageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNoteActivity.this, ViewNoteActivity.class);

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
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                    } else {
                        Toast.makeText(AddNoteActivity.this, "Nothing is chosen to be edited!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(AddNoteActivity.this, "You should add a new note", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
