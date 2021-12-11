using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace day11
{
    class Octo
    {
        private static readonly int FLASH_ENERGY = 10;
        private bool _flashedOnTurn = false;
        
        public Octo(int energy)
        {
            this.Energy = energy;
        }

        public void Step()
        {
            Energy++;
            if (Energy >= FLASH_ENERGY)
            {
                Flash();
            }
        }

        public void Reset()
        {
            if (_flashedOnTurn)
            {
                Energy = 0;
                _flashedOnTurn = false;
            }
        }

        public void Flash()
        {
            if (_flashedOnTurn)
                return;
            
            _flashedOnTurn = true;
            
            foreach (var o in Neighbors)
            {
                o.Energy++;
                if (o.IsFlashing && !o._flashedOnTurn)
                {
                    o.Flash();
                }
            }

        }

        public bool IsFlashing
        {
            get { return Energy >= FLASH_ENERGY; }
        }
        
        public int Energy { get; protected set; }
        
        public List<Octo> Neighbors { get; set; }
        
        public Tuple<int, int> rc { get; set; }
    }
    
    class Day11
    {
        private static Octo[,] BuildOctoGrid(List<List<int>> energies)
        {
            var octoGrid = new Octo[energies.Count, energies[0].Count];
            for (int i = 0; i < energies.Count; i++)
                for (int j = 0; j < energies.Count; j++)
                    octoGrid[i, j] = new Octo(energies[i][j]);
            
            for (int r = 0; r < energies.Count; r++)
            {
                for (int c = 0; c < energies.Count; c++)
                {
                    var neighbors = new List<Octo>();
                    for (int rr = r - 1; rr <= r + 1; rr++)
                    {
                        for (int cc = c - 1; cc <= c + 1; cc++)
                        {
                            if (rr == r && cc == c)
                                continue;
                            if (rr >= 0 && rr < octoGrid.GetLength(0) 
                                        && cc >= 0 && cc < octoGrid.GetLength(1))
                                neighbors.Add(octoGrid[rr, cc]);
                        }
                    }
                    octoGrid[r, c].Neighbors = neighbors;
                }
            }

            return octoGrid;
        }
        
        static void Main(string[] args)
        {
            var energyGrid = File.ReadLines("day11_input.txt").Select(
                l => l.Select(
                    energyChar => int.Parse(energyChar.ToString())
                ).ToList()
            ).ToList();
            Part1(BuildOctoGrid(energyGrid), 195);
            Part2(BuildOctoGrid(energyGrid));
        }

        private static void Part1(Octo[,] octos, int numSteps)
        {
            long numFlashes = 0;
            for (int i = 0; i < numSteps; i++)
            {
                for (var r = 0; r < octos.GetLength(0); r++)
                for (var c = 0; c < octos.GetLength(1); c++)
                    octos[r, c].Step();
                
                Console.WriteLine();
                Console.WriteLine($"Step {i + 1}:");
                for (var r = 0; r < octos.GetLength(0); r++)
                {
                    for (var c = 0; c < octos.GetLength(1); c++)
                    {
                        Console.ForegroundColor = ConsoleColor.Black;
                        if (octos[r, c].IsFlashing)
                        {
                            numFlashes++;
                            Console.ForegroundColor = ConsoleColor.Cyan;
                        }

                        octos[r, c].Reset();
                        Console.Write(octos[r, c].Energy);
                    }
                    Console.WriteLine();
                }
            }

            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine($"\nPart 1 Number of Flashes: {numFlashes}");
        }
        
        private static void Part2(Octo[,] octos)
        {
            bool allOctosFlashed = false;
            int step;
            for (step = 0; !allOctosFlashed && step < 1000; step++)
            {
                for (var r = 0; r < octos.GetLength(0); r++)
                    for (var c = 0; c < octos.GetLength(1); c++)
                        octos[r, c].Step();
                
                Console.WriteLine();
                Console.WriteLine($"Step {step + 1}:");

                allOctosFlashed = true;
                for (var r = 0; r < octos.GetLength(0); r++)
                {
                    for (var c = 0; c < octos.GetLength(1); c++)
                    {
                        Console.ForegroundColor = (!octos[r, c].IsFlashing) ? ConsoleColor.Black : ConsoleColor.Cyan;
                        if (!octos[r, c].IsFlashing)
                            allOctosFlashed = false;
                        octos[r, c].Reset();
                        Console.Write(octos[r, c].Energy);
                    }
                    Console.WriteLine();
                }
            }

            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine($"\nPart 2 Number of Steps to Sync: {step}");
        }
    }
}