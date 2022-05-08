package com.example.sandiegozooseeker.adapaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.AnimalDB.VertexDao;
import com.example.sandiegozooseeker.AnimalDB.VertexDatabase;
import com.example.sandiegozooseeker.AnimalDB.VertexViewModel;
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import org.jgrapht.GraphPath;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>{
    private List<Vertex> vertices = Collections.emptyList();
    private BiConsumer<Vertex, View> onClicked;
    private Context context;
    private Map<String, String> distanceMapping;

    public PlanListAdapter(Context context) {
        this.context = context;
    }

    public void setVertices(List<Vertex> newVertices) {
        this.vertices.clear();

        VertexDao vertexDao = VertexDatabase.getSingleton(context).vertexDao();
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);

        Pathfinder p = new Pathfinder(selectedExhibits, context);
        List<GraphPath<String, IdentifiedWeightedEdge>> planList = p.plan();
        distanceMapping = p.pathsToStringMap(planList);

        this.vertices = newVertices;
        notifyDataSetChanged();

    }

    public void setOnClickedHandler(BiConsumer<Vertex, View> onClicked) {
        this.onClicked = onClicked;
    }

    @NonNull
    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_plan_list_item, parent,false);

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

    //since vertex id is a string
    public String getVertexId(int position) {
        return vertices.get(position).id;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CardView materialCardView;
        private final View view;
        private Vertex vertex;
        private TextView distance_display;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_name);
            this.view = itemView.findViewById(R.id.plan_list_cell);
            this.materialCardView = itemView.findViewById(R.id.materialCardView_plan);
            this.distance_display = itemView.findViewById(R.id.distance_display);

            view.setOnClickListener(view -> {
                if (onClicked == null) return;
                onClicked.accept(vertex, itemView);
            });
        }

        public Vertex getVertex() { return vertex; }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
            this.textView.setText(vertex.name);
            this.distance_display.setText(distanceMapping.get(vertex.getName()));

        }
    }
}
