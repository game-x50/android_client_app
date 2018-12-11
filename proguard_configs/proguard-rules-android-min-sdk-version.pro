##---------------Begin: proguard configuration for Android app minSdkVersion----------
-assumevalues class android.os.Build$VERSION {
    int SDK_INT return 21..2147483647;
}
##---------------End: proguard configuration for Android app minSdkVersion  ----------