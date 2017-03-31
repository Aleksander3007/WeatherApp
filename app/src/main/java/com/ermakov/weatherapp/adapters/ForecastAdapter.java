package com.ermakov.weatherapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ermakov.weatherapp.R;
import com.ermakov.weatherapp.models.weather.Weather;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Адаптер для работы с прогнозом погоды.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<Weather> mForecastList;

    public ForecastAdapter(List<Weather> forecastList) {
        this.mForecastList = forecastList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather weather = mForecastList.get(position);

        holder.mDateTimeTextView.setText(String.valueOf(weather.getDataCalculation()));
        holder.mTempMinTextView.setText(String.valueOf(weather.getCharacteristics().getTemperatureMin()));
        holder.mTempMaxTextView.setText(String.valueOf(weather.getCharacteristics().getTemperatureMax()));

        Log.d("ForecastAdapter", "pos = " + position);
    }

    @Override
    public int getItemCount() {
        return (mForecastList != null) ? mForecastList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date_time) TextView mDateTimeTextView;
        @BindView(R.id.tv_temp_min) TextView mTempMinTextView;
        @BindView(R.id.tv_temp_max) TextView mTempMaxTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
