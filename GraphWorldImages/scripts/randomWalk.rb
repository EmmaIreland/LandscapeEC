require 'yaml'
require 'matrix'

if ARGV.size != 1
  puts "Usage : ./graph_metrics.rb [yamlFile]"
end


yamlFile = ARGV[0]
node = Integer(ARGV[1])

def readGraphFile(yamlFile)
  openFile = File.open( yamlFile )
  y = YAML::load_documents( openFile ) { |graph|
    paths = Array.new(graph.length-1)
    for i in 0...graph.length-1
      paths[i] = Array.new(graph.length-1)
      for j in 0...graph.length-1
        if i==j
          paths[i][j] = 0
        elsif graph[i].include?(j)
          paths[i][j] = 1
        else paths[i][j] = 0
        end
      end
    end
  return paths
  }
end

def computeTransitionProbabilities(paths)
  for i in 0...paths.length

    sum = 0
    paths[i].each { |a| sum+=a }

    for j in 0...paths.length
      if paths[i][j] != 0
        paths[i][j] = 1.0/sum
      end
    end
  end
  return paths
end
    

def removeRowAndColumn(original_paths, node)
  paths = original_paths.map { |row| row.clone }
  for i in 0...paths.length
    if i == node
      paths.delete_at(i)
    end
  end

  for i in 0...paths.length
    for j in 0...paths.length+1
      if j == node
        paths[i].delete_at(j)
      end
    end
  end
  
  return paths
end


def computeExpectedWalkTimeToNode(paths, node)
  sub_matrix = removeRowAndColumn(paths, node)
  puts "About to invert the matrix"
  d = (Matrix.identity(sub_matrix.length) - Matrix.rows(sub_matrix))
  puts "Subtracted the matrices"
  m = d.inverse
  puts "We have inverted the matrix!"

  #distance = Matrix.zero(paths.length)
  
  row = (0...m.row_size).map do |i|
    sum = 0
    for j in 0...m.row_size
      sum += m[i, j]
    end

    #distance[i, node] = sum
    sum
  end
  
  return row[0...node] + [0] + row[node..-1]
end
  
p paths = readGraphFile(ARGV[0])
p paths = computeTransitionProbabilities(paths)

puts "About to compute Walk Times"

expectedWalkTimes = (0...paths.length).map do |i|
  r = computeExpectedWalkTimeToNode(paths, i)
  p r
  r
end

p eccentricities = expectedWalkTimes.transpose.map { |r| r.max }
p radius = eccentricities.min
p diameter = eccentricities.max