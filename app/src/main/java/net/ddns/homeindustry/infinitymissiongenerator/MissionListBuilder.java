package net.ddns.homeindustry.infinitymissiongenerator;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MissionListBuilder {

    Mission readMission(JsonReader reader) throws IOException {
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

    List<Mission> readMissionArray(JsonReader reader) throws IOException {
        List<Mission> missions = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            missions.add(readMission(reader));
        }
        reader.endArray();
        return missions;
    }

    public List<Mission> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        try {
            return readMissionArray(reader);
        } finally {
            reader.close();
        }
    }
}
