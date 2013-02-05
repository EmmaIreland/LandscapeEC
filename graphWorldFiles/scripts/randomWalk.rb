require 'yaml'
require 'matrix'

if ARGV.size != 1
  puts "Usage : ./graph_metrics.rb [yamlFile]"
end


yamlFile = ARGV[0]
node = Integer(ARGV[1])

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
  
    for i in 0...paths.length-1
      
      sum = 0
      paths[i].each { |a| sum+=a }
      
      for j in 0...paths.length-1
        if paths[i][j] != 0
          paths[i][j] = 1.0/sum
        end
      end
    end
    
    for i in 0...paths.length-1
      if i == node
        paths.delete_at(i)
      end
    end
    
    for i in 0...paths.length
      for j in 0...paths.length
        if j == node
          paths[i].delete_at(j)
        end
      end
    end

    p paths
        
  (Matrix.identity(paths.length-2) - Matrix[paths]).inverse
  p Matrix.column_vector(paths.length-2).each { |e| e=1 }
   
    
    matrix = Matrix[paths]
  p matrix
}