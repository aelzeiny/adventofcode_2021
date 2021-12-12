const {readFile} = require('fs');

class CavesSystem {
    static START = 'start';
    static END = 'end';

    constructor() {
        this.graph = {};
    }

    /**
     * Connect Cave A to B and vice-versa
     * @param a Cave Node; big or small
     * @param b Cave Node; big or small
     */
    connect(a, b) {
        if (!(a in this.graph))
            this.graph[a] = [];
        if (!(b in this.graph))
            this.graph[b] = [];
        if (b !== CavesSystem.START && a !== CavesSystem.END)
            this.graph[a].push(b);
        if (a !== CavesSystem.START && b !== CavesSystem.END)
            this.graph[b].push(a);
    }

    isBigCave(cave) {
        return cave === cave.toUpperCase() || cave === CavesSystem.START || cave === CavesSystem.END;
    }

    /**
     * dfs algo to roll through the cave-system
     * without cycles (modified dijkstra).
     * maxSmallCaveVisits max number of small-cave traversals
     * allowSmallCaveException boolean in which one small cave can be traversed maxSmallCaveVisits + 1 number of times
     * @returns number of paths it takes to dfs the system
     */
    dfs(maxSmallCaveVisits, allowSmallCaveException) {
        const stack = [[CavesSystem.START, {}, []]];

        let numPaths = 0;
        while (stack.length) {
            const [cave, pathToHere, fullPathToHere] = stack.pop();
            pathToHere[cave] = (cave in pathToHere) ? pathToHere[cave] + 1 : 1;
            fullPathToHere.push(cave);
            if (cave === CavesSystem.END) {
                console.log(fullPathToHere.join(','));
                numPaths++;
                continue
            }

            const canUseSmallCaveException = allowSmallCaveException && !(
                Array.from(Object.keys(pathToHere))
                    .some(k => !this.isBigCave(k) && pathToHere[k] > maxSmallCaveVisits)
            );

            for (let neighboringCave of this.graph[cave]) {
                const isBigBoi = this.isBigCave(neighboringCave);
                const numVisits = (neighboringCave in pathToHere) ? pathToHere[neighboringCave] : 0;
                if (isBigBoi || canUseSmallCaveException || numVisits < maxSmallCaveVisits) {
                    stack.push([neighboringCave, {...pathToHere}, [...fullPathToHere]]);
                }
            }
        }
        return numPaths;
    }
}



readFile('./day12_input.txt', 'utf8' , (err, data) => {
    if (err) {
        console.error(err)
        return
    }
    const system = new CavesSystem();
    data.split('\n').forEach(line => {
       const [from, to] = line.split('-');
       system.connect(from, to);
    });
    const partOne = () => {
        console.log("Part 1 solution: ", system.dfs(1, false));
    };
    const partTwo = () => {
        console.log("Part 2 solution: ", system.dfs(1, true));
    };
    partOne();
    partTwo();
});
