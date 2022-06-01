package com.example.sandiegozooseeker.graph;

public class GraphEdge {

    private String id;
    private String street;

    public GraphEdge(String id, String street){
        this.id = id;
        this.street = street;
    }

    public void setId(String id){ this.id = id; }
    public void setStreet(String street){ this.street = street; }
    public String getId(){ return id; }
    public String getStreet(){ return street; }
}
