package com.example.sandiegozooseeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VertexListAdapter extends RecyclerView.Adapter<VertexListAdapter.ViewHolder> {
    private List<Vertex> vertices = Collections.emptyList();
    //adding and deleting record in database
    private BiConsumer<Vertex, CheckBox> onCheckBoxClicked;
    //private Consumer<Vertex> onDeleteClicked;

    public void setVertices(List<Vertex> newVertices) {
        this.vertices.clear();
        this.vertices = newVertices;
        notifyDataSetChanged();
    }

//    public void setOnDeleteClickedHandler(Consumer<Vertex> onDeleteClicked) {
//        this.onDeleteClicked = onDeleteClicked;
//    }
    public void setOnCheckBoxClickedHandler(BiConsumer<Vertex, CheckBox> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.vertex_list_item,parent,false);

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
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.vertex_list_item);
            this.checkBox = itemView.findViewById(R.id.add_animal);


            this.checkBox.setOnClickListener(view -> {
                if (onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(vertex, checkBox);
            });

            //remove animal record in your plan
//            itemView.findViewById(R.id.remove_animal).setOnClickListener(view -> {
//                if (onDeleteClicked == null) return;
//                onDeleteClicked.accept(vertex);
//            });
        }

        public Vertex getVertex() { return vertex; }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
            this.textView.setText(vertex.name);
            this.checkBox.setChecked(vertex.isSelected);
        }
    }

}
