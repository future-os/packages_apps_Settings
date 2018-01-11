/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.development;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.view.IWindowManager;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class TransitionAnimationScalePreferenceController extends
        DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener,
        PreferenceControllerMixin {

    private static final String TRANSITION_ANIMATION_SCALE_KEY = "transition_animation_scale";

    @VisibleForTesting
    static final int TRANSITION_ANIMATION_SCALE_SELECTOR = 1;
    @VisibleForTesting
    static final float DEFAULT_VALUE = 1;

    private final IWindowManager mWindowManager;
    private final String[] mListValues;
    private final String[] mListSummaries;
    private ListPreference mPreference;

    public TransitionAnimationScalePreferenceController(Context context) {
        super(context);

        mWindowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        mListValues = context.getResources().getStringArray(
                R.array.transition_animation_scale_values);
        mListSummaries = context.getResources().getStringArray(
                R.array.transition_animation_scale_entries);
    }

    @Override
    public String getPreferenceKey() {
        return TRANSITION_ANIMATION_SCALE_KEY;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mPreference = (ListPreference) screen.findPreference(getPreferenceKey());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        writeAnimationScaleOption(newValue);
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        updateAnimationScaleValue();
    }

    @Override
    protected void onDeveloperOptionsSwitchEnabled() {
        mPreference.setEnabled(true);
    }

    @Override
    protected void onDeveloperOptionsSwitchDisabled() {
        writeAnimationScaleOption(null);
        mPreference.setEnabled(false);
    }

    private void writeAnimationScaleOption(Object newValue) {
        try {
            float scale = newValue != null ? Float.parseFloat(newValue.toString()) : DEFAULT_VALUE;
            mWindowManager.setAnimationScale(TRANSITION_ANIMATION_SCALE_SELECTOR, scale);
            updateAnimationScaleValue();
        } catch (RemoteException e) {
            // intentional no-op
        }
    }

    private void updateAnimationScaleValue() {
        try {
            final float scale = mWindowManager.getAnimationScale(
                    TRANSITION_ANIMATION_SCALE_SELECTOR);
            int index = 0; // default
            for (int i = 0; i < mListValues.length; i++) {
                float val = Float.parseFloat(mListValues[i]);
                if (scale <= val) {
                    index = i;
                    break;
                }
            }
            mPreference.setValue(mListValues[index]);
            mPreference.setSummary(mListSummaries[index]);
        } catch (RemoteException e) {
            // intentional no-op
        }
    }
}