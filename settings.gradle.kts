//https://proandroiddev.com/using-type-safe-project-dependencies-on-gradle-493ab7337aa
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
        //CORE
        ":features:core:extensions",

        ":features:core:android:api",
        ":features:core:android:extensions",
        ":features:core:android:storage",
        ":features:core:test_utils",

        ":features:core:api:lib",
        ":features:core:api:debug_tools",
        ":features:core:api:staging_tools",
        ":features:core:api:test_utils",

        ":features:core:parsing:impl",

        ":features:core:network:api",
        ":features:core:network:impl:lib",
        ":features:core:network:impl:debug_tools",
        ":features:core:network:impl:staging_tools",

        ":features:core:impl:lib",
        ":features:core:impl:debug_tools",
        ":features:core:impl:staging_tools",

        ":features:core:ui:api:lib",
        ":features:core:ui:api:test_utils",

        ":features:core:ui:impl:lib",
        ":features:core:ui:impl:debug_tools",
        ":features:core:ui:impl:staging_tools",

        //GAME
        ":features:game:api:lib",
        ":features:game:api:test_utils",

        ":features:game:auth:impl",
        ":features:game:auth:ui",

        ":features:game:storage:impl",

        ":features:game:play:ui",

        ":features:game:settings:ui",

        ":features:game:top:impl",
        ":features:game:top:ui",

        //APPS
        ":app",

        //THIRD_PARTY

        ":features:third_party:androidx:permissions",

        "features:third_party:androidx:fragment:extensions",
        "features:third_party:androidx:material:extensions",
        "features:third_party:androidx:recyclerview:extensions",
        "features:third_party:androidx:insets",
        ":features:third_party:androidx:work_manager:utils",

        ":features:third_party:three_ten:extensions",

        ":features:third_party:rxjava2:extensions",
        ":features:third_party:rxjava2:test_utils"
)