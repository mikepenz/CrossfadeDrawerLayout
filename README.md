# CrossfadeDrawerLayout  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/crossfadedrawerlayout/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.mikepenz/crossfadedrawerlayout) [![Join the chat at https://gitter.im/mikepenz/crossfadedrawerlayout](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mikepenz/crossfadedrawerlayout?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

> Many modern applications come with a `Drawer`, which is a great pattern to implement one level of navigation to your app, but this only comes with one level, and after opening it will take the whole view.

The CrossfadeDrawerLayout is here to solve this. It alllows to implement a 2 step navigation. By default it does not show up, after the user swipes, or clicks the `NavigationIcon` it will show the quick navigation, which
only consists out of a profile (if given), icons and badges. Users can quickly choose what they want, without interacting with any additional information. If the user wants to switch his profile, or want have access to additional
functions, he can drag in the `MiniDrawer` and resize it to a normal `Drawer`. 

# Preview
## Screenshots
![Image](https://raw.githubusercontent.com/mikepenz/CrossfadeDrawerLayout/develop/DEV/screenshots/screenshots.jpg)

# Include in your project
## Using Maven
```javascript
implementation "com.mikepenz:crossfadedrawerlayout:1.1.0"
implementation "com.mikepenz:materialize:${latestVersion}"
implementation "androidx.appcompat:appcompat:${androidX}"
```

To use appcompat please use a version smaller than 1.1.0-rc01. (See the releases on GitHub)

## How to use

### With the MaterialDrawer
[MaterialDrawer](https://github.com/mikepenz/MaterialDrawer)
```java
crossfadeDrawerLayout = new CrossfadeDrawerLayout(this);

//Create the drawer
result = new DrawerBuilder()
        .withActivity(this)
        .withToolbar(toolbar)
        //provide the custom crossfadeDrawerLaoyout
        .withDrawerLayout(crossfadeDrawerLayout)
        .withHasStableIds(true)
        //define the width to the width of our smaller view
        .withDrawerWidthDp(72)
        //generate the MiniDrawer which is used for the smaller view
        .withGenerateMiniDrawer(true)
        //add some items, those are alo automatically added to the MiniDrawer
        .addDrawerItems(
                ...
        )
        //the listener which is called when an item inside the drawer or miniDrawer is clicked
        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                return false;
            }
        })
        .withSavedInstance(savedInstanceState)
        .build();

//define maxDrawerWidth (this is the width in the complete opened state)
crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));

//add second view (which is the miniDrawer)
MiniDrawer miniResult = result.getMiniDrawer();
//build the view for the MiniDrawer
View view = miniResult.build(this);
//set the background of the MiniDrawer as this would be transparent
view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
//we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
miniResult.withCrossFader(new ICrossfader() {
    @Override
    public void crossfade() {
        crossfadeDrawerLayout.crossfade(400);
        //only close the drawer if we were already faded and want to close it now
        if (isCrossfaded()) {
            result.getDrawerLayout().closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean isCrossfaded() {
        return crossfadeDrawerLayout.isCrossfaded();
    }
});

```


# Developed By

* Mike Penz 
 * [mikepenz.com](http://mikepenz.com) - <mikepenz@gmail.com>
 * [paypal.me/mikepenz](http://paypal.me/mikepenz)

# License

    Copyright 2018 Mike Penz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
