package firstproject.tranhaison.takenote.helper;

import java.io.Serializable;

public class NoteFolder implements Serializable {

    private long id;
    private long note_id;
    private long folder_id;

    public NoteFolder(long id, long note_id, long folder_id) {
        this.id = id;
        this.note_id = note_id;
        this.folder_id = folder_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public long getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(long folder_id) {
        this.folder_id = folder_id;
    }
}
