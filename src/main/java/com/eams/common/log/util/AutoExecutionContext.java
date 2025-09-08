package com.eams.common.log.util;

public class AutoExecutionContext {
    private static final ThreadLocal<Boolean> FLAG = ThreadLocal.withInitial(() -> false);

    public static void markAutoExecution() {
        FLAG.set(true);
    }

    public static boolean isInAutoExecution() {
        return FLAG.get();
    }

    public static void clear() {
        FLAG.remove();
    }
}
