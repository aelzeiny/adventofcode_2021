
class Array
  def median
    return nil if self.empty?
    sorted = self.sort
    len = sorted.length
    (sorted[(len - 1) / 2] + sorted[len / 2]) / 2.0
  end

  def mean
    return nil if self.empty?
    return self.sum.to_f / self.length
  end
end

def brute_force(subs)
  min_fuel = Float::INFINITY
  min_alignment = nil
  subs.each do |align_to|
    fuel = 0
    subs.each do |align_from|
      fuel += (align_from - align_to).abs
    end
    if fuel < min_fuel
      min_fuel = fuel
      min_alignment = align_to
    end
  end
  [min_fuel, min_alignment]
end

def part_one(subs)
  median_crab = subs.median
  fuel = 0
  subs.each do |sub|
    fuel += (sub - median_crab).abs
  end
  fuel
end


def weighted_brute_force(subs)
  min_fuel = Float::INFINITY
  min_alignment = nil
  (subs.min..subs.max).each do |align_to|
    fuel = 0
    subs.each do |align_from|
      delta = (align_from - align_to).abs
      fuel += delta * (delta + 1) / 2
    end
    if fuel < min_fuel
      min_fuel = fuel
      min_alignment = align_to
    end
  end
  [min_fuel, min_alignment]
end

def part_two(subs)
  mean_crab = subs.mean.round
  fuel = 0
  subs.each do |sub|
    delta = (sub - mean_crab).abs
    # p "#{sub} -> #{mean_crab}: costs #{(delta * (delta + 1)) / 2}"
    fuel += (delta * (delta + 1)) / 2
  end
  fuel
end


crab_subs = File.read("./day7_input.txt").split(",").map(&:to_i)
p part_one(crab_subs)  # Median works for part i
# p brute_force(crab_subs)  # mean works for part i
p weighted_brute_force(crab_subs)  # brute force works for part ii
p part_two(crab_subs)  # mean does not work for part ii. Probably needs a more weighted mean.
