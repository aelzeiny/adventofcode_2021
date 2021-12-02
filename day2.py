from typing import List
from day1 import count_increments_and_decrements


def sum_sliding_window(depths: List[int], sliding_window: int = 3):
    sliding_depths = []
    for start_idx in range(0, len(depths)):
        end_idx = start_idx + sliding_window
        if end_idx - start_idx == sliding_window:
            sliding_depths.append(sum(depths[start_idx: end_idx]))
    return sliding_depths


with open('./day1_input.txt') as f:
    depths = [int(line) for line in f.readlines() if line]


if __name__ == '__main__':
    sliding_depths_sums = sum_sliding_window(depths)
    print(sliding_depths_sums)
    print(count_increments_and_decrements(sliding_depths_sums))
