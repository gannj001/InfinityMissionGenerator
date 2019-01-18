package net.ddns.homeindustry.infinitymissiongenerator;

class Mission {
    int id;
    String icon;
    String name;
    String standardReq;
    String standardObjective;

    Mission(int in_id, String in_icon, String in_name, String in_standardReq, String in_standardObjective) {
        id = in_id;
        icon = in_icon;
        name = in_name;
        standardReq = in_standardReq;
        standardObjective = in_standardObjective;
    }
}
