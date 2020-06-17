
# 2020年06月16日更新
## 一、背景
咱们来分析一下多任务，在使用者的角度可以简单理解为其是一个黑盒，使用者放入之后经过黑盒处理之后再取出这样就达到了最终的结果。
## 二、分析抽象
既然是这样我们可以抽象一下，因为在`linux`中万物皆文件，所以咱们传入的其实是一个废弃：~~sourcePath~~，新增：`source:S`，那么下一步我们就考虑我们想要的是什么了，然后咱们可以把那个黑盒理解为规则，那么可以抽象为，废弃：~~fun parse(sourcePath:String,targetPath:String?):String，~~
新增：`fun parse(source: S, target: T?): T`这里估计大家会疑问为什么会有`targetPath`，不是已经有处理结果了么，这其实是使其更具扩展性，比如要处理一个文件，使用者在处理之前就已经为其定好了`targetPath`，那么在使用时直接传入即可，但是还有一种比如将图片转为`base64`那么知道的只是规则结果是未知的，这时候就需要去取这个返回值了。
经过以上的分析这个库的主干就出来了，那么下一步就是要为其装饰了。
使用者在使用时肯定想的是这个库能够处理批量任务并且能够有相应的回调通知，这样使用者只需要自己定义好处理规则封装为对象，传入这个多任务处理器引擎中，得到相应的回调。
**更新后定义的接口：**
但是更新后的调用基本没有变化，回调结果由泛型推导出实际类型

```
/**
 * Author: fanyafeng
 * Data: 2020/6/3 19:15
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ProcessModel<S, T> : Serializable {

    companion object {
        const val PROCESS_ITEM = "process_item"
        const val PROCESS_LIST = "process_list"
    }

    /**
     * 获取需要处理的源路径
     */
    fun getSource(): S

    /**
     * 目标路径
     */
    fun getTarget(): T?

    /**
     * 处理后的目标路径不能为空
     */
    fun setTarget(target: T)

    /**
     * 任务解析器
     * 这里按道理说如果有了targetPath那么这个返回值是可以不需要的
     * 但是就是因为如果你去处理一个任务但是有规则没有输出那么这个返回值就是必须的了
     * 而且不能为空
     *
     * 分为以下两种以下两种情况：
     * 1.处理文件类
     * @param source 源
     * @param target 结果
     *
     * 2.有处理规则，和原路径，那么方法的返回值就是处理结果
     * @param source 源
     *
     */
    fun parse(source: S, target: T?): T

    /**
     * 简化接口调用
     */
    abstract class ProcessSimpleModel<S, T>(var sourcePath: S, var targetPath: T) : ProcessModel<S, T> {

        override fun getSource(): S {
            return sourcePath
        }

        override fun getTarget(): T? {
            return targetPath
        }

        override fun setTarget(target: T) {
            this.targetPath = target
        }
    }
}
```
## 三、多任务处理器结构图

![多任务处理器结构图](https://github.com/1181631922/ModuleSample/blob/master/ripple_task/%E5%A4%9A%E4%BB%BB%E5%8A%A1%E5%A4%84%E7%90%86.jpg)

## 四、伪代码真调用
这里以`kotlin`为例，当然也支持`java`，只是`kotlin`使用起来会更简洁，来模拟一下咱们想要调用

```
handleTaskList(taskList){
    onSuccess{ successResult->
    
    }
    
    onFailed{ failedResult->
    }
}
```
以上只是简单写了几个回调，基本调用方式是这样，可能使用者还想要自定义一下线程池，并行，串行等
## 五、编码
因为要写的是一个框架类的库，需要对扩展开放对修改关闭，此时再根据上面的抽象以及图便可以很清晰明了的将其完成，下面来细看
### 5.1 任务Model封装
因为是批量任务处理，并且还需要兼容不同类型，那么这个`Model`必须要实现`ProcessModel`接口，设计之初为了灵活设计了三个接口，但是又发现用起来比较麻烦，后来又将其粒度变大，有舍有得吧。下面来看一下接口的实现：
**PS：注释中有详细的说明不再做细说明**

```
/**
 * Author: fanyafeng
 * Data: 2020/6/3 19:15
 * Email: fanyafeng@live.cn
 * Description: 单项任务接口
 */
interface ProcessModel : Serializable {

    companion object {
        const val PROCESS_ITEM = "process_item"
        const val PROCESS_LIST = "process_list"
    }

    /**
     * 获取需要处理的源路径
     * 不能为空皮之不存毛将焉附
     */
    fun getSourcePath(): String

    /**
     * 目标路径可以为空
     * 因为有时只知道规则不知道结果
     */
    fun getTargetPath(): String?

    /**
     * 处理后的目标路径不能为空
     */
    fun setTargetPath(target: String)

    /**
     * 任务解析器
     * 这里按道理说如果有了targetPath那么这个返回值是可以不需要的
     * 但是就是因为如果你去处理一个任务但是有规则没有输出那么这个返回值就是必须的了
     * 而且不能为空
     *
     * 分为以下两种以下两种情况：
     * 1.处理文件类
     * @param sourcePath 文件原路径
     * @param targetPath 文件目标路径
     *
     * 2.有处理规则，和原路径，那么方法的返回值就是处理结果
     * @param sourcePath 文件原路径
     *
     */
    fun parse(sourcePath: String, targetPath: String?): String
}
```

## 5.2 引擎处理设置
这里使用的是`java`的线程池的主要接口`ExecutorService`，主要是其中为我们封装好了我们需要的一些通用的方法，我这里还是把任务交给线程去处理，如果是单线程则是串行，多的话就是并行处理了，还可以实现接口进行自定义
更新后不仅不会每次都新建线程，而是去复用之前的线程并且为线程添加了name方便定位问题，防止重复创建太多的匿名线程，

```
/**
 * Author: fanyafeng
 * Data: 2020/5/6 17:51
 * Email: fanyafeng@live.cn
 * Description: 使用java自带的任务服务
 */
interface ProcessEngine : Serializable {


    companion object {

        internal var singleExecutorInner = Executors.newSingleThreadExecutor(object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内部单线程池-" + atomic.getAndIncrement())
            }
        })
        internal val SINGLE_THREAD_EXECUTOR_INNER: ProcessEngine =
            object : ProcessEngine {
                override fun getExecutorService(): ExecutorService {
                    return if (!singleExecutorInner.isShutdown) {
                        singleExecutorInner
                    } else {
                        singleExecutorInner = Executors.newSingleThreadExecutor(object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内部单线程池-" + atomic.getAndIncrement())
                            }
                        })
                        singleExecutorInner
                    }
                }

                override fun shutdown() {
                    singleExecutorInner.shutdown()
                }

            }

        /**
         * 单线程处理器
         * 处理任务为串行处理
         */
        private var singleExecutor = Executors.newSingleThreadExecutor(object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置单线程池-" + atomic.getAndIncrement())
            }
        })
        val SINGLE_THREAD_EXECUTOR: ProcessEngine =
            object : ProcessEngine {
                override fun getExecutorService(): ExecutorService {
                    return if (!singleExecutor.isShutdown) {
                        singleExecutor
                    } else {
                        singleExecutor = Executors.newSingleThreadExecutor(object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置单线程池-" + atomic.getAndIncrement())
                            }
                        })
                        singleExecutor
                    }
                }

                override fun shutdown() {
                    singleExecutor.shutdown()
                }

            }

        /**
         * 自带6个线程的处理器
         * 不用纠结个数为什么这么定义，纯属个人喜欢的数字
         * 处理任务为并行处理，并且顺序是打乱的
         */
        private var maxExecutor = Executors.newFixedThreadPool(Thread.MAX_PRIORITY, object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置最大线程池-" + atomic.getAndIncrement())
            }
        })
        val MULTI_THREAD_EXECUTOR_MAX: ProcessEngine =
            object : ProcessEngine {
                override fun getExecutorService(): ExecutorService {
                    return if (!maxExecutor.isShutdown) {
                        maxExecutor
                    } else {
                        maxExecutor = Executors.newFixedThreadPool(Thread.MAX_PRIORITY, object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置最大线程池-" + atomic.getAndIncrement())
                            }
                        })
                        maxExecutor
                    }
                }

                override fun shutdown() {
                    maxExecutor.shutdown()
                }

            }

        private var normalExecutor = Executors.newFixedThreadPool(Thread.NORM_PRIORITY, object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置一般线程池-" + atomic.getAndIncrement())
            }
        })

        val MULTI_THREAD_EXECUTOR_NORMAL: ProcessEngine =
            object : ProcessEngine {

                override fun getExecutorService(): ExecutorService {
                    return if (!normalExecutor.isShutdown) {
                        normalExecutor
                    } else {
                        normalExecutor = Executors.newFixedThreadPool(Thread.NORM_PRIORITY, object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置一般线程池-" + atomic.getAndIncrement())
                            }
                        })
                        normalExecutor
                    }
                }

                override fun shutdown() {
                    normalExecutor.shutdown()
                }

            }

        private var minExecutor = Executors.newFixedThreadPool(Thread.MIN_PRIORITY, object :
            ThreadFactory {
            val atomic = AtomicInteger(1)
            override fun newThread(r: Runnable): Thread {
                return Thread(r, "ripple-task-内置最小线程池-" + atomic.getAndIncrement())
            }
        })

        val MULTI_THREAD_EXECUTOR_MIN: ProcessEngine =
            object : ProcessEngine {

                override fun getExecutorService(): ExecutorService {
                    return if (!minExecutor.isShutdown) {
                        minExecutor
                    } else {
                        minExecutor = Executors.newFixedThreadPool(Thread.MIN_PRIORITY, object :
                            ThreadFactory {
                            val atomic = AtomicInteger(1)
                            override fun newThread(r: Runnable): Thread {
                                return Thread(r, "ripple-task-内置最小线程池-" + atomic.getAndIncrement())
                            }
                        })
                        minExecutor
                    }
                }

                override fun shutdown() {
                    minExecutor.shutdown()
                }

            }
    }

    /**
     * 获取任务处理任务
     *
     * 1、线程池： 提供一个线程队列，队列中保存着所有等待状态的线程。避免了创建与销毁的额外开销，提高了响应的速度。
     *
     * [java.util.concurrent.Executor]
     * [java.util.concurrent.ExecutorService]
     * [java.util.concurrent.ThreadPoolExecutor]
     * [java.util.concurrent.ScheduledExecutorService]
     * [java.util.concurrent.ScheduledThreadPoolExecutor]
     *
     * 2、线程池的体系结构：
     * java.util.concurrent.Executor 负责线程的使用和调度的根接口
     *      |--ExecutorService 子接口： 线程池的主要接口
     *              |--ThreadPoolExecutor 线程池的实现类
     *              |--ScheduledExecutorService 子接口： 负责线程的调度
     *                      |--ScheduledThreadPoolExecutor : 继承ThreadPoolExecutor，实现了ScheduledExecutorService
     *
     * [java.util.concurrent.Executor]
     *
     * 3、工具类 ： Executors
     * ExecutorService newFixedThreadPool() : 创建固定大小的线程池
     * ExecutorService newCachedThreadPool() : 缓存线程池，线程池的数量不固定，可以根据需求自动的更改数量。
     * ExecutorService newSingleThreadExecutor() : 创建单个线程池。 线程池中只有一个线程
     *
     * ScheduledExecutorService newScheduledThreadPool() : 创建固定大小的线程，可以延迟或定时的执行任务
     */
    fun getExecutorService(): ExecutorService

    /**
     * 停止任务
     */
    fun shutdown()

}
```
## 5.3 核心任务处理
这里就包含了任务处理器以及任务回调了，因为使用者想要的就是把任务处理完成以及结果的回调不论成功或者失败。
更新后的task添加了泛型支持，需要外部传入类型，增加了通用性

```
/**
 * Author: fanyafeng
 * Data: 2020/6/3 19:39
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ProcessTask<S, T> {

    /**
     * 所有单个任务回调，任务回调包含以下所有回调，但是为了简化使用
     * 除去单项任务完成回调必须重写其余进行了空实现
     *
     * 单项任务开始回调
     * [com.ripple.task.callback.OnItemStart]
     *
     * 单项任务进行回调
     * [com.ripple.task.callback.OnItemDoing]
     *
     * 单项任务打断回调
     * [com.ripple.task.callback.OnItemInterrupted]
     *
     * 单项任务失败回调
     * [com.ripple.task.callback.OnItemFailed]
     *
     * 单项任务成功回调
     * [com.ripple.task.callback.OnItemSuccess]
     *
     * 单项任务完成回调
     * [com.ripple.task.callback.OnItemFinish]
     */
    fun getItemResult(): OnItemResult<ProcessModel<S, T>>?

    /**
     * 所有任务回调，基本同上除去具体回调
     *
     * 所有任务开始回调
     * [com.ripple.task.callback.OnStart]
     *
     * 所有任务进行回调
     * [com.ripple.task.callback.OnDoing]
     *
     * 所有任务失败回调
     * [com.ripple.task.callback.OnFailed]
     *
     * 所有任务成功回调
     * [com.ripple.task.callback.OnSuccess]
     *
     * 所有任务完成结束回调
     * [com.ripple.task.callback.OnFinish]
     */
    fun getAllResult(): OnAllResult<List<ProcessModel<S, T>>>?

    /**
     * 获取任务处理器引擎
     * [com.ripple.task.engine.ProcessEngine]
     */
    fun getProcessEngine(): ProcessEngine
}
```
## 5.4 以上任务处理的必须部分都完成，剩下的就是将任务model交付ProcessTask处理即可
因为库的主要作用是为了方便大家使用，这里进行默认实现，因为代码量略大不在这里贴了，如果感觉默认的使用不能满足大家的使用，大家可以自己在实现相关接口之后进行自己的`impl`，这里贴一下库的使用代码，所有回调的结果都有，需要哪个回调实现哪个回调方法即可。

### 5.4.1 以下为java方式调用
主要是`kotlin`使用比较简洁，但是兼容了`java`的调用：
更新后以string为例：
```
val task = ProcessTaskImpl<String,String>()
task.onAllResult = object : OnAllResult<List<ProcessModel<String,String>>> {
    override fun onFinish(
        finishResult: List<ProcessModel<String,String>>?,
        unFinishResult: List<ProcessModel<String,String>>?
    ) {
        TODO("所有任务完成回调")
    }

}
task.handleTaskList(listOf(
    Task1("abdaafda"),
    Task2("不会输出"),
    Task3("abdaafda又变大写了")
))
```
### 5.4.2 以下为kotlin方式调用
`kotlin`调用的话就稍微简单一点

```
handleTaskList(
    listOf(
        Task1("abdaafda"),
        Task2("不会输出"),
        Task3("abdaafda又变大写了")
    )
) {
    onSuccess { successResult ->

    }

    onFailed { failedResult ->

    }

    onFinish { finishResult, unFinishResult ->
        println("结果回调" + finishResult.toString())

        println(unFinishResult.toString())
    }
}
```