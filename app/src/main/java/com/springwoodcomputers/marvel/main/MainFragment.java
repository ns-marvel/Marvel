package com.springwoodcomputers.marvel.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;
import com.springwoodcomputers.marvel.database.entity.CharacterSearch;
import com.springwoodcomputers.marvel.utility.KeyboardHelper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

public class MainFragment extends DaggerFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.search_bar)
    AutoCompleteTextView searchBar;

    private MainViewModel viewModel;
    private Unbinder unbinder;
    private SavedSearchesAdapter savedSearchesAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
            viewModel.getMainViewState().observe(this, this::processViewModelState);
            viewModel.getSavedSearches().observe(this, this::updateSavedSearches);
        }
    }

    private void updateSavedSearches(List<CharacterSearch> savedSearches) {
        savedSearchesAdapter.clear();
        savedSearchesAdapter.addAll(savedSearches);
        savedSearchesAdapter.notifyDataSetChanged();
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
}
