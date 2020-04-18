package firstproject.tranhaison.takenote.model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import firstproject.tranhaison.takenote.R;

/**
 * MainActivity is the Home Page of this app
 * You can choose to add a new note or view all the notes
 */
public class MainActivity extends AppCompatActivity {

    ImageButton imageButtonAdd, imageButtonView;

    Button buttonDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAdd);
        imageButtonView = (ImageButton) findViewById(R.id.imageButtonView);

        buttonDemo = (Button) findViewById(R.id.buttonDemo);
        clickDemo();

        clickAdd();
        clickView();
    }

    public void clickDemo() {
        buttonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoActivity.class));
            }
        });
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
