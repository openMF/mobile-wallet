package org.mifos.mobilewallet.mifospay.common;

import org.mifos.mobilewallet.core.domain.model.SearchResult;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import java.util.List;

/**
 * This is a contract class working as an Interface for UI
 * and Presenter components of the Common package.
 * @author naman
 * @since 21/8/17
 */

public interface SearchContract {

    /**
     * Defines all the functions in UI Component.
     */
    interface SearchView extends BaseView<SearchPresenter> {

        void showSearchResult(List<SearchResult> searchResults);

        void showToast(String message);

        void showSnackbar(String message);
    }

    /**
     * Defines all the functions in Presenter Component.
     */
    interface SearchPresenter extends BasePresenter {

        void performSearch(String query);
    }
}
