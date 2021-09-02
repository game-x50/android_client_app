##---------------Begin: proguard configuration for Chucker  ----------
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}
-dontwarn kotlinx.coroutines.debug.*
##---------------End: proguard configuration for Chucker  ----------

##---------------Begin: proguard configuration for LeakCanary  ----------
-dontwarn leakcanary.internal.activity.screen.*
##---------------End: proguard configuration for LeakCanary  ----------