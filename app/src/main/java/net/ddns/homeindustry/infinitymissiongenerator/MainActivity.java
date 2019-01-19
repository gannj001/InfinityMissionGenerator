package net.ddns.homeindustry.infinitymissiongenerator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private int mission_count = 0;
    private LinearLayout mission_lnrlyt;
    private TextView no_mission_label;
    private List<Mission> mission_list;
    private Mission default_mission;
    private boolean placeholder_removed = false;

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
            mission_list = readJsonStream(missionFile);
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
        mission_lnrlyt.addView(build_mission(new_mission));
    }

    private List<Mission> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        try {
            return readMissionArray(reader);
        } finally {
            reader.close();
        }
    }

    private List<Mission> readMissionArray(JsonReader reader) throws IOException {
        List<Mission> missions = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            missions.add(readMission(reader));
        }
        reader.endArray();
        return missions;
    }

    private Mission readMission(JsonReader reader) throws IOException {
        int id = -1;
        String icon = "none";
        String mission_name = "none";
        String standardReq = "none";
        String standardObjective = "none";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("number")) {
                id = reader.nextInt();
            } else if (name.equals("icon")) {
                icon = reader.nextString();
            } else if (name.equals("name")) {
                mission_name = reader.nextString();
            } else if (name.equals("standardReq")) {
                standardReq = reader.nextString();
            } else if (name.equals("standardObjective")) {
                standardObjective = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Mission(id, icon, mission_name, standardReq, standardObjective);
    }

    private LinearLayout build_mission(Mission mission) {
        LinearLayout single_mission_lnrlyt = new LinearLayout(this);
        int mission_number = mission.id;
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
