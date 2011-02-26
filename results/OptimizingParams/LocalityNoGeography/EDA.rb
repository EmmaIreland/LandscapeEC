#!/usr/bin/ruby

problem_files = ["exampleproblems/uf75-015.cnf", "exampleproblems/uf75-05.cnf", "exampleproblems/uf100-0193.cnf"]
problem_file_position = 7

lines = File.open("LnoG_2011_02_26_02_42_26.R").readlines
lines = lines.map { |l| l.chomp }
lines = lines.map { |l| l.split }

labels = lines[0]

counts = Hash.new
num_successes = Hash.new

problem_files.each do |problem_file|
  counts[problem_file] = Hash.new
  num_successes[problem_file] = 0
  labels.size.times do |index|
    counts[problem_file][labels[index]] = Hash.new { |hash, key| hash[key] = 0 }
  end
end

lines.each do |l|
  if l[-3] == "true"
    prob_file = l[problem_file_position]
    num_successes[prob_file] += 1
    l.size.times do |index|
      counts[prob_file][labels[index]][l[index]] += 1
    end
  end
end

#counts.each_key do |key|
#  p counts[key]["STARTING_POPULATION"]
#end

def draw_from_distribution(count_hash, num_successes)
  #p count_hash
  #p num_successes
  r = rand(num_successes)
  total = 0
  count_hash.each_pair do |option, count|
    total += count
    if total > r
      #puts "returning #{option}"
      return option
    end
  end
  #puts "Didn't find anything"
end

@numeric_params = ["CARRYING_CAPACITY", "REPRODUCTION_RATE", "ELITE_PROPORTION", "WORLD_DIMENSIONS"]
@non_numeric_params = ["PROBLEM_FILE", "PROMOTE_SMALL_POPULATIONS", "TOROIDAL", "GEOGRAPHY_TYPE", "STARTING_POPULATION"]
@params_to_choose = @numeric_params + @non_numeric_params

def print_properties(choices, time_string, file_number)
  filename = time_string + "__" + file_number.to_s + ".properties"
  File.open(filename, "w") do |file|
    file.puts '#include "default.properties"'
    @params_to_choose.each do |param|
      file.puts "#{param}=#{choices[param]}"
    end
  end
end

def fiddle_CARRYING_CAPACITY(carrying_capacity)
  carrying_capacity = carrying_capacity.to_i
  result = carrying_capacity + rand(5) - 2
  result = 1 if result < 1
  return result
end

# We may want to try multiplying by 2^x, where x in [-1, 1] (or a smaller
# range around 0). This would make doubling as likely as halving, which would
# allow a paramter to grow pretty quickly if there was support for that.

def fiddle_REPRODUCTION_RATE(reproduction_rate)
  reproduction_rate = reproduction_rate.to_f
  result = reproduction_rate * (1 + (rand-0.5)/2)
  result = result.truncate
  result = 1 if result < 1
  return result
end

def fiddle_ELITE_PROPORTION(elite_proportion)
  elite_proportion = elite_proportion.to_f
  result = elite_proportion * (1 + (rand-0.5)/5)
  result = (result*100).round / 100.0
  result = 0 if result < 0
  result = 1 if result > 1
  return result
end

def fiddle_WORLD_DIMENSIONS(world_dimensions)
  size = world_dimensions.split(",")[0].to_i
  x_size = size + rand(5) - 2
  x_size = 1 if x_size < 1
  y_size = size + rand(5) - 2
  y_size = 1 if y_size < 1
  return "#{x_size},#{y_size}"
end

num_new_configs = 50
now = Time.now.strftime("%Y_%m_%d_%H_%M_%S")

file_number = 0
problem_files.each do |problem_file|
  num_new_configs.times do
    result = Hash.new
    labels.each do |label|
      cnts = counts[problem_file][label]
      choice = draw_from_distribution(cnts, num_successes[problem_file])
      if @numeric_params.member?(label)
        choice = method("fiddle_#{label}").call(choice)
      end
      result[label] = choice
      # puts "Label = #{label}, choice = #{choice}"
    end
    print_properties(result, now, file_number)
    file_number += 1
  end
end