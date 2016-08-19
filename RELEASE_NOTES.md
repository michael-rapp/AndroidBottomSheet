# AndroidBottomSheet - RELEASE NOTES

## Version 1.4.0 (Aug. 19th 2016)

A feature release, which introduces the following changes:

- The properties of a `BottomSheet` are now stored/restored in its `onSaveInstanceState`- and `onRestoreInstanceState`-method. This does not include properties, which are not serializable, e.g. listeners.
- Updated dependency "AndroidUtil" to version 1.11.0.

## Version 1.3.0 (May 26th 2016)

A feature release, which introduces the following changes:

- It is now possible to globally change the theme, which is used by a `BottomSheet` by the default, by using the theme attribute `bottomSheetTheme`.
- Updated AppCompat v7 support library to version 23.4.0.

## Version 1.2.0 (Apr. 23th 2016)

A feature release, which introduces the following changes:

- Each item of a `BottomSheet` is now associated with an id. This allows to reference individual items regardless of their current position. The id of an item is passed to the registered `OnItemClickListener` when it is clicked.

## Version 1.1.1 (Mar. 18th 2016)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/AndroidBottomSheet/issues/4

## Version 1.1.0 (Mar. 18th 2016)

A feature release, which introduces the following changes:

- A `BottomSheet`'s getter and setter methods can now also be used when the bottom sheet has not been shown yet.
- Updated dependency "AndroidUtil" to version 1.4.5.
- Updated AppCompat support library to version 23.2.1.

## Version 1.0.1 (Feb. 25th 2016)

A minor release, which introduces the following changes:

- The library is from now on distributed under the Apache License version 2.0. 
- Updated dependency "AndroidUtil" to version 1.4.3.
- Updated AppCompat support library to version 23.2.0.

## Version 1.0.0 (Feb. 22th 2016)

The first stable release, which provides a bottom sheet as a proposed by Android's Material design guidelines. The implementation initially provides the following features:

- The library provides a builder, which allows to create bottom sheets by specifying a title, icon, items etc.
- A bottom sheet's items can be displayed as a list, as a two-columned list or as a grid.
- It is possible to separate a bottom sheet's items by adding dividers (with optional titles).
- The library offers a possibility to display all applications, which are suited for handling an Intent, as a bottom sheet's items
- The items of a bottom sheet can be enabled/disabled individually.
- As an alternative to displaying items, a bottom sheet's items can be be replaced with a custom view.
- The library comes with a dark theme in addition to the default light theme. Both themes can be modified by using theme attributes.