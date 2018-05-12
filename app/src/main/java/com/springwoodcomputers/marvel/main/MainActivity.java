package com.springwoodcomputers.marvel.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.api.MarvelService;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    MarvelService marvelService;

    @BindView(R.id.main_container)
    FrameLayout mainContainer;

    @Nullable
    @BindView(R.id.child_container)
    FrameLayout childContainer;

    @BindView(R.id.attribution_text)
    TextView attributionTextView;

    private MainViewModel viewModel;
    private boolean isSinglePane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpView(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        viewModel.getAttributionText().observe(this, this::setAttributionText);

    }

    private void setAttributionText(String newAttributionText) {
        attributionTextView.setText(newAttributionText);
    }


    private void setUpView(Bundle savedInstanceState) {
        isSinglePane = childContainer == null;

        if (savedInstanceState == null) {
            addMainFragment();
            addChildFragmentIfRequired();
        }

    }

    private void addMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, MainFragment.newInstance())
                .commitNow();
    }

    private void addChildFragmentIfRequired() {
        if (!isSinglePane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.child_container, ChildFragment.newInstance())
                    .commitNow();
        }

    }
}
