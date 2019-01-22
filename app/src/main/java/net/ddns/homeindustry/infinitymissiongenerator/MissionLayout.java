package net.ddns.homeindustry.infinitymissiongenerator;

import android.content.Context;
import android.widget.LinearLayout;

public class MissionLayout extends LinearLayout {
    int missionNumber;

    public MissionLayout(Context context) {
        super(context);
    }

    public int getMissionNumber() {
        return missionNumber;
    }

    public void setMissionNumber(int mn) {
        missionNumber = mn;
    }
}
