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

type Board struct {
	cells     [][]int
	activated map[Coord]bool
}

func newBoard(cells [][]int) Board {
	return Board{
		cells:     cells,
		activated: map[Coord]bool{},
	}
}

func (b *Board) reset() {
	b.activated = map[Coord]bool{}
}

func (b *Board) activate(num int) {
	for r, row := range b.cells {
		for c, cell := range row {
			if cell == num {
				b.activated[Coord{r, c}] = true
			}
		}
	}
}

func (b *Board) buildCellGroups() (cellGroups [][]Coord) {
	// Add horizontal Cell Groups
	for r, row := range b.cells {
		horizontal := make([]Coord, len(row))
		for c := range row {
			horizontal[c] = Coord{r, c}
		}
		cellGroups = append(cellGroups, horizontal)
	}
	// Add vertical Cell Groups
	for c := range b.cells[0] {
		vertical := make([]Coord, 0, len(b.cells[0]))
		for r := range b.cells {
			vertical = append(vertical, Coord{r, c})
		}
		cellGroups = append(cellGroups, vertical)
	}
	// NOTE: In retrospect, diagonals were never required like in real bingo...
	// Add forward diagonal Cell Group (\)
	//forwardDiag := make([]Coord, 0, len(b.cells))
	//for r, c := 0, 0; r < len(b.cells) && c < len(b.cells[0]); r, c = r+1, c+1 {
	//	forwardDiag = append(forwardDiag, Coord{r, c})
	//}
	//cellGroups = append(cellGroups, forwardDiag)
	//// Add backwards diagonal Cell Groups (/)
	//backwardDiag := make([]Coord, 0, len(b.cells))
	//for r, c := 0, len(b.cells[0])-1; r < len(b.cells) && c >= 0; r, c = r+1, c-1 {
	//	backwardDiag = append(backwardDiag, Coord{r, c})
	//}
	//cellGroups = append(cellGroups, backwardDiag)
	return
}

func (b *Board) isWinner() bool {
	cellGroups := b.buildCellGroups()
	for _, cellGroup := range cellGroups {
		allActivated := true
		for _, cell := range cellGroup {
			if !b.activated[cell] {
				allActivated = false
				break
			}
		}
		if allActivated {
			return true
		}
	}
	return false
}

func (b *Board) sumUnactivatedCells() int {
	sum := 0
	for r, row := range b.cells {
		for c, cell := range row {
			if !b.activated[Coord{r, c}] {
				sum += cell
			}
		}
	}
	return sum
}

func lineToInt(parsedLine []string) []int {
	intLine := make([]int, 0, len(parsedLine))
	for _, numStr := range parsedLine {
		num, err := strconv.Atoi(numStr)
		if err == nil {
			intLine = append(intLine, num)
		}
	}
	return intLine
}

func partOne(moves []int, boards []Board) *Board {
	for _, move := range moves {
		for _, b := range boards {
			b.activate(move)

			if b.isWinner() {
				unactivatedSum := b.sumUnactivatedCells()
				log.Printf("Part 1 Winner Board! @%v * %v = %v", move, unactivatedSum, move*unactivatedSum)
				return &b
			}
		}
	}
	log.Panicln("No winning board found?")
	return nil
}

func partTwo(moves []int, boards []Board) *Board {
	var lastWinningBoard *Board
	boardsThatWon := make(map[*Board]bool, len(boards))
	for _, move := range moves {
		for bIdx, b := range boards {
			b.activate(move)

			if !boardsThatWon[&boards[bIdx]] && b.isWinner() {
				boardsThatWon[&boards[bIdx]] = true
				unactivatedSum := b.sumUnactivatedCells()
				log.Printf("Part 2 Another Board Won! @%v * %v = %v", move, unactivatedSum, move*unactivatedSum)
				lastWinningBoard = &b
			}
		}
	}
	if lastWinningBoard == nil {
		log.Panicln("No winning board found?")
	}
	return lastWinningBoard
}

func main() {
	file, err := os.Open("./day4_input.txt")
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
	var boards []Board

	var moves []int
	for i := 0; scanner.Scan(); i++ {
		if i == 0 {
			moves = lineToInt(strings.Split(scanner.Text(), ","))
		} else {
			line := scanner.Text()
			if len(line) != 0 {
				parsedLine := lineToInt(strings.Split(line, " "))
				boards[len(boards)-1].cells = append(boards[len(boards)-1].cells, parsedLine)
			} else {
				boards = append(boards, newBoard([][]int{}))
			}
		}
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	//log.Printf("Moves: %v", moves)
	//for _, b := range boards {
	//	log.Printf("Board: %v", b)
	//}

	partOne(moves, boards)
	for i := range boards {
		boards[i].reset()
	}
	partTwo(moves, boards)
}
