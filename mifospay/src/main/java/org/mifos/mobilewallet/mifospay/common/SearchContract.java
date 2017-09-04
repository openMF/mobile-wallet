package org.mifos.mobilewallet.mifospay.common;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

import org.mifos.mobilewallet.core.domain.model.SearchResult;

/**
 * Created by naman on 21/8/17.
 */

public interface SearchContract {

    interface SearchView extends BaseView<SearchPresenter> {

        void showSearchResult(List<SearchResult> searchResults);
    }

    interface SearchPresenter extends BasePresenter {

        void performSearch(String query);
    }
}
