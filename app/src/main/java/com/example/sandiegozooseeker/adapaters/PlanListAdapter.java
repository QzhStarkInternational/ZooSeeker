package com.example.sandiegozooseeker.adapaters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.R;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>{
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
        private final View view;
        private Vertex vertex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_name);
            this.view = itemView.findViewById(R.id.plan_list_item_layout);

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
