package com.springwoodcomputers.marvel.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.api.MarvelService;
import com.springwoodcomputers.marvel.child.ChildFragment;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;
import com.springwoodcomputers.marvel.main.MainFragment;
import com.springwoodcomputers.marvel.pojo.Character;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    public static final String CHILD_FRAGMENT = "child_fragment";
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
        viewModel.getSelectedCharacter().observe(this, this::launchChildFragment);

    }

    private void launchChildFragment(Character character) {
        if (isSinglePane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, ChildFragment.newInstance(character), CHILD_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment childFragment = fragmentManager.findFragmentByTag(CHILD_FRAGMENT);
            if (childFragment == null) {
                fragmentManager
                        .beginTransaction()
                        .add(R.id.child_fragment, ChildFragment.newInstance(character), CHILD_FRAGMENT)
                        .commit();
            } else {
                ((ChildFragment) childFragment).setCharacter(character);
            }
        }
    }

    private void setAttributionText(String newAttributionText) {
        attributionTextView.setText(newAttributionText);
    }


    private void setUpView(Bundle savedInstanceState) {
        isSinglePane = childContainer == null;

//        if (savedInstanceState == null) {
            addMainFragment();
        addEmptyChildFragmentIfRequired();
//        }

    }

    private void addMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, MainFragment.newInstance())
                .commitNow();
    }

    private void addEmptyChildFragmentIfRequired() {
        if (!isSinglePane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.child_container, ChildFragment.newInstance(), CHILD_FRAGMENT)
                    .commitNow();
        }

    }
}
