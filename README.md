HoldToLoadLayout
================
<a href="http://developer.android.com/index.html" target="_blank"><img src="https://img.shields.io/badge/platform-android-green.svg"/></a>
<a href="https://android-arsenal.com/api?level=15" target="_blank"><img src="https://img.shields.io/badge/API-15%2B-green.svg?style=flat"/></a> 
<a href="http://opensource.org/licenses/MIT" target="_blank"><img src="https://img.shields.io/badge/License-MIT-blue.svg?style=flat"/></a>
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e6d90fbd33ec42cfaf3501312f79d114)](https://www.codacy.com/app/aksoy-melihcan/HoldToLoadLayout?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=melihaksoy/HoldToLoadLayout&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/melihaksoy/HoldToLoadLayout.svg?branch=master)](https://travis-ci.org/melihaksoy/HoldToLoadLayout)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-HoldToLoadLayout-green.svg?style=true)](https://android-arsenal.com/details/1/3743)


HoldToLoadLayout is a view group that can contain a single child. It draws your child to middle of layout, and performs loading wheel around it with settings you determined.


Sample Gifs
===========
![Anim](https://github.com/melihaksoy/HoldToLoadLayout/blob/master/gifs/htl_1.gif)
![Anim](https://github.com/melihaksoy/HoldToLoadLayout/blob/master/gifs/hlt_2.gif)
![Anim](https://github.com/melihaksoy/HoldToLoadLayout/blob/master/gifs/htl_3.gif)
![Anim](https://github.com/melihaksoy/HoldToLoadLayout/blob/master/gifs/htl_4.gif)

# Usage

Simply, add a child to HoldToLoadLayout, than set properties about the animation you'd like.

```xml
     <com.melih.holdtoload.HoldToLoadLayout
            android:id="@+id/holdToLoadLayout"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
    
            <ImageView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/github"/>
        </com.melih.holdtoload.HoldToLoadLayout>
```


```java 
         HoldToLoadLayout holdToLoadLayout = (HoldToLoadLayout) findViewById(R.id.holdToLoadLayout);
         
         holdToLoadLayout.setStrokeWidth(10); // Set stroke width in px ( dp in xml )
         holdToLoadLayout.setStrokeAlpha(255); // Set alpha value of paint ( 0 - 255 )
         holdToLoadLayout.setPlayReverseAnimation(true); // Reverse like it fills, with animation
         holdToLoadLayout.setStopWhenFilled(false); // Stop when holded fully
         holdToLoadLayout.setColorAnimator(Color.YELLOW, Color.RED); // Animate color while drawing
         holdToLoadLayout.setStartAngle(30); // Starting angle of loading
```

**Customization**

```java
setStrokeWidth(int strokeWidth)
```
<ul><li> Sets stroke width in pixels if <b>strokewidth</b> is greater than 0.</br>
You can set dp values from XML. Default is 0.
</li></ul>

```java
setPlayReverseAnimation(boolean isReverseAnimationEnabled)
``` 
<ul><li>
If set true, loading will reverse from the point user stopped touching to 0. If set false, loading will disappear instantly.</br>
Default value is true.
</li></ul>

```java
setStopWhenFilled(boolean stopWhenFilled)
``` 
<ul><li>
If set true, loading will stop when it's completed. If set false, loading will be reversed /disappear even if it is filled.</br>
Default value is true;
</li></ul>

```java
setColorAnimator(int startingColor, int endingColor)
```
<ul><li>
When set, changes color of loading animation up to progress, starting with <b>startingColor</b> and ending with <b>endingColor</b>.</br>
There is no color animation by default.
</li></ul>

```java
setStrokeColor(int color)
setStrokeColor(String color)
```
<ul><li>
Set loading's color. Default color is <b>Color.GREEN</b> ( <b> HoldToLoadLayout.DEFAULT_COLOR </b> ).
</li></ul>

```java
setDuration(int durationInMillis)
```
<ul><li>
Set duration of fill time in milliseconds. This will throw <b>IllegalArgumentException</b> if <b>durationInMillis is not greater than 0</b>.</br>
Default value is 1500 ( <b> HoldToLoadLayout.DEFAULT_DURATION </b> ).
</li></ul>

```java
setStrokeAlpha(int alpha)
```
<ul><li>
Set paint's alpha value. This will throw <b>IllegalArgumentException</b> if <b>alpha is less than 0 or greater than 255</b>.</br>
Default value is 255 ( <b> HoldToLoadLayout.DEFAULT_ALPHA </b> ).
</li></ul>

```java
setStartAngle(float startAngle)
```
<ul><li>
Set loading's starting point as angle. Default value is 270 ( top ) ( <b> HoldToLoadLayout.DEFAULT_START_ANGLE </b> ).</br>
</li></ul>

```java
setFillListener(FillListener fillListener)
```
<ul><li>
Set a fill listener ( HoldToLoadLayout.FillListener() ), which has <b>onFull()</b>, <b>onEmpty()</b> and <b>onAngleChanged(float angle)</b> methods.
</li></ul>

## XML Attributes

```xml
        <attr name="strokeColor" format="string"/>
        <attr name="strokeWidth" format="dimension"/>
        <attr name="strokeAlpha" format="integer"/>
        <attr name="startAngle" format="integer"/>
        <attr name="duration" format="integer"/>
        <attr name="stopWhenFilled" format="boolean"/>
```

## Download
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

Add library dependency to your `build.gradle` file:

```groovy
dependencies {    
     compile 'com.github.melihaksoy:HoldToLoadLayout:1.0.0'
}
```

## License
```
The MIT License (MIT)

Copyright (c) 2015 melihaksoy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```