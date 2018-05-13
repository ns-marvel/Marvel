package com.springwoodcomputers.marvel.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.activity.MainViewModel;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;
import com.springwoodcomputers.marvel.pojo.Character;
import com.springwoodcomputers.marvel.utility.InfiniteScrollListener;
import com.springwoodcomputers.marvel.utility.KeyboardHelper;
import com.springwoodcomputers.marvel.utility.SnackbarUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.springwoodcomputers.marvel.main.SearchResultsAdapter.OnCharacterClickedListener;
import static com.springwoodcomputers.marvel.utility.InfiniteScrollListener.OnInfiniteScrollThresholdReachedListener;

public class MainFragment extends DaggerFragment implements OnCharacterClickedListener, OnInfiniteScrollThresholdReachedListener {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.search_bar)
    AutoCompleteTextView searchBar;

    @BindView(R.id.search_results)
    RecyclerView searchResults;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.search_button)
    Button searchButton;

    private MainViewModel viewModel;
    private Unbinder unbinder;
    private SavedSearchesAdapter savedSearchesAdapter;
    private SearchResultsAdapter searchResultsAdapter;
    private int limit;
    private InfiniteScrollListener infiniteScrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResultsAdapter = new SearchResultsAdapter(this);
        if (getActivity() != null) {
            viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
            viewModel.getMainViewState().observe(this, this::processViewModelState);
            viewModel.getSavedSearches().observe(this, this::updateSavedSearches);
            viewModel.getIsSearchButtonEnabled().observe(this, this::enableSearchButton);
            viewModel.getSearchResults().observe(this, this::updateSearchResults);
            viewModel.getLoadingInProgress().observe(this, this::updateLoadingInProgress);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpSearchBar();
        setUpRecyclerView();
    }

    @SuppressWarnings("ConstantConditions")
    private void setUpSearchBar() {
        savedSearchesAdapter = new SavedSearchesAdapter(getContext(), viewModel.getCharacterSearchFilter());
        searchBar.setThreshold(1);
        searchBar.setAdapter(savedSearchesAdapter);
        searchBar.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSearchButtonClicked();
                return true;
            }
            return false;
        });
    }

    private void setUpRecyclerView() {
        searchResults.post(() -> {
            int numberOfColumns = calculateNumberOfColumns();
            int numberOfRows = calculateNumberOfRows();
            int threshold = numberOfColumns * numberOfRows + numberOfColumns + 1;
            limit = numberOfColumns * numberOfRows * 2;
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
            searchResults.setLayoutManager(layoutManager);
            infiniteScrollListener = new InfiniteScrollListener(layoutManager, threshold, this);
            searchResults.setAdapter(searchResultsAdapter);
            searchResults.addOnScrollListener(infiniteScrollListener);

            if (viewModel.getSearchResults().getValue() != null) {
                // must have rotated
                viewModel.setNewLimit(limit);
            }
        });
    }

    private int calculateNumberOfColumns() {
        if (getView() != null) {
            int widthInPixels = getView().getWidth();
            int pixelDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
            int widthInDpi = (widthInPixels / pixelDpi) * 160;
            return Math.min(6, widthInDpi / 112);
        }
        return 3;
    }

    private int calculateNumberOfRows() {
        if (getView() != null) {
            int heightInPixels = getView().getHeight();
            int pixelDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
            int heightInDpi = (heightInPixels / pixelDpi) * 160;
            return heightInDpi / 130;
        }
        return 8;
    }

    private void updateSavedSearches(List<CharacterSearch> savedSearches) {
        savedSearchesAdapter.clear();
        savedSearchesAdapter.addAll(savedSearches);
        savedSearchesAdapter.notifyDataSetChanged();
    }

    private void enableSearchButton(Boolean enabled) {
        searchButton.setEnabled(enabled != null && enabled);
    }

    private void updateSearchResults(List<Character> characters) {
        int previousCharacterCount = searchResultsAdapter.getItemCount();

        searchResultsAdapter.setCharacterList(characters);
        searchResultsAdapter.notifyDataSetChanged();

        if (characters.size() > previousCharacterCount && infiniteScrollListener != null) {
            infiniteScrollListener.setThresholdReachHandled(characters.size() - previousCharacterCount);
        }
    }

    private void processViewModelState(MainViewState mainViewState) {
        if (mainViewState.isShowError()) {
            showErrorSnackBar(mainViewState);
        }
        if (mainViewState.isNoResultsFound()) {
            Toast.makeText(getContext(), "Not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorSnackBar(MainViewState errorState) {
        Snackbar snackbar = SnackbarUtils.makeErrorSnackbar(getView(), errorState.getErrorMessageResource(), errorState.getDuration());
        snackbar.setAction(errorState.getActionMessageResource(), view -> viewModel.retryFailedCharacterSearch());
        snackbar.show();
    }

    private void updateLoadingInProgress(Boolean isLoadingInProgress) {
        progressBar.setVisibility(isLoadingInProgress == null || !isLoadingInProgress ? GONE : VISIBLE);
    }

    @OnClick(R.id.search_button)
    void onSearchButtonClicked() {
        viewModel.searchForCharacter(new CharacterSearch(searchBar.getText().toString()), limit);
        clearSearchBar();
    }

    private void clearSearchBar() {
        KeyboardHelper.hideSoftKeyboard(getView());
        searchBar.dismissDropDown();
        searchBar.clearFocus();
    }

    @Override
    public void onCharacterClicked(Character character) {
        viewModel.onCharacterClicked(character);
    }

    @Override
    public void onInfiniteScrollThresholdReached() {
        viewModel.getMoreSearchResults();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
