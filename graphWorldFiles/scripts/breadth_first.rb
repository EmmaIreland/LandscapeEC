require 'yaml'
if ARGV.size != 1
	puts "Usage : ./graph_metrics.rb [yamlFile]"
end


yamlFile = ARGV[0]
openFile = File.open( yamlFile )
y = YAML::load_documents( openFile ) { |graph|
	
	for i in 0...graph.length-1
	  queue = [i]
    visited = {i => true}
    print i.to_s() + " "
    size = 1
    
    while(!queue.empty?)
      node = queue.pop()
      graph[node].each do |child|
        
				if visited[child] != true then
				  print "#{child} "
				  queue.push(child)
				  visited[child] = true
          size+=1
				end
				
		  end
	  end
	  
	  puts "size: " + size.to_s()
	end
} 