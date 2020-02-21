package org.mifos.mobilewallet.mifospay.history.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.CheckBoxStatus;
import org.mifos.mobilewallet.mifospay.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CheckBoxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CheckBoxStatus> statusList;

    @Inject
    public CheckBoxAdapter() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_checkbox, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CheckBoxStatus statusModel = statusList.get(position);

        ((ViewHolder) holder).cbStatusSelect.setChecked(statusModel.getChecked());

        ((ViewHolder) holder).tvStatus.setText(statusModel.getText());
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public void setStatusList(List<CheckBoxStatus> statusList) {
        this.statusList = statusList;
    }

    public List<CheckBoxStatus> getStatusList() {
        return statusList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cb_status_select)
        CheckBox cbStatusSelect;

        @BindView(R.id.tv_status)
        TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ll_row_checkbox)
        public void rowClicked() {
            cbStatusSelect.setChecked(!cbStatusSelect.isChecked());
        }

        @OnCheckedChanged(R.id.cb_status_select)
        public void checkChanges() {
            statusList.get(getAdapterPosition()).setChecked(cbStatusSelect.isChecked());
        }
    }
}
