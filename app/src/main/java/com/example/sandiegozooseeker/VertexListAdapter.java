package com.example.sandiegozooseeker;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VertexListAdapter extends RecyclerView.Adapter<VertexListAdapter.ViewHolder> {
    private List<Vertex> vertices = Collections.emptyList();
    //adding and deleting record in database
    private BiConsumer<Vertex, ConstraintLayout> onLayoutClicked;

    public void setVertices(List<Vertex> newVertices) {
        this.vertices.clear();
        this.vertices = newVertices;
        notifyDataSetChanged();
    }

//    public void setOnDeleteClickedHandler(Consumer<Vertex> onDeleteClicked) {
//        this.onDeleteClicked = onDeleteClicked;
//    }
    public void setOnLayoutClickedHandler(BiConsumer<Vertex, ConstraintLayout> onLayoutClicked) {
        this.onLayoutClicked = onLayoutClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.street_distance_name_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    //each VH is going to keep track of the TextView inside it as well as the individual Vertex it is responsible for displaying
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Vertex vertex;
        private final ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_name);
            this.layout = itemView.findViewById(R.id.add_animal_layout);


            this.layout.setOnClickListener(view -> {
                if (onLayoutClicked == null) return;
                onLayoutClicked.accept(vertex, layout);
            });

        }

        public Vertex getVertex() { return vertex; }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
            this.textView.setText(vertex.name);
            //this.checkBox.setChecked(vertex.isSelected);
        }
    }

}
