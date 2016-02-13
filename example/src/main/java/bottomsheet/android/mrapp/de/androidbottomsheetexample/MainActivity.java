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
package bottomsheet.android.mrapp.de.androidbottomsheetexample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import de.mrapp.android.bottomsheet.BottomSheet;

/**
 * The main activity of the example app.
 *
 * @author Michael Rapp
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                BottomSheet.Builder builder = new BottomSheet.Builder(MainActivity.this);
                builder.setTitle("Title");

                Drawable listItemIcon =
                        ContextCompat.getDrawable(MainActivity.this, R.drawable.list_item);

                builder.addItem("Item 1", listItemIcon);
                builder.addItem("Item 2", listItemIcon);
                builder.addItem("Item 3", listItemIcon);
                builder.setItemEnabled(2, false);
                builder.addDivider();
                builder.addItem("Item 4", listItemIcon);
                builder.addItem("Item 5", listItemIcon);
                builder.addItem("Item 6", listItemIcon);
                builder.addItem("Item 7", listItemIcon);
                builder.addItem("Item 8", listItemIcon);
                builder.addItem("Item 9", listItemIcon);
                builder.addItem("Item 10", listItemIcon);
                builder.addDivider("Divider");
                builder.addItem("Item 11", listItemIcon);
                builder.addItem("Item 12", listItemIcon);
                builder.addItem("Item 13", listItemIcon);
                builder.addItem("Item 14", listItemIcon);
                builder.addItem("Item 15", listItemIcon);
                builder.addItem("Item 16", listItemIcon);
                builder.addItem("Item 17", listItemIcon);
                builder.addItem("Item 18", listItemIcon);
                builder.addItem("Item 19", listItemIcon);
                builder.addItem("Item 20", listItemIcon);
                builder.addItem("Item 21", listItemIcon);
                builder.addItem("Item 22", listItemIcon);
                builder.addItem("Item 23", listItemIcon);
                builder.addItem("Item 24", listItemIcon);
                builder.addItem("Item 25", listItemIcon);
                builder.addItem("Item 26", listItemIcon);
                builder.addItem("Item 27", listItemIcon);
                builder.addItem("Item 28", listItemIcon);
                builder.addItem("Item 29", listItemIcon);
                builder.addItem("Item 30", listItemIcon);

                builder.show();
            }

        });
    }

}