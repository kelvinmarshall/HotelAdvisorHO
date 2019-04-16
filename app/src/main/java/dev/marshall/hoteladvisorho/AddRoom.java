package dev.marshall.hoteladvisorho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.model.MoreInformation;
import dev.marshall.hoteladvisorho.model.MyHotels;
import dev.marshall.hoteladvisorho.model.Rooms;

import static dev.marshall.hoteladvisorho.HotelDetails.HotelId;

public class AddRoom extends AppCompatActivity {
    Uri saveUri=null;

    Rooms newroom;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference rooms;
    FirebaseStorage storage;
    StorageReference storageReference;


    MaterialSpinner capacity,type;
    EditText price;
    CheckBox breakfast,lunch,dinner,booknow,cancelation,hotshower,bathtab,digitaltv;
    Button Submit;
    ImageView Imageroom;
    CardView rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        String HotelId= HotelDetails.HotelId;
        rootLayout = findViewById(R.id.layoutaddroom);

        //Firebase
        db = FirebaseDatabase.getInstance();
        rooms = db.getReference("Rooms");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        capacity= findViewById(R.id.roomcapacity);
        type= findViewById(R.id.roomtype);

        capacity.setItems("1","2","3");
        type.setItems("Standard Room","Standard-tripple room","Conference Room","VIP Room");

        price= findViewById(R.id.room_p);

        Submit= findViewById(R.id.submit_room);
        breakfast= findViewById(R.id.breakfast);
        lunch= findViewById(R.id.lunch);
        dinner= findViewById(R.id.dinner);
        cancelation= findViewById(R.id.free);
        booknow= findViewById(R.id.booknow);
        hotshower= findViewById(R.id.hotshower);
        bathtab= findViewById(R.id.bathtab);
        digitaltv= findViewById(R.id.digital);

        Imageroom= findViewById(R.id.editimageroom);

        Imageroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddRoom.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(AddRoom.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AddRoom.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(16, 9)
                                .start(AddRoom.this);

                    }

                }
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(price.getText().toString())){

                    Uploadroom();
                }

            }
        });

    }

    private void Uploadroom() {
        if (saveUri != null) {
            final boolean breakf = breakfast.isChecked();
            final boolean dine = lunch.isChecked();
            final boolean lunc = dinner.isChecked();
            final boolean booknw = booknow.isChecked();
            final boolean frecancel = cancelation.isChecked();
            final boolean hotshw = hotshower.isChecked();
            final boolean tub = bathtab.isChecked();
            final boolean tv = digitaltv.isChecked();

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("room_images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newroom = new Rooms();
                                    newroom.setImage(uri.toString());
                                    newroom.setCapacity(Common.Roomcapacity(String.valueOf(capacity.getSelectedIndex())));
                                    newroom.setRoomtype(Common.Roomtype(String.valueOf(type.getSelectedIndex())));
                                    newroom.setRooprice(price.getText().toString());
                                    newroom.setBathtub(String.valueOf(tub));
                                    newroom.setBooknow(String.valueOf(booknw));
                                    newroom.setCancelation(String.valueOf(frecancel));
                                    newroom.setBreakfast(String.valueOf(breakf));
                                    newroom.setDinner(String.valueOf(dine));
                                    newroom.setHotshower(String.valueOf(hotshw));
                                    newroom.setLunch(String.valueOf(lunc));
                                    newroom.setTv(String.valueOf(tv));


                                    if (newroom !=null)
                                    {
                                        rooms.child(HotelId).push().setValue(newroom);
                                        Snackbar.make(rootLayout,"New room"+ newroom.getRoomtype()+"was added",Snackbar.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(AddRoom.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded" + progress + "%");
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                saveUri=result.getUri();
                Imageroom.setImageURI(saveUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
