import java.util.*;

public class Instructions {
    private Map<Integer, List<String>> stage1 = new HashMap<>();
    private Map<Integer, List<String>> stage2 = new HashMap<>();

    void addInstruction(String stage, int number, String brickCode) {
        Map<Integer, List<String>> map = "stage1".equals(stage) ? stage1 : stage2;
        map.computeIfAbsent(number, k -> new ArrayList<>()).add(brickCode);
    }

    Map<Integer, List<String>> getAllInstructions(String stage) {
        return "stage1".equals(stage) ? stage1 : stage2;
    }

    List<String> getInstruction(String stage, int instruction) {
        return getAllInstructions(stage).get(instruction);
    }
}
