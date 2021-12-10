from typing import List, Dict, Tuple
from collections import defaultdict


def binary_counter_to_gamma_and_epsilon(binary_counters: Dict[int, int]) -> Tuple[int, int]:
    gamma = 0
    alpha = 0
    for counter in binary_counters:
        gamma <<= 1
        alpha <<= 1
        if counter[1] > counter[0]:
            gamma |= 1
        else:
            alpha |= 1
    return gamma, alpha


def get_binary_counter_at_pos(binary_nums: List[List[int]], pos: int):
    binary_counter = defaultdict(int)
    for binary in binary_nums:
        binary_counter[binary[pos]] += 1
    return binary_counter


def get_binary_counters(binary_nums: List[List[int]]) -> Dict[int, int]:
    dim = len(binary_nums[0])
    binary_counters = [
        get_binary_counter_at_pos(binary_nums, i)
        for i in range(dim)
    ]
    return binary_counters


def part_one(binary_nums: List[List[int]]):
    binary_counters = get_binary_counters(binary_nums)
    sub_gamma, sub_epsilon = binary_counter_to_gamma_and_epsilon(binary_counters)
    print('Part 1: ', sub_gamma * sub_epsilon)


def filtering_calculator(binary_nums: List[List[int]], filter_type: str):
    filtered_nums = binary_nums
    dim = len(filtered_nums[0])
    for i in range(dim):
        counter = get_binary_counter_at_pos(filtered_nums, i)
        if filter_type == 'oxygen':
            filtering_criteria = 1 if counter[1] >= counter[0] else 0
        elif filter_type == 'co2':
            filtering_criteria = 0 if counter[0] <= counter[1] else 1
        else:
            raise ValueError('Unknown Type')

        filtered_nums = [
            b for b in filtered_nums
            if b[i] == filtering_criteria
        ]
        if len(filtered_nums) == 1:
            return filtered_nums[0]
    raise ValueError('Incomplete dataset?')


def list_to_decimal(nums: List[int]):
    answer = 0
    for num in nums:
        answer <<= 1
        answer |= num
    return answer


def part_two(binary_nums: List[List[int]]):
    oxygen_score = filtering_calculator(binary_nums, 'oxygen')
    co2_score = filtering_calculator(binary_nums, 'co2')
    print('Part 2:', list_to_decimal(oxygen_score) * list_to_decimal(co2_score))


if __name__ == '__main__':
    with open('./day03_input.txt', 'r') as input_file:
        sub_output = [
            [int(dig) for dig in binary.strip()]
            for binary in input_file.readlines()
            if binary
        ]

    part_one(sub_output)
    part_two(sub_output)
