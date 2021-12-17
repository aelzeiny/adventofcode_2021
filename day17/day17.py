from collections import namedtuple
import math


class Box(namedtuple('Box', ('min_x', 'min_y', 'max_x', 'max_y'))):
    def contains_x(self, x):
        return x >= self.min_x and x <= self.max_x

    def contains_y(self, y):
        return y >= self.min_y and y <= self.max_y
    
    def contains(self, x, y):
        return self.contains_x(x) and self.contains_y(y)


def fire_probe(velo_x: int, velo_y: int, target: Box):
    x, y = 0, 0
    coords = [(x, y)]
    while x <= target.max_x and y >= target.min_y:
        if target.contains(x, y):
            return True, coords
        x += velo_x
        y += velo_y
        if velo_x > 0:
            velo_x -= 1
        elif velo_x < 0:
            velo_x += 1
        velo_y -= 1
        coords.append((x, y))
    return False, coords

def render_grid(coords, target: Box):
    min_x = min(*[c[0] for c in coords], target.min_x)
    min_y = min(*[c[1] for c in coords], target.min_y)
    max_x = max(*[c[0] for c in coords], target.max_x)
    max_y = max(*[c[1] for c in coords], target.max_y)
    for y in range(max_y + 1, min_y - 1, -1):
        for x in range(min_x - 1, max_x + 1):
            char = '.'
            if target.contains(x, y):
                char = 'T'
            if (x, y) in coords:
                char = '#'
            if x == 0 and y == 0:
                char = 'S'
            print(char, end='')
        print()


def visualize_and_control(bx):
    vx = 7
    vy = 2
    while True:
        print(f'@(VX, VY): ({vx}, {vy})')
        did_hit, cs = fire_probe(vx, vy, bx)
        print('HIT' if did_hit else 'MISS')
        print(f'MAXS: ({max(x[0] for x in cs)}, {max(y[1] for y in cs)})')
        render_grid(cs, bx)
        print()
        direction = input('Enter W/A/S/D: ')
        if direction == 'w':
            vy += 1
        elif direction == 'a':
            vx -= 1
        elif direction == 's':
            vy -= 1
        elif direction == 'd':
            vx += 1


def max_height(bx):
    height = bx.min_y
    for i in range(abs(bx.min_y) + 1):
        height += i
    return height

def timestep(p, v):
    """Given min Y & velocity, solve for the number of time-steps it would take to intersect"""
    return (math.sqrt(math.pow(2 * v + 1, 2) - 8 * p) + 2 * v + 1) / 2

def all_possible_velos(bx):
    max_velo_x = abs(bx.max_x)
    max_velo_y = abs(bx.min_y)
    for vy in range(-max_velo_y, max_velo_y + 1):
        for vx in range(max_velo_x + 1):
            did_hit, _ = fire_probe(vx, vy, bx)
            if did_hit:
                yield vx, vy
    

example_bx = Box(20, -10, 30, -5)
challenge_bx = Box(138, -125, 184, -71)

print('example', max_height(example_bx))
print('challenge', max_height(challenge_bx))

print('example', len(list(all_possible_velos(example_bx))))
print('challenge', len(list(all_possible_velos(challenge_bx))))

