##---------------Begin: proguard configuration for RxJava2  ----------
-keep class io.reactivex.android.schedulers.AndroidSchedulers { public static <methods>; }
-keep class io.reactivex.Schedulers { public static <methods>; }
-keep class io.reactivex.ImmediateScheduler { public <methods>; }
-keep class io.reactivex.TestScheduler { public <methods>; }
-keep class io.reactivex.Schedulers {  public static ** test(); }
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
##---------------End: proguard configuration for RxJava2  ----------