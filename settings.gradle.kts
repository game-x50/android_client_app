//https://proandroiddev.com/using-type-safe-project-dependencies-on-gradle-493ab7337aa
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
        //CORE
        ":features:core:extensions",

        ":features:core:android:api",
        ":features:core:android:extensions",
        ":features:core:android:storage",
        ":features:core:android:strict:mode:lib",
        ":features:core:android:strict:mode:fragment",

        ":features:core:recycler:item",

        ":features:core:pagination:api",

        ":features:core:foreground:observer:api",
        ":features:core:foreground:observer:impl",

        ":features:core:test_utils",

        ":features:core:command:lib",

        ":features:core:error",

        ":features:core:manager:api",

        ":features:core:logger:api:lib",
        ":features:core:logger:api:test_utils",
        ":features:core:logger:impl",

        ":features:core:language:code",
        ":features:core:language:api",
        ":features:core:language:impl",

        ":features:core:thread:lib",
        ":features:core:thread:test_utils",

        ":features:core:result",
        ":features:core:value:holder",

        ":features:core:config:app",

        ":features:core:di",

        ":features:core:api:debug_tools",
        ":features:core:api:staging_tools",

        ":features:core:parsing:impl",

        ":features:core:network:api",
        ":features:core:network:impl:lib",
        ":features:core:network:impl:debug_tools",
        ":features:core:network:impl:staging_tools",

        ":features:core:impl:lib",
        ":features:core:impl:debug_tools",
        ":features:core:impl:staging_tools",

        ":features:core:ui:api:lib",
        ":features:core:ui:activity",
        ":features:core:ui:fragment:lib",
        ":features:core:ui:fragment:manager:api",
        ":features:core:ui:dialog",
        ":features:core:ui:routing",
        ":features:core:ui:views",
        ":features:core:ui:lifecycle:lib",
        ":features:core:ui:lifecycle:test_utils",
        ":features:core:ui:lifecycle:utils",
        ":features:core:ui:viewmodel:lib",
        ":features:core:ui:viewmodel:test_utils",
        ":features:core:ui:viewmodel:extensions",
        ":features:core:ui:recycler:adapter",
        ":features:core:ui:pagination:viewmodel",
        ":features:core:ui:pagination:view",
        ":features:core:ui:layout:container",
        ":features:core:ui:viewbinding:extensions",

        ":features:core:ui:resources:colors",
        ":features:core:ui:resources:dimens",

        ":features:core:ui:impl:lib",
        ":features:core:ui:impl:debug_tools",
        ":features:core:ui:impl:staging_tools",

        //GAME
        ":features:game:api:lib",
        ":features:game:api:test_utils",

        ":features:game:auth:impl",
        ":features:game:auth:ui",

        ":features:game:storage:impl",

        ":features:game:play:api",
        ":features:game:play:ui:view",
        ":features:game:play:ui:screens",

        ":features:game:settings:ui",

        ":features:game:top:impl",
        ":features:game:top:ui",

        //APPS
        ":app",

        //THIRD_PARTY

        ":features:third_party:androidx:permissions",

        ":features:third_party:androidx:fragment:extensions",
        ":features:third_party:androidx:material:extensions",
        ":features:third_party:androidx:recyclerview:extensions",
        ":features:third_party:androidx:insets",
        ":features:third_party:androidx:work_manager:utils",
        ":features:third_party:androidx:room:utils",

        ":features:third_party:three_ten:extensions",

        ":features:third_party:rxjava2:extensions",
        ":features:third_party:rxjava2:test_utils"
)