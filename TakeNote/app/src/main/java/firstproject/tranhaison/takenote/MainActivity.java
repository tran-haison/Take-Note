package firstproject.tranhaison.takenote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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
