package com.ruslan.hlushan.extensions

inline fun <T, R> ifNotNull(arg1: T?, block: (T) -> R): R? =
		if (arg1 != null) block(arg1) else null

inline fun <T1, T2, R> ifNotNull(arg1: T1?, arg2: T2?, block: (T1, T2) -> R): R? =
		if (arg1 != null && arg2 != null) block(arg1, arg2) else null

inline fun <T1, T2, T3, R> ifNotNull(arg1: T1?, arg2: T2?, arg3: T3?, block: (T1, T2, T3) -> R): R? =
		if (arg1 != null && arg2 != null && arg3 != null) block(arg1, arg2, arg3) else null

@SuppressWarnings("ComplexCondition")
inline fun <T1, T2, T3, T4, R> ifNotNull(arg1: T1?, arg2: T2?, arg3: T3?, arg4: T4?, block: (T1, T2, T3, T4) -> R): R? =
		if (arg1 != null && arg2 != null && arg3 != null && arg4 != null) block(arg1, arg2, arg3, arg4) else null