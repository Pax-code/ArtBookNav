package com.emresahin.artbooknav.View;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emresahin.artbooknav.Adapter.ArtBookRecyclerViewAdapter;
import com.emresahin.artbooknav.Model.ArtModel;
import com.emresahin.artbooknav.databinding.FragmentGalleryBinding;

import java.util.ArrayList;


public class GalleryFragment extends Fragment{
    ArrayList<ArtModel> artModelArrayList;
    private FragmentGalleryBinding binding;
    ArtBookRecyclerViewAdapter artBookRecyclerViewAdapter;
    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        binding.recyclerView.setLayoutManager(layoutManager);

        artModelArrayList = new ArrayList<>();

        artBookRecyclerViewAdapter = new ArtBookRecyclerViewAdapter(artModelArrayList);
        binding.recyclerView.setAdapter(artBookRecyclerViewAdapter);

        getData();

    }

    public void getData(){
        //Getting data for recyclerView
        try {
            SQLiteDatabase sqLiteDatabase = requireActivity().openOrCreateDatabase("ArtsDB", Context.MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM artsDB", null);
            int ArtNameIx = cursor.getColumnIndex("ArtNameDB");
            int IdIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String artname = cursor.getString(ArtNameIx);
                int id = cursor.getInt(IdIx);
                ArtModel artModel = new ArtModel(id,artname);

                artModelArrayList.add(artModel);


            }

            artBookRecyclerViewAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}