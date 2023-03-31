package com.emresahin.artbooknav.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.emresahin.artbooknav.Model.ArtModel;
import com.emresahin.artbooknav.View.GalleryFragmentDirections;
import com.emresahin.artbooknav.databinding.ArtBookRecyclerRowBinding;
import java.util.ArrayList;

public class ArtBookRecyclerViewAdapter  extends RecyclerView.Adapter<ArtBookRecyclerViewAdapter.ArtAdapterHolder> {
    ArrayList<ArtModel> artModelArrayList;
    public ArtBookRecyclerViewAdapter(ArrayList<ArtModel>artModelArrayList){
        this.artModelArrayList = artModelArrayList;
    }

    public static class ArtAdapterHolder extends RecyclerView.ViewHolder{
        private ArtBookRecyclerRowBinding binding;
        public ArtAdapterHolder(ArtBookRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    @NonNull
    @Override
    public  ArtAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtBookRecyclerRowBinding artBookRecyclerRowBinding = ArtBookRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtAdapterHolder(artBookRecyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtAdapterHolder holder, int position) {
        holder.binding.recyclerViewTextView.setText(artModelArrayList.get(position).artname);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFragmentDirections.ActionGalleryFragmentToAddArtFragment action = GalleryFragmentDirections.actionGalleryFragmentToAddArtFragment("old");
                action.setArtId(artModelArrayList.get(position).id);
                action.setInfo("old");
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
    @Override
    public int getItemCount() {
            return artModelArrayList.size();
    }

    }


