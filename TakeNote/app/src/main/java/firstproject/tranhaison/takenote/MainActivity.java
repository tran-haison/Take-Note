package firstproject.tranhaison.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * MainActivity is the Home Page of this app
 * You can choose to add a new note or view all the notes
 */
public class MainActivity extends AppCompatActivity {

    ImageButton imageButtonAdd, imageButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAdd);
        imageButtonView = (ImageButton) findViewById(R.id.imageButtonView);

        clickAdd();
        clickView();
    }

    /**
     * Click the Add button to add a new note
     */
    public void clickAdd () {
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
            }
        });
    }

    /**
     * Click the View button to view all the notes
     */
    public void clickView () {
        imageButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewNoteActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_enter_from_right, R.anim.anim_exit_to_left);
            }
        });
    }

}
