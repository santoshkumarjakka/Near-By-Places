package com.example.demo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.apiCall.APIInterface;
import com.example.demo.apiCall.ApiClient;
import com.example.demo.modal.PlacesDetails_Modal;
import com.example.demo.easyroads.R;
import com.example.demo.response.PlacesResponse;
import com.example.demo.response.PlacesResponse.CustomA;
import com.example.demo.response.PlacesResponse.Root;
import com.example.demo.response.Places_details;
import com.example.demo.adapter.Rv_adapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetIdeaFragment extends Fragment {


    APIInterface apiService;
    public String latLngString;

    RecyclerView recyclerView;
    public static final String  PREFS_FILE_NAME = "sharedPreferences";
    ArrayList<PlacesResponse.CustomA> results;

    protected Location mLastLocation;

    ProgressBar progress;

    private static final String TAG = "MainActivity";

    public ArrayList<PlacesDetails_Modal> details_modal;


    private long radius = 3 * 1000 ;

    private static final int MY_PERMISION_CODE = 10;

    public String mAddressOutput;
    public RelativeLayout rl_layout;
    public String Location_type = "ROOFTOP";
    private Button btnNearByIdeas;
    private String placeID;

    // newInstance constructor for creating fragment with arguments
    public static GetIdeaFragment newInstance(String type, String currentLocation) {
        GetIdeaFragment fragmentFirst = new GetIdeaFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("currentLocation", currentLocation);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latLngString=getArguments().getString("currentLocation");
        placeID=getArguments().getString("type");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        Log.i("On Create","true");

        progress = (ProgressBar)view. findViewById(R.id.progress);
         rl_layout  = (RelativeLayout)view. findViewById(R.id.rl_layout);


        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progress.setProgress(0,true);
        }
        else progress.setProgress(0);



        apiService = ApiClient.getClient().create(APIInterface.class);

        recyclerView = (RecyclerView)view. findViewById(R.id.recyclerView);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        

        // Manual check internet conn. on activity start
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            showSnack(true);
        } else {
            progress.setVisibility(View.GONE);
            showSnack(false);
        }

        getUserLocation();

        return view;



    }

  
    private void fetchStores(final String placeType) {

        Call<PlacesResponse.Root> call = apiService.doPlaces(latLngString,radius,placeType, ApiClient.GOOGLE_PLACE_API_KEY);
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<PlacesResponse.Root> call, Response<PlacesResponse.Root> response) {

                PlacesResponse.Root root = (Root) response.body();


                if (response.isSuccessful()) {

                    switch (root.status) {
                        case "OK":

                            results = root.customA;

                            details_modal = new ArrayList<PlacesDetails_Modal>();
                            String photourl;


                            for (int i = 0; i < results.size(); i++) {

                                CustomA info = (CustomA) results.get(i);

                                String place_id = results.get(i).place_id;
                                 List<String> s = results.get(i).types;
                                if (results.get(i).photos != null) {

                                    String photo_reference = results.get(i).photos.get(0).photo_reference;

                                     photourl = ApiClient.base_url + "place/photo?maxwidth=100&photoreference=" + photo_reference +
                                            "&key=" + ApiClient.GOOGLE_PLACE_API_KEY;

                                } else {
                                   photourl = "NA";
                                }
                                if(s.contains(placeType)){
                                    fetchPlace_details(info, place_id, "0", info.name, photourl);
                                }
                                Log.i("Coordinates  ", info.geometry.locationA.lat + " , " + info.geometry.locationA.lng);
                                Log.i("Names  ",info.name);

                            }

                            break;
                        case "ZERO_RESULTS":
                            Toast.makeText(getActivity(), "No matches found near you", Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                            break;
                        case "OVER_QUERY_LIMIT":
                            Toast.makeText(getActivity(), "You have reached the Daily Quota of Requests", Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                            break;
                        default:
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                            break;
                    }

                } else if (response.code() != 200) {
                    Toast.makeText(getActivity(), "Error " + response.code() + " found.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(),"Error in Fetching Details,Please Refresh",Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });

    }





    private void fetchPlace_details(final PlacesResponse.CustomA info, final String place_id, final String totaldistance, final String name, final String photourl)
    {

            Call<Places_details> call = apiService.getPlaceDetails(place_id, ApiClient.GOOGLE_PLACE_API_KEY);
            call.enqueue(new Callback<Places_details>() {
                @Override
                public void onResponse(Call<Places_details> call, Response<Places_details> response) {

                    Places_details details = (Places_details) response.body();

                    if ("OK".equalsIgnoreCase(details.status)) {

                        String address = details.result.formatted_adress;
                        String phone = details.result.international_phone_number;
                        float rating = details.result.rating;

                        details_modal.add(new PlacesDetails_Modal(address, phone, rating,totaldistance,name,photourl));

                        Log.i("details : ", info.name + "  " + address);

                        if (details_modal.size()!=0) {

                            progress.setVisibility(View.GONE);
                            Rv_adapter adapterStores = new Rv_adapter(getActivity(), details_modal, mAddressOutput);
                            recyclerView.setAdapter(adapterStores);
                            adapterStores.notifyDataSetChanged();
                        }

                    }

                }

                @Override
                public void onFailure(Call call, Throwable t)
                {
                    call.cancel();
                }
            });

    }


    private void fetchCurrentAddress(final String latLngString) {

        Call<Places_details> call = apiService.getCurrentAddress(latLngString,ApiClient.GOOGLE_PLACE_API_KEY);
        call.enqueue(new Callback<Places_details>() {
            @Override
            public void onResponse(Call<Places_details> call, Response<Places_details> response) {

                Places_details details = (Places_details) response.body();

                if ("OK".equalsIgnoreCase(details.status)) {

                    mAddressOutput = details.results.get(0).formatted_adress;
                    Log.i("Addr Current and coord.",mAddressOutput + latLngString);
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                call.cancel();
            }
        });

    }





    private void getUserLocation()
    {
//        fetchCurrentAddress(latLngString);
//        assert getArguments() != null;
        Log.i("values",getArguments().getString("type"));
        fetchStores(placeID);
    }






    public void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            getUserLocation();
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }




}


