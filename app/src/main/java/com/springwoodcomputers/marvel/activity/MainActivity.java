package com.springwoodcomputers.marvel.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

    @BindView(R.id.placeholder_image)
    ImageView placeholderImage;

    private MainViewModel viewModel;
    private boolean isSinglePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpView();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        viewModel.getAttributionText().observe(this, this::setAttributionText);
        viewModel.getSelectedCharacter().observe(this, this::launchChildFragment);
    }

    private void setUpView() {
        isSinglePane = childContainer == null;

        addMainFragment();
        addEmptyChildFragmentIfRequired();
    }

    private void addMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, MainFragment.newInstance())
                .commitNow();
    }

    private void addEmptyChildFragmentIfRequired() {
        placeholderImage.setVisibility(VISIBLE);
        if (!isSinglePane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.child_container, ChildFragment.newInstance(), CHILD_FRAGMENT)
                    .commitNow();
        }
    }

    private void setAttributionText(String newAttributionText) {
        attributionTextView.setText(newAttributionText);
        if (isSinglePane) {
            placeholderImage.setVisibility(GONE);
        }
    }

    private void launchChildFragment(Character character) {
        if (character != null) {
            if (isSinglePane) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, ChildFragment.newInstance(), CHILD_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            } else {
                placeholderImage.setVisibility(GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isSinglePane) {
            viewModel.onCharacterClicked(null);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}