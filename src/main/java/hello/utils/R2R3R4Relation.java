package hello.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class R2R3R4Relation {
    public static final Map<String, ArrayList<String>> R3ToR2UserIdMap = Map.of(
            "14", new ArrayList<>(Arrays.asList("16", "17")),
            "15", new ArrayList<>(Arrays.asList("18","19"))
    );

    public static final Map<String, ArrayList<String>> R4ToR2UserIdMap = Map.of(
            "27", new ArrayList<>(Arrays.asList("16","17")),
            "11", new ArrayList<>(Arrays.asList("16","17")),
            "12", new ArrayList<>(Arrays.asList("18","19")),
            "13", new ArrayList<>(Arrays.asList("18","19"))
    );

    public static final Map<String, String> R2ToR3UserIdMAP = Map.of(
            "16", "14",
            "17", "14",
            "18", "15",
            "19", "15");

    public static final Map<String, ArrayList<String>> R2ToR4UserIdMap = Map.of(
            "16", new ArrayList<>(Arrays.asList("27", "11")),
            "17", new ArrayList<>(Arrays.asList("27","11")),
            "18", new ArrayList<>(Arrays.asList("12","13")),
            "19", new ArrayList<>(Arrays.asList("12","13"))
    );
}
