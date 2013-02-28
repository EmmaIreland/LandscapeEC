#/usr/bin/ruby

require 'yaml'
require 'optparse'


#this chunk deals with the options
options = {}
options[:dot] = false
opt_parser = OptionParser.new do |opt|
  opt.on("-d", "--Dot", "Run with the dot visualizer indicated in second ARG") do
    options[:dot] = true
  end
options[:node] = false
  opt.on("-n", "--Node", "Specify node function") do
    options[:node] = true
  end
options[:conn] = false
  opt.on("-c", "--Conn", "Specify connection function") do
    options[:conn] = true
  end
  opt.on("-b", "--Both", "Specify both node and connection function") do
    options[:node] = true
    options[:conn] = true
  end

end
opt_parser.parse!

#here is our colormap!

colorMap = {}

colorMap[0] = "#0000FF"
colorMap[1] = "#003366"
colorMap[2] = "#006699"
colorMap[3] = "#009966"
colorMap[4] = "#00CC66"
colorMap[5] = "#00FF00"
colorMap[6] = "#99FF33"
colorMap[7] = "#CCFF33"
colorMap[8] = "#FFFF00"
colorMap[9] = "#FF9900"
colorMap[10] = "#FF6600"
colorMap[11] = "#FF3300"
colorMap[12] = "#FF0000"


yamlFile = ARGV[0]
openFile = File.open( yamlFile )
outFileName = yamlFile+"-dot"
outFile = File.open( outFileName, 'w' )

#if the option is on takes in dot visualizer to run at end
if options[:dot]
  #function call is always last argument
  dotViz = ARGV[ARGV.length-1]
end



#this is the attributes for the non-starting locations
nodeAttributes = " [height=.1, width=0.1, fontsize=1, label=\"\", forcelabels=FALSE, style=filled, fixedsize=TRUE, fillcolor=\"#000000\"]"
#this is the attributes for the starting locations
startingLocationAttributes = " [shape=box]"
#startingLocationAttributes = " [height=.1, width=0.1, fontsize=1, label=\"\", forcelabels=FALSE, style=filled, fixedsize=TRUE,  fillcolor=\"#000000\", shape=box]"

#opens yaml file as a map of lists.
listOfConns = YAML::load_documents( openFile ) { |graph|


  #Functions go down here
#-----------------------------hashmap of all of the node functions------------------------------------------
nodeFunctions = {}
nodeFunctions["black"]=Proc.new do |i| 
  "  #{i.to_s} [height=.1, width=0.1, fontsize=1, label=\"\", forcelabels=FALSE, style=filled, fixedsize=TRUE, fillcolor=\"#000000\"]\n"
end
nodeFunctions["red"]=Proc.new do |i|
  "  #{i.to_s} [height=.1, width=0.1, fontsize=1, label=\"\", forcelabels=FALSE, style=filled, fixedsize=TRUE, fillcolor=\"red\"]\n"
end

#---------------------------hashmap of all the conn functions-----------------------------------------------
connFunctions = {}
connFunctions["black"]=Proc.new do |i, j|
  "  #{i.to_s}--"+graph[i][j].to_s+"\n"
end
connFunctions["red"]=Proc.new do |i, j|
  "  #{i.to_s}--"+graph[i][j].to_s+" [color = \"red\"]\n"
end



nodeFunc = nodeFunctions["black"]
connFunc = connFunctions["black"]

if options[:node]
  nodeFunc = nodeFunctions[ARGV[1]]
end
if options[:conn]
  connFunc = connFunctions[ARGV[1]]
end
if options[:conn] && options[:node]
  connFunc = connFunctions[ARGV[2]]
end

  outFile.write( "graph G { \n" )
  
  #this block of for loops writes the "normal" nodes and connections
  for i in 0..graph.length-2
    #this writes the node number and it's attributes
    outFile.write( nodeFunc.call(i) )
    for j in 0..graph[i].length-1
      #this writes the connections, not writing to "lower" nodes to avoid double connections
      if i < graph[i][j]
        outFile.write( connFunc.call(i, j) )
      end 
    end
  end

  #this block of code writes the starting location's stuff
  for i in 0..graph["Corners"].length-1
    outFile.write( "  "+graph["Corners"][i].to_s+startingLocationAttributes+"\n")
  end  

  outFile.write( "}" )
  outFile.flush()    
  outFile.close()
 
}

#run visualizer if called for
if options[:dot]
  system( "#{dotViz} -odot#{yamlFile}-#{dotViz}.pdf -Tpdf #{outFileName}" )
end





