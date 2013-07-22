package com.intel.dleyna.lib;

import java.util.HashMap;

public class RendererControllerProps {

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
        UNKNOWN("çN0t#A@Va1id£Pr0p!"),
        PLAYBACK_STATUS("PlaybackStatus"),
        RATE("Rate"),
        METADATA("Metadata"),
        VOLUME("Volume"),
        POSITION("Position"),
        MINIMUM_RATE("MinimumRate"),
        MAXIMUM_RATE("MaximumRate"),
        CAN_GO_NEXT("CanGoNext"),
        CAN_GO_PREVIOUS("CanGoPrevious"),
        CAN_PLAY("CanPlay"),
        CAN_PAUSE("CanPause"),
        CAN_SEEK("CanSeek"),
        CAN_CONTROL("CanControl"),
        TRANSPORT_PLAY_SPEEDS("TransportPlaySpeeds"),
        CURRENT_TRACK("CurrentTrack"),
        NUMBER_OF_TRACKS("NumberOfTracks"),
        MUTE("Mute");

        private String name;

        private Enum(String name) {
            this.name = name;
        }

        public String getPropName() {
            return name;
        }
    }

}
