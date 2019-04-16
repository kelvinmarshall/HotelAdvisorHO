package dev.marshall.hoteladvisorho;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisorho.common.Common;


import static android.app.Activity.RESULT_OK;

/**
 * Created by Marshall on 04/03/2018.
 */

public class FragmentProfile extends Fragment{

    ImageView profileImage,Pickpicture;
    ImageButton Eusername;
    TextView phonenumber,email,username;
    MaterialEditText editname;

    Uri saveUri= null;

    FirebaseDatabase db;
    DatabaseReference profile;
    FirebaseStorage storage;
    StorageReference storageReference;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db=FirebaseDatabase.getInstance();
        profile=db.getReference("User");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.profile,container,false);

        getActivity().setTitle("Profile");

        profileImage= view.findViewById(R.id.profile_image);
        Eusername= view.findViewById(R.id.editusername);
        phonenumber= view.findViewById(R.id.phonenum);
        phonenumber.setText(Common.currentHotelOwner.getPhone());
        email= view.findViewById(R.id.emailadd);
        email.setText(Common.currentHotelOwner.getEmail());
        username= view.findViewById(R.id.username);
        username.setText(Common.currentHotelOwner.getName());


        Eusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
        GlideApp.with(getActivity()).load(Common.currentHotelOwner.getImage())
                .placeholder(R.drawable.ic_person_black_24dp)
                .into(profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile();
            }
        });


        return view;
    }

    private void updateprofile() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Change your Profile image");
        LayoutInflater inflater = this.getLayoutInflater();
        View changepicture = inflater.inflate(R.layout.changeprofile,null);

        Pickpicture=changepicture.findViewById(R.id.pickImage);
        Pickpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(getActivity());

                    }

                }
                else
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(getActivity());
            }
        });

        alertdialog.setView(changepicture);

        alertdialog.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (saveUri!=null) {
                    final ProgressDialog mDialog = new ProgressDialog(getActivity());

                    mDialog.setMessage("Uploading");
                    mDialog.show();

                    String imageName = UUID.randomUUID().toString();
                    final StorageReference imageFolder = storageReference.child("profileimages/" + imageName);
                    imageFolder.putFile(saveUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mDialog.dismiss();
                                    Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map<String, Object> updateImage = new HashMap<>();
                                            updateImage.put("image", uri.toString());

                                            profile.child(Common.currentHotelOwner.getPhone())
                                                    .updateChildren(updateImage)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getActivity(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Profile picture update failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded" + progress + "%");
                        }
                    });

                }
                else {
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Missing Image")
                            .setContentText("Please make sure you have selected an image from your gallery before proceeding")
                            .show();
                }

            }
        });

        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertdialog.show();




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                saveUri = result.getUri();
                Pickpicture.setImageURI(saveUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void showdialog() {
        AlertDialog.Builder alertdialog =new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Change your Username");
        LayoutInflater inflater = this.getLayoutInflater();
        View changeusername = inflater.inflate(R.layout.edtprofile,null);

        editname=changeusername.findViewById(R.id.edtname);

        ////set default value for view
        editname.setText(Common.currentHotelOwner.getName());

        alertdialog.setView(changeusername);
        alertdialog.setIcon(R.drawable.ic_person_black_24dp);

        alertdialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String,Object> usernameupdate=new HashMap<>();
                usernameupdate.put("name",editname.getText().toString());


                //make update
                DatabaseReference user=FirebaseDatabase.getInstance().getReference("User");
                user.child(Common.currentHotelOwner.getPhone())
                        .updateChildren(usernameupdate)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Username update failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertdialog.show();


    }

}
