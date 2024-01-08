package org.mifos.mobilewallet.mifospay.notification.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    init {
        mNotificationPayloadList = ArrayList()
    }

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
        holder.mTvBody!!.text = mNotificationPayloadList!![position].body
        holder.mTvTitle!!.text = mNotificationPayloadList!![position].title
        holder.mTvTimestamp!!.text = mNotificationPayloadList!![position].timestamp
    }

    override fun getItemCount(): Int {
        return if (mNotificationPayloadList != null) {
            mNotificationPayloadList!!.size
        } else {
            0
        }
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
            ButterKnife.bind(this, v!!)
        }
    }
}