def count_increments_and_decrements(depths):
    last = None
    num_increments = 0
    num_decrements = 0
    for depth in depths:
        curr = int(depth)
        if last is None:
            print('--')
        elif last < curr:
            print('(increased)')
            num_increments += 1
        elif last > curr:
            print('(decreased)')
            num_decrements += 1
        else:
            print('(same)')
        last = curr

    return num_increments, num_decrements


if __name__ == '__main__':
    with open('day1_input.txt') as f:
        print(count_increments_and_decrements([int(l) for l in f.readlines()]))

