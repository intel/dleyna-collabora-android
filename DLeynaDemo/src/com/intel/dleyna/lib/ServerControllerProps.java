package com.intel.dleyna.lib;

import java.util.HashMap;

public class ServerControllerProps {

    private static HashMap<String, Enum> map = new HashMap<String, Enum>();

    static {
        for (Enum e : Enum.values()) {
            map.put(e.getPropName(), e);
        }
    }

    public static Enum getEnum(String name) {
        Enum e = map.get(name);
        return e != null ? e : Enum.UNKNOWN;
    }

    public static enum Enum {
        UNKNOWN("Unknown"),
        SYSTEM_UPDATE_ID("SystemUpdateID");

        private String name;

        private Enum(String name) {
            this.name = name;
        }

        public String getPropName() {
            return name;
        }
    }

}
