package com.dailyreminder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarText;
    FragmentManager fm;
    int menuState;
    int crossInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        toolbar = (Toolbar)findViewById(R.id.mToolbar);
        toolbarText = (TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        fm.beginTransaction()
                .replace(R.id.mainContainer ,new MainFragment())
                .commit();

    }

    /**
     ******* Toolbar
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (menuState) {
            case 0:
                getMenuInflater().inflate(R.menu.settings_menu, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.check_menu, menu);
                break;
            case 2:
                return super.onCreateOptionsMenu(menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeTitle(String title, boolean cross, int arrowOrCross, int menu) {
        toolbarText.setText(title);
        if (cross)
            addCross(arrowOrCross);
        else
            removeCross();
        this.menuState = menu;
    }



    /**
     ******* inflate info
     */

    public void toFragmentInfo(String title, String notes, int hora, int minuto, int position, int listPosition) {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        EditReminderFragment frag = new EditReminderFragment();
        Bundle subjectInfo = new Bundle();
        subjectInfo.putString("title", title);
        subjectInfo.putString("notes", notes);
        subjectInfo.putInt("hora", hora);
        subjectInfo.putInt("minuto", minuto);
        subjectInfo.putInt("position", position);
        subjectInfo.putInt("listPosition", listPosition);
        frag.setArguments(subjectInfo);
        fragmentManager.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_back_in, R.anim.slide_back_out)
                .replace(R.id.mainContainer, frag)
                .addToBackStack(null)
                .commit();
    }

    public void toFragmentNewReminder() {
        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        NewReminderFragment frag = new NewReminderFragment();
        fragmentManager.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, R.anim.slide_back_in, R.anim.slide_back_out)
                .replace(R.id.mainContainer, frag)
                .addToBackStack(null)
                .commit();
    }

    public void toSettings() {
        SettingsFragment frag = new SettingsFragment();
        fm.beginTransaction()
                .replace(R.id.mainContainer, frag)
                .addToBackStack(null)
                .commit();
    }

    public void toLegal() {
        LegalFragment frag = new LegalFragment();
        fm.beginTransaction()
                .replace(R.id.mainContainer, frag)
                .addToBackStack(null)
                .commit();
    }

    public void addCross(int arrowOrCross) {
        Log.e("bruh", ""+arrowOrCross);
        crossInt = arrowOrCross;
        if (arrowOrCross == 0) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.window_close);
        }
        else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        invalidateOptionsMenu();
    }

    public void removeCross() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        invalidateOptionsMenu();
    }

    public int getCrossInt() {
        return crossInt;
    }

}
