import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.stream.IntStream;

class Pattern extends HashSet<Character> {
    public Pattern(String segments) {
        super();
        for (int i = 0; i < segments.length(); i++) {
            this.add(segments.charAt(i));
        }
    }
}

/**
  0:      1:      2:      3:      4:
 aaaa    ....    aaaa    aaaa    ....
b    c  .    c  .    c  .    c  b    c
b    c  .    c  .    c  .    c  b    c
 ....    ....    dddd    dddd    dddd
e    f  .    f  e    .  .    f  .    f
e    f  .    f  e    .  .    f  .    f
 gggg    ....    gggg    gggg    ....

  5:      6:      7:      8:      9:
 aaaa    aaaa    aaaa    aaaa    aaaa
b    .  b    .  .    c  b    c  b    c
b    .  b    .  .    c  b    c  b    c
 dddd    dddd    ....    dddd    dddd
.    f  e    f  .    f  e    f  .    f
.    f  e    f  .    f  e    f  .    f
 gggg    gggg    ....    gggg    gggg
 */
class PatternSolver {
    private final Pattern[] digits;

    public PatternSolver(List<Pattern> encodedPatterns) {
        this.digits = solve(encodedPatterns);
    }

    private Pattern[] solve(List<Pattern> encodedPatterns) {
        List<Pattern> remainingPatterns = new ArrayList<>(encodedPatterns);
        Pattern[] digits = new Pattern[10];

        // Start with the obvious digits based on counts: 1, 7, 4, and 8
        digits[1] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 2);
        digits[7] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 3);
        digits[4] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 4);
        digits[8] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 7);

        // 3 has a count of five and is a superset of 7
        digits[3] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 5 && p.containsAll(digits[7]));
        // 9 has a count of six and is a superset of 3
        digits[9] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 6 && p.containsAll(digits[3]));

        // We have to find 5 and 6 together. Only two remaining Patterns match this specific criteria.
        // 6 has a count of six and is a superset of 5 which has a count of 5
        boolean foundFiveAndSix = false;
        for (int i = 0; i < remainingPatterns.size() && !foundFiveAndSix; i++) {
            Pattern sixCountPattern = remainingPatterns.get(i);
            if (sixCountPattern.size() != 6) continue;
            for (int j = 0; j < remainingPatterns.size() && !foundFiveAndSix; j++) {
                Pattern fiveCountPattern = remainingPatterns.get(j);
                if (i == j || fiveCountPattern.size() != 5) continue;
                if (sixCountPattern.containsAll(fiveCountPattern)) {
                    digits[6] = sixCountPattern;
                    digits[5] = fiveCountPattern;
                    // beware of array shifting indexes when removing
                    remainingPatterns.remove(Math.max(i, j));
                    remainingPatterns.remove(Math.min(i, j));
                    foundFiveAndSix = true;
                }
            }
        }

        // Only two patterns remaining 0 and 2 with a count of 6 and 5, respectively
        digits[0] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 6);
        digits[2] = popPatternOnPredicate(remainingPatterns, p -> p.size() == 5);
        return digits;
    }

    private Pattern popPatternOnPredicate(List<Pattern> remainingPatterns, java.util.function.Predicate<Pattern> predicate) {
        int indexOfPredicate = IntStream.range(0, remainingPatterns.size())
                                .filter(i -> predicate.test(remainingPatterns.get(i)))
                                .iterator().next();
        Pattern matchingPattern = remainingPatterns.get(indexOfPredicate);
        remainingPatterns.remove(indexOfPredicate);
        return matchingPattern;
    }

    /**
     * Given a pattern decode the matching integer
     */
    public int decode(Pattern encodedPattern) {
        return IntStream
                .range(0, this.digits.length)
                .filter(i -> this.digits[i].equals(encodedPattern))
                .iterator().next();
    }

    /*
     * Given a List of patterns decode the matching integer in a base-10 system
     */
    public int decode(List<Pattern> encodedPatterns) {
        int decoded = 0;
        for(Pattern encodedPattern: encodedPatterns) {
            decoded = decoded * 10 + this.decode(encodedPattern);
        }
        return decoded;
    }
}

public class Day8 {
    public static List<Pattern> parsePattern(String patternList) {
        return Arrays.stream(patternList.split(" "))
                .filter(l -> !l.isEmpty() && !l.isBlank())
                .map(Pattern::new)
                .collect(Collectors.toList());
    }

    public static void part1(List<PatternSolver> solvers, List<List<Pattern>> encodedPatterns) {
        int part1Counts = 0;
        for (int i = 0; i < solvers.size(); i++) {
            PatternSolver solver = solvers.get(i);
            for (Pattern encodedPattern : encodedPatterns.get(i)) {
                int decodedNumber = solver.decode(encodedPattern);
                if (decodedNumber == 4 || decodedNumber == 1 || decodedNumber == 7 || decodedNumber == 8) {
                    part1Counts ++;
                }
            }
        }
        System.out.println("Solution for Part 1: " + part1Counts);
    }

    public static void part2(List<PatternSolver> solvers, List<List<Pattern>> encodedPatterns) {
        int part2Sums = 0;
        for (int i = 0; i < solvers.size(); i++) {
            PatternSolver solver = solvers.get(i);
            List<Pattern> encodedPattern = encodedPatterns.get(i);
            part2Sums += solver.decode(encodedPattern);
        }
        System.out.println("Solution for Part 2: " + part2Sums);
    }

    public static void main(String[] args) {
        ArrayList<PatternSolver> solvers = new ArrayList<>();
        ArrayList<List<Pattern>> encoded = new ArrayList<>();
        try {
            File myObj = new File("day8_input.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] splitData = data.split("\\|");
                solvers.add(new PatternSolver(parsePattern(splitData[0])));
                encoded.add(parsePattern(splitData[1]));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        part1(solvers, encoded);
        part2(solvers, encoded);
    }
}
