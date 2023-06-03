package com.example.javemovil20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.javemovil20.databinding.ActivityUserProfileBinding;
import com.example.javemovil20.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private final int CAMERA_PERMISSION_ID = 101;
    public static final String PATH_USERS="users/";

    private File photoFile;
    String cameraPerm = Manifest.permission.CAMERA;
    ImageView imageView;
    String currentPhotoPath;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Create a Cloud Storage reference from the app
    private StorageReference mStorageReference;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestPermission(this, cameraPerm, "Permiso para utiliza la camara", CAMERA_PERMISSION_ID);
        initView();
        imageView = binding.imageView;

        this.mStorageReference = FirebaseStorage.getInstance().getReference();
        this.database = FirebaseDatabase.getInstance();


        this.userEmail = getIntent().getStringExtra("userEmail");
        this.loadData();


        try {
            this.downloadFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        binding.takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera(view);
            }
        });

        binding.saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        binding.saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });


    }

    public void requestPermission(Activity context, String permission, String justification, int id) {
        // Se verifica si no hay permisos
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            // ¿Deberiamos mostrar una explicación?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, cameraPerm)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            // Solicitar el permiso
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    private void initView() {
        if (ContextCompat.checkSelfPermission(this, cameraPerm) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Failed to getting the permission", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success getting the permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_ID) {
            initView();
        }
    }

    public void startCamera(View view) {
        if (ContextCompat.checkSelfPermission(this, cameraPerm) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "Failed to getting the permission :(", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Asegurarse de que hay una actividad de camara para manejar el intent
        if (takePictureIntent != null) {
            //Crear el archivo donde debería ir la foto
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            //Continua solo el archivo ha sido exitosamente creado
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.javemovil20.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    private File createImageFile() throws IOException {
        //Crear un nombre dde archivo de imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("IMG",".jpg", storageDir);

        // Guardar un archivo: Ruta para usar con ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("Ruta: "+currentPhotoPath);
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int rresultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, rresultCode, data);
        if (rresultCode == Activity.RESULT_OK) {
            imageView.setImageURI(Uri.parse(currentPhotoPath));
            Toast.makeText(this, "Image capture successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {
        // Uri file = Uri.fromFile(new File("path/to/images/image.jpg"));
        if(photoFile != null){
            Uri file = Uri.fromFile(this.photoFile);
            StorageReference imageRef = mStorageReference.child("images/profile/" + this.userEmail.trim() + "/image.jpg");
            imageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(UserProfileActivity.this, "Imagen guardada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
        }

    }

    private void downloadFile() throws IOException {
        File localFile = File.createTempFile("images", "jpg");
        StorageReference imageRef = mStorageReference.child("images/profile/" + this.userEmail.trim() + "/image.jpg");
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        binding.imageView.setImageURI(Uri.fromFile(localFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download

                    }
                });
    }

    private void saveUserInfo(){

        User editUser = new User();
        editUser.setAge(Integer.parseInt(binding.editTextAge.toString()));
        editUser.setCareer(binding.editTextCareer.toString());
        editUser.setName(binding.editTextName.toString());
        editUser.setLastName(binding.editTextLastName.toString());
        editUser.setEmail(binding.emailEditText.toString());

        String key = myRef.push().getKey();
        myRef = database.getReference(PATH_USERS + key);
        myRef.setValue(editUser);
    }

    private void loadData(){
        binding.emailEditText.setText(this.userEmail);
    }


}