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
    private BiConsumer<Vertex, View> onClicked;

    public void setVertices(List<Vertex> newVertices) {
        this.vertices.clear();
        this.vertices = newVertices;
        notifyDataSetChanged();
    }


    public void setOnClickedHandler(BiConsumer<Vertex, View> onClicked) {
        this.onClicked = onClicked;
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
        private final View view;
        private Vertex vertex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_name);
            this.view = itemView.findViewById(R.id.add_animal_layout);

            view.setOnClickListener(view -> {
                if (onClicked == null) return;
                onClicked.accept(vertex, itemView);
            });
        }

        public Vertex getVertex() { return vertex; }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
            this.textView.setText(vertex.name);
        }
    }

}
