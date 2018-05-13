package com.springwoodcomputers.marvel.child;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.springwoodcomputers.marvel.R;
import com.springwoodcomputers.marvel.dagger.ViewModelFactory;
import com.springwoodcomputers.marvel.pojo.Character;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ChildFragment extends DaggerFragment {

    public static final String MARVEL_CHARACTER = "marvel-character";

    public static ChildFragment newInstance() {
        return new ChildFragment();
    }

    public static ChildFragment newInstance(Character selectedCharacter) {
        Bundle bundle = new Bundle();
        bundle.putString(MARVEL_CHARACTER, new Gson().toJson(selectedCharacter));
        ChildFragment childFragment = new ChildFragment();
        childFragment.setArguments(bundle);
        return childFragment;
    }

    @Inject
    ViewModelFactory viewModelFactory;

    private Character character;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle argumentsBundle = getArguments();
        if (argumentsBundle != null) {
            String characterJson = argumentsBundle.getString(MARVEL_CHARACTER);
            if (characterJson != null) {
                setCharacter(new Gson().fromJson(characterJson, Character.class));
            }
        }
        return inflater.inflate(R.layout.fragment_child, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setCharacter(Character character) {
        Toast.makeText(getContext(), "Hello World!", Toast.LENGTH_SHORT).show();
    }
}

