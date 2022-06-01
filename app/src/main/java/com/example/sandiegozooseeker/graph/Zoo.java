package com.example.sandiegozooseeker.graph;

import android.content.Context;

public class Zoo {
    private static ZooGraph zooGraph = null;

    public synchronized static ZooGraph getZoo(Context context){
        if(zooGraph == null){ zooGraph = new ZooGraph(context); }
        return zooGraph;
    }
}
