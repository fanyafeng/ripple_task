package com.ripple.task.config.impl;

import com.ripple.task.config.ProcessModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Author: fanyafeng
 * Data: 2020/6/4 17:04
 * Email: fanyafeng@live.cn
 * Description:
 */
public class ProcessModelJavaImpl implements ProcessModel<String, String> {


    @NotNull
    @Override
    public String getSource() {
        return "null";
    }

    @Nullable
    @Override
    public String getTarget() {
        return "null";
    }

    @Override
    public void setTarget(@NotNull String target) {

    }

    @NotNull
    @Override
    public String parse(@NotNull String source, @Nullable String target) {
        return "null";
    }
}

