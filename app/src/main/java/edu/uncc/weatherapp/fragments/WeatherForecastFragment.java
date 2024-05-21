package edu.uncc.weatherapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.weatherapp.R;
import edu.uncc.weatherapp.databinding.ForecastListItemBinding;
import edu.uncc.weatherapp.databinding.FragmentWeatherForecastBinding;
import edu.uncc.weatherapp.models.City;
import edu.uncc.weatherapp.models.Forecast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WeatherForecastFragment extends Fragment {
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private City mCity;
    Double lat, lng;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    public static WeatherForecastFragment newInstance(City city) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    FragmentWeatherForecastBinding binding;
    ArrayList<Forecast> forecasts = new ArrayList<>();
    WeatherDetailsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new WeatherDetailsAdapter();
        binding.recyclerView.setAdapter(adapter);

        lat = mCity.getLat();
        lng = mCity.getLng();
        weatherForecast();
    }

    private final OkHttpClient client = new OkHttpClient();
    void weatherForecast(){

//        HttpUrl url = HttpUrl.parse("https://api.weather.gov/points").newBuilder()
//                .addQueryParameter("lat", String.valueOf(mCity.getLat()))
//                .addQueryParameter("lng", String.valueOf(mCity.getLng()))
//                .build();

        HttpUrl.Builder builder = new HttpUrl.Builder();
        HttpUrl url = builder.scheme("https")
                .host("api.weather.gov")
                .addPathSegment("points")
                .addPathSegment(lat + "," + lng)
                .build();



        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d("demo3", "weatherForecast: "+request);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    Log.d("demo2", "onResponse: " + body);


                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONObject propertyJsonObject = jsonObject.getJSONObject("properties");
                        String forecast = propertyJsonObject.getString("forecast");
                        Log.d("demo2", "onResponse: " + forecast);
                        showforecast(forecast);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }

    void showforecast(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    Log.d("demo4", "onResponse: " + body);

                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        JSONObject propertyJsonObject = jsonObject.getJSONObject("properties");
                        JSONArray periodJsonArray = propertyJsonObject.getJSONArray("periods");

                        for(int i=0; i<periodJsonArray.length(); i++){
                            JSONObject forecastJsonObject = periodJsonArray.getJSONObject(i);
                            Forecast forecast = new Forecast();
                            forecast.setStartTime(forecastJsonObject.getString("startTime"));
                            forecast.setTemperature(forecastJsonObject.getString("temperature"));
                            forecast.setShortforcast(forecastJsonObject.getString("shortForecast"));
                            forecast.setWindspeed(forecastJsonObject.getString("windSpeed"));
                            forecast.setIcon(forecastJsonObject.getString("icon"));
                            JSONObject humidity = forecastJsonObject.getJSONObject("relativeHumidity");
                            forecast.setHumidity(humidity.getString("value"));
                            Log.d("Forecast", "onResponse: "+forecast);

                            forecasts.add(forecast);
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
                }
            }
        });
    }

    class WeatherDetailsAdapter extends RecyclerView.Adapter<WeatherDetailsAdapter.ForecastViewHolder>{

        @NonNull
        @Override
        public WeatherDetailsAdapter.ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ForecastListItemBinding mbinding = ForecastListItemBinding.inflate(getLayoutInflater(), parent, false);
            ForecastViewHolder holder = new ForecastViewHolder(mbinding);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherDetailsAdapter.ForecastViewHolder holder, int position) {
            Forecast forecast = forecasts.get(position);
            holder.setupUI(forecast);
        }

        @Override
        public int getItemCount() {
            return forecasts.size();
        }

        class ForecastViewHolder extends RecyclerView.ViewHolder{

            ForecastListItemBinding mBinding;

            public ForecastViewHolder(ForecastListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Forecast forecast){
                mBinding.textViewTemperature.setText(forecast.getTemperature() + " F");
                mBinding.textViewDateTime.setText(forecast.getStartTime());
                mBinding.textViewForecast.setText(forecast.getShortforcast());
                mBinding.textViewHumidity.setText("Humidity: " + forecast.getHumidity() + "%");
                mBinding.textViewWindSpeed.setText("Wind speed: " + forecast.getWindspeed());
                Picasso.get().load(forecast.getIcon()).into(mBinding.imageView);
            }
        }
    }
}