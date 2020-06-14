package firstproject.tranhaison.takenote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import firstproject.tranhaison.takenote.R;
import firstproject.tranhaison.takenote.database.NotesDbAdapter;
import firstproject.tranhaison.takenote.helper.Folder;
import firstproject.tranhaison.takenote.helper.Note;

public class FolderAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Folder> folderList;
    private NotesDbAdapter myDb;

    public FolderAdapter(Context context, ArrayList<Folder> folderList, NotesDbAdapter myDb) {
        this.context = context;
        this.folderList = folderList;
        this.myDb = myDb;
    }

    @Override
    public int getGroupCount() {
        folderList = myDb.fetchAllFolders();
        return folderList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        folderList = myDb.fetchAllFolders();
        ArrayList<Note> notesInFolder = myDb.fetchAllNotesInFolder(folderList.get(groupPosition).getId());
        return notesInFolder.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        folderList = myDb.fetchAllFolders();
        return folderList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        folderList = myDb.fetchAllFolders();
        ArrayList<Note> notesInFolder = myDb.fetchAllNotesInFolder(folderList.get(groupPosition).getId());
        return notesInFolder.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        folderList = myDb.fetchAllFolders();
        Folder folder = folderList.get(groupPosition);
        return folder.getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        folderList = myDb.fetchAllFolders();
        Folder folder = folderList.get(groupPosition);
        ArrayList<Note> noteArrayList = myDb.fetchAllNotesInFolder(folder.getId());
        Note note = noteArrayList.get(childPosition);
        return note.getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Folder folder = (Folder) getGroup(groupPosition);
        String folderName = folder.getName();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

        TextView textViewListGroup = convertView.findViewById(R.id.textViewListGroup);
        textViewListGroup.setText(folderName);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Note note = (Note) getChild(groupPosition, childPosition);
        String noteTitle = note.getTitle();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView textViewListItem = convertView.findViewById(R.id.textViewListItem);
        textViewListItem.setText(noteTitle);
        
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
