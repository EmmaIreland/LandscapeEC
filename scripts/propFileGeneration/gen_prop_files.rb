#!/usr/bin/ruby 

require "yaml"

# Make YAML file a command line argument
# Take an optional additional argument that is the directory to put the
# properties files into.
@param_hash = YAML::load_file('params.yaml')
@file_prefix = @param_hash.delete('File_prefix')
@base_prop_file = @param_hash.delete('Base_properties_file')
# p @param_hash

def generate_file_name(property_list)
  result = @file_prefix
  property_list.each do |prop, val|
    prop = prop[0..0]+prop[1..-1].gsub(/[AEIOUaeiou]/,"")
	val = val.to_s.gsub(/\//,"_")
    result = result + "_#{prop}:#{val}"
  end
  result = result + ".properties"
  return result.gsub(/ /, "")
end

def generate_file(property_list)
    # p property_list
    filename = generate_file_name(property_list)
    puts filename
    file=File.new(filename, "w+")
    file.puts("#include \"" + @base_prop_file + "\"")
    property_list.each do |prop, val|
      file.puts("#{prop}:#{val}")
    end
    filename.gsub(/"#"/,"_")
    file.puts("RESULTS_FOLDER:" + filename.gsub(/.properties/,""))
end

def lookup_value(hashes, key)
  hashes.each do |hash|
    if !hash[key].nil?
      return hash[key]
    end
  end
  return nil
end

# Generates all the properties files using the properties
# specified in property_list, and all the possible values
# for the properties in keys.
def gen_prop_files(hashes, keys, property_list, parent)
  if keys == []
    generate_file(property_list)
  else
    property = keys[0]
    values = lookup_value(hashes, property)
    if values.class == Hash
      gen_prop_files([values] + hashes, values.keys + keys[1..-1], property_list + [[parent, property]], property)
    else
       values.each do |val|
         if val.class == Hash
           gen_prop_files([val] + hashes, val.keys + keys[1..-1], property_list, property)
         else
           gen_prop_files(hashes, keys[1..-1], property_list + [[property, val]], property)
         end
       end
    end
  end 
end

gen_prop_files([@param_hash], @param_hash.keys, [], nil)
  
# When we actually construct the files, make sure we add a RESULTS_FOLDER property
# that is probably the file name without the ".properties" extension.

