require 'yaml'
if ARGV.size != 1
	puts "Usage : ./graph_metrics.rb [yamlFile]"
end


yamlFile = ARGV[0]
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
			end
		end
	end
	
	p paths
	
	for k in 0...graph.length-1 do
		for i in 0...graph.length-1 do
			for j in 0...graph.length-1 do
				if (paths[i][j] == nil) && (paths[i][k] != nil && paths[k][j] != nil)
            		paths[i][j] = paths[i][k]+paths[k][j]
          		elsif (paths[i][k] != nil && paths[k][j] != nil) && (paths[i][j] > paths[i][k]+paths[k][j])
            		paths[i][j] = paths[i][k]+paths[k][j]
          		end						
			end
		end
	end
	
	p paths
	
	
}