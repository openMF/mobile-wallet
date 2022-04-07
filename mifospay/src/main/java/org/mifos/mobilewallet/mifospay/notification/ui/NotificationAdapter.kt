package org.mifos.mobilewallet.mifospay.notification.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.core.domain.model.NotificationPayload
import org.mifos.mobilewallet.mifospay.R
import javax.inject.Inject

/**
 * Created by ankur on 23/July/2018
 */
class NotificationAdapter @Inject constructor() :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private var mNotificationPayloadList: List<NotificationPayload>?
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_notification, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTvBody?.text = mNotificationPayloadList?.get(position)?.body
        holder.mTvTitle?.text = mNotificationPayloadList?.get(position)?.title
        holder.mTvTimestamp?.text = mNotificationPayloadList?.get(position)?.timestamp
    }

    override fun getItemCount(): Int {
        return mNotificationPayloadList?.size ?: 0
    }

    fun setNotificationPayloadList(notificationPayloadList: List<NotificationPayload>?) {
        mNotificationPayloadList = notificationPayloadList
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.tv_body)
        var mTvBody: TextView? = null

        @JvmField
        @BindView(R.id.tv_title)
        var mTvTitle: TextView? = null

        @JvmField
        @BindView(R.id.tv_timestamp)
        var mTvTimestamp: TextView? = null

        init {
            v?.let { ButterKnife.bind(this, it) }
        }
    }

    init {
        mNotificationPayloadList = ArrayList()
    }
}