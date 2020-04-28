package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import firstproject.tranhaison.takenote.Image;
import firstproject.tranhaison.takenote.Note;
import firstproject.tranhaison.takenote.adapter.NotesDbAdapter;
import firstproject.tranhaison.takenote.R;

/**
 * AddNoteActivity is used to add a new note or edit/view a note
 */
public class AddNoteActivity extends AppCompatActivity {

    NotesDbAdapter myDB;
    EditText editTextTitle, editTextNote;
    TextView textViewDate;
    ImageView imageViewPhoto;
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
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        toolbarAddNote = (Toolbar) findViewById(R.id.toolBarAddNote);
        setSupportActionBar(toolbarAddNote);

        getCameraImage();
        getFolderImage();

        Note editedNote = getEditedNote();
        updateNote(editedNote);
        addNewNote(editedNote);
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
                Note editedNote = getEditedNote();
                if (editedNote != null) {
                    myDB.deleteNote(editedNote.getId());
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Note discarded", Toast.LENGTH_SHORT).show();
                }
                activityIntent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the image after taking photo or the image in user's folder
     * then display in the ImageView
     */
    private byte[] getCameraImage() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("take_photo");

        Image cameraImage = new Image();

        if (bundle != null) {
            cameraImage.setImage(bundle.getByteArray("imageCamera"));
            Bitmap bitmap = cameraImage.convertToBitmap();
            if (bitmap != null) {
                cameraImage.rescaleBitmap(bitmap, imageViewPhoto);
            }
        }
        return cameraImage.getImage();
    }

    private byte[] getFolderImage() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("choose_photo");

        Image folderImage = new Image();

        if (bundle != null) {
            folderImage.setImage(bundle.getByteArray("imageFolder"));
            Bitmap bitmap = folderImage.convertToBitmap();
            if (bitmap != null) {
                folderImage.rescaleBitmap(bitmap, imageViewPhoto);
            }
        }
        return folderImage.getImage();
    }

    /**
     * Get a note that user want to edit or view from ViewNoteActivity
     * @return
     */
    private Note getEditedNote() {
        Intent getIntent = getIntent();
        Bundle bundle = getIntent.getBundleExtra("data");

        Note editedNote = null;

        if (bundle != null) {
            editedNote = (Note) bundle.getSerializable("editedNote");
        }

        if (editedNote != null) {
            editTextTitle.setText(editedNote.getTitle());
            editTextNote.setText(editedNote.getNote());
            textViewDate.setText(editedNote.getDate());

            if (editedNote.getImage() != null) {
                Image imageNote = new Image(editedNote.getImage());
                Bitmap bitmapImage = imageNote.convertToBitmap();
                imageNote.rescaleBitmap(bitmapImage, imageViewPhoto);
            }
        }

        return editedNote;
    }

    /**
     * When user click on the floating action button
     * Add a new note to database after filling in Title and Note
     * if both fields are missed, note discarded
     * otherwise add a new note, date of the note will be added automatically
     * @param editedNote the note need to be edited
     */
    private void addNewNote(final Note editedNote) {
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNote(editedNote);
            }
        });
    }

    /**
     * When user click on arrow back navigation button
     * Same as addNewNote
     */
    private void updateNote(final Note editedNote) {
        toolbarAddNote.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNote(editedNote);
            }
        });
    }

    /**
     * When user click on DEVICE back button -> return to previous activity
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityIntent();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Add a new note or update an old one
     * If all fields are empty -> discard note
     * otherwise update the note depend on the note ID
     * A note must have at least title or note or image
     * @param editedNote
     */
    private void confirmNote(Note editedNote) {
        // Get the current date
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String currentDate = simpleDateFormat.format(date);

        // Get the input title and note
        String title = editTextTitle.getText().toString();
        String note = editTextNote.getText().toString();

        // Get the image
        byte[] noteImage = null;
        byte[] imageCamera = getCameraImage();
        byte[] imageFolder = getFolderImage();
        byte[] newImage;

        if (editedNote != null)
            noteImage = editedNote.getImage();

        if (title.equals("") && note.equals("") && noteImage == null && imageCamera == null && imageFolder == null) {
            Toast.makeText(AddNoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
            activityIntent();
        }
        else {
            if (noteImage != null && imageCamera == null && imageFolder == null) {
                newImage = noteImage;
            } else if (noteImage == null && imageCamera != null && imageFolder == null) {
                newImage = imageCamera;
            } else if (noteImage == null && imageCamera == null && imageFolder != null) {
                newImage = imageFolder;
            } else {
                newImage = null;
            }

            if (editedNote != null) {
                myDB.updateNote(editedNote.getId(), title, note, currentDate, newImage);

                Intent intent = new Intent();
                intent.putExtra("isEdited", RESULT_OK);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
            } else {
                myDB.createNote(title, note, currentDate, newImage);
                activityIntent();
            }
        }
    }

    private void activityIntent() {
        Intent intent = new Intent(AddNoteActivity.this, ViewNoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
    }
}
