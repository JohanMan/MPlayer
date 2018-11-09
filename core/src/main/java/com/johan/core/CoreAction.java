package com.johan.core;

import com.johan.async.task.Task;

/**
 * Created by johan on 2018/11/7.
 */

public interface CoreAction <T> extends Task.UIAction<T>, Task.ErrorAction {

}
