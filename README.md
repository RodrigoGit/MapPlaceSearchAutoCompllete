 # **VMAP**
Library to facilitate and expedite the implementation of maps in your project
## **Requirements**
The libray requires Android **API level 15+.**

## **ScreenShot**
<img src="https://github.com/rviannaoliveira/VMap/blob/master/images/first.png" width="360" height="600">
<img src="https://github.com/rviannaoliveira/VMap/blob/master/images/second.png" width="360" height="600">

## **Features**
* Zoom
* Current Location
* AutoComplete using Google Places SDK

## **Installation**
Step 1. Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
       url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```gradle
dependencies {
    compile 'com.github.rviannaoliveira:VMap:v1.0'
}
```

## **Usage**
Declare a variable in Activity or Fragment to get later
```java
private final int RESULT_MAP = 1234;
```
and add it as you call a Activity or Fragment
```java
Intent intent = new Intent(context, MapsActivity.class);
```
or you can pass latitude and longitude to open with marker already

```java
map.putExtra(MapsActivity.LATITUDE, latLng.latitude);
map.putExtra(MapsActivity.LONGITUDE, latLng.longitude);
```
then you need to call Activity or Fragment with result

```java
this.startActivityForResult(map, RESULT_MAP); //Activity 
getActivity().startActivityForResult(map, RESULT_MAP); //Fragment
```

then you will receive the LatLng 
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
    if (RESULT_MAP == requestCode && resultCode == Activity.RESULT_OK) {
       LatLng latLng = MapsUtil.getLatLgn(data.getExtras());
    }
}
```
## **Tools**
Inside of library there are some utils that you can use

**get List of Address**
```java
List<Address> addresses = MapsUtil.getAddresses(context, latLng);
```
or you can take the address with **formatting**
```java
String addressComplete = MapsUtil.formatAddressAutoComplete(addresses.get(0).getThoroughfare(), addresses.get(0).getSubThoroughfare());
```

## **Applications that use this library**

https://play.google.com/store/apps/details?id=com.rviannaoliveira.jachegou

## **License**

```
Copyright 2017 Rodrigo Vianna

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

















