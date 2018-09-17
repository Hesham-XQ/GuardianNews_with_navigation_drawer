package com.example.android.guardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by SAMO on 3/21/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    private static final String LOG_TAG = "";
    private List<News> mNews;

    private static class ViewHolder {
        private TextView title, date, authers, section;
        private ImageView thumbnail;
    }

    public NewsAdapter(@NonNull Context context, @NonNull List<News> news) {
        super(context, 0, news);
        mNews = news;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.title = ButterKnife.findById(rowView, R.id.title);
            viewHolder.section = ButterKnife.findById(rowView, R.id.section);
            viewHolder.date = ButterKnife.findById(rowView, R.id.date);
            viewHolder.authers = ButterKnife.findById(rowView, R.id.authers);
            viewHolder.thumbnail = ButterKnife.findById(rowView, R.id.thumbnail);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        final News my_custom = (News) getItem(position);

        TextView articleTitle = holder.title;
        if (my_custom != null) {
            articleTitle.setText(my_custom.getTitle());
        }

        TextView articleDate = holder.date;
        articleDate.setText(formatPublishTime(my_custom.getDate()));

        TextView articleSection = holder.section;
        articleSection.setText(my_custom.getSection());

        TextView articleContributer = holder.authers;
        articleContributer.setText(my_custom.getContributer());

        ImageView img = holder.thumbnail;

        if (!my_custom.getImageSmallThumbLink().matches("")) {
            Picasso.with(getContext())
                    .load(my_custom.getImageSmallThumbLink())
                    .resize((int) getContext().getResources().getDimension(R.dimen.thumb_width), (int) getContext().getResources().getDimension(R.dimen.thumb_height))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerInside()
                    .into(img);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.no_image)
                    .resize((int) getContext().getResources().getDimension(R.dimen.thumb_width), (int) getContext().getResources().getDimension(R.dimen.thumb_height))
                    .centerInside()
                    .into(img);
        }

        return rowView;

    }


    private String formatPublishTime(final String time) {
        String rTime = "";
        if ((time != null) && (!time.isEmpty())) {
            try {
                SimpleDateFormat currentSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat newSDF = new SimpleDateFormat("yyyy.MM.dd / HH:mm");
                rTime = newSDF.format(currentSDF.parse(time));
            } catch (ParseException e) {
                rTime = "";
                Log.e(LOG_TAG, "Error while parsing the published date", e);
            }
        }
        return rTime;
    }

    public void clearAll() {
        mNews.clear();
        notifyDataSetChanged();
    }

}

