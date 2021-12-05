from typing import List


def count_increments_and_decrements(depths: List[int]):
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


def sum_sliding_window(depths: List[int], sliding_window: int = 3):
    sliding_depths = []
    for start_idx in range(0, len(depths)):
        end_idx = start_idx + sliding_window
        if end_idx - start_idx == sliding_window:
            sliding_depths.append(sum(depths[start_idx: end_idx]))
    return sliding_depths


if __name__ == '__main__':
    with open('./day1_input.txt') as f:
        terrain_depths = [int(line) for line in f.readlines() if line]

    print(count_increments_and_decrements(terrain_depths))
    sliding_depths_sums = sum_sliding_window(terrain_depths)
    print(sliding_depths_sums)
    print(count_increments_and_decrements(sliding_depths_sums))


