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

package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.v7.preference.Preference;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base model for a battery tip(e.g. suggest user to turn on battery saver)
 *
 * Each {@link BatteryTip} contains basic data(e.g. title, summary, icon) as well as the
 * pre-defined action(e.g. turn on battery saver)
 */
public abstract class BatteryTip implements Comparable<BatteryTip>, Parcelable {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({StateType.NEW,
            StateType.HANDLED,
            StateType.INVISIBLE})
    public @interface StateType {
        int NEW = 0;
        int HANDLED = 1;
        int INVISIBLE = 2;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TipType.SUMMARY,
            TipType.BATTERY_SAVER,
            TipType.HIGH_DEVICE_USAGE,
            TipType.SMART_BATTERY_MANAGER,
            TipType.APP_RESTRICTION,
            TipType.REDUCED_BATTERY,
            TipType.LOW_BATTERY,
            TipType.REMOVE_APP_RESTRICTION})
    public @interface TipType {
        int SMART_BATTERY_MANAGER = 0;
        int APP_RESTRICTION = 1;
        int HIGH_DEVICE_USAGE = 2;
        int BATTERY_SAVER = 3;
        int REDUCED_BATTERY = 4;
        int LOW_BATTERY = 5;
        int SUMMARY = 6;
        int REMOVE_APP_RESTRICTION = 7;
    }

    private static final String KEY_PREFIX = "key_battery_tip";

    protected int mType;
    protected int mState;
    protected boolean mShowDialog;

    BatteryTip(Parcel in) {
        mType = in.readInt();
        mState = in.readInt();
        mShowDialog = in.readBoolean();
    }

    BatteryTip(int type, int state, boolean showDialog) {
        mType = type;
        mState = state;
        mShowDialog = showDialog;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        dest.writeInt(mState);
        dest.writeBoolean(mShowDialog);
    }

    public abstract CharSequence getTitle(Context context);

    public abstract CharSequence getSummary(Context context);

    @IdRes
    public abstract int getIconId();

    /**
     * Update the current {@link #mState} using the new {@code tip}.
     *
     * @param tip used to update
     */
    public abstract void updateState(BatteryTip tip);

    public Preference buildPreference(Context context) {
        Preference preference = new Preference(context);

        preference.setKey(getKey());
        preference.setTitle(getTitle(context));
        preference.setSummary(getSummary(context));
        preference.setIcon(getIconId());
        return preference;
    }

    public boolean shouldShowDialog() {
        return mShowDialog;
    }

    public String getKey() {
        return KEY_PREFIX + mType;
    }

    public int getType() {
        return mType;
    }

    @StateType
    public int getState() {
        return mState;
    }

    public boolean isVisible() {
        return mState != StateType.INVISIBLE;
    }

    @Override
    public int compareTo(BatteryTip o) {
        return mType - o.mType;
    }
}