package firstproject.tranhaison.takenote;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * NoteAdapter is used to connect between an ArrayList (which keeps all the notes)
 * and ListView to display notes
 */
public class NoteAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Note> noteList;
    private NotesDbAdapter db;
    private SparseBooleanArray selectedItem;

    public NoteAdapter(NotesDbAdapter db, Context context, int layout, List<Note> noteList) {
        this.db = db;
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
        selectedItem = new SparseBooleanArray();
    }

    /**
     * Count the number of notes in list
     * @return
     */
    @Override
    public int getCount() {
        noteList = db.fetchAllNotes();
        return noteList.size();
    }

    /**
     * Get one specific note from list
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        noteList = db.fetchAllNotes();
        return noteList.get(position);
    }

    /**
     * Get the ID of one specific note from the list
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        noteList = db.fetchAllNotes();
        return noteList.get(position).getId();
    }

    /**
     * Keep the Views
     */
    private class ViewHolder {
        TextView textViewTitle, textViewNote;
    }

    /**
     * Set layout for convertView, setTag and pass ViewHolder as argument
     * mapping Views in layout to Views in ViewHolder if not exists
     * otherwise getTag for ViewHolder
     * setText for Title and Note
     * delete a note when clicking the image "garbage"
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Note note = noteList.get(position);
        viewHolder.textViewTitle.setText(note.getTitle());
        viewHolder.textViewNote.setText(note.getNote());

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_listview);
        convertView.startAnimation(animation);

        return convertView;
    }

    /**
     * Delete a note in ListView
     */
    public void remove(Note note) {
        db.deleteNote(note.getId());
        noteList.clear();
        noteList = db.fetchAllNotes();
        notifyDataSetChanged();
    }

    /**
     * If no note is selected -> return to normal
     */
    public void removeSelection() {
        selectedItem = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    /**
     * Put a selected note into selectedItem
     * otherwise delete from selectedItem
     * @param position
     * @param isSelected
     */
    public void selectView(int position, boolean isSelected) {
        if (isSelected) {
            selectedItem.put(position, true);
        } else {
            selectedItem.delete(position);
        }
    }

    /**
     * Notify if the note is selected or not
     * @param position
     */
    public void toggleSelection(int position) {
        selectView(position, !selectedItem.get(position));
    }

    /**
     * Return the Array of selected notes
     * @return
     */
    public SparseBooleanArray getSelectedID() {
        return selectedItem;
    }
}
