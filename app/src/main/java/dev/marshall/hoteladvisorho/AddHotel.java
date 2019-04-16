package dev.marshall.hoteladvisorho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.GsonBuilder;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.marshall.hoteladvisorho.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.common.Config;
import dev.marshall.hoteladvisorho.model.Amenities;
import dev.marshall.hoteladvisorho.model.MoreInfo;
import dev.marshall.hoteladvisorho.model.MoreInformation;
import dev.marshall.hoteladvisorho.model.MyHotels;

public class AddHotel extends AppCompatActivity {

    private IGeoCoordinates mService;

    String nameError = "Correct Hotel Name is requierd";
    String countyError = "Correct County is requierd";
    String addressError = "Correct  Location address is requierd";
    String pricErrore = "Correct  price is requierd";
    String urlError = "Correct  url is requierd";
    String infoError = "Correct  Hotel Information is requierd";
    String checkiError = "Correct  Check-In time is requierd";
    String checkoError = "Correct  Check-Out time is requierd";
    String extraError = "Correct  Extras is requierd";
    String diningError = "Correct  Dining is requierd";
    String recreationError = "Correct  Recreation is requierd";
    String nearError = "Correct  What's Near is requierd";
    String additionalError = "Correct  Additional Information is requierd";

    public void setnameError(String nameError) {
        this.nameError = nameError;
    }

    public void setCountyError(String countyError) {
        this.countyError = countyError;
    }

    public void setaddressError(String addressError) {
        this.addressError = addressError;
    }

    public void setpricErrore(String pricErrore) {
        this.pricErrore = pricErrore;
    }

    public void seturlError(String urlError) {
        this.urlError = urlError;
    }

    public void setinfoError(String infoError) {
        this.infoError = infoError;
    }

    public void setcheckiError(String checkiError) {
        this.checkiError = checkiError;
    }

    public void setcheckoError(String checkoError) {
        this.checkoError = checkoError;
    }

    public void setDiningError(String diningError) {
        this.diningError = diningError;
    }
    public void setRecreationError(String recreationError) {
        this.recreationError = recreationError;
    }
    public void setNearError(String nearError) {
        this.nearError = nearError;
    }
    public void setAdditionalError(String additionalError) {
        this.additionalError = additionalError;
    }
    public void setextraError(String extraError) {
        this.extraError = extraError;
    }


    RelativeLayout rootLayout;
    ImageView addimagehotel;
    MaterialSpinner hotelclass;
    Button submitdetails;
    EditText hname, hcounty, hladdres, hAprice, hurl, hinfo, checkin, checkout, extras
            ,dining,recreation,near,additional;
    com.rey.material.widget.CheckBox parking,food,security,internet ,laundry,golf,bar,beach,pool,gym,rservice,childfriendly,spa,casino,airportshuttle,airconditioning;
    //Firebase
    FirebaseDatabase db;
    DatabaseReference hotels;
    FirebaseStorage storage;
    StorageReference storageReference;

    MyHotels newhotel;
    Amenities amenities;
    MoreInformation moreInfo;
    Uri saveUri = null;

    String uniquekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hotel);

        rootLayout = findViewById(R.id.rootlayout);

        mService=Common.getGeoCodeService();
        //Firebase
        db = FirebaseDatabase.getInstance();
        hotels = db.getReference("Hotels");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        hotelclass= findViewById(R.id.hotelclass);
        hotelclass.setItems("1 Star","2 Star","3 Star","4 Star","5 Star","6 Star","7 Star");

        hname = findViewById(R.id.addname);
        hcounty = findViewById(R.id.addcounty);
        hladdres = findViewById(R.id.addLaddress);
        hAprice = findViewById(R.id.addprice);
        hurl = findViewById(R.id.addurl);
        hinfo = findViewById(R.id.addinfo);
        checkin = findViewById(R.id.chkin);
        checkout = findViewById(R.id.chkout);
        extras = findViewById(R.id.Extras);
        dining = findViewById(R.id.dinin);
        recreation = findViewById(R.id.recrea);
        near = findViewById(R.id.nea);
        additional = findViewById(R.id.add);


        parking = findViewById(R.id.chbparking);
        food = findViewById(R.id.chbfood);
        laundry = findViewById(R.id.chblaundry);
        security = findViewById(R.id.chbsecurity);
        pool = findViewById(R.id.chbpool);
        internet = findViewById(R.id.chbinternet);
        golf = findViewById(R.id.chbgolf);
        spa = findViewById(R.id.chbspa);
        airconditioning = findViewById(R.id.chbair);
        airportshuttle = findViewById(R.id.chbairport);
        childfriendly = findViewById(R.id.child);
        casino = findViewById(R.id.casino);
        bar = findViewById(R.id.chbbar);
        beach = findViewById(R.id.chbbeach);
        rservice = findViewById(R.id.chbroom);
        gym = findViewById(R.id.chbgym);
        addimagehotel = findViewById(R.id.addimagehotel);


        addimagehotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(AddHotel.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(AddHotel.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AddHotel.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(16, 9)
                                .start(AddHotel.this);

                    }

                }
                else
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(16, 9)
                            .start(AddHotel.this);
            }

        });


        submitdetails = findViewById(R.id.submitdetails);
        submitdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(getString(hname))) {
                    hname.setError(nameError);
                    return;
                }
                if (TextUtils.isEmpty(getString(hcounty))) {
                    hcounty.setError(countyError);
                    return;
                }
                if (TextUtils.isEmpty(getString(hurl))) {
                    hurl.setError(urlError);
                    return;
                }
                if (TextUtils.isEmpty(getString(hinfo))) {
                    hinfo.setError(infoError);
                    return;
                }
                if (TextUtils.isEmpty(getString(hladdres))) {
                    hladdres.setError(addressError);
                    return;
                }
                if (TextUtils.isEmpty(getString(checkin))) {
                    checkin.setError(checkiError);
                    return;
                }
                if (TextUtils.isEmpty(getString(checkout))) {
                    checkout.setError(checkoError);
                    return;
                }
                if (TextUtils.isEmpty(getString(hAprice))) {
                    hAprice.setError(pricErrore);
                    return;
                }
                if (TextUtils.isEmpty(getString(extras))) {
                    extras.setError(extraError);
                    return;
                }
                if (TextUtils.isEmpty(getString(dining))) {
                    dining.setError(diningError);
                    return;
                }
                if (TextUtils.isEmpty(getString(recreation))) {
                    recreation.setError(recreationError);
                    return;
                }
                if (TextUtils.isEmpty(getString(near))) {
                    near.setError(nearError);
                    return;
                }
                if (TextUtils.isEmpty(getString(additional))) {
                    additional.setError(additionalError);
                    return;
                }
                uploadandsubmitdetails();
            }
        });


    }
    private String getString(EditText ed) {
        return ed.getText().toString().trim();
    }


    private void uploadandsubmitdetails() {

        if (saveUri != null) {
            final boolean park = parking.isChecked();
            final boolean p = pool.isChecked();
            final boolean s = security.isChecked();
            final boolean l = laundry.isChecked();
            final boolean f = food.isChecked();
            final boolean i = internet.isChecked();
            final boolean sp = spa.isChecked();
            final boolean rservic = rservice.isChecked();
            final boolean aircon = airconditioning.isChecked();
            final boolean airportsh = airportshuttle.isChecked();
            final boolean casin = casino.isChecked();
            final boolean golfc = golf.isChecked();
            final boolean chilfriendly = childfriendly.isChecked();
            final boolean bar_lounge = bar.isChecked();
            final boolean beac = beach.isChecked();
            final boolean _gym = gym.isChecked();

             moreInfo=new MoreInformation(
                    dining.getText().toString(),
                    recreation.getText().toString(),
                    near.getText().toString(),
                    additional.getText().toString()
            );

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading");
            mDialog.show();

            amenities=new Amenities(
                    String.valueOf(park),
                    String.valueOf(f),
                    String.valueOf(s),
                    String.valueOf(i),
                    String.valueOf(l),
                    String.valueOf(golfc),
                    String.valueOf(bar_lounge),
                    String.valueOf(beac),
                    String.valueOf(p),
                    String.valueOf(_gym),
                    String.valueOf(rservic),
                    String.valueOf(chilfriendly),
                    String.valueOf(sp),
                    String.valueOf(casin),
                    String.valueOf(aircon),
                    String.valueOf(airportsh)
            );

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newhotel = new MyHotels();
                                    newhotel.setName(hname.getText().toString());
                                    newhotel.setDescription(hinfo.getText().toString());
                                    newhotel.setPrice(hAprice.getText().toString());
                                    newhotel.setLocation(hcounty.getText().toString());
                                    newhotel.setLocationdetail(hladdres.getText().toString());
                                    newhotel.setUrl(hurl.getText().toString());
                                    newhotel.setCheckin(checkin.getText().toString());
                                    newhotel.setCheckout(checkout.getText().toString());
                                    newhotel.setExtras(extras.getText().toString());
                                    newhotel.setAmenities(amenities);
                                    newhotel.setPhone(Common.currentHotelOwner.getPhone());
                                    newhotel.setImage(uri.toString());
                                    newhotel.setMoreInfo(moreInfo);
                                    newhotel.setHotelstar(Common.Hotelclass(String.valueOf(hotelclass.getSelectedIndex())));

                                    if (newhotel !=null)
                                    {hotels.push().setValue(newhotel, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                               uniquekey = databaseReference.getKey();

                                                hotels.getKey();
                                                Client client = new Client(Common.APPLICATION_ID,Common. ADMIN_API_KEY);
                                                final Index index = client.getIndex("hotel_LOCATION");

                                                String json = new GsonBuilder().create().toJson(newhotel);
                                                JSONObject j = null;
                                                try {
                                                    j = new JSONObject(json);
                                                    j.put("Rating", "0.0");
                                                    j.put("_geoloc", new JSONObject().put("lat", Common.currenthotellocation_lat).put("lng", Common.currenthotellocation_lng));
                                                    index.addObjectAsync(j,uniquekey,null);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                Snackbar.make(rootLayout,"New hotel"+ newhotel.getName()+"was added",Snackbar.LENGTH_SHORT)
                                                        .show();
                                                Intent myhotels=new Intent(AddHotel.this,Myhotel.class);
                                                startActivity(myhotels);
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(AddHotel.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                addimagehotel.setImageURI(saveUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
