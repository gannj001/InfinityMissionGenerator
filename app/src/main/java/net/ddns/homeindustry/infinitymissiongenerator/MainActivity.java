package net.ddns.homeindustry.infinitymissiongenerator;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private static final String TEMP_FILE_NAME = "temp_file.txt";
    private int mission_count = 0;
    private LinearLayout mission_lnrlyt;
    private TextView no_mission_label;
    private List<Mission> mission_list;
    private Mission default_mission;
    private boolean placeholder_removed = false;
    private List<Mission> selected_mission_list;
    private MissionListBuilder mlb = new MissionListBuilder();

    private Mission buildDefaultMission() {
        Mission default_mission = new Mission();

        default_mission.icon = "none";
        default_mission.standardReq = "none";
        default_mission.name = "Secure HVT";
        default_mission.standardObjective = ("The Secure HVT optional Classified Objective is" +
                "accomplished when at the end of the game the player has one of his troopers (who" +
                "is not in a null state) inside the Zone of Control of the enemy HVT and at the" +
                "same time, the Zone of Control of his own HVT is free of enemy troops(not" +
                "counting those in a Null state).");

        return default_mission;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FileOutputStream fileOutputStream;
        List<Integer> selected_ids = new ArrayList<>();

        for (int i = 0; i < selected_mission_list.size(); i++) {
            selected_ids.add(selected_mission_list.get(i).id);
        }

        try {
            fileOutputStream = openFileOutput(TEMP_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(TextUtils.join(",", selected_ids).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException fnfe) {
            //oh well...
        } catch (IOException ioe) {
            //also oh well...
        }

    }

    protected void onResume() {
        super.onResume();

        System.out.println("Removing default mission...");
        mission_lnrlyt.removeAllViews();
        placeholder_removed = true;

        FileInputStream fileInputStream;
        String mission_string;
        String[] mission_ids;

        try {
            fileInputStream = openFileInput(TEMP_FILE_NAME);
            fileInputStream.read();
            mission_string = fileInputStream.toString();
            mission_ids = mission_string.split(",");
            for (int i = 0; i < mission_ids.length; i++) {
                add_mission(Integer.getInteger(mission_ids[i]));
            }
        } catch (FileNotFoundException fnfe) {
            //still oh well...
        } catch (IOException ioe) {
            //yup, oh well
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mission_lnrlyt = findViewById(R.id.mission_lnrlyt);
        no_mission_label = findViewById(R.id.no_mission_label);
        default_mission = buildDefaultMission();

        InputStream missionFile = getResources().openRawResource(R.raw.missions);
        try {
            mission_list = mlb.readJsonStream(missionFile);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load JSON", Toast.LENGTH_LONG).show();
            mission_list = null;
        }

        FloatingActionButton add_mission_btn = findViewById(R.id.add_mission);
        add_mission_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You added a mission!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                add_mission();
            }
        });
        if (mission_list == null) {
            add_mission_btn.setEnabled(false);
        }
    }

    private void add_mission() {
        Mission new_mission;
        System.out.println("Adding a new mission...");
        mission_count++;
        if (!placeholder_removed) {
            System.out.println("Removing default mission...");
            mission_lnrlyt.removeAllViews();
            placeholder_removed = true;
        }
        System.out.println("Adding Mission #" + Integer.toString(mission_count));
        new_mission = mission_list.get(ThreadLocalRandom.current().nextInt(1, mission_list.size() + 1));
        selected_mission_list.add(new_mission);
        mission_lnrlyt.addView(build_mission(new_mission));
    }

    private void add_mission(int mission_id) {
        Mission new_mission = null;
        if (!placeholder_removed) {
            System.out.println("Removing default mission...");
            mission_lnrlyt.removeAllViews();
            placeholder_removed = true;
        }
        for (int i = 0; i < mission_list.size(); i++) {
            if (mission_list.get(i).id == mission_id) {
                new_mission = mission_list.get(i);
            }
        }
        if (new_mission != null) {
            mission_lnrlyt.addView(build_mission(new_mission));
        }

    }

    private LinearLayout build_mission(final Mission mission) {
        MissionLayout single_mission_lnrlyt = new MissionLayout(this);
        int mission_number = mission.id;
        single_mission_lnrlyt.setMissionNumber(mission_number);
        int viewID = View.generateViewId();

        single_mission_lnrlyt.setId(viewID);
        single_mission_lnrlyt.setOrientation(LinearLayout.VERTICAL);

        TextView mission_title = new TextView(this);
        TextView mission_standardReq = new TextView(this);
        TextView mission_standardObjective = new TextView(this);
        Button remove_mission_btn = new Button(this);
        remove_mission_btn.setText("^^ Remove this mission ^^");

        mission_title.setText("#" + Integer.toString(mission_number) + " " + mission.name);
        mission_title.setAllCaps(true);
        mission_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        mission_standardReq.setSingleLine(false);
        mission_standardReq.setText("Requirement: " + mission.standardReq);
        mission_standardObjective.setSingleLine(false);
        mission_standardObjective.setText("Objective: " + mission.standardObjective);
        remove_mission_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) v.getParent().getParent();
                layout.removeView((View) v.getParent());
                selected_mission_list.remove(mission);
            }
        });
        single_mission_lnrlyt.addView(mission_title);
        single_mission_lnrlyt.addView(mission_standardReq);
        single_mission_lnrlyt.addView(mission_standardObjective);
        single_mission_lnrlyt.addView(remove_mission_btn);
        return single_mission_lnrlyt;
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
