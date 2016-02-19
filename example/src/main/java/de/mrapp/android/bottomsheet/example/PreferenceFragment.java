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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

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
     * Creates and returns a listener, which allows to show a bottom sheet.
     *
     * @return The listener, which has been created, as an instance of the type {@link
     * OnPreferenceClickListener}
     */
    private OnPreferenceClickListener createShowBottomSheetPreferenceListener() {
        return new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(final Preference preference) {
                BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
                builder.setStyle(Style.LIST_COLUMNS);
                builder.addItem("Item 1");
                builder.addItem("Item 2");
                builder.addItem("Item 3");
                builder.addItem("Item 4");
                builder.addItem("Item 5");
                builder.addItem("Item 6");
                builder.addItem("Item 7");
                builder.addItem("Item 8");
                builder.addItem("Item 9");
                builder.show();
                return true;
            }

        };
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initializeShowBottomSheetPreference();
    }

}