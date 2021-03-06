# LocationLibrary
>LocationAPI helps to fetch location of device in your application. Location API comes with android play services.
This is Android library to track users location. 
I have created library that will help you to fetch users location by implementing listener that will fetch users location after an specified interval.
This library deals with handling of location permission and enabling GPS setting, you don't need to worry about it.

If you want to fetch user's location only on request, below snippet of code will help you;
- Implment below callback LocationCallbacks;

```
  interface LocationCallbacks {
    fun onLocationReceived(location: UsersLocation) // use this callback to receive user's location
    fun onAccessLocationGranted() // This callback is called after location permission granted
    fun onAccessLocationDenied() // This callback is called if user denied location permission 
    fun onLocationSettingEnabled() // This callback is called after enabled GPS settings
    fun onLocationSettingDisabled() // This callback is called after disabling GPS settings
}
```
- Create LocationClient instance as below
```
   val locationClient = LocationClient(
            locationCallbacks = this,
            context = this,
            interval = 100,
            fastestInterval = 100,
            locationType = HIGH_ACCURACY
        )
```
Parameters of Location client are described as below;
locationCallbacks : This is instance of callback where location is received
locationType: You can receive locations that are more/less accurate, and faster/slower than requested.
Different values for this attribute: HIGH_ACCURACY, BALANCED_POWER_ACCURACY, LOW_POWER, NO_POWER
Default value is HIGH_ACCURACY. If have to use BALANCED_POWER_ACCURACY, you can specify fastestInterval, it will receive location triggered by other applications but power blame will be assigned on value of fastest interval. 
Add LocationClient as lifecycle observer
```
lifecycle.addObserver(locationClient)
```

I have published this library as maven artifacts on Jitpack;
For Integration follow below steps;
1. Add below repository into global build.gradle;
```
repositories {
        maven { url 'https://jitpack.io' }
    }
```
2. Add below dependency as app level build.gradle
```
dependencies {
implementation 'com.github.mchakkarwar:LocationLibrary:0.0.2'
}
```



implementation 'com.github.mchakkarwar:LocationLibrary:0.0.2'
