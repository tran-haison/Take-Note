package firstproject.tranhaison.takenote.model;

import androidx.appcompat.app.AppCompatActivity;
import firstproject.tranhaison.takenote.R;
import firstproject.tranhaison.takenote.adapter.FolderAdapter;
import firstproject.tranhaison.takenote.database.NotesDbAdapter;
import firstproject.tranhaison.takenote.helper.Folder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FolderModifyActivity extends AppCompatActivity {

    NotesDbAdapter myDb;
    ArrayList<Folder> folderArrayList;
    FolderAdapter folderAdapter;

    ImageButton imageButtonBack;
    FloatingActionButton floatingActionButtonAddFolder;
    ExpandableListView expandableListViewFolder;

    final int CREATE_NEW_FOLDER = 0;
    final int EDIT_FOLDER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_modify);

        myDb = new NotesDbAdapter(FolderModifyActivity.this);
        myDb.open();

        imageButtonBack = (ImageButton) findViewById(R.id.imageButtonBack);
        floatingActionButtonAddFolder = (FloatingActionButton) findViewById(R.id.floatingActionButtonAddFolder);
        expandableListViewFolder = (ExpandableListView) findViewById(R.id.expandableListViewFolder);

        folderArrayList = new ArrayList<>();
        folderAdapter = new FolderAdapter(FolderModifyActivity.this, folderArrayList, myDb);
        expandableListViewFolder.setAdapter(folderAdapter);

        getFolders();
        folderItemClick();
        noteItemClick();
        buttonCreateFolderClick();
        buttonBackClick();
    }

    private void getFolders() {
        folderArrayList.clear();
        folderArrayList = myDb.fetchAllFolders();
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            intent();
        return super.onKeyDown(keyCode, event);
    }

    private void buttonBackClick() {
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent();
            }
        });
    }

    private void intent() {
        Intent intent = new Intent(FolderModifyActivity.this, ViewNoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_enter_from_left, R.anim.anim_exit_to_right);
    }

    private void buttonCreateFolderClick() {
        floatingActionButtonAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateFolder(CREATE_NEW_FOLDER, -1);
            }
        });
    }

    private void noteItemClick() {
        expandableListViewFolder.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                long folder_id = folderAdapter.getGroupId(groupPosition);
                long note_id = folderAdapter.getChildId(groupPosition, childPosition);
                dialogRemoveNoteFromFolder(folder_id, note_id);
                return false;
            }
        });
    }

    private void folderItemClick() {
        expandableListViewFolder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Folder folder = folderArrayList.get(position);
                dialogModifyFolder(folder);
                return false;
            }
        });
    }

    private void dialogRemoveNoteFromFolder(final long folder_id, final long note_id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(FolderModifyActivity.this);
        alert.setTitle("NOTE REMOVE");
        alert.setIcon(R.drawable.ic_delete_red_24dp);
        alert.setMessage("Do you want to remove note from this folder?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // remove the selected note from folder
                myDb.deleteNoteFolder(folder_id, note_id);
                getFolders();
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing if user click cancel
            }
        });
        alert.show();
    }

    /**
     * Show a dialog to delete or edit folder's name
     */
    private void dialogModifyFolder(Folder folder) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(FolderModifyActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_modify_folder, null);

        Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        Button buttonDeleteFolder = (Button) view.findViewById(R.id.buttonDeleteFolder);
        Button buttonDeleteFolderWithNotes = (Button) view.findViewById(R.id.buttonDeleteFolderWithNotes);
        Button buttonEditFolderName = (Button) view.findViewById(R.id.buttonEditFolderName);
        TextView textViewEditFolder = (TextView) view.findViewById(R.id.textViewEditFolder);
        textViewEditFolder.setText(folder.getName());

        alert.setView(view);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        final long folder_id = folder.getId();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        buttonDeleteFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteFolder(folder_id, false);
                getFolders();
                alertDialog.dismiss();
            }
        });

        buttonDeleteFolderWithNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteFolder(folder_id, true);
                getFolders();
                alertDialog.dismiss();
            }
        });

        buttonEditFolderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateFolder(EDIT_FOLDER, folder_id);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * Show a dialog to create new folder
     */
    private void dialogCreateFolder(final int option, final long folder_id) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(FolderModifyActivity.this);
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
                    Toast.makeText(FolderModifyActivity.this, "Please enter folder's name", Toast.LENGTH_SHORT).show();
                } else {
                    if (option == CREATE_NEW_FOLDER) {
                        myDb.createFolder(folderName);
                    } else if (option == EDIT_FOLDER) {
                        myDb.updateFolder(folder_id, folderName);
                    }
                    getFolders();
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }
}
