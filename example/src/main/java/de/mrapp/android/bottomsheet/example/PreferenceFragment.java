/*
 * AndroidBottomSheet Copyright 2016 Michael Rapp
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package de.mrapp.android.bottomsheet.example;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import de.mrapp.android.bottomsheet.BottomSheet;
import de.mrapp.android.bottomsheet.BottomSheet.Style;

/**
 * A preference fragment, which contains the example app's settings.
 *
 * @author Michael Rapp
 */
public class PreferenceFragment extends android.preference.PreferenceFragment {

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
    private void initializeShowCustomBottmSheetPreference() {
        Preference showCustomBottomSheetPreference =
                findPreference(getString(R.string.show_custom_bottom_sheet_preference_key));
        showCustomBottomSheetPreference
                .setOnPreferenceClickListener(createShowCustomBottomSheetPreferenceListener());
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
        BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
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
     *         {@link BottomSheet.Builder}
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
                Drawable icon =
                        showIcon ? ContextCompat.getDrawable(getActivity(), R.drawable.list_item) :
                                null;
                builder.addItem(title, icon);

                if (disableItems) {
                    builder.setItemEnabled(index, false);
                }

                index++;
            }
        }
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

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initializeShowBottomSheetPreference();
        initializeShowCustomBottmSheetPreference();
    }

}