package firstproject.tranhaison.takenote.model;

import androidx.appcompat.app.AppCompatActivity;
import firstproject.tranhaison.takenote.R;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

public class FolderModifyActivity extends AppCompatActivity {

    ImageButton imageButtonBack;
    ListView listViewFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_modify);

        imageButtonBack = (ImageButton) findViewById(R.id.imageButtonBack);
        listViewFolder = (ListView) findViewById(R.id.listViewFolder);
    }
}
