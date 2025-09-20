package com.example.androidproject;

import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToastIdlingResource implements IdlingResource {
    private volatile ResourceCallback resourceCallback;
    private AtomicBoolean isIdle = new AtomicBoolean(true);

    @Override
    public String getName() {
        return ToastIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void setIdleState(boolean isIdle) {
        this.isIdle.set(isIdle);
        if (isIdle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
}