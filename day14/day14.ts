const {readFile} = require('fs');

class PolymerTemplate {
  private counts: Map<string, number> = new Map<string, number>();
  public letters: Map<string, number> = new Map<string, number>();

  constructor(template?: string) {
    if (template !== undefined) {
      for (let i = 0; i < template.length - 1; i++) {
        this.increment(template.substring(i, i + 2));
      }
      for (let c of template) {
        this.incrementLetter(c)
      }
    }
  }

  private increment(polymer: string, count: number = 1) {
    this.counts.set(polymer, (this.counts.get(polymer) || 0) + count);
  }

  private incrementLetter(c: string, count: number = 1) {
    this.letters.set(c, (this.letters.get(c) || 0) + count);
  }

  step(polymerMap: Map<string, string>): PolymerTemplate {
    let newTemplate = new PolymerTemplate();
    newTemplate.letters = new Map(this.letters);
    for (let [pair, count] of this.counts) {
      const newPairedPoly = polymerMap.get(pair);
      newTemplate.increment(pair[0] + newPairedPoly!, count);
      newTemplate.increment(newPairedPoly! + pair[1], count);
      newTemplate.incrementLetter(newPairedPoly!, count);
    }
    return newTemplate;
  }
}

function solve(polyPairs: Map<string, string>, template: string, steps: number) {
  let temp = new PolymerTemplate(template);
  console.log(`Template: ${template}`);
  for (let i = 0; i < steps; i++) {
    temp = temp.step(polyPairs);
    console.log(`After step ${i + 1}:`);
    console.log(temp.letters);
  }
  console.log('Finished');
  const letterCounts = Array.from(temp.letters.entries()).sort(
    (i: [string, number], j: [string, number]) => i[1] - j[1]
  );
  console.log('Most common', letterCounts[letterCounts.length - 1], 'least common', letterCounts[0]);
}

readFile('./day14_input.txt', 'utf8' , (err: Error, data: string) => {
  if (err) {
    console.error(err)
    return
  }
  let polymerTemplate: string = '';
  const polyPairs = new Map<string, string>();
  data.toString().split('\n').forEach((line, i) => {
    if (i === 0) {
      polymerTemplate = line;
    } else if (line.length) {
      let [pair, poly] = line.split('->');
      pair = pair.trim();
      poly = poly.trim();
      polyPairs.set(pair, poly);
    }
  });
  const part1 = () => {
    solve(polyPairs, polymerTemplate, 10);
  };
  const part2 = () => {
    solve(polyPairs, polymerTemplate, 40);
  }
  part1();
  part2();
});
