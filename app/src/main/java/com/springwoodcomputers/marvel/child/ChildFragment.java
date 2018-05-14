package com.springwoodcomputers.marvel.child;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.activity.MainViewModel;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;
import com.springwoodcomputers.marvel.pojo.Character;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

import static android.view.View.GONE;

public class ChildFragment extends DaggerFragment {

    public static ChildFragment newInstance() {
        return new ChildFragment();
    }

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.big_image)
    ImageView bigImage;

    @BindView(R.id.character_name)
    TextView characterName;

    @BindView(R.id.character_description)
    TextView characterDescription;

    @BindView(R.id.character_comics)
    TextView characterComics;

    @BindView(R.id.character_stories)
    TextView characterStories;

    @BindView(R.id.character_events)
    TextView characterEvents;

    @BindView(R.id.character_series)
    TextView characterSeries;

    @Inject
    ViewModelFactory viewModelFactory;

    private MainViewModel viewModel;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
            viewModel.getSelectedCharacter().observe(this, this::processSelectedCharacter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void processSelectedCharacter(Character character) {
        if (character != null) {
            progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(character.getThumbnail().getBigImageUrl()).into(bigImage, new Callback() {
                @Override
                public void onSuccess() {
                    if (progressBar != null) progressBar.setVisibility(GONE);
                }

                @Override
                public void onError(Exception e) {
                    if (progressBar != null) progressBar.setVisibility(GONE);
                }
            });
            characterName.setText(character.getName());
            characterDescription.setText(character.getDescription());

            Resources resources = getResources();
            characterComics.setText(resources.getQuantityString(R.plurals.comicAchievements, character.getComicListCount(), character.getComicListCount()));
            characterStories.setText(resources.getQuantityString(R.plurals.storyAchievements, character.getStoryListCount(), character.getStoryListCount()));
            characterEvents.setText(resources.getQuantityString(R.plurals.eventAchievements, character.getEventListCount(), character.getEventListCount()));
            characterSeries.setText(resources.getString(R.string.series_achievements, character.getSeriesListCount()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}