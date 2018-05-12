package com.springwoodcomputers.marvel.main;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class MainViewState {

    private boolean showError;
    private boolean noResultsFound;
    private int duration;
    private int errorMessageResource;
    private int actionMessageResource;

    public MainViewState(int duration, int errorMessageResource, int actionMessageResource) {
        this.showError = true;
        this.duration = duration;
        this.errorMessageResource = errorMessageResource;
        this.actionMessageResource = actionMessageResource;
    }

    public MainViewState(boolean noResultsFound) {
        this.noResultsFound = noResultsFound;
    }
}