package org.mifos.mobilewallet.mifospay.notification.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.NotificationPayload;
import org.mifos.mobilewallet.mifospay.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ankur on 23/July/2018
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationPayload> mNotificationPayloadList;

    @Inject
    public NotificationAdapter() {
        mNotificationPayloadList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTvBody.setText(mNotificationPayloadList.get(position).getBody());
        holder.mTvTitle.setText(mNotificationPayloadList.get(position).getTitle());
        holder.mTvTimestamp.setText(mNotificationPayloadList.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        if (mNotificationPayloadList != null) {
            return mNotificationPayloadList.size();
        } else {
            return 0;
        }
    }

    public void setNotificationPayloadList(List<NotificationPayload> notificationPayloadList) {
        mNotificationPayloadList = notificationPayloadList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_body)
        TextView mTvBody;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_timestamp)
        TextView mTvTimestamp;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}