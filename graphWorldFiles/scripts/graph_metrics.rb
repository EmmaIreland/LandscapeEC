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
	
	#puts "shortest path:"
	#p paths
	
	
	
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
  
  eccentricitiesArray = []
  dataPoints = []
  
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

      #puts "\n" + "Component elements: "
      #p componentElements
      puts "\n"
      puts "Component size: " + componentElements.length.to_s()
      
      #puts "   eccentricities:"
      i = 0
      while i < componentElements.length
        shortPathArray = paths[componentElements[i]]
        maximum = shortPathArray.max
        # print from node # to node # and eccentricity of those nodes
        #puts "      " + componentElements[i].to_s() + "  " + shortPathArray.index(maximum).to_s() + ": " + shortPathArray.max.to_s()
        eccentricitiesArray.push(shortPathArray.max)
        shortPathArray[shortPathArray.index(maximum)] = -1
        if !shortPathArray.include?(maximum)
          i = i + 1
        end
      end
      
      componentElements.clear
      sorted = eccentricitiesArray.sort
      
      #radius
      dataPoints.push(sorted.min)
      
      if sorted.length % 2 == 0 # length is even
        #25% quartile
        lenOfHalf = (sorted.length/2)
        lowPosition1 = (lenOfHalf/2) - 1
        lowPosition2 = (lenOfHalf/2)
        lowQuart = (sorted[lowPosition1] + sorted[lowPosition2]) / 2.0
        #median
        medPosition1 = lenOfHalf - 1
        medPosition2 = lenOfHalf
        median = (sorted[medPosition1] + sorted[medPosition2]) / 2.0
        #75% quartile
        upPosition1 = lenOfHalf
        upPosition2 = sorted.length - 1
        upQuart = (sorted[upPosition1] + sorted[upPosition2]) / 2.0
        
        dataPoints.push(lowQuart)
        dataPoints.push(median)
        dataPoints.push(upQuart)
      else # length is odd
        #25% quartile
        lenOfHalf = ((sorted.length + 1) / 2) - 1
        lowPosition = (lenOfHalf + 1) / 2
        lowQuart = sorted[lowPosition]
        #median
        medPosition = (sorted.length + 1) / 2
        median = sorted[medPosition]
        #75% quartile
        upPosition = (lenOfHalf + 1)
        upQuart = sorted[upPosition]
        
        dataPoints.push(lowQuart)
        dataPoints.push(median)
        dataPoints.push(upQuart)
      end
      
      #diameter
      dataPoints.push(sorted.max)
      
      puts "[radius, 25% quartile, median, 75% quartile, diameter]"
      p dataPoints # prints out [radius, 25% quartile, median, 75% quartile, diameter]
      eccentricitiesArray.clear
      dataPoints.clear
    end
  end
}