package firstproject.tranhaison.takenote.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import firstproject.tranhaison.takenote.R;

public class DemoActivity extends AppCompatActivity {

    Toolbar toolbarAddNote;
    FloatingActionButton floatingActionButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        toolbarAddNote = (Toolbar) findViewById(R.id.toolBarAddNote);
        setSupportActionBar(toolbarAddNote);

        fabAdd();

    }

    private void fabAdd() {
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DemoActivity.this, "Add", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_note_photo:
                // TODO
                Toast.makeText(this, "photo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_add_note_delete:
                // TODO
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
