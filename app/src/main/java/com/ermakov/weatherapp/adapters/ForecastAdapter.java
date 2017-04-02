package com.ermakov.weatherapp.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ermakov.weatherapp.R;
import com.ermakov.weatherapp.activities.SettingsActivity;
import com.ermakov.weatherapp.models.weather.Weather;
import com.ermakov.weatherapp.net.WeatherApiFactory;
import com.ermakov.weatherapp.utils.WeatherUtils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Адаптер для работы с прогнозом погоды.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    public static final String TAG = ForecastAdapter.class.getSimpleName();

    private List<Weather> mForecastList;
    private WeakReference<Context> mContext;

    public ForecastAdapter(Context context, List<Weather> forecastList) {
        this.mContext = new WeakReference<>(context);
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

        if (mContext.get() == null) {
            Log.d(TAG, "mContext.get() == null");
            return;
        }

        Weather weather = mForecastList.get(position);

        Date cityDate = WeatherUtils.convertToDate(weather.getDataCalculation());

        String temperatureUnit = PreferenceManager.getDefaultSharedPreferences(mContext.get())
                .getString(SettingsActivity.PREF_TEMPERATURE_UNITS, "");
        String temperatureMinStr = WeatherUtils.getTemperatureStr(mContext.get(),
                weather.getCharacteristics().getTemperatureMin(), temperatureUnit);
        String temperatureMaxStr = WeatherUtils.getTemperatureStr(mContext.get(),
                weather.getCharacteristics().getTemperatureMax(), temperatureUnit);

        holder.mDateTimeTextView.setText(getDateTimeStr(cityDate));
        holder.mTempMinTextView.setText(temperatureMinStr);
        holder.mTempMaxTextView.setText(temperatureMaxStr);

        // Скачиваем иконку отображающую текущую погоду.
        String iconId = weather.getDescriptions().get(0).getIconId();
        Picasso.with(holder.itemView.getContext())
                .load(WeatherApiFactory.createUrlToIcon(iconId))
                .fit()
                .centerInside()
                .into(holder.mIconImageView);
    }

    @Override
    public int getItemCount() {
        return (mForecastList != null) ? mForecastList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date_time) TextView mDateTimeTextView;
        @BindView(R.id.tv_temp_min) TextView mTempMinTextView;
        @BindView(R.id.tv_temp_max) TextView mTempMaxTextView;
        @BindView(R.id.iv_icon) ImageView mIconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getDateTimeStr(Date date) {

        int day = WeatherUtils.getDayOfMoth(Calendar.getInstance(), date);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat;
        if (day == today) {
            simpleDateFormat = new SimpleDateFormat(
                    String.format("'%s,' HH:mm", mContext.get().getString(R.string.today)));
        }
        else {
            simpleDateFormat = new SimpleDateFormat("dd MMMM");
        }

        simpleDateFormat.setTimeZone(WeatherUtils.getLocalTimeZone());
        String formattedDate = simpleDateFormat.format(date);

        return formattedDate;
    }
}
