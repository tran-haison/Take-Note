package firstproject.tranhaison.takenote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

// Create an adapter to display the Note in ListView
public class NoteAdapter extends BaseAdapter {

    // 4 attributes of the Adapter
    private Context context;
    private int layout;
    private List<Note> noteList;
    private NotesDbAdapter db;

    public NoteAdapter(NotesDbAdapter db, Context context, int layout, List<Note> noteList) {
        this.db = db;
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
    }

    @Override
    public int getCount() {
        noteList = db.fetchAllNotes();
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        noteList = db.fetchAllNotes();
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        noteList = db.fetchAllNotes();
        return noteList.get(position).getId();
    }

    private class ViewHolder {
        TextView textViewTitle, textViewNote;
        ImageButton imageButtonEdit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        noteList = db.fetchAllNotes();

        if (convertView == null) {
            // Call layoutInflater
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Set convertView as note's layout
            convertView = inflater.inflate(layout, null);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            viewHolder.textViewNote = (TextView) convertView.findViewById(R.id.textViewNote);
            viewHolder.imageButtonEdit = (ImageButton) convertView.findViewById(R.id.imageButtonEdit);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Note note = noteList.get(position);
        viewHolder.textViewTitle.setText(note.getTitle());
        viewHolder.textViewNote.setText(note.getNote());

        viewHolder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNoteActivity.class);

                Note editedNote = db.fetchNote(noteList.get(position).getId());

                Bundle bundle = new Bundle();
                bundle.putSerializable("editedNote", editedNote);
                intent.putExtra("data", bundle);

                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
