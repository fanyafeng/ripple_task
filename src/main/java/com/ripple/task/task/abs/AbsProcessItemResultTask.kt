package com.ripple.task.task.abs

import com.ripple.task.config.ProcessModel
import com.ripple.task.task.ProcessItemResultTask

/**
 * Author: fanyafeng
 * Data: 2020/6/4 10:51
 * Email: fanyafeng@live.cn
 * Description:
 */
abstract class AbsProcessItemResultTask<M : ProcessModel<S, T>, S, T> :
    ProcessItemResultTask<M, S, T> {
}