/*
 * Copyright 2014 - 2016 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.android.bottomsheet.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.BottomSheet.Style;

/**
 * A preference fragment, which contains the example app's settings.
 *
 * @author Michael Rapp
 */
public class PreferenceFragment extends android.preference.PreferenceFragment {

    /**
     * The toast, which is used to indicate, when a bottom sheet's item has been clicked.
     */
    private Toast toast;

    /**
     * Initializes the preference, which allows to change the app's theme.
     */
    private void initializeThemePreference() {
        Preference themePreference = findPreference(getString(R.string.theme_preference_key));
        themePreference.setOnPreferenceChangeListener(createThemeChangeListener());
    }

    /**
     * Creates and returns a listener, which allows to adapt the app's theme, when the value of the
     * corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * Preference.OnPreferenceChangeListener}
     */
    private Preference.OnPreferenceChangeListener createThemeChangeListener() {
        return new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                getActivity().recreate();
                return true;
            }

        };
    }

    /**
     * Initializes the preference, which allows to show a bottom sheet.
     */
    private void initializeShowBottomSheetPreference() {
        Preference showBottomSheetPreference =
                findPreference(getString(R.string.show_bottom_sheet_preference_key));
        showBottomSheetPreference
                .setOnPreferenceClickListener(createShowBottomSheetPreferenceListener());
    }

    /**
     * Initializes the preference, which allows to show a bottom sheet with custom content.
     */
    private void initializeShowCustomBottomSheetPreference() {
        Preference showCustomBottomSheetPreference =
                findPreference(getString(R.string.show_custom_bottom_sheet_preference_key));
        showCustomBottomSheetPreference
                .setOnPreferenceClickListener(createShowCustomBottomSheetPreferenceListener());
    }

    /**
     * Initializes the preference, which allows to display the applications, which are suited for
     * handling an intent.
     */
    private void initializeShowIntentBottmSheetPreference() {
        Preference showIntentBottomSheetPreference =
                findPreference(getString(R.string.show_intent_bottom_sheet_preference_key));
        showIntentBottomSheetPreference
                .setOnPreferenceClickListener(createShowIntentBottomSheetPreferenceListener());
    }

    /**
     * Creates and returns a listener, which allows to show a bottom sheet.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceClickListener}
     */
    private OnPreferenceClickListener createShowBottomSheetPreferenceListener() {
        return new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(final Preference preference) {
                BottomSheet.Builder builder = createBottomSheetBuilder();
                addItems(builder);
                builder.show();
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to show a bottom sheet, which displays the
     * application, which are suited for handling an intent.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceClickListener}
     */
    private OnPreferenceClickListener createShowIntentBottomSheetPreferenceListener() {
        return new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                BottomSheet.Builder builder = createBottomSheetBuilder();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                intent.setType("text/plain");
                builder.setIntent(getActivity(), intent);
                builder.show();
                return true;
            }

        };
    }

    /**
     * Creates and returns a listener, which allows to show a bottom sheet with custom content.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceClickListener}
     */
    private OnPreferenceClickListener createShowCustomBottomSheetPreferenceListener() {
        return new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(final Preference preference) {
                BottomSheet.Builder builder = createBottomSheetBuilder();
                builder.setView(R.layout.custom_view);
                builder.show();
                return true;
            }

        };
    }

    /**
     * Creates and returns a builder, which allows to create bottom sheets, depending on the app's
     * settings.
     *
     * @return The builder, which has been created, as an instance of the class {@link
     * BottomSheet.Builder}
     */
    private BottomSheet.Builder createBottomSheetBuilder() {
        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity(),
                isDarkThemeSet() ? R.style.BottomSheet : R.style.BottomSheet_Light);
        builder.setStyle(getStyle());

        if (shouldTitleBeShown()) {
            builder.setTitle(getBottomSheetTitle());
        }

        if (shouldIconBeShown()) {
            builder.setIcon(
                    ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
        }

        return builder;
    }

    /**
     * Adds items, depending on the app's settings, to a builder, which allows to create a bottom
     * sheet.
     *
     * @param builder
     *         The builder, which allows to create the bottom sheet, as an instance of the class
     *         {@link BottomSheet.Builder}. The builder may not be null
     */
    private void addItems(@NonNull final BottomSheet.Builder builder) {
        int dividerCount = getDividerCount();
        boolean showDividerTitle = shouldDividerTitleBeShown();
        int itemCount = getItemCount();
        boolean showIcon = shouldItemIconsBeShown();
        boolean disableItems = shouldItemsBeDisabled();
        int index = 0;

        for (int i = 0; i < dividerCount + 1; i++) {
            if (i > 0) {
                builder.addDivider(showDividerTitle ? getString(R.string.divider_title, i) : null);
                index++;
            }

            for (int j = 0; j < itemCount; j++) {
                String title = getString(R.string.item_title, i * itemCount + j + 1);
                Drawable icon;

                if (isDarkThemeSet()) {
                    icon = showIcon ? ContextCompat.getDrawable(getActivity(),
                            getStyle() == Style.GRID ? R.drawable.grid_item_dark :
                                    R.drawable.list_item_dark) : null;
                } else {
                    icon = showIcon ? ContextCompat.getDrawable(getActivity(),
                            getStyle() == Style.GRID ? R.drawable.grid_item :
                                    R.drawable.list_item) : null;
                }

                builder.addItem(i * dividerCount + j, title, icon);

                if (disableItems) {
                    builder.setItemEnabled(index, false);
                }

                index++;
            }
        }

        builder.setOnItemClickListener(createItemClickListener());
    }

    private AdapterView.OnItemClickListener createItemClickListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                if (toast != null) {
                    toast.cancel();
                }

                int itemCount = getItemCount();
                String text = getString(R.string.item_clicked_toast,
                        position - position / (itemCount + 1) + 1);
                toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                toast.show();
            }

        };
    }

    /**
     * Returns the style, which should be used to create bottom sheets, depending on the app's
     * settings.
     *
     * @return The style, which should be used to create bottom sheets, as a value of the enum
     * {@link Style}
     */
    private Style getStyle() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.bottom_sheet_style_preference_key);
        String defaultValue = getString(R.string.bottom_sheet_style_preference_default_value);
        String style = sharedPreferences.getString(key, defaultValue);

        switch (style) {
            case "list":
                return Style.LIST;
            case "list_columns":
                return Style.LIST_COLUMNS;
            default:
                return Style.GRID;
        }
    }

    /**
     * Returns, whether the title of bottom sheets should be shown, depending on the app's settings,
     * or not.
     *
     * @return True, if the title of bottom sheets should be shown, false otherwise
     */
    private boolean shouldTitleBeShown() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.show_bottom_sheet_title_preference_key);
        boolean defaultValue =
                getResources().getBoolean(R.bool.show_bottom_sheet_title_preference_default_value);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Returns the title of bottom sheets, depending on the app's settings.
     *
     * @return The title of the bottom sheets
     */
    private String getBottomSheetTitle() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.bottom_sheet_title_preference_key);
        String defaultValue = getString(R.string.bottom_sheet_title_preference_default_value);
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Returns, whether the icon of bottom sheets should be shown, depending on the app's settings,
     * or not.
     *
     * @return True, if the icon of bottom sheets should be shown, false otherwise
     */
    private boolean shouldIconBeShown() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.show_bottom_sheet_icon_preference_key);
        boolean defaultValue =
                getResources().getBoolean(R.bool.show_bottom_sheet_icon_preference_default_value);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Returns the number of dividers, which should be shown, depending on the app's settings.
     *
     * @return The number of dividers, which should be shown, as an {@link Integer} value
     */
    private int getDividerCount() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.divider_count_preference_key);
        String defaultValue = getString(R.string.divider_count_preference_default_value);
        return Integer.valueOf(sharedPreferences.getString(key, defaultValue));
    }

    /**
     * Returns, whether the title of dividers should be shown, depending on the app's settings, or
     * not.
     *
     * @return True, if the title of dividers should be shown, false otherwise
     */
    private boolean shouldDividerTitleBeShown() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.show_divider_title_preference_key);
        boolean defaultValue =
                getResources().getBoolean(R.bool.show_divider_title_preference_default_value);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Returns the number of items, which should be shown per divider, depending on the app's
     * settings.
     *
     * @return The number of items, which should be shown per divider, as an {@link Integer} value
     */
    private int getItemCount() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.item_count_preference_key);
        String defaultValue = getString(R.string.item_count_preference_default_value);
        return Integer.valueOf(sharedPreferences.getString(key, defaultValue));
    }

    /**
     * Returns, whether icons should be shown next to items, depending on the app's settings, or
     * not.
     *
     * @return True, if icons should be shown next to items, false otherwise
     */
    private boolean shouldItemIconsBeShown() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.show_item_icons_preference_key);
        boolean defaultValue =
                getResources().getBoolean(R.bool.show_item_icons_preference_default_value);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Returns, whether items should be disabled, depending on the app's settings, or not.
     *
     * @return True, if items should be disabled, false otherwise.
     */
    private boolean shouldItemsBeDisabled() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.disable_items_preference_key);
        boolean defaultValue =
                getResources().getBoolean(R.bool.disable_items_preference_default_value);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Returns, whether the app uses the dark theme, or not.
     *
     * @return True, if the app uses the dark theme, false otherwise
     */
    private boolean isDarkThemeSet() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getString(R.string.theme_preference_key);
        String defaultValue = getString(R.string.theme_preference_default_value);
        return Integer.valueOf(sharedPreferences.getString(key, defaultValue)) != 0;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initializeThemePreference();
        initializeShowBottomSheetPreference();
        initializeShowCustomBottomSheetPreference();
        initializeShowIntentBottmSheetPreference();
    }

}