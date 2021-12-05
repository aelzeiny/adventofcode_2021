package main

import (
	"bufio"
	"log"
	"os"
	"strconv"
	"strings"
)

type Coord struct {
	r int
	c int
}

type CoordPair struct {
	from Coord
	to   Coord
}

// I wish I had python/C# generators
func traverse1D(from int, to int) []int {
	var answer []int
	if from < to {
		answer = make([]int, 0, to-from+1)
		for i := from; i <= to; i++ {
			answer = append(answer, i)
		}
	} else {
		answer = make([]int, 0, from-to+1)
		for i := from; i >= to; i-- {
			answer = append(answer, i)
		}
	}
	return answer
}

func (pair *CoordPair) traverse(traversalFunc func(x Coord)) {
	rTrav := traverse1D(pair.from.r, pair.to.r)
	cTrav := traverse1D(pair.from.c, pair.to.c)

	for i := 0; i < len(rTrav) || i < len(cTrav); i++ {
		currR := pair.to.r
		currC := pair.to.c
		if i < len(rTrav) {
			currR = rTrav[i]
		}
		if i < len(cTrav) {
			currC = cTrav[i]
		}
		traversalFunc(Coord{currR, currC})
	}
}

func partOne(coords []CoordPair) {
	traveledCoords := make(map[Coord]int)
	for _, pair := range coords {
		//log.Printf("Traversing %v -> %v", pair.from, pair.to)
		if pair.from.r == pair.to.r || pair.from.c == pair.to.c {
			pair.traverse(func(x Coord) {
				//log.Printf("\t@%v", x)
				traveledCoords[x]++
			})
		}
	}

	intersections := 0
	for _, numTraversals := range traveledCoords {
		if numTraversals > 1 {
			intersections++
		}
	}

	log.Printf("Num intersections for only horizontal & vertical lines: %v", intersections)
}

func partTwo(coords []CoordPair) {
	traveledCoords := make(map[Coord]int)
	for _, pair := range coords {
		//log.Printf("Traversing %v -> %v", pair.from, pair.to)
		pair.traverse(func(x Coord) {
			//log.Printf("\t@%v", x)
			traveledCoords[x]++
		})
	}

	intersections := 0
	for _, numTraversals := range traveledCoords {
		if numTraversals > 1 {
			intersections++
		}
	}

	log.Printf("Num intersections for all lines: %v", intersections)
}

func scannedLineToCoord(coordString string) Coord {
	coordSplit := strings.Split(coordString, ",")
	x, errX := strconv.Atoi(strings.TrimSpace(coordSplit[0]))
	y, errY := strconv.Atoi(strings.TrimSpace(coordSplit[1]))
	if errX != nil || errY != nil {
		log.Fatalf("Cannot parse line: %v", coordSplit)
	}
	return Coord{x, y}
}

func main() {
	file, err := os.Open("./day5_input.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatal("Cannot close AoC file")
		}
	}(file)

	scanner := bufio.NewScanner(file)

	var coords []CoordPair

	for scanner.Scan() {
		line := scanner.Text()
		fromTo := strings.Split(line, "->")
		from := scannedLineToCoord(fromTo[0])
		to := scannedLineToCoord(fromTo[1])
		log.Printf("%v -> %v", from, to)
		coords = append(coords, CoordPair{from, to})
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	partOne(coords)
	partTwo(coords)
}
