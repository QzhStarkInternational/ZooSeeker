package com.example.sandiegozooseeker.adapaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.PathFinder.PathFinderNew;
import com.example.sandiegozooseeker.graph.GraphVertex;

import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.graph.Zoo;
import com.example.sandiegozooseeker.PathFinder.PathFinder;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>{
    private List<GraphVertex> vertices = Collections.emptyList();
    private BiConsumer<GraphVertex, View> onClicked;
    private final Context context;
    private Map<String, Integer> distances;

    public PlanListAdapter(Context context) {
        this.context = context;
    }
    public void setVertices(List<GraphVertex> newVertices) {
        this.vertices.clear();

        PathFinderNew pf = new PathFinderNew(context, Zoo.getZoo(context).getVertex("entrance_exit_gate"));
        distances = pf.getDistances();

        newVertices.sort((v1, v2) -> {
            Integer vertex1;
            Integer vertex2;

            if(v1.getGroup_id() != null){
                vertex1 = distances.get(v1.getGroup_id());
            } else {
                vertex1 = distances.get(v1.getId());
            }

            if(v2.getGroup_id() != null){
                vertex2 = distances.get(v2.getGroup_id());
            } else {
                vertex2 = distances.get(v2.getId());
            }

            if (vertex1 == null)
                return -1;
            if (vertex2 == null)
                return 1;
            return vertex1.compareTo(vertex2);
        });

        this.vertices = newVertices;

        notifyDataSetChanged();
    }


    public void setOnClickedHandler(BiConsumer<GraphVertex, View> onClicked) {
        this.onClicked = onClicked;
    }

    @NonNull
    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_exhibit_plan, parent,false);

        return new PlanListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanListAdapter.ViewHolder holder, int position) {
        holder.setVertex(vertices.get(position));
    }

    @Override
    public int getItemCount() {
        return vertices.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private GraphVertex graphVertex;
        private final TextView distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_name);
            View view1 = itemView.findViewById(R.id.plan_list_cell);
            this.distance = itemView.findViewById(R.id.distance_display);

            view1.setOnClickListener(view -> {
                if (onClicked == null) return;
                onClicked.accept(graphVertex, itemView);
            });
        }

        public void setVertex(GraphVertex graphVertex) {
            this.graphVertex = graphVertex;
            this.textView.setText(graphVertex.getName());

            if(graphVertex.getGroup_id() != null){
                this.distance.setText(String.format(Locale.US, "%d m", distances.get(graphVertex.getGroup_id())));
            } else {
                this.distance.setText(String.format(Locale.US, "%d m", distances.get(graphVertex.getId())));
            }
        }
    }
}