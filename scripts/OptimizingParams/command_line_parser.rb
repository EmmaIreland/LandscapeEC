require 'optparse'
require 'ostruct'

class CommandLineParser
  def self.parse(args)
    options = OpenStruct.new
    
    opts = OptionParser.new do |opts|
      opts.banner = "Usage: EDA.rb CONFIG_FILE"
      opts.separator "\twhere CONFIG_FILE has the configuration info for this EDA run"
      opts.separator ""
      opts.separator "Specific options:"

      opts.on("-R", "--rfile FILE", "Specify the R file to read performance stats from") do |rfile|
        options.rfile = rfile
      end
      
      opts.on("-h", "--help", "Show this message") do
        puts opts
        exit
      end
      
      ARGV << "-h" if ARGV.empty?
      opts.parse!(ARGV)
    end
  end
end