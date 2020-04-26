package firstproject.tranhaison.takenote;

import java.io.Serializable;

/**
 * This class Note is used to store the information about a note:
 * 1. ID of the note
 * 2. Title of the note
 * 3. Note
 * 4. Date of the note
 * 5. The image of the note (if has)
 */
public class Note implements Serializable {
    private long id;
    private String title;
    private String note;
    private String date;
    private byte[] image;

    public Note() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
