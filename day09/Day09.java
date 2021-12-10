import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Each node in a disjointed set points to another node, which follows another node,
 * which eventually points to nothing. The last node is considered the 'key' in which
 * the disjointed set is grouped on.
 */
class DisjointedSet<T> {
    private HashMap<T, Optional<T>> disjointedSet;

    public DisjointedSet() {
        this.disjointedSet = new HashMap<>();
    }

    public void connect(T from, T to) {
        this.disjointedSet.putIfAbsent(to, Optional.empty());
        this.disjointedSet.put(from, Optional.of(to));
    }

    public Collection<Set<T>> group() {
        HashMap<T, Set<T>> groups = new HashMap<>();
        for (T key : this.disjointedSet.keySet()) {
            T nextKey = key;
            while (this.disjointedSet.get(nextKey).isPresent()) {
                nextKey = this.disjointedSet.get(nextKey).get();
            }
            groups.putIfAbsent(nextKey, new HashSet<>());
            groups.get(nextKey).add(key);

            // Implement collapsing find optimization to speed up future queries.
            T currKey = key;
            while (this.disjointedSet.get(currKey).isPresent()) {
                T reroutedKey = this.disjointedSet.get(currKey).get();
                this.disjointedSet.put(currKey, Optional.of(nextKey));
                currKey = reroutedKey;
            }
        }
        return groups.values();
    }
}


/**
 * Data-structure for a 2D array.
 */
class HeatMap {
    private ArrayList<List<Integer>> heatmap;
    private static final int MAX_PEAK = 9;
    public HeatMap() {
        heatmap = new ArrayList<>();
    }

    public boolean add(List<Integer> e) {
        return this.heatmap.add(e);
    }

    public int get(int r, int c) {
        return this.heatmap.get(r).get(c);
    }

    public int get(Coord coord) {
        return this.get(coord.r, coord.c);
    }

    public int size() {
        return this.heatmap.size();
    }

    public int size(int r) {
        return this.heatmap.get(r).size();
    }

    public List<Coord> getNeighbors(int r, int c) {
        List<Coord> neighbors = new ArrayList<>();

        int lr = r - 1;
        int rr = r + 1;
        int lc = c - 1;
        int rc = c + 1;
        if (lr >= 0) {
            neighbors.add(new Coord(lr, c));
        }
        if (rr < this.heatmap.size()) {
            neighbors.add(new Coord(rr, c));
        }
        if (lc >= 0) {
            neighbors.add(new Coord(r, lc));
        }
        if (rc < this.heatmap.get(r).size()) {
            neighbors.add(new Coord(r, rc));
        }
        return neighbors;
    }

    public boolean isLowestPoint(int r, int c) {
        int point = this.get(r, c);

        for (Coord coord : getNeighbors(r, c)) {
            if (this.get(coord.r, coord.c) <= point) {
                return false;
            }
        }
        return true;
    }

    public DisjointedSet<HeatMap.Coord> buildBasins() {
        DisjointedSet<Coord> basins = new DisjointedSet<>();
        for (int r = 0; r < this.heatmap.size(); r++) {
            for (int c = 0; c < this.heatmap.get(r).size(); c++) {
                int point = this.get(r, c);
                if (point == MAX_PEAK)
                    continue;
                // Find the lowest coordinate from the surrounding neighbors
                Coord lowestCoord = null;
                for (Coord coord : getNeighbors(r, c)) {
                    if (this.get(coord) < point && this.get(coord) != MAX_PEAK) {
                        lowestCoord = coord;
                        point = this.get(coord);
                    }
                }
                if (lowestCoord != null) {
                    // Connect the current coordinate to the lowest coordinate in the disjointed set.
                    basins.connect(new Coord(r, c), lowestCoord);
                }
            }
        }
        return basins;
    }

    public class Coord {
        private int r;
        private int c;

        public Coord(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return r == coord.r && c == coord.c;
        }

        @Override
        public int hashCode() {
            return Objects.hash(r, c);
        }

        @Override
        public String toString() {
            return "(" + this.r + ", " + this.c + ")";
        }
    }
}

public class Day9 {
    public static void main(String[] args) {
        try {
            File myObj = new File("day09_input.txt");
            Scanner myReader = new Scanner(myObj);
            HeatMap heatmap = new HeatMap();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                heatmap.add(
                        Arrays.stream(data.split(""))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList())
                );
            }
            myReader.close();
            part1(heatmap);
            part2(heatmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void part1(HeatMap heatmap) {
        int risk = 0;
        for (int r = 0; r < heatmap.size(); r++) {
            for (int c = 0; c < heatmap.size(r); c++) {
                if (heatmap.isLowestPoint(r, c)) {
                    risk += heatmap.get(r, c) + 1;
                }
            }
        }
        System.out.println("Part 1 Solution: " + risk);
    }

    private static void part2(HeatMap heatMap) {
        // Build disjoined set with collapsing find optimization
        DisjointedSet<HeatMap.Coord> basins = heatMap.buildBasins();
        // Get basins, grouped on the lowest point in the disjointed set
        Collection<Set<HeatMap.Coord>> groups = basins.group();
        // Find the top 3 sized basins
        PriorityQueue<Integer> rankedBasins = new PriorityQueue<>(groups.size(), (s1, s2) -> s2 - s1);
        rankedBasins.addAll(groups.stream().map(Set::size).collect(Collectors.toList()));
        System.out.println("Part 2 Solution: " + rankedBasins.poll() * rankedBasins.poll() * rankedBasins.poll());
    }
}