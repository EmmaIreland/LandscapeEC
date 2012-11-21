require 'yaml'
if ARGV.size != 1
	puts "Usage : ./graph_metrics.rb [yamlFile]"
end


yamlFile = ARGV[0]
openFile = File.open( yamlFile )
y = YAML::load_documents( openFile ) { |graph|
  
  listOfAll = []
  
	for i in 0...graph.length-1
    if !listOfAll.include?(i)
      queue = [i]
      visited = {i => true}
      print i.to_s() + " "
      componentSize = 1
      
      while(!queue.empty?)
        node = queue.pop()
        graph[node].each do |child|
        
				  if visited[child] != true then
				    print "#{child} "
            listOfAll.push(child)
				    queue.push(child)
				    visited[child] = true
            componentSize += 1
				  end
				
		    end
      end
      puts "Component size: " + componentSize.to_s() + "\n "
	  end
	end
} 