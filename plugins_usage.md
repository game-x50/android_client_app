
-----------------------------------------------------------------------------------
https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin

run
    ./gradlew buildHealt

and check <buildDir>/reports/dependency-analysis/advice-holistic-strict-pretty.json
-----------------------------------------------------------------------------------
https://github.com/jeremylong/dependency-check-gradle

run
    ./gradlew dependencyCheckAnalyze

and check <buildDir>/reports/dependency-check-report.html
-----------------------------------------------------------------------------------
for full project run
    ./gradlew jacocoTestReport

and check <buildDir>/coverage-report/index.html FOR EACH MODULE
-----------------------------------------------------------------------------------
https://github.com/ben-manes/gradle-versions-plugin

run
    ./gradlew dependencyUpdates

and check .<buildDir>/dependencyUpdates/report.txt
-----------------------------------------------------------------------------------
https://github.com/cashapp/licensee

run
    ./gradlew  app:licensee

and check
for Android modules:
<buildDir>/reports/licensee/<variant name>/
for other modules:
<buildDir>/reports/licensee

best way is to check app module
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
https://appsweep.guardsquare.com/

Guardsquare tool for scanning apps to find security issues.
-----------------------------------------------------------------------------------
https://github.com/jraska/modules-graph-assert

A Gradle plugin that helps keep your module graph healthy and lean.
-----------------------------------------------------------------------------------
https://github.com/dropbox/AffectedModuleDetector
https://github.com/ismaeldivita/change-tracker-plugin
https://live.jugru.org/video?v=MTAwMTA4iiM3MjM0ijA

Check to run unit tests and lint just for affected modules.
-----------------------------------------------------------------------------------
https://github.com/gradle/android-cache-fix-gradle-plugin

Improve gradle cache.

but potential problem with room schema: without changing schema version number and updating f.e. column's names
schema json file won't be updated.
-----------------------------------------------------------------------------------
https://github.com/dipien/releases-hub-gradle-plugin
https://akjaw.com/keeping-gradle-dependencies-up-to-date-with-github-actions-and-refreshversions/

Automatically upgrade your java gradle project dependencies and send a GitHub pull request with the changes.
-----------------------------------------------------------------------------------