class Lanterfish
    def initialize(time_to_spawn=8, reset_time=6)
        @time_to_spawn = time_to_spawn
        @reset_time = reset_time
    end

    def tick
        @time_to_spawn -= 1
        if @time_to_spawn < 0
            @time_to_spawn = @reset_time
            return true
        end
        return false
    end

    def to_s
        return @time_to_spawn.to_s
    end
end


def part_one(days, fish_input)
    fishies = fish_input.map {|i| Lanterfish.new(i)}

    days.times do |i|
        # puts "Day #{i}: #{fishies.join(",")}"
        new_fishies = []
        fishies.each do |f|
            if f.tick
                new_fishies << Lanterfish.new
            end
        end
        fishies.concat(new_fishies)
    end

    p "#{fishies.count} fishes after #{days} days"
end


def part_two(days, fishies)
    future_fishies = [0] * 9
    fishies.each do |f|
        future_fishies[f] += 1
    end

    days.times do |d|
        num_fish_to_spawn = future_fishies.shift
        future_fishies << num_fish_to_spawn
        future_fishies[6] += num_fish_to_spawn
        # p "@#{d + 1}: #{future_fishies.join(',')}"
    end

    p "#{future_fishies.sum} fishes after #{days} days"
end


fish_input = File.read("./day06_input.txt").split(",").map {|s| s.to_i}
part_one(80, fish_input)
part_two(80, fish_input)
part_two(256, fish_input)