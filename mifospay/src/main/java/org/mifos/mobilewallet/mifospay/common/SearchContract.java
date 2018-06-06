package org.mifos.mobilewallet.mifospay.common;

import org.mifos.mobilewallet.core.domain.model.SearchResult;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * Created by naman on 21/8/17.
 */

public interface SearchContract {

    interface SearchView extends BaseView<SearchPresenter> {

        void showSearchResult(List<SearchResult> searchResults);

        void showToast(String message);

        void showSnackbar(String message);
    }

    interface SearchPresenter extends BasePresenter {

        void performSearch(String query);
    }
}
