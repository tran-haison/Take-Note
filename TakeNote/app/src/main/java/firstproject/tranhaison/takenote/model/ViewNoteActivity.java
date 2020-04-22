package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    ImageButton imageButtonPhoto;

    final int REQUEST_CODE_EDIT = 1;
    final int REQUEST_CODE_CAMERA = 2;
    final int REQUEST_CODE_FOLDER = 3;

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
        listViewNote.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        getNotes();
        addNewNote();
        editNote();
        deleteMultiNotes();
    }

    /**
     * Show the icon in Popup Menu
     * @param popupMenu
     */
    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the popup menu when clicking the photo icon
     * to let user take photo or choose image from library
     */
    private void popupMenuPhoto() {
        PopupMenu popupMenu = new androidx.appcompat.widget.PopupMenu(this, imageButtonPhoto);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_photo, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_context_photo_camera:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                        break;
                    case R.id.menu_context_photo_library:
                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                        intent1.setType("image/*");
                        startActivityForResult(intent1, REQUEST_CODE_FOLDER);
                        break;
                }
                return false;
            }
        });
        setForceShowIcon(popupMenu);
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note_activity, menu);

        imageButtonPhoto = (ImageButton) menu.findItem(R.id.menu_view_note_photo).getActionView();
        imageButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuPhoto();
            }
        });

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
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
            intent.putExtra("take_photo", bitmap);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
        }
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                intent.putExtra("choose_photo", bitmap);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Delete multiple notes when long click at least one
     */
    private void deleteMultiNotes() {
        listViewNote.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total items checked
                final int checkedCount = listViewNote.getCheckedItemCount();

                // Set the title according to total check items
                mode.setTitle(String.valueOf(checkedCount));

                // Calls toggleSelection method from NoteAdapter class
                noteAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_multi_items_click, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_multi_item_delete:
                        SparseBooleanArray selected = noteAdapter.getSelectedID();
                        // Capture all selected ID with a loop
                        // Delete all selected notes
                        for (int i=(selected.size() - 1); i>=0; i--) {
                            Note selectedNote = (Note) noteAdapter.getItem(selected.keyAt(i));
                            noteAdapter.remove(selectedNote);
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                noteAdapter.removeSelection();
            }
        });
    }

}
