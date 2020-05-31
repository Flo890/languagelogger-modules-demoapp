package de.lmu.ifi.justanothermobilesensingapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.researchime.contentextraction.model.event.ContentChangeEvent;
import de.lmu.ifi.researchime.contentextraction.model.event.Event;
import de.lmu.ifi.researchime.contentextraction.model.event.EventInputMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final List<Event> contentChangeEvents = new ArrayList<>(); // change events from the textfield are collected here

        TextInputEditText textInputEditText = findViewById(R.id.text_input);
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contentChangeEvents.add(new ContentChangeEvent(s.toString(), EventInputMode.DEFAULT));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Button setupButton = findViewById(R.id.setup_button);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanuageLoggerCaller.setupLanguageLogger(getApplicationContext());
            }
        });


        Button processDataButton = findViewById(R.id.process_button);
        processDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initialContent = "";
                LanuageLoggerCaller.runLanguageLogger(getApplicationContext(), contentChangeEvents, initialContent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
