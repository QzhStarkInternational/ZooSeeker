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
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sandiegozooseeker.AnimalDB.Vertex;
import com.example.sandiegozooseeker.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> implements Filterable{
    private List<Vertex> vertices = Collections.emptyList();
    private List<Vertex> filteredVertices = Collections.emptyList();
    private BiConsumer<Vertex, View> onClicked;

    private ItemFilter filter = new ItemFilter();

    public void setVertices(List<Vertex> newVertices) {
        this.vertices.clear();
        this.filteredVertices.clear();
        this.vertices = newVertices;
        this.filteredVertices = newVertices;
        notifyDataSetChanged();
    }


    public void setOnClickedHandler(BiConsumer<Vertex, View> onClicked) {
        this.onClicked = onClicked;
    }

    @NonNull
    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_search_list_item,parent,false);

        return new SearchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapter.ViewHolder holder, int position) {
        holder.setVertex(filteredVertices.get(position));
    }

    @Override
    public int getItemCount() {
        //return vertices.size();
        return filteredVertices.size();
    }

    //since vertex id is a string
    public String getVertexId(int position) {
        return filteredVertices.get(position).id;
        // return vertices.get(position).id;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView animalNameTextView;
        private final CardView materialCardView;
        private  final ChipGroup tagsChipGroup;
        private final ImageView checkMarkImageView;
        private final Context context;

        private final int transparent;
        private final int tinted;
        private Vertex vertex;

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
                onClicked.accept(vertex, itemView);
            });

        }

        public Vertex getVertex() { return vertex; }

        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
            this.animalNameTextView.setText(vertex.name);

            tagsChipGroup.removeAllViews();
            //display tags (search terms) for each animal exhibit
            for(String tag : vertex.tags){

                Chip chip = new Chip(context);
                chip.setText(tag);
                chip.setClickable(false);
                chip.setBackgroundColor(transparent);
                chip.setRippleColorResource(R.color.transparent);
                tagsChipGroup.addView(chip);
            }

            //indicator that animal exhibit has been selected
            if(this.vertex.isSelected){
                this.materialCardView.setCardBackgroundColor(tinted);
                this.checkMarkImageView.setVisibility(View.VISIBLE);
            } else {
                this.materialCardView.setCardBackgroundColor(transparent);
                this.checkMarkImageView.setVisibility(View.GONE);
            }
        }
    }
    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Vertex> list = vertices;

            int count = list.size();
            final ArrayList<Vertex> selected = new ArrayList<Vertex>(count);
            if (filterString.isEmpty() || filterString == null) {
                selected.addAll(vertices);
            } else {
                for (int i = 0; i < count; i++) {
                    for (String s : list.get(i).tags) {
                        if (s.toLowerCase().equals(filterString) && !selected.contains(list.get(i))) {
                            selected.add(list.get(i));
                        }
                    }
                }
            }

            results.values = selected;
            results.count = selected.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredVertices = (ArrayList<Vertex>) results.values;
            notifyDataSetChanged();
        }

    }

}
