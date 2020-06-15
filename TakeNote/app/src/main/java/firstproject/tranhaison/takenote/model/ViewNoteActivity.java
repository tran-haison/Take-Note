package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import firstproject.tranhaison.takenote.Image;
import firstproject.tranhaison.takenote.helper.Folder;
import firstproject.tranhaison.takenote.helper.Note;
import firstproject.tranhaison.takenote.adapter.NoteAdapter;
import firstproject.tranhaison.takenote.database.NotesDbAdapter;
import firstproject.tranhaison.takenote.R;

/**
 * ViewNoteActivity displays all the Notes from database in a ListView
 * in which each item can be clicked to be viewed, edited and deleted.
 * You can also add a new one.
 */
public class ViewNoteActivity extends AppCompatActivity {

    /**
     * Initialize objects for Database, NoteAdapter, ArrayList of notes
     * and View  as FAB, ImageButton, ListView
     */
    NotesDbAdapter myDB;
    NoteAdapter noteAdapter;
    ArrayList<Note> noteArrayList;
    ArrayList<Note> noteListSearch;
    ArrayList<Folder> folderArrayList;

    ListView listViewNote;
    FloatingActionButton floatingActionButtonAdd;
    ImageButton imageButtonPhoto, imageButtonMenu;
    ImageView imageViewNoteAdd;
    TextView textViewPromptNote;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu navigationViewMenu;
    MaterialSearchBar materialSearchBar;

    /**
     * Code to define the activity for result
     */
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
        imageButtonMenu = (ImageButton) findViewById(R.id.imageButtonMenu);
        imageButtonPhoto = (ImageButton) findViewById(R.id.imageButtonPhoto);
        listViewNote = (ListView) findViewById(R.id.listViewNote);
        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        imageViewNoteAdd = (ImageView) findViewById(R.id.imageViewNoteAdd);
        textViewPromptNote = (TextView) findViewById(R.id.textViewPromptNote);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationViewMenu = navigationView.getMenu();
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.search_bar);

        searchBarClick();

        /**
         * noteAdapter is used to connect between notes (which are stored in noteArrayList) and ListView
         */
        folderArrayList = new ArrayList<>();
        noteArrayList = new ArrayList<>();
        noteListSearch = new ArrayList<>();
        noteAdapter = new NoteAdapter(myDB,ViewNoteActivity.this, R.layout.note_row, noteArrayList, "");
        listViewNote.setAdapter(noteAdapter);
        listViewNote.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        getFolders();
        getNotes();
        addNewNote();
        editNote(false);
        deleteMultiNotes();

        buttonPhotoClick();
        buttonMenuClick();
        navigationItemSelect();

        promptEmptyNote();
    }

    private void promptEmptyNote() {
        if (noteArrayList.isEmpty()) {
            imageViewNoteAdd.setVisibility(View.VISIBLE);
            textViewPromptNote.setVisibility(View.VISIBLE);
            imageViewNoteAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
                }
            });
        } else {
            imageViewNoteAdd.setVisibility(View.GONE);
            textViewPromptNote.setVisibility(View.GONE);
        }
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
        PopupMenu popupMenu = new PopupMenu(this, imageButtonPhoto);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup_photo, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_context_photo_camera:
                        // Request permission from user to let app open camera
                        ActivityCompat.requestPermissions(
                                ViewNoteActivity.this,
                                new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_CAMERA);
                        break;
                    case R.id.menu_context_photo_library:
                        // Request permission from user to let app get image from folder
                        ActivityCompat.requestPermissions(
                                ViewNoteActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_FOLDER);
                        break;
                }
                return false;
            }
        });
        setForceShowIcon(popupMenu);
        popupMenu.show();
    }

    /**
     * When user clicks the photo button
     * then show the pop up menu
     */
    private void buttonPhotoClick() {
        imageButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuPhoto();
            }
        });
    }

    /**
     * Open the Navigation Menu
     */
    private void buttonMenuClick() {
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                navigationView.bringToFront();
            }
        });
    }

    private void searchBarClick() {
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    noteAdapter = new NoteAdapter(myDB,ViewNoteActivity.this, R.layout.note_row, noteArrayList, "");
                    listViewNote.setAdapter(noteAdapter);
                    editNote(false);
                } else {
                    editNote(true);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(String text) {
        noteListSearch = myDB.fetchAllNotesByText(text);
        noteAdapter = new NoteAdapter(myDB,ViewNoteActivity.this, R.layout.note_row, noteListSearch, text);
        listViewNote.setAdapter(noteAdapter);
    }

    private void navigationItemSelect() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_settings:
                        Toast.makeText(ViewNoteActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_create_new_folder:
                        dialogCreateFolder();
                        break;
                    case R.id.navigation_edit_folder:
                        Intent intent = new Intent(ViewNoteActivity.this, FolderModifyActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Show a dialog to create new folder
     */
    private void dialogCreateFolder() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ViewNoteActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_folder, null);

        final EditText editTextAddFolder = (EditText) view.findViewById(R.id.editTextAddFolder);
        Button buttonCancelFolder = (Button) view.findViewById(R.id.buttonCancelFolder);
        Button buttonAddFolder = (Button) view.findViewById(R.id.buttonAddFolder);

        alert.setView(view);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        buttonCancelFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        buttonAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = editTextAddFolder.getText().toString();

                if (folderName.isEmpty()) {
                    Toast.makeText(ViewNoteActivity.this, "Please enter folder's name", Toast.LENGTH_SHORT).show();
                } else {
                    myDB.createFolder(folderName);
                    navigationViewMenu.add(folderName);
                    folderArrayList.clear();
                    folderArrayList = myDB.fetchAllFolders();
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    /**
     * If user allow to access Camera or Folder -> call intent
     * otherwise toast on screen to inform user
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * 1. Check to see if the note has been updated or not
     * if "yes" -> Toast on screen
     * 2. Check to see if user click "check" button to save photo after taking it
     * if "yes" -> convert to byte[] and send to AddNoteActivity
     * 3. Check to see if user choose an image from folder
     * if "yes" -> convert to byte[] and send to AddNoteActivity
     *
     * Put byte array into Bundle
     * then put Bundle into intent and call activity
     * We do not put byte array straight into intent because it might be null
     * when you first open AddNoteActivity instead of ViewNoteActivity
     * The App will going to crash
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK && data != null) {
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            noteAdapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Image image = new Image();
            Uri tempUri = image.getImageUri(ViewNoteActivity.this, bitmap);
            String path = image.getRealPathFromURI(tempUri, ViewNoteActivity.this);

            Bundle bundle = new Bundle();
            bundle.putString("imageCamera", path);
            Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
            intent.putExtra("take_photo", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
        }

        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Image image = new Image();
            String path = image.getRealPathFromURI(uri, ViewNoteActivity.this);

            Bundle bundle = new Bundle();
            bundle.putString("imageFolder", path);
            Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
            intent.putExtra("choose_photo", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    private void getFolders() {
        folderArrayList.clear();
        folderArrayList = myDB.fetchAllFolders();

        for (int i=0; i<folderArrayList.size(); i++) {
            navigationViewMenu.add(folderArrayList.get(i).getName());
        }
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
    private void editNote(final boolean searchEnabled) {
        listViewNote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note editedNote;

                if (searchEnabled)
                    editedNote = myDB.fetchNote(noteListSearch.get(position).getId());
                else
                    editedNote = myDB.fetchNote(noteArrayList.get(position).getId());

                Bundle bundle = new Bundle();
                bundle.putSerializable("editedNote", editedNote);
                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                intent.putExtra("data", bundle);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
                overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
            }
        });
    }

    /**
     * Delete multiple notes when long click at least one
     * A Contextual Action Bar will show up and overlay the Custom Toolbar
     */
    private void deleteMultiNotes() {
        listViewNote.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total items checked
                final int checkedCount = listViewNote.getCheckedItemCount();
                // Set the title to be the number of items selected according to total check items
                mode.setTitle(String.valueOf(checkedCount));
                // Calls toggleSelection method from NoteAdapter class
                noteAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_multi_choice_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray selected = noteAdapter.getSelectedID();

                switch (item.getItemId()) {
                    case R.id.menu_multi_item_delete:
                        // Capture all selected ID with a loop
                        // Delete all selected notes
                        for (int i=(selected.size() - 1); i>=0; i--) {
                            Note selectedNote = (Note) noteAdapter.getItem(selected.keyAt(i));
                            myDB.deleteNote(selectedNote.getId());
                            getNotes();
                            promptEmptyNote();
                        }
                        // Close CAB
                        mode.finish();
                        return true;

                    case R.id.menu_multi_item_folder_add:
                        ArrayList<Note> noteList = new ArrayList<>();
                        for (int i=(selected.size() - 1); i>=0; i--) {
                            Note selectedNote = (Note) noteAdapter.getItem(selected.keyAt(i));
                            noteList.add(selectedNote);
                        }
                        dialogAddNoteToFolder(noteList);
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

    private void dialogAddNoteToFolder(final ArrayList<Note> noteList) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ViewNoteActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_list_folder, null);

        ListView listViewFolder = (ListView) view.findViewById(R.id.listViewFolder);
        Button buttonCancelAddToFolder = (Button) view.findViewById(R.id.buttonCancelAddToFolder);

        ArrayList<String> folderName = new ArrayList<>();
        for (Folder folder : folderArrayList) {
            folderName.add(folder.getName());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(ViewNoteActivity.this, android.R.layout.simple_list_item_1, folderName);
        listViewFolder.setAdapter(arrayAdapter);

        alert.setView(view);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        buttonCancelAddToFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        listViewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Folder folder = folderArrayList.get(position);
                for (Note note : noteList) {
                    myDB.createNoteFolder(note.getId(), folder.getId());
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
