package org.mifos.mobilewallet.mifospay.common.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mifos.mobilewallet.core.domain.model.SearchResult;
import org.mifos.mobilewallet.mifospay.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 21/8/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchResult> results;

    @Inject
    public SearchAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvSearchResult.setText(results.get(position).getResultName());

    }

    @Override
    public int getItemCount() {
        if (results != null) {
            return results.size();
        } else {
            return 0;
        }
    }

    public void setData(List<SearchResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.results.clear();
        notifyDataSetChanged();
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_search_result)
        TextView tvSearchResult;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}

