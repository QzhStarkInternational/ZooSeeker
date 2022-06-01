package com.example.sandiegozooseeker.adapaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.graph.GraphVertex;
import com.example.sandiegozooseeker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> implements Filterable{
    private List<GraphVertex> vertices = Collections.emptyList();
    private List<GraphVertex> filteredVertices = Collections.emptyList();
    private BiConsumer<GraphVertex, View> onClicked;

    private ItemFilter filter = new ItemFilter();

    public void setVertices(List<GraphVertex> newVertices) {
        this.vertices.clear();
        this.filteredVertices.clear();
        this.vertices = newVertices;
        this.filteredVertices = newVertices;
        notifyDataSetChanged();
    }


    public void setOnClickedHandler(BiConsumer<GraphVertex, View> onClicked) {
        this.onClicked = onClicked;
    }


    @NonNull
    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_exhibit_search,parent,false);

        return new SearchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapter.ViewHolder holder, int position) {
        holder.setVertex(filteredVertices.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredVertices.size();
    }

    public String getVertexId(int position) {
        return filteredVertices.get(position).getId();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView animalNameTextView;
        private final CardView materialCardView;
        private  final ChipGroup tagsChipGroup;
        private final ImageView checkMarkImageView;
        private final Context context;

        private final int transparent;
        private final int tinted;
        private GraphVertex graphVertex;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animalNameTextView = itemView.findViewById(R.id.animal_name);
            this.materialCardView = itemView.findViewById(R.id.materialCardView);
            this.tagsChipGroup = itemView.findViewById(R.id.tag_chip_group);
            this.checkMarkImageView = itemView.findViewById(R.id.checkMarkImageView);
            this.context = itemView.getContext();


            View view1 = itemView.findViewById(R.id.search_list_item_layout);

            transparent = itemView.getResources().getColor(R.color.transparent);
            tinted = itemView.getResources().getColor(R.color.primary_100);

            view1.setOnClickListener(view -> {
                if (onClicked == null) return;
                onClicked.accept(graphVertex, itemView);
            });

        }

        public GraphVertex getVertex() { return graphVertex; }

        public void setVertex(GraphVertex graphVertex) {
            this.graphVertex = graphVertex;
            this.animalNameTextView.setText(graphVertex.getName());

            tagsChipGroup.removeAllViews();
            //display tags (search terms) for each animal exhibit
            for(String tag : graphVertex.getTags()){

                Chip chip = new Chip(context);
                chip.setText(tag);
                chip.setClickable(false);
                chip.setRippleColorResource(R.color.transparent);
                tagsChipGroup.addView(chip);
            }

            //indicator that animal exhibit has been selected
            if(this.graphVertex.getIsSelected()){
                this.materialCardView.setCardBackgroundColor(tinted);
                this.checkMarkImageView.setVisibility(View.VISIBLE);
            } else {
                this.materialCardView.setCardBackgroundColor(transparent);
                this.checkMarkImageView.setVisibility(View.GONE);
            }
        }
    }

    // Search Filter
    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<GraphVertex> list = vertices;

            int count = list.size();
            final ArrayList<GraphVertex> selected = new ArrayList<GraphVertex>(count);
            if (filterString.isEmpty()) {
                selected.addAll(vertices);
            } else {
                for (int i = 0; i < count; i++) {
                    if(!selected.contains(list.get(i))){
                        if(list.get(i).getName().toLowerCase().contains(filterString)){
                            selected.add(list.get(i));
                        } else {
                            for (String s : list.get(i).getTags()) {
                                if (s.toLowerCase().contains(filterString)) {
                                    selected.add(list.get(i));
                                }
                            }
                        }
                    }
                }
            }

            results.values = selected;
            results.count = selected.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredVertices = (ArrayList<GraphVertex>) results.values;
            notifyDataSetChanged();
        }

    }
}
