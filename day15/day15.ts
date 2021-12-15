import {readFile} from "fs";

type Neighbor = Map<number, number>;

class Graph {
  private verts = new Map<number, Neighbor>();
  private width: number;

  constructor(width: number) {
    this.width = width;
  }

  add(fromR: number, fromC: number, toR: number, toC: number, edge: number) {
    const from = this.rcToKey(fromR, fromC);
    const to = this.rcToKey(toR, toC);
    if (!this.verts.has(from))
      this.verts.set(from, new Map<number, number>());
    if (!this.verts.has(to))
      this.verts.set(to, new Map<number, number>());
    (this.verts.get(from)!).set(to, edge);
    (this.verts.get(to)!).set(from, edge);
  }

  private rcToKey(r: number, c: number): number {
    return r * this.width + c;
  }

  private keyToRC(n: number): [number, number] {
    return [Math.floor(n / this.width), n % this.width];
  }

  private shortestDistanceNode(distances: Map<number, number>, visited: Set<number>): number | undefined {
    if (distances.size === 0)
      throw new TypeError("should be a full array");

    let shortest: number | undefined = undefined;

    for (let [node, nodeDist] of distances) {
      let currentIsShortest =
        shortest === undefined || nodeDist < (distances.get(shortest)!);
      if (currentIsShortest && !visited.has(node)) {
        shortest = node;
      }
    }
    return shortest;
  }

  dijistrasRisk(): number {
    const distances = new Map<number, number>();
    let start = 0;
    let end = Math.max(...Array.from(this.verts.keys()));
    distances.set(end, Number.MAX_VALUE);
    for (let [e, dist] of this.verts.get(start)!) {
      distances.set(e, dist);
    }

    let parents = new Map<number, number|null>();
    for (let [child, _] of this.verts.get(start)!) {
      parents.set(child, 0);
    }
    let visited: Set<number> = new Set<number>();
    let node: number = (this.shortestDistanceNode(distances, visited)!);

    while (node) {
      // find its distance from the start node & its child nodes
      let distance = distances.get(node)!;
      let children = this.verts.get(node)!;
      // for each of those child nodes
      for (let [child, childDistToStart] of children) {
        // make sure each child node is not the start node
        if (child === start) {
          continue;
        } else {
          // save the distance from the start node to the child node
          let newdistance = distance + childDistToStart;
          const oldDist = distances.get(child);
          // if there's no recorded distance from the start node to the child node in the distances object
          // or if the recorded distance is shorter than the previously stored distance from the start node to the child node
          // save the distance to the object
          // record the path
          if (oldDist === undefined || oldDist! > newdistance) {
            distances.set(child, newdistance);
            parents.set(child, node);
          }
        }
      }
      // move the node to the visited set
      visited.add(node);
      // move to the nearest neighbor node
      node = this.shortestDistanceNode(distances, visited)!;
    }

    let prevParent = end;
    let parent: number | undefined = parents.get(end)!;
    let risk = 0;
    let shortestPath = [this.keyToRC(end)];
    while (parent !== undefined) {
      risk += this.verts.get(prevParent)!.get(parent!)!;
      shortestPath.push(this.keyToRC(parent!));
      prevParent = parent!;
      parent = parents.get(parent)!;
    }
    console.table(shortestPath.reverse());
    return risk;
  }
}


readFile('./day15_input.txt', 'utf8', (err: Error | null, data: string | Buffer) => {
  if (err) {
    console.error(err)
    return
  }
  const grid = data.toString().split('\n').map(line => Array.from(line).map(i => Number.parseInt(i)));

  let graph = new Graph(grid.length);
  for (let r = 0; r < grid.length; r++) {
    for (let c = 0; c < grid[r].length; c++) {
      const weight = grid[r][c];
      let lr = r - 1;
      let rr = r + 1;
      let lc = c - 1;
      let rc = c + 1;
      if (lr >= 0) {
        graph.add(r, c, lr, c, weight);
      }
      if (rr < grid.length) {
        graph.add(r, c, rr, c, weight);
      }
      if (lc >= 0) {
        graph.add(r, c, r, lc, weight);
      }
      if (rc < grid[r].length) {
        graph.add(r, c, r, rc, weight);
      }
    }
  }
  const risk = graph.dijistrasRisk();
  console.log('MIN RISK PART 1: ', risk);
});