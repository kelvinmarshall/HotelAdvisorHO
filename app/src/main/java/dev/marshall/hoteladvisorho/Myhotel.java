package dev.marshall.hoteladvisorho;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rilixtech.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.model.MyHotels;
import dev.marshall.hoteladvisorho.viewholder.MyHotelViewHolder;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dev.marshall.hoteladvisorho.common.Common.currentHotel;

public class Myhotel extends AppCompatActivity implements View.OnCreateContextMenuListener {

    LinearLayout myhotellist;
    LinearLayout loadingProgress;
    //update
    RelativeLayout rootLayout;
    ImageView editimagehotel;
    EditText hname,hcounty,hladdres,hAprice;


    FirebaseDatabase database;
    DatabaseReference myhotel;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri saveUri;
    RecyclerView recycler_myhotel;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<MyHotels,MyHotelViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhotel);
        //toolbar
        Toolbar toolbar= findViewById(R.id.toolb);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home =new Intent(Myhotel.this,Home.class);
                startActivity(home);
            }
        });
        toolbar.setTitle("My Hotels");
        //layouts
        myhotellist= findViewById(R.id.myhotellist);
        loadingProgress= findViewById(R.id.progressloading);
        loadingProgress.setVisibility(View.INVISIBLE);

        //Init Firebase
        database=FirebaseDatabase.getInstance();
        myhotel=database.getReference("Hotels");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //Load myhotels
        recycler_myhotel= findViewById(R.id.recycler_menu);
        recycler_myhotel.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recycler_myhotel.setLayoutManager(layoutManager);

        showView(loadingProgress);
        hideView(myhotellist);

        if (Common.isConnectedToInternet(getBaseContext())) {

        loadMyHotel(Common.currentHotelOwner.getPhone());
        }
        else {

            AlertDialog.Builder alertdialog=new AlertDialog.Builder(Myhotel.this);
            alertdialog.setTitle("Internet Connection");
            alertdialog.setIcon(R.drawable.ic_signal_wifi_off_black_24dp);
            alertdialog.setMessage("Please check your internet connection and try again");
            alertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertdialog.show();
        }
    }


    private void showView(View... views) {
        for (View v: views){
            v.setVisibility(View.VISIBLE);
        }
    }
    private void hideView(View... views) {
        for (View v: views){
            v.setVisibility(View.INVISIBLE);
        }
    }


    private void loadMyHotel(String phone) {
            adapter=new FirebaseRecyclerAdapter<MyHotels, MyHotelViewHolder>(
                    MyHotels.class,
                    R.layout.hotel,
                    MyHotelViewHolder.class,
                    myhotel.orderByChild("phone").equalTo(phone)) {
                @Override
                protected void populateViewHolder(MyHotelViewHolder viewHolder, MyHotels model, int position) {
                    viewHolder.txtMenuName.setText(model.getName());
                    viewHolder.txtLocation.setText(model.getLocation());
                    viewHolder.txtDistance.setText(Common.currentlocation);
                    viewHolder.txtPrice.setText(model.getPrice());
                    Picasso.with(getBaseContext()).load(model.getImage())
                            .into(viewHolder.imageView);
                    final MyHotels clickItem = model;
                    Common.currentHotel=clickItem;

                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //start new activity
                            Intent hoteldetail =new Intent(Myhotel.this,HotelDetails.class);
                            hoteldetail.putExtra("hotelId",adapter.getRef(position).getKey()); //send hotel Id to new activity
                            startActivity(hoteldetail);

                        }
                    });

                }
            };
            showView(myhotellist);
            hideView(loadingProgress);
            recycler_myhotel.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdatehotelDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deletehotel(adapter.getRef(item.getOrder()).getKey());
        }


        return true;
    }

    private void showUpdatehotelDialog(final String key,final MyHotels item) {
        final android.support.v7.app.AlertDialog.Builder alertdialog =new android.support.v7.app.AlertDialog.Builder(Myhotel.this);
        alertdialog.setTitle("Edit Hotel");
        alertdialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View update_hotel = inflater.inflate(R.layout.updatehotel,null);

        hname= update_hotel.findViewById(R.id.addname);
        hcounty= update_hotel.findViewById(R.id.addcounty);
        hladdres= update_hotel.findViewById(R.id.addLaddress);
        hAprice= update_hotel.findViewById(R.id.addprice);
        editimagehotel= update_hotel.findViewById(R.id.addimagehotel);

        //set default value for view
        hname.setText(item.getName());
        hcounty.setText(item.getLocation());
        hladdres.setText(item.getLocationdetail());
        hAprice.setText(item.getPrice());
        //Event for button
        editimagehotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(Myhotel.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        // Toast.makeText(Myhotel.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Myhotel.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(16, 9)
                                .start(Myhotel.this);

                    }
                }
            }
        });

        alertdialog.setView(update_hotel);
        alertdialog.setIcon(R.drawable.ic_local_hotel_black_24dp);

        //Set button
        alertdialog.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                changeImage(key ,item);
                //update info

            }
        });
        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertdialog.show();

    }
    private void changeImage(final String key, final MyHotels item) {
        if(saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(Myhotel.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // set value for new Category if image uploaded and we can get download link
                                    item.setImage(uri.toString());
                                    item.setName(hname.getText().toString());
                                    item.setPrice(hAprice.getText().toString());
                                    item.setLocationdetail(hladdres.getText().toString());
                                    item.setLocation(hcounty.getText().toString());

                                    myhotel.child(key).setValue(item);

                                    Snackbar.make(rootLayout,"Hotel "+item.getName()+" was edited",Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(Myhotel.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded"+progress+"%");
                }
            });
        }
    }

    private void deletehotel(String key) {
        myhotel.child(key).removeValue();

        Client client = new Client(Common.APPLICATION_ID,Common. ADMIN_API_KEY);
        final Index index = client.getIndex("hotel_LOCATION");
        index.deleteObjectAsync(key, null);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                saveUri=result.getUri();
                editimagehotel.setImageURI(saveUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
