const {readFile} = require('fs');


class Grid {
    constructor() {
        this.grid = [];
    }

    add(x, y) {
        this.grid.push([x, y]);
        if (Number.isNaN(x) || Number.isNaN(y)) {
            console.error('?');
        }
        if (this.maxX === undefined || this.maxX < x)
            this.maxX = x;
        if (this.maxY  === undefined || this.maxY < y)
            this.maxY = y;
    }

    foldX(xDim) {
        const newGrid = new Grid();
        for (let [x, y] of this.grid) {
            if (x > xDim) {
                const reflectX = xDim - (x - xDim)
                newGrid.add(reflectX, y);
            } else {
                newGrid.add(x, y);
            }
        }
        return newGrid;
    }

    foldY(yDim) {
        const newGrid = new Grid();
        for (let [x, y] of this.grid) {
            if (y > yDim) {
                const reflectY = yDim - (y - yDim)
                newGrid.add(x, reflectY);
            } else {
                newGrid.add(x, y);
            }
        }
        return newGrid;
    }

    toString() {
        let answer = [[]];
        for (let i = 0; i <= this.maxY; i++) {
            for (let j = 0; j <= this.maxX; j++) {
                answer[answer.length - 1].push('.');
            }
            answer.push([]);
        }

        for (let [x, y] of this.grid) {
            answer[y][x] = '#';
        }
        return answer.map(l => l.join('')).join('\n');
    }
}

function part1(grid, folds) {
    // console.log(grid.toString());
    const [foldAxis, foldVal] = folds[0];
    console.log('---')
    grid = foldAxis.toLowerCase() === 'x' ? grid.foldX(foldVal) : grid.foldY(foldVal);
    console.log("Part 1 NUM dots:", (grid.toString().match(/#/g)||[]).length);
    // console.log(grid.toString());
}

function part2(grid, folds) {
    for (const [foldAxis, foldVal] of folds) {
        grid = foldAxis.toLowerCase() === 'x' ? grid.foldX(foldVal) : grid.foldY(foldVal);
    }
    console.log('PART 2 answer');
    console.log(grid.toString());
}


readFile('./day13_input.txt', 'utf8' , (err, data) => {
    if (err) {
        console.error(err)
        return
    }
    const folds = [];
    let grid = new Grid();
    data.split('\n').forEach(line => {
        if (line.startsWith('fold along')) {
            let [axis, val] = line.split('=');
            axis = axis[axis.length - 1]
            val = Number.parseInt(val);
            folds.push([axis, val]);
        } else if (line.length) {
            const [x, y] = line.split(',');
            grid.add(Number.parseInt(x), Number.parseInt(y));
        }
    });

    part1(grid, folds);
    part2(grid, folds);
});
