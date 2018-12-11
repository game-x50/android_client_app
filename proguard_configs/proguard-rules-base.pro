##---------------Begin: proguard configuration for base  ----------
# todo: uncomment and run ./gradlew app:assembleProdRelease to compare with previous output
#-printconfiguration 'full-proguard-output-report-config.txt'

-repackageclasses
-renamesourcefileattribute SourceFile

##---------------Begin ----------
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn javax.lang.**
##---------------End: proguard configuration for base  ----------