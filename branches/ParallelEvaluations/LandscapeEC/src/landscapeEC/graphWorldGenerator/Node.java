package landscapeEC.graphWorldGenerator;


import java.util.ArrayList;

public class Node {
    
    private Integer community;
    private Integer name;
    private ArrayList<Integer> conn;
    
    public Node(Integer name, Integer comm) {
        this.name = name;
        community = comm;
        conn = new ArrayList<Integer>();
    }

    public Node(Integer name) {
        this.name = name;
        conn = new ArrayList<Integer>();
    }
    
    public Integer getName() {
        return name;
    }
    
    public void add(Integer num) {
        if(num.equals(this.name)) {
            throw new IllegalArgumentException("Cannot add self to list.");
        }
        conn.add(num);
    }

    public int getSize() {
        return conn.size();
    }
    
    public boolean contains(Integer num) {
        return conn.contains(num);
    }
    
    public Integer getCommunity() {
        return community;
    }
    
}
