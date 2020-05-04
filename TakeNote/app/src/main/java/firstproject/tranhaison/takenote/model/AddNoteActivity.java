package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

    final int REQUEST_CODE_CAMERA = 2;
    final int REQUEST_CODE_FOLDER = 3;
    final int REQUEST_CODE_GRAB_TEXT = 4;
    // Current image in ImageView
    String currentImage = "";

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
        grabImageText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_add_note_photo_camera:
                ActivityCompat.requestPermissions(AddNoteActivity.this,
                        new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_CAMERA);
                break;

            case R.id.menu_add_note_photo_library:
                ActivityCompat.requestPermissions(AddNoteActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_FOLDER);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else {
                    Toast.makeText(this, "Camera access denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_FOLDER:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent(Intent.ACTION_PICK);
                    intent1.setType("image/*");
                    startActivityForResult(intent1, REQUEST_CODE_FOLDER);
                } else {
                    Toast.makeText(this, "Folder access denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Image imageCamera = new Image();
            Uri tempUri = imageCamera.getImageUri(AddNoteActivity.this, bitmap);
            currentImage = imageCamera.getRealPathFromURI(tempUri, AddNoteActivity.this);
            imageCamera.rescaleBitmap(bitmap, imageViewPhoto);
        }

        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            Image imageFolder = new Image();
            currentImage = imageFolder.getRealPathFromURI(uri, AddNoteActivity.this);
            Bitmap bitmapFolder = imageFolder.getBitmap(currentImage);
            imageFolder.rescaleBitmap(bitmapFolder, imageViewPhoto);
        }

        if (requestCode == REQUEST_CODE_GRAB_TEXT && resultCode == RESULT_OK && data != null) {
            currentImage = "";
            imageViewPhoto.setImageResource(R.drawable.ic_photo_white_1dp);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get the image after taking photo from ViewNoteActivity
     * then display in the ImageView
     */
    private String getCameraImage() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("take_photo");

        String getCamera = "";
        Image cameraImage = new Image();

        if (bundle != null) {
            getCamera = bundle.getString("imageCamera");
            Bitmap bitmap = cameraImage.getBitmap(getCamera);
            if (bitmap != null) {
                cameraImage.rescaleBitmap(bitmap, imageViewPhoto);
                currentImage = getCamera;
            }
        }
        return getCamera;
    }

    /**
     * Get the image after chosing from ViewNoteActivity
     * @return
     */
    private String getFolderImage() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("choose_photo");

        String getFolder = "";
        Image folderImage = new Image();

        if (bundle != null) {
            getFolder = bundle.getString("imageFolder");
            Bitmap bitmap = folderImage.getBitmap(getFolder);
            if (bitmap != null) {
                folderImage.rescaleBitmap(bitmap, imageViewPhoto);
                currentImage = getFolder;
            }
        }
        return getFolder;
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

            if (!editedNote.getImage().isEmpty()) {
                Image imageNote = new Image();
                Bitmap bitmap = imageNote.getBitmap(editedNote.getImage());
                imageNote.rescaleBitmap(bitmap, imageViewPhoto);
                currentImage = editedNote.getImage();
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
     * @param editedNote
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
     * If user clicks on an image, send it to ImageCustomViewActivity
     */
    private void grabImageText() {
        if (!currentImage.isEmpty()) {
            imageViewPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddNoteActivity.this, ImageCustomView.class);
                    intent.putExtra("imageCustom", currentImage);
                    startActivityForResult(intent, REQUEST_CODE_GRAB_TEXT);
                    overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
                }
            });
        }
    }

    /**
     * When user click on DEVICE back button -> return to previous activity
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

        if (title.isEmpty() && note.isEmpty() && currentImage.isEmpty()) {
            Toast.makeText(AddNoteActivity.this, "Empty note discarded", Toast.LENGTH_SHORT).show();
            activityIntent();
        }
        else {
            if (editedNote != null) {
                myDB.updateNote(editedNote.getId(), title, note, currentDate, currentImage);
                Intent intent = new Intent();
                intent.putExtra("isEdited", RESULT_OK);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
            } else {
                myDB.createNote(title, note, currentDate, currentImage);
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
