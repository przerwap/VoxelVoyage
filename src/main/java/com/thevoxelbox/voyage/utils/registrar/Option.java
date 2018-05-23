package com.thevoxelbox.voyage.utils.registrar;

import java.lang.annotation.Repeatable;

@Repeatable(CliOptions.class)
public @interface Option {
    String opt() default "";
    String longOpt() default "";
    boolean hasArg() default false;
    String description() default "";
    boolean required() default false;

    int argCount() default 0;
}
