package firstproject.tranhaison.takenote.helper;

import java.io.Serializable;

/**
 * This class is used to store the id and name of the folder
 * in which we can categorize the note
 */
public class Folder implements Serializable {

    private long id;
    private String name;

    public Folder() {}

    public Folder(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
