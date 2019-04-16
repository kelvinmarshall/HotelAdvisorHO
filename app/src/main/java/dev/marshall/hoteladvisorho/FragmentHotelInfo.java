package dev.marshall.hoteladvisorho;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.marshall.hoteladvisorho.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.common.DirectionJSONParser;
import dev.marshall.hoteladvisorho.model.Amenities;
import dev.marshall.hoteladvisorho.model.MoreInfo;
import dev.marshall.hoteladvisorho.model.MoreInfoCategory;
import dev.marshall.hoteladvisorho.model.MoreInformation;
import dev.marshall.hoteladvisorho.model.MyHotels;
import dev.marshall.hoteladvisorho.model.Rating;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dev.marshall.hoteladvisorho.R.drawable.ic_arrow_left_black_48dp;

public class FragmentHotelInfo extends Fragment implements OnMapReadyCallback {
    int color;

    public FragmentHotelInfo() {
    }

    @SuppressLint("ValidFragment")
    public FragmentHotelInfo(int color) {
        this.color = color;
    }
            int valsec,valamen,valaccess,valmoney,valcomf,valstaff,valueclean;
            float averagesec,averageamen,averageacces,averageval,averagecomf,averagestaff,averageclean;
    private GoogleMap mMap;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;

    private Location mLastLocation;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    Marker mCurrentmarker;
    Polyline polyline;

    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private IGeoCoordinates mService;

    RelativeLayout rootlayout;
    com.rey.material.widget.CheckBox parking,food,security,internet ,laundry,golf,bar,beach,pool,gym,rservice,childfriendly,spa,casino,airportshuttle,airconditioning;

    EditText cout,cin,conditions,detail;

    ProgressBar securit,amenities,accessbility,value,comfort,staff,cleanliness;
    TextView sec,amen,access,val,comf,staf,clean;
    ImageButton editamenities,edithoteldetails,editpolicies,editmoreinfo;
    TextView HotelName,RealLocation,description,checkin,checkout,extras;
    EditText dining,recreation,near,additional;
    TextView pa,fo,la,se,po,in,ba,be,rser,ch,sp,ca,airc,airport,gol,gy;
    String HotelId="";

    String sdining,srecreation,snear,sadditional;

    FirebaseDatabase database;
    DatabaseReference hotels,rate,moreinfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        hotels=database.getReference("Hotels");
        rate=database.getReference("Rating");
        moreinfo=database.getReference("Hotels");

        getRateSecurity(HotelId);
        getRateStaff(HotelId);
        getRateAmenities(HotelId);
        getRateAccessbility(HotelId);
        getRateComfort(HotelId);
        getRateCleanliness(HotelId);
        getRateValue(HotelId);

    }

    MyHotels currentHotel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_fragment_hotel_info,container,false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rootlayout= view.findViewById(R.id.rootlayout);

        sdining= Common.currentHotel.getMoreInfo().getDining();
        srecreation=Common.currentHotel.getMoreInfo().getRecreation();
        snear= Common.currentHotel.getMoreInfo().getNear();
        sadditional=Common.currentHotel.getMoreInfo().getAdditional();

        securit= view.findViewById(R.id.sec);
        staff= view.findViewById(R.id.staf);
        amenities= view.findViewById(R.id.amen);
        accessbility= view.findViewById(R.id.access);
        value= view.findViewById(R.id.val);
        comfort= view.findViewById(R.id.comf);
        cleanliness= view.findViewById(R.id.clean);

        sec= view.findViewById(R.id.valuesec);


        CircleDisplay cd = view.findViewById(R.id.circleDisplay);
        cd.setColor(Color.parseColor("#00a680"));
        cd.setAnimDuration(3000);
        cd.setValueWidthPercent(55f);
        cd.setTextSize(14f);
        cd.setColor(Color.GREEN);
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);
        cd.setFormatDigits(1);
        cd.setTouchEnabled(true);
        cd.setUnit("%");
        cd.setStepSize(0.5f);
        // cd.setCustomText(...); // sets a custom array of text
        cd.showValue(75f, 100f, true);

        ExpandableLayout layout= view.findViewById(R.id.expandablelayout);

        layout.setRenderer(new ExpandableLayout.Renderer<MoreInfoCategory, MoreInfo>() {

            @Override
            public void renderParent(View view, MoreInfoCategory moreInfoCategory, boolean isExpanded, int parentPosition) {
                ((TextView)view.findViewById(R.id.parent_name)).setText(moreInfoCategory.name);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.ic_arrow_up:R.drawable.ic_arrow_down);

            }

            @Override
            public void renderChild(View view, MoreInfo moreInfo, int parentPosition, int childPosition) {
                ((TextView)view.findViewById(R.id.childtext)).setText(moreInfo.name);

            }
        });

        sec = view.findViewById(R.id.valuesec);
        access = view.findViewById(R.id.valaccess);
        amen= view.findViewById(R.id.valamen);
        comf = view.findViewById(R.id.valcomf);
        val = view.findViewById(R.id.valmoney);
        clean = view.findViewById(R.id.valclean);
        staf = view.findViewById(R.id.valstaff);

        layout.addSection(getDining());
        layout.addSection(getRecreation());
        layout.addSection(getNear());
        layout.addSection(getAdditional());

        HotelId=HotelDetails.HotelId;

        pa= view.findViewById(R.id.pa);
        fo= view.findViewById(R.id.fo);
        se= view.findViewById(R.id.se);
        in= view.findViewById(R.id.in);
        la= view.findViewById(R.id.la);
        po= view.findViewById(R.id.po);
        ba= view.findViewById(R.id.txtbar);
        be= view.findViewById(R.id.txtbeach);
        rser= view.findViewById(R.id.txtrservice);
        ch= view.findViewById(R.id.txtchild);
        sp= view.findViewById(R.id.txtsap);
        ca= view.findViewById(R.id.txtcasino);
        airc= view.findViewById(R.id.txtair);
        airport= view.findViewById(R.id.textshuttle);
        gol= view.findViewById(R.id.txtgolf);
        gy= view.findViewById(R.id.txtgym);
        HotelId = HotelDetails.HotelId;


        hotels.child(HotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentHotel = dataSnapshot.getValue(MyHotels.class);
                if (currentHotel != null) {

                    if (currentHotel.getAmenities().getFood().equals("true")) {
                        fo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_cafe_black_24dp, 0, 0, 0);
                        fo.setTextColor(Color.BLACK);
                    } else {
                        fo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.food_false, 0, 0, 0);
                        fo.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getInternet().equals("true")) {
                        in.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_black_24dp, 0, 0, 0);
                        in.setTextColor(Color.BLACK);
                    } else {
                        in.setCompoundDrawablesWithIntrinsicBounds(R.drawable.internet_false, 0, 0, 0);
                        in.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getLaundry().equals("true")) {
                        la.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_laundry_service_black_24dp, 0, 0, 0);
                        la.setTextColor(Color.BLACK);
                    } else {
                        la.setCompoundDrawablesWithIntrinsicBounds(R.drawable.laundry_false, 0, 0, 0);
                        la.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getSecurity().equals("true")) {
                        se.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_security_black_24dp, 0, 0, 0);
                        se.setTextColor(Color.BLACK);
                    } else {
                        se.setCompoundDrawablesWithIntrinsicBounds(R.drawable.security_false, 0, 0, 0);
                        se.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getPool().equals("true")) {
                        po.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pool_black_24dp, 0, 0, 0);
                        po.setTextColor(Color.BLACK);
                    } else {
                        po.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pool_false, 0, 0, 0);
                        po.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getParking().equals("true")) {
                        pa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_parking_black_24dp, 0, 0, 0);
                        pa.setTextColor(Color.BLACK);
                    } else {
                        pa.setCompoundDrawablesWithIntrinsicBounds(R.drawable.parking_false, 0, 0, 0);
                        pa.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getAirconditioning().equals("true")) {
                        airc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_airconditioning, 0, 0, 0);
                        airc.setTextColor(Color.BLACK);
                    } else {
                        airc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_airconditioning, 0, 0, 0);
                        airc.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getAirportshuttle().equals("true")) {
                        airport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_airport_shuttle_black_24dp, 0, 0, 0);
                        airport.setTextColor(Color.BLACK);
                    } else {
                        airport.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_shuttle, 0, 0, 0);
                        airport.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getBar().equals("true")) {
                        ba.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_bar_black_24dp, 0, 0, 0);
                        ba.setTextColor(Color.BLACK);
                    } else {
                        ba.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_bar, 0, 0, 0);
                        ba.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getBeach().equals("true")) {
                        be.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_beach_access_black_24dp, 0, 0, 0);
                        be.setTextColor(Color.BLACK);
                    } else {
                        be.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_beach, 0, 0, 0);
                        be.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getCasino().equals("true")) {
                        ca.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_casino_black_24dp, 0, 0, 0);
                        ca.setTextColor(Color.BLACK);
                    } else {
                        ca.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_calsino, 0, 0, 0);
                        ca.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getChild().equals("true")) {
                        ch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_child_friendly_black_24dp, 0, 0, 0);
                        ch.setTextColor(Color.BLACK);
                    } else {
                        ch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_child, 0, 0, 0);
                        ch.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getGolf().equals("true")) {
                        gol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_golf_course_black_24dp, 0, 0, 0);
                        gol.setTextColor(Color.BLACK);
                    } else {
                        gol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_golf, 0, 0, 0);
                        gol.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getGym().equals("true")) {
                        gy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fitness_center_black_24dp, 0, 0, 0);
                        gy.setTextColor(Color.BLACK);
                    } else {
                        gy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_fitness, 0, 0, 0);
                        gy.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getRservice().equals("true")) {
                        rser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_room_service_black_24dp, 0, 0, 0);
                        rser.setTextColor(Color.BLACK);
                    } else {
                        rser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_rservice, 0, 0, 0);
                        rser.setTextColor(Color.GRAY);
                    }
                    if (currentHotel.getAmenities().getSpa().equals("true")) {
                        sp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_spa_black_24dp, 0, 0, 0);
                        sp.setTextColor(Color.BLACK);
                    } else {
                        sp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.false_spa, 0, 0, 0);
                        sp.setTextColor(Color.GRAY);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        HotelName= view.findViewById(R.id.hotel_name);
        RealLocation= view.findViewById(R.id.real_location);
        description= view.findViewById(R.id.hotel_description);

        checkin= view.findViewById(R.id.chkin);
        checkout= view.findViewById(R.id.chkout);
        extras= view.findViewById(R.id.Extras);

        editamenities= view.findViewById(R.id.editamenities);
        edithoteldetails= view.findViewById(R.id.editdetail);
        editpolicies= view.findViewById(R.id.editpolicies);
        editmoreinfo= view.findViewById(R.id.editmoreinfo);

        editamenities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editamenities();
            }
        });
        edithoteldetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editdetails();
            }
        });
        editpolicies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editpolicies();
            }
        });
        editmoreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editmoreinfo();
            }
        });

        mService = Common.getGeoCodeService();

        //check permission
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_PERMISSION_REQUEST);
        } else {
            builLocationRequest();
            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }

        if(!HotelId.isEmpty())
        {
            getDetailHotel(HotelId);
        }


        return  view;

    }
    private void builLocationRequest() {
        locationRequest=LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

    }

    private void buildLocationCallBack() {
        locationCallback =new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation=locationResult.getLastLocation();
                if (mCurrentmarker !=null)
                    mCurrentmarker.setPosition(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                drawRoute(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),Common.currentHotel);
            }
        };
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        builLocationRequest();
                        buildLocationCallBack();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "You should assign permission", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
            default:
                break;
        }
    }


    private void drawRoute(final LatLng yourlocation, final MyHotels currentHotel) {
                if (polyline!=null)
                    polyline.remove();

                if (currentHotel.getLocationdetail() !=null && !currentHotel.getLocationdetail().isEmpty())
                {
                    String address=currentHotel.getLocationdetail();
                    mService.getGeoCode(address).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String>call, Response<String>response) {
                            try{
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                String lat = ((JSONArray)jsonObject.get("results"))
                                        .getJSONObject(0)
                                        .getJSONObject("geometry")
                                        .getJSONObject("location")
                                        .get("lat").toString();

                                String lng = ((JSONArray)jsonObject.get("results"))
                                        .getJSONObject(0)
                                        .getJSONObject("geometry")
                                        .getJSONObject("location")
                                        .get("lng").toString();

                                LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                                float results[]=new float[1];
                                Location.distanceBetween(yourlocation.latitude,yourlocation.longitude,orderLocation.latitude
                                        ,orderLocation.longitude,results);
                                Common.currentlocation = results[0]/1000.0f+" Km";
                               // update to distance to firebase
                                Map<String,Object>updatelatlng = new HashMap<>();
                                updatelatlng.put("lat",Double.parseDouble(lat));
                                updatelatlng.put("lng",Double.parseDouble(lng));
                                hotels.child(HotelId).updateChildren(updatelatlng);

                                //update algolia
                                Client client = new Client(Common.APPLICATION_ID, Common.ADMIN_API_KEY);
                                final Index index = client.getIndex("hotel_LOCATION");
                                JSONObject j = new JSONObject();
                                j.put("_geoloc", new JSONObject().put("lat", Double.parseDouble(lat)).put("lng",Double.parseDouble(lng)));
                                index.partialUpdateObjectAsync(j,HotelId,null);



                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.hotel_icon);

                                bitmap = Common.scaleBitmap(bitmap,200,200);

                                MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                        .title("Order of"+Common.currentHotel.getPhone())
                                        .position(orderLocation)
                                        .snippet("Distance = "+results[0]);
                                mMap.addMarker(marker);

                                //draw route
                                mService.getDirections(yourlocation.latitude+","+yourlocation.longitude
                                        ,orderLocation.latitude+","+orderLocation.longitude)
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                new ParserTask().execute(response.body().toString());
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {

                                            }
                                        });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }

    private void editamenities() {
        AlertDialog.Builder alertdialog =new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Check or uncheck according to availability");
        LayoutInflater inflater = this.getLayoutInflater();
        View changeamenities = inflater.inflate(R.layout.editamenities,null);

        parking= changeamenities.findViewById(R.id.chbparking);
        food= changeamenities.findViewById(R.id.chbfood);
        laundry= changeamenities.findViewById(R.id.chblaundry);
        security= changeamenities.findViewById(R.id.chbsecurity);
        pool= changeamenities.findViewById(R.id.chbpool);
        internet= changeamenities.findViewById(R.id.chbinternet);
        golf = changeamenities.findViewById(R.id.chbgolf);
        spa = changeamenities.findViewById(R.id.chbspa);
        airconditioning = changeamenities.findViewById(R.id.chbair);
        airportshuttle = changeamenities.findViewById(R.id.chbairport);
        childfriendly = changeamenities.findViewById(R.id.child);
        casino = changeamenities.findViewById(R.id.casino);
        bar = changeamenities.findViewById(R.id.chbbar);
        beach = changeamenities.findViewById(R.id.chbbeach);
        rservice = changeamenities.findViewById(R.id.chbroom);
        gym = changeamenities.findViewById(R.id.chbgym);


        alertdialog.setView(changeamenities);

        alertdialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

                final Amenities amenities=new Amenities(
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
                //make update
                currentHotel.setAmenities(amenities);

                hotels.child(HotelId).setValue(currentHotel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Snackbar.make(rootlayout,"Amenities values were successfuly edited edited",Snackbar.LENGTH_SHORT)
                                .show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootlayout,"Amenities values update failed",Snackbar.LENGTH_SHORT)
                                .show();

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

    private void editdetails() {
        AlertDialog.Builder alertdialog =new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Update hotel details");
        LayoutInflater inflater = this.getLayoutInflater();
        View changedetails = inflater.inflate(R.layout.edithoteldetails,null);

        detail=changedetails.findViewById(R.id.details);

        ////set default value for view

        detail.setText(currentHotel.getDescription());

        alertdialog.setView(changedetails);

        alertdialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String,Object> updatedetail=new HashMap<>();
                updatedetail.put("description",detail.getText().toString());


                //make update
                hotels.child(HotelId)
                        .updateChildren(updatedetail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Hotel detail updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Hotel detail update failed", Toast.LENGTH_SHORT).show();
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
    private void editpolicies() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Update your hotel policies");
        LayoutInflater inflater = this.getLayoutInflater();
        View changepolicies = inflater.inflate(R.layout.editpolicies, null);

        cin = changepolicies.findViewById(R.id.chkin);
        cout = changepolicies.findViewById(R.id.chkout);
        conditions = changepolicies.findViewById(R.id.Extras);

        ////set default value for view
        cin.setText(currentHotel.getCheckin());
        cout.setText(currentHotel.getCheckout());
        conditions.setText(currentHotel.getExtras());

        alertdialog.setView(changepolicies);

        alertdialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                //make update
                currentHotel.setCheckin(cin.getText().toString());
                currentHotel.setCheckout(cout.getText().toString());
                currentHotel.setExtras(conditions.getText().toString());
                hotels.child(HotelId).setValue(currentHotel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Snackbar.make(rootlayout, "Policies were successfuly edited edited", Snackbar.LENGTH_SHORT)
                                        .show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootlayout, "Policies Amenities values update failed", Snackbar.LENGTH_SHORT)
                                .show();

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
    private void editmoreinfo() {
        AlertDialog.Builder alertdialog =new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Update your hotel More Information");
        LayoutInflater inflater = this.getLayoutInflater();
        View changepolicies = inflater.inflate(R.layout.editmoreinfo,null);



        dining= changepolicies.findViewById(R.id.dining);
        recreation= changepolicies.findViewById(R.id.recreation);
        near= changepolicies.findViewById(R.id.near);
        additional= changepolicies.findViewById(R.id.additional);

                ////set default value for view
        dining.setText(currentHotel.getMoreInfo().getDining());
        recreation.setText(currentHotel.getMoreInfo().getRecreation());
        near.setText(currentHotel.getMoreInfo().getNear());
        additional.setText(currentHotel.getMoreInfo().getAdditional());

        alertdialog.setView(changepolicies);

        alertdialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final MoreInformation moreInfo=new MoreInformation(
                        dining.getText().toString(),
                        recreation.getText().toString(),
                        near.getText().toString(),
                        additional.getText().toString()
                );

                //make update
                currentHotel.setMoreInfo(moreInfo);

                hotels.child(HotelId).setValue(currentHotel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Snackbar.make(rootlayout,"Hotel More information were successfuly edited",Snackbar.LENGTH_SHORT)
                                        .show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootlayout,"More Information values update failed",Snackbar.LENGTH_SHORT)
                                        .show();

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

    private void getDetailHotel(String hotelId) {
        hotels.child(HotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentHotel=dataSnapshot.getValue(MyHotels.class);

                HotelName.setText(currentHotel.getName());

                RealLocation.setText(currentHotel.getLocationdetail());

                description.setText(currentHotel.getDescription());

                checkin.setText(currentHotel.getCheckin());

                checkout.setText(currentHotel.getCheckout());

                extras.setText(currentHotel.getExtras());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getRateSecurity(String hotelId) {


        com.google.firebase.database.Query securityrating=rate.orderByChild("hotelID").equalTo(HotelId);
        securityrating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getValSecurity());
                    count++;
                    Toast.makeText(getActivity(), "===="+sum, Toast.LENGTH_SHORT).show();
                }
                if (count!=0)
                {
                    averagesec = sum/count;


                }
                valsec= (int) (averagesec/5*100);
                sec.setText(String.valueOf(averagesec)+"/5");
                securit.setMax(100);
                securit.setProgress(0);
                securit.setProgress(valsec);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRateAmenities(String hotelId) {
      com.google.firebase.database.Query productRating=rate.orderByChild("hotelID").equalTo(hotelId);

      productRating.addValueEventListener(new ValueEventListener() {
          int count=0,sum=0;
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
              {
                  Rating item = postSnapshot.getValue(Rating.class);
                  sum+=Integer.parseInt(item.getValAmenities());
                  count++;
              }
              if (count!=0)
              {
                  averageamen = sum/count;

              }
              amen.setText(String.valueOf(averageamen)+"/5");
              valamen= (int) (averageamen/5*100);
              amenities.setMax(100);
              amenities.setProgress(0);
              amenities.setProgress(valamen);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

    }
    private void getRateAccessbility(String hotelId){
       com.google.firebase.database.Query productRating=rate.orderByChild("hotelID").equalTo(hotelId);

       productRating.addValueEventListener(new ValueEventListener() {
          int count=0,sum=0;
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
              {
                  Rating item = postSnapshot.getValue(Rating.class);
                  sum+=Integer.parseInt(item.getValAccessbility());
                  count++;
              }
              if (count!=0)
              {
                  averageacces = sum/count;

              }
              valaccess= (int) (averageacces/5*100);
              accessbility.setMax(100);
              accessbility.setProgress(0);
              accessbility.setProgress(valaccess);
              access.setText(String.valueOf(averageacces)+"/5");
          }

          @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });

    }
    private void getRateComfort(String hotelId) {
        com.google.firebase.database.Query productRating=rate.orderByChild("hotelID").equalTo(hotelId);

        productRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getValConfort());
                    count++;
                }
                if (count!=0)
                {
                    averagecomf = sum/count;


                }
                valcomf= (int) (averagecomf/5*100);
                comfort.setMax(100);
                comfort.setProgress(0);
                comfort.setProgress(valcomf);
                comf.setText(String.valueOf(averagecomf)+"/5");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getRateCleanliness(String hotelId) {
        com.google.firebase.database.Query productRating=rate.orderByChild("hotelID").equalTo(hotelId);

      productRating.addValueEventListener(new ValueEventListener() {
          int count=0,sum=0;
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
              {
                  Rating item = postSnapshot.getValue(Rating.class);
                  sum+=Integer.parseInt(item.getValCleanliness());
                  count++;
              }
              if (count!=0)
              {
                  averageclean = sum/count;


              }
              clean.setText(String.valueOf(averageclean)+"/5");
              valueclean= (int) (averageclean/5*100);
              cleanliness.setMax(100);
              cleanliness.setProgress(0);
              cleanliness.setProgress(valueclean);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

    }
    private void getRateStaff(String hotelId) {
      com.google.firebase.database.Query productRating=rate.orderByChild("hotelID").equalTo(hotelId);

      productRating.addValueEventListener(new ValueEventListener() {
          int count=0,sum=0;
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
              {
                  Rating item = postSnapshot.getValue(Rating.class);
                  sum+=Integer.parseInt(item.getValStaff());
                  count++;
              }
              if (count!=0)
              {
                  averagestaff = sum/count;

              }
              staf.setText(String.valueOf(averagestaff)+"/5");
              valstaff= (int) (averagestaff/5*100);
              staff.setMax(100);
              staff.setProgress(0);
              staff.setProgress(valstaff);

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

    }
    private void getRateValue(String hotelId){
     com.google.firebase.database.Query productRating = rate.orderByChild("hotelID").equalTo(hotelId);

     productRating.addValueEventListener(new ValueEventListener() {
         int count = 0, sum = 0;

         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                 Rating item = postSnapshot.getValue(Rating.class);
                 sum += Integer.parseInt(item.getValMoney());
                 count++;
             }
             if (count != 0) {
                 averageval = sum / count;

             }
             val.setText(String.valueOf(averageval) + "/5");
             valmoney = (int) (averageval / 5 * 100);
             value.setMax(100);
             value.setProgress(0);
             value.setProgress(valmoney);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });

    }
    private Section<MoreInfoCategory,MoreInfo> getDining() {
      Section<MoreInfoCategory,MoreInfo>section= new Section<>();
      MoreInfo moreInfo=new MoreInfo(sdining);

      MoreInfoCategory moreInfoCategory= new MoreInfoCategory("Dining");
      section.parent=moreInfoCategory;
      section.children.add(moreInfo);
      return section;
    }
    private Section<MoreInfoCategory,MoreInfo> getRecreation() {
     Section<MoreInfoCategory,MoreInfo>section= new Section<>();
     MoreInfo moreInfo=new MoreInfo(srecreation);
     MoreInfoCategory moreInfoCategory= new MoreInfoCategory("Recreation");

     section.parent=moreInfoCategory;
     section.children.add(moreInfo);
     return section;
    }
    private Section<MoreInfoCategory,MoreInfo> getNear (){
     Section<MoreInfoCategory,MoreInfo>section= new Section<>();
     MoreInfo moreInfo=new MoreInfo(snear);
     MoreInfoCategory moreInfoCategory= new MoreInfoCategory("What's Near");

     section.parent=moreInfoCategory;
     section.children.add(moreInfo);
     return section;
    }
    private Section<MoreInfoCategory,MoreInfo> getAdditional() {
      Section<MoreInfoCategory,MoreInfo>section= new Section<>();
      MoreInfo moreInfo=new MoreInfo(sadditional);
      MoreInfoCategory moreInfoCategory= new MoreInfoCategory("Additional Information");

      section.parent=moreInfoCategory;
      section.children.add(moreInfo);
      return section;
    }
    @Override
    public void onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        getRateSecurity(HotelId);
        getRateStaff(HotelId);
        getRateAmenities(HotelId);
        getRateAccessbility(HotelId);
        getRateComfort(HotelId);
        getRateCleanliness(HotelId);
        getRateValue(HotelId);

    }

    @Override
    public void onResume() {
        super.onResume();
        getRateSecurity(HotelId);
        getRateStaff(HotelId);
        getRateAmenities(HotelId);
        getRateAccessbility(HotelId);
        getRateComfort(HotelId);
        getRateCleanliness(HotelId);
        getRateValue(HotelId);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLastLocation=location;
                LatLng yourlocation=new LatLng(location.getLatitude(),location.getLatitude());
                mCurrentmarker= mMap.addMarker(new MarkerOptions().position(yourlocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourlocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            }
        });

    }
    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {

        ProgressDialog mDialog = new ProgressDialog(getContext());
        @Override
        protected void onPreExecute() {
                super.onPreExecute();
                mDialog.setMessage("Please wait...");
                mDialog.show();

        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String,String>>> routes=null;
            try{
                jObject=new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions lineOptions=null;

            for (int i=0;i<lists.size();i++)
            {
                points=new ArrayList();
                lineOptions=new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for (int j=0;j<path.size();j++)
                {
                    HashMap<String,String> point = path.get(j);

                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }
            if (points !=null) {
                mMap.addPolyline(lineOptions);
            }

        }
    }
}

