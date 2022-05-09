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
import com.example.sandiegozooseeker.R;
import com.example.sandiegozooseeker.pathfinder.IdentifiedWeightedEdge;
import com.example.sandiegozooseeker.pathfinder.Pathfinder;

import org.jgrapht.GraphPath;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>{
    private List<Vertex> vertices = Collections.emptyList();
    private BiConsumer<Vertex, View> onClicked;
    private Context context;
    private Map<String,Integer> distanceMapping;

    public PlanListAdapter(Context context) {
        this.context = context;
    }
    public void setVertices(List<Vertex> newVertices) {
        this.vertices.clear();

        //ordering the plan listview
        //call the PathFinder class during the plan phase as you need to sort the plan list ordered by distance
        VertexDao vertexDao = VertexDatabase.getSingleton(context).vertexDao();
        List<String> selectedExhibits = vertexDao.getSelectedExhibitsID(Vertex.Kind.EXHIBIT);

        Pathfinder pf = new Pathfinder(selectedExhibits, context);

        List<GraphPath<String, IdentifiedWeightedEdge>> plan = pf.plan();

        //map with key-value pairs as vertex id and distance
        distanceMapping = pf.getDistanceMapping(plan);
        Collections.sort(newVertices, new Comparator<Vertex>() {
            @Override
            public int compare(final Vertex v1, final Vertex v2) {
                Integer vertex1 = distanceMapping.get(v1.id);
                Integer vertex2 = distanceMapping.get(v2.id);

                if(vertex1 == null)
                    return -1;
                if(vertex2 == null)
                    return 1;
                return vertex1.compareTo(vertex2);
            }
        });

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

    //since vertex id is a string
    public String getVertexId(int position) {
        return vertices.get(position).id;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CardView materialCardView;
        private final View view;
        private Vertex vertex;
        private TextView distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_name);
            this.view = itemView.findViewById(R.id.plan_list_cell);
            this.materialCardView = itemView.findViewById(R.id.materialCardView_plan);
            this.distance = itemView.findViewById(R.id.distance_display);

            String animalName = this.textView.getText().toString();


            view.setOnClickListener(view -> {
                if (onClicked == null) return;
                onClicked.accept(vertex, itemView);
            });
        }

        public Vertex getVertex() { return vertex; }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
            this.textView.setText(vertex.name);
            this.distance.setText(distanceMapping.get(vertex.id) != null ? distanceMapping.get(vertex.id).toString() + " m": "LOSSSS");
        }
    }
}