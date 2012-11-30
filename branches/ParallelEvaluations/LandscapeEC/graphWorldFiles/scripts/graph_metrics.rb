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
	
	#p paths
	
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
	
	puts "shortest path:"
	p paths
	
	
	
  # paths array now has -1 in place of nil (because .max doesn't work with nil)
	for i in 0...paths.length
	  for j in 0...paths.length
	    if (paths[i][j] == nil)
	      paths[i][j] = -1
      end
	  end
	end
	
	# finds the connected components (using breadth first search)
  listOfAll = []
  componentElements = []
  
  for i in 0...graph.length-1
    if !listOfAll.include?(i)
      componentElements.push(i)
      queue = [i]
      visited = {i => true}
      
      while(!queue.empty?)
        node = queue.pop()
        graph[node].each do |child|
        
          if visited[child] != true then
            listOfAll.push(child)
            componentElements.push(child)
            queue.push(child)
            visited[child] = true
          end
        
        end
      end

      puts "\n" + "Component elements: "
      p componentElements
      
      puts "Component size: " + componentElements.length.to_s()
      
      puts "   eccentricities:"
      for i in 0...componentElements.length
        shortPathArray = paths[componentElements[i]]
        puts "      " + componentElements[i].to_s() + ": " + shortPathArray.max.to_s()
      end
      
      componentElements.clear 
    end
  end
}