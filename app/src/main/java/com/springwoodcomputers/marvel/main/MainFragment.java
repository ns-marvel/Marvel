package com.springwoodcomputers.marvel.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;
import com.springwoodcomputers.marvel.pojo.Character;
import com.springwoodcomputers.marvel.utility.KeyboardHelper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

public class MainFragment extends DaggerFragment implements SearchResultsAdapter.OnCharacterClickedListener {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.search_bar)
    AutoCompleteTextView searchBar;

    @BindView(R.id.search_results)
    RecyclerView searchResults;

    private MainViewModel viewModel;
    private Unbinder unbinder;
    private SavedSearchesAdapter savedSearchesAdapter;
    private SearchResultsAdapter searchResultsAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResultsAdapter = new SearchResultsAdapter(this);
        if (getActivity() != null) {
            viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
            viewModel.getMainViewState().observe(this, this::processViewModelState);
            viewModel.getSavedSearches().observe(this, this::updateSavedSearches);
            viewModel.getSearchResults().observe(this, this::updateSearchResults);
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
        searchBar.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.searchForCharacter(savedSearchesAdapter.getItem(position));
        });
    }

    private void setUpRecyclerView() {
        searchResults.post(() -> {
            int numberOfColumns = calculateNumberOfColumns();
            searchResults.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
            searchResults.setAdapter(searchResultsAdapter);
        });
    }

    private int calculateNumberOfColumns() {
        if (getView() != null) {
            int widthInPixels = getView().getWidth();
            int pixelDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
            int widthInDpi = (widthInPixels / pixelDpi) * 160;
            return widthInDpi / 112;
        }
        return 3;
    }

    private void updateSavedSearches(List<CharacterSearch> savedSearches) {
        savedSearchesAdapter.clear();
        savedSearchesAdapter.addAll(savedSearches);
        savedSearchesAdapter.notifyDataSetChanged();
    }

    private void updateSearchResults(List<Character> characters) {
        searchResultsAdapter.setCharacterList(characters);
        searchResultsAdapter.notifyDataSetChanged();
    }

    private void processViewModelState(MainViewState mainViewState) {
        Toast.makeText(getContext(), "viewmodelstate", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.search_button)
    void onSearchButtonClicked() {
        viewModel.searchForCharacter(new CharacterSearch(searchBar.getText().toString()));
        KeyboardHelper.hideSoftKeyboard(getView());
        searchBar.dismissDropDown();
        searchBar.setText("");
        searchBar.clearFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCharacterClicked(Character character) {
        // TODO
        Toast.makeText(getContext(), "character clicked " + character.getName(), Toast.LENGTH_LONG).show();
    }
}
