package firstproject.tranhaison.takenote;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

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

    public NoteAdapter(NotesDbAdapter db, Context context, int layout, List<Note> noteList) {
        this.db = db;
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
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
        ImageView imageViewDelete;
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
            viewHolder.imageViewDelete = (ImageView) convertView.findViewById(R.id.imageViewDelete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Note note = noteList.get(position);
        viewHolder.textViewTitle.setText(note.getTitle());
        viewHolder.textViewNote.setText(note.getNote());

        viewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(position);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_listview);
        convertView.startAnimation(animation);

        return convertView;
    }

    /**
     * Display a dialog to ask user to delete a note or not
     * Click "OK" to delete the note
     * Click "Cancel" to cancel
     * @param position
     */
    private void deleteDialog(final long position) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("NOTE DELETE");
        alertDialog.setMessage("Do you want to delete this note?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteNote(noteList.get((int) position).getId());
                noteList.clear();
                noteList = db.fetchAllNotes();
                notifyDataSetChanged();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

}
