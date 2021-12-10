using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace day10
{
    enum LineState
    {
        Complete,
        Corrupted,
        Incomplete
    }

    class Line
    {
        private string line;
        private static readonly HashSet<char> allOpenBrackets = new("([{<");
        private static readonly HashSet<char> allClosedBrackets = new(")]}>");
        
        public Line(string line)
        {
            this.line = line;
        }

        private Tuple<int, Stack<char>> Analyze()
        {
            var brackets = new Stack<char>();
            for (var i = 0; i < line.Length; i++)
            {
                var chr = line[i];
                if (allOpenBrackets.Contains(chr))
                {
                    brackets.Push(chr);
                } 
                else if (allClosedBrackets.Contains(chr))
                {
                    var didPop = brackets.TryPop(out var lastOpenBracket);
                    if (!didPop)
                        return new Tuple<int, Stack<char>>(i, brackets);

                    var closingBracket = MatchOpeningBracket(lastOpenBracket);
                    if (!closingBracket.HasValue || closingBracket.Value != chr)
                        return new Tuple<int, Stack<char>>(i, brackets);
                }
            }

            return new Tuple<int, Stack<char>>(-1, brackets);
        }

        public char? FirstIllegalChar()
        {
            var analyzed = Analyze();
            var lastIndex = analyzed.Item1;
            if (lastIndex >= 0)
                return line[lastIndex];
            return null;
        }

        public IEnumerable<char> RemainingBrackets()
        {
            var remainingStack = Analyze().Item2;
            var ans = remainingStack.Select(ch => MatchOpeningBracket(ch).Value).ToList();
            Console.WriteLine(string.Join("", ans));
            return ans;
        }

        private char? MatchOpeningBracket(char bracket)
        {
            if (bracket == '(') return ')';
            if (bracket == '[') return ']';
            if (bracket == '{') return '}';
            if (bracket == '<') return '>';
            return null;
        }

        public LineState LineState
        {
            get
            {
                var analyzed = Analyze();
                var lastIndex = analyzed.Item1;
                var remainingStack = analyzed.Item2;
                if (lastIndex >= 0)
                    return LineState.Corrupted;
                return remainingStack.Any() ? LineState.Incomplete : LineState.Complete;
            }
        }

        public override string ToString()
        {
            return line;
        }
    }
    
    class Day10
    {
        public static void Main(string[] args)
        {
            var lines = new List<Line>();
            foreach (string line in File.ReadLines("day10_input.txt"))
            {
                lines.Add(new Line(line));
            } 
            Part1(lines);
            Part2(lines);
        }
        
        public static void Part1(List<Line> lines)
        {
            var points = 0;
            foreach (var line in lines)
            {
                var incompleteChar = line.FirstIllegalChar();
                if (!incompleteChar.HasValue) 
                    continue;
                
                switch (incompleteChar.Value)
                {
                    case ')':
                        points += 3;
                        break;
                    case ']':
                        points += 57;
                        break;
                    case '}':
                        points += 1197;
                        break;
                    case '>':
                        points += 25137;
                        break;
                    default:
                        throw new ArgumentException("Was not expecting a " + incompleteChar.Value);
                }
            }
            Console.WriteLine($"Part 1 Score: {points}");
        }
        
        public static void Part2(List<Line> lines)
        {
            var allPoints = new List<long>(lines.Count);
            foreach (var line in lines)
            {
                if (line.LineState != LineState.Incomplete && line.LineState != LineState.Complete)
                    continue;
                
                var points = 0L;
                foreach (var closingPair in line.RemainingBrackets())
                {
                    switch (closingPair)
                    {
                        case ')':
                            points = points * 5 + 1;
                            break;
                        case ']':
                            points = points * 5 + 2;
                            break;
                        case '}':
                            points = points * 5 + 3;
                            break;
                        case '>':
                            points = points * 5 + 4;
                            break;
                        default:
                            throw new ArgumentException("Was not expecting a " + closingPair);
                    }
                }
                allPoints.Add(points);
            }
            allPoints.Sort();
            Console.WriteLine($"Part 2 Score: {allPoints[allPoints.Count / 2]}");
        }
    }
}