package com.emresahin.artbooknav.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emresahin.artbooknav.R;
import com.emresahin.artbooknav.databinding.FragmentAddArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;


public class AddArtFragment extends Fragment{
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Bitmap addImage;
    SQLiteDatabase database;
    private FragmentAddArtBinding binding;
    String info = "";
    boolean isEmpty;
    int artNameIx;
    public AddArtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = getActivity().openOrCreateDatabase("ArtsDB", Context.MODE_PRIVATE,null);
        registerLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddArtBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button saveButton =  binding.AdArtSaveButton;
        Button changeButton = binding.artChangeButton;

        if(getArguments() != null) {
            info = AddArtFragmentArgs.fromBundle(getArguments()).getInfo();
        } else {
            info = "new";
        }

        binding.AddArtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddArtImageOnClick(view);
            }
        });

        binding.AdArtSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdArtSaveButtonOnClick(view);
            }
        });

        binding.ArtDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtDeleteButtonOnClick(v);
            }
        });


        if (info.equals("new")) {
            changeButton.setVisibility(View.GONE);
            saveButton.setEnabled(false);

            textListener();

            binding.ArtDeleteButton.setVisibility(View.GONE);
            binding.AddArtImage.setImageResource(R.drawable.select_image);

        } else {
            changeButton.setEnabled(false);
            //
            saveButton.setVisibility(View.GONE);
            int artId = AddArtFragmentArgs.fromBundle(getArguments()).getArtId();
            try {
                Cursor cursor = database.rawQuery("SELECT * FROM artsDB WHERE id = ?", new String[] {String.valueOf(artId)});

                artNameIx = cursor.getColumnIndex("ArtNameDB");
                int artistNameIx = cursor.getColumnIndex("ArtistNameDB");
                int artDateIx = cursor.getColumnIndex("ArtDateDB");
                int artIx = cursor.getColumnIndex("ArtDB");

                while (cursor.moveToNext()){
                    binding.AddArtNameText.setText(cursor.getString(artNameIx));
                    binding.AddArtistNameText.setText(cursor.getString(artistNameIx));
                    binding.AddArtDateText.setText(cursor.getString(artDateIx));

                    byte[] bytes = cursor.getBlob(artIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.AddArtImage.setImageBitmap(bitmap);
                }
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();

            }
        }

    }

    public void AddArtImageOnClick(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //Android Version 33+ ==> READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {

                    Snackbar.make(view, "Permission needed for gallery.", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else{
                //to gallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        } else{
            //Android Version 32- ==> READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Snackbar.make(view, "Permission needed for gallery.", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else{
                //to gallery
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }
        }


    public void AdArtSaveButtonOnClick(View view){

    String ArtName, ArtDate, ArtistName;
    ArtName = binding.AddArtNameText.getText().toString();
    ArtDate = binding.AddArtDateText.getText().toString();
    ArtistName = binding.AddArtistNameText.getText().toString();

    Bitmap smallImage = scaleBitmap(addImage,300); //scaled bitmap input

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);  //Converting image to bytearray for database
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS artsDB(id INTEGER PRIMARY KEY, ArtNameDB VARCHAR, ArtistNameDB VARCHAR, ArtDateDB VARCHAR, ArtDB BLOB)");

            String sqlString = "INSERT INTO artsDB(ArtNameDB, ArtistNameDB, ArtDateDB, ArtDB) VALUES(?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, ArtName);
            sqLiteStatement.bindString(2, ArtistName);
            sqLiteStatement.bindString(3, ArtDate);
            sqLiteStatement.bindBlob(4, byteArray);
            sqLiteStatement.execute();


        }catch (Exception e){
            e.printStackTrace();
        }
        NavDirections action = AddArtFragmentDirections.actionAddArtFragmentToGalleryFragment();
        Navigation.findNavController(requireActivity(),R.id.fragment).navigate(action);
    }

    public void ArtDeleteButtonOnClick(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setTitle("Art Book Navigation");
        alert.setMessage("This Content will be deleted, are you sure about that?");
        alert.setNegativeButton("Keep",null);
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFunction();
            }
        });
        alert.show();
    }



    public Bitmap scaleBitmap(Bitmap bitmap, int maxImageSize ) {
        //scaling image for SQLite ==>
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        float bitmapRatio = (float) imageWidth / (float) imageHeight;

        if(bitmapRatio > 1){
            //landscape image
            imageWidth = maxImageSize;
            imageHeight = (int) (imageWidth / bitmapRatio);
        }else{
            //portrait image
            imageHeight = maxImageSize;
            imageWidth = (int) (imageHeight * bitmapRatio);
        }
        //<==
        return Bitmap.createScaledBitmap(bitmap,imageWidth,imageHeight,true);
    }

    private void registerLauncher(){

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null){
                       Uri imageData = intentFromResult.getData();
                        try {
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), imageData);
                                addImage = ImageDecoder.decodeBitmap(source);
                                binding.AddArtImage.setImageBitmap(addImage);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }else{
                    //permission denied
                    Toast.makeText(requireContext(),"Permission Needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void textListener(){

        EditText addArtNameText =  binding.AddArtNameText;
        EditText addArtistNameText =  binding.AddArtistNameText;
        EditText addArtDateText = binding.AddArtDateText;
        Button saveButton =  binding.AdArtSaveButton;
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEmpty = addArtNameText.getText().toString().isEmpty() ||
                        addArtistNameText.getText().toString().isEmpty() ||
                        addArtDateText.getText().toString().isEmpty();

                if (isEmpty) {
                    saveButton.setEnabled(false);
                }else{
                    saveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        addArtNameText.addTextChangedListener(textWatcher);
        addArtistNameText.addTextChangedListener(textWatcher);
        addArtDateText.addTextChangedListener(textWatcher);

    }
    public void deleteFunction(){
        try {
            int artId = AddArtFragmentArgs.fromBundle(getArguments()).getArtId();

            String delete = "DELETE FROM artsDB WHERE id = ?";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1,String.valueOf(artId));
            sqLiteStatement.execute();


        }catch (Exception e){
            e.printStackTrace();
        }
        NavDirections action = AddArtFragmentDirections.actionAddArtFragmentToGalleryFragment();
        Navigation.findNavController(requireActivity(),R.id.fragment).navigate(action);
        database.close();
    }
}
