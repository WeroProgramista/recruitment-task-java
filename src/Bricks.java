import java.time.Instant;
import java.util.*;
import java.util.regex.*;

public class Bricks {
    private Map<String, Integer> box = new HashMap<>();
    private int totalMissing = 0;
    private int completedBuildings = 0;
    private int incompleteBuildings = 0;
    private static BricksUsed bricksUsed = new BricksUsed();
    private Instructions instructions = new Instructions();
    private static final Pattern BRICK_CODE_REGEX = Pattern.compile("^[A-OM]{4}$");
    private static int totalBricksInBox = 0;

    public static void main(String[] args) {
        long start = Instant.now().toEpochMilli();
        Bricks bricks = new Bricks();
        processInput(bricks);
        bricks.construct();
        bricks.printRes();
        long end = Instant.now().toEpochMilli();
        System.out.println("Execution Time: " + (end - start) / 1000.0 + " seconds");
    }

    void construct() {
        constructBuilding("stage1");
        constructBuilding("stage2");
    }

    void constructBuilding(String stage) {
        for (int instruction : instructions.getAllInstructions(stage).keySet()) {
            Map<String, Integer> bricksNeeded = new HashMap<>();
            for (String brick : instructions.getInstruction(stage, instruction)) {
                bricksNeeded.put(brick, bricksNeeded.getOrDefault(brick, 0) + 1);
            }

            int missingBricks = calculateMissingBricks(bricksNeeded);
            totalMissing += missingBricks;

            if (missingBricks == 0) {
                processSuccessfulConstruction(stage, instruction);
            } else {
                incompleteBuildings++;
            }
        }
    }

    int calculateMissingBricks(Map<String, Integer> bricksNeeded) {
        return bricksNeeded.entrySet()
                .stream()
                .mapToInt(entry -> Math.max(0, entry.getValue() - box.getOrDefault(entry.getKey(), 0)))
                .sum();
    }

    void processSuccessfulConstruction(String stage, int instruction) {
        removeUsedBricksFromBox(stage, instruction);
        completedBuildings++;
        if ("stage1".equals(stage)) {
            bricksUsed.setStage1(bricksUsed.getStage1() + instructions.getInstruction(stage, instruction).size());
        } else {
            bricksUsed.setStage2(bricksUsed.getStage2() + instructions.getInstruction(stage, instruction).size());
        }
    }

    void removeUsedBricksFromBox(String stage, int instruction) {
        for (String brick : instructions.getInstruction(stage, instruction)) {
            box.put(brick, box.get(brick) - 1);
        }
    }

    void printRes() {
        System.out.println(bricksUsed.getStage1());
        System.out.println(bricksUsed.getStage2());
        System.out.println(box.values().stream().mapToInt(Integer::intValue).sum());
        System.out.println(totalMissing);
        System.out.println(completedBuildings);
        System.out.println(incompleteBuildings);
    }

    static void validate(String line, Map<Integer, Integer> instructionCounters) {
        if (line == null || line.strip().isEmpty()) {
            return;
        }

        String[] parts = line.strip().split(":");
        if (parts.length != 2) {
            klops();
        }

        String numStr = parts[0];
        String brickCode = parts[1];

        if (brickCode.endsWith(";")) {
            brickCode = brickCode.substring(0, brickCode.length() - 1);
        }

        if (!Pattern.compile("\\d+").matcher(numStr).matches()) {
            klops();
        }

        int num = Integer.parseInt(numStr);
        if (num < 0) {
            klops();
        }

        if (!BRICK_CODE_REGEX.matcher(brickCode).matches()) {
            klops();
        }

        if (num>0&&brickCode.contains("O")){
            klops();
        }

        if (totalBricksInBox > 10000000 || (bricksUsed.getStage1()+bricksUsed.getStage2()) > 1000 || instructionCounters.getOrDefault(num, 0) > 5000) {
            klops();
        }
    }

    static void klops(){
        System.out.println("klops");
        System.exit(0);
    }

    static void processInput(Bricks bricks) {
        Map<Integer, Integer> instructionCounters = new HashMap<>();
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                validate(line, instructionCounters);

                String[] parts = line.strip().split(":");
                String numStr = parts[0];
                String brickCode = parts[1];

                int num = Integer.parseInt(numStr);
                if (num == 0) {
                    bricks.box.put(brickCode, bricks.box.getOrDefault(brickCode, 0) + 1);
                    totalBricksInBox++;
                } else {
                    String stage = num % 3 == 0 ? "stage1" : "stage2";
                    bricks.instructions.addInstruction(stage, num, brickCode);   
                }
            }
        }
    }
}