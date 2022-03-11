package edu.quinnipiac.ser210.jokegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class JokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        //creating toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting setup and punchline strings from intent extras
        Intent intent = getIntent();
        String setup = intent.getStringExtra("setup");
        String punchline = intent.getStringExtra("punchline");

        //setting textViews to contain setup and punchline
        TextView setupTextView = (TextView)findViewById(R.id.setup);
        TextView punchlineTextView = (TextView)findViewById(R.id.punchline);

        setupTextView.setText(setup);
        punchlineTextView.setText(punchline);
    }


    //same onOptionsItemSelected as in CategorySelectorActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_help:
                Intent intent = new Intent(this,HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                //TODO: Change background color
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //sets punchline visibility to true to reveal
    public void revealPunchline(View view) {
        TextView punchline = (TextView)findViewById(R.id.punchline);
        punchline.setVisibility(View.VISIBLE);
    }
}