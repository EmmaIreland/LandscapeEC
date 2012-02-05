package landscapeEC.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.yaml.snakeyaml.Yaml;


public class YamlSpike {
	 public static void main(String[] args) throws FileNotFoundException {
		 
		 Yaml yaml = new Yaml();
		 InputStream input = new FileInputStream("graphWorldFiles/testGraphWorld");
		 
		int counter = 1;
		for (Object data : yaml.loadAll(input)) {
			String dataStr = data.toString();
			if(dataStr.startsWith("[")){
				dataStr = dataStr.substring(1, dataStr.length()-1);
				
				System.out.println(dataStr);
			}
			counter++;
		}
		 
		 List<String> list = (List<String>) yaml.load(input);
		 System.out.println(list);
	 }
	 
}
