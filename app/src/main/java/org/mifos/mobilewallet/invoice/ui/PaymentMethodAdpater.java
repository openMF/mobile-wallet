package org.mifos.mobilewallet.invoice.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.invoice.domain.model.PaymentMethod;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 20/6/17.
 */

public class PaymentMethodAdpater extends RecyclerView.Adapter<PaymentMethodAdpater.ViewHolder> {

    private Context context;
    private List<PaymentMethod> paymentMethods;
    private int currentPosition = 1;

    @Inject
    public PaymentMethodAdpater() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_payment_method, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.ivMethodImage.setImageDrawable(
                ContextCompat.getDrawable(context, paymentMethods.get(position).getImage()));
        holder.tvMethodName.setText(paymentMethods.get(position).getTitle());

        if (position == currentPosition) {
            holder.parentView.setBackgroundResource(R.drawable.bg_layout_stroke);
        } else {
            holder.parentView.setBackgroundResource(R.drawable.bg_layout_stroke_grey);
        }
    }

    @Override
    public int getItemCount() {
        if (paymentMethods != null) {
            return paymentMethods.size();
        } else {
            return 0;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.iv_payment_method)
        ImageView ivMethodImage;

        @BindView(R.id.tv_payment_method)
        TextView tvMethodName;

        @BindView(R.id.ll_parent)
        View parentView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setData(List<PaymentMethod> methods) {
        this.paymentMethods = methods;
        notifyDataSetChanged();
    }

    public PaymentMethod getPaymentMethod(int position) {
        return paymentMethods.get(position);
    }

    public void setFocused(int position) {
        this.currentPosition = position;
        notifyDataSetChanged();
    }
}
