package edu.uncc.weatherapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uncc.weatherapp.R;
import edu.uncc.weatherapp.databinding.FragmentCitiesBinding;
import edu.uncc.weatherapp.models.City;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CitiesFragment extends Fragment {
    public CitiesFragment() {
        // Required empty public constructor
    }

    private final OkHttpClient client = new OkHttpClient();
    FragmentCitiesBinding binding;
    ArrayAdapter<City> adapter;
    ArrayList<City> cityList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cityList.clear();
        adapter = new CityAdapter(getActivity(), cityList);
        binding.listView.setAdapter(adapter);
        getCities();


        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = adapter.getItem(position);
                mListener.gotoWeatherForecast(city);
            }
        });
    }

    void getCities(){
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/cities")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    Log.d("demo", "onResponse: " + body);


                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONArray citiesJsonArray = jsonObject.getJSONArray("cities");

                        for(int i=0; i<citiesJsonArray.length(); i++){
                            JSONObject cityJsonObject = citiesJsonArray.getJSONObject(i);
                            City city = new City(cityJsonObject);
                            cityList.add(city);
                            Log.d("demo", "onResponse: " + cityList);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    String m = "not successful";
                    Log.d("demo", "onResponse: " + m);
                }
            }
        });
    }

    class CityAdapter extends ArrayAdapter<City>{

        public CityAdapter(@NonNull Context context, @NonNull List<City> objects) {
            super(context, R.layout.city_list_item, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_list_item, parent, false);
            }
            City city = getItem(position);

            TextView textViewCity = convertView.findViewById(R.id.textViewCity);
            textViewCity.setText(city.getName() + ", " + city.getState());

            return convertView;
        }
    }

    CityListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CityListener) context;
    }

    public interface CityListener{
        void gotoWeatherForecast(City city);
    }
}