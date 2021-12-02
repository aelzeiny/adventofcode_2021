from enum import Enum
from typing import List, Tuple


class Direction(Enum):
    FORWARD = "forward"
    DOWN = "down"
    UP = "up"


def navigate(commands: List[Tuple[Direction, int]]):
    x = 0
    y = 0
    for direction, dist in commands:
        if direction == Direction.DOWN:
            y += dist
        elif direction == Direction.UP:
            y -= dist
        elif direction == Direction.FORWARD:
            x += dist
    return x, y


def navigate_with_aim(commands: List[Tuple[Direction, int]]):
    aim = 0
    x = 0
    y = 0
    for direction, units in commands:
        if direction == Direction.DOWN:
            aim += units
        elif direction == Direction.UP:
            aim -= units
        elif direction == Direction.FORWARD:
            x += units
            y += aim * units
    return x, y


if __name__ == '__main__':
    with open('./day2_input.txt') as f:
        sub_commands = [
            (Direction(l.split()[0]), int(l.split()[1]))
            for l in f.readlines()
            if l
        ]

    dx, dy = navigate(sub_commands)
    print('normal', dx, dy, dx * dy)

    dx, dy = navigate_with_aim(sub_commands)
    print('aimed', dx, dy, dx * dy)
