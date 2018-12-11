
-----------------------------------------------------------------------------------
https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin

run
    ./gradlew buildHealt

and check ../build/reports/dependency-analysis/advice-holistic-pretty.json
-----------------------------------------------------------------------------------
https://github.com/jeremylong/dependency-check-gradle

run
    ./gradlew dependencyCheckAnalyze

and check ../build/reports/dependency-check-report.html
-----------------------------------------------------------------------------------
for full project run
    ./gradlew testDebugUnitTestCoverage

for some specific module run
    ./gradlew some_module_name:testDebugUnitTestCoverage

for some specific module and specific flavor run
    ./gradlew some_module_name:testFlavornameDebugUnitTestCoverage
where Flavorname - flavor name

and check ../build/coverage-report/index.html FOR EACH MODULE
-----------------------------------------------------------------------------------
https://github.com/ben-manes/gradle-versions-plugin

run
    ./gradlew dependencyUpdates

and check ../build/dependencyUpdates/report.txt
-----------------------------------------------------------------------------------
run
    ./gradlew app:assembleDebug --scan

check terminal to provide approve for sending data(type: yes)
and open link in terminal output
analyze scan(especially Performance block on left menu, scroll to right top bar until Settings and suggestions)

also can try clean build
-----------------------------------------------------------------------------------
Use standart Android Studio inspector

top menu: Analyze -> Inspect Code
-----------------------------------------------------------------------------------
Use standart Android Studio suggestions

top menu: File -> Project Structure -> Suggestions
-----------------------------------------------------------------------------------


-----------------------------------------------------------------------------------
-----------------------------------------------------------------------------------
-----------------------------------------------------------------------------------

Think about:
-----------------------------------------------------------------------------------
https://github.com/ismaeldivita/change-tracker-plugin

Check to run unit tests and lint just for affected modules.
-----------------------------------------------------------------------------------
https://github.com/gradle/android-cache-fix-gradle-plugin

Improve gradle cache.

but potential problem with room schema: without changing schema version number and updating f.e. column's names
schema json file won't be updated.
-----------------------------------------------------------------------------------
https://github.com/dipien/releases-hub-gradle-plugin

Automatically upgrade your java gradle project dependencies and send a GitHub pull request with the changes.
-----------------------------------------------------------------------------------