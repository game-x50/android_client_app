##---------------Begin: proguard configuration for Room  ----------
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

##reason: https://issuetracker.google.com/issues/198549508
-keep class io.reactivex.Single { *; }
##---------------End: proguard configuration for Room  ----------