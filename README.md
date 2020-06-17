# 多任务处理器
>多任务处理库为的是解决批量任务处理应运而生，最初是因为写图片压缩库，当时图片处理作为一个单独的库，里面其实抽象了多任务。
>PS：暂时只能在`android`中使用，但是如果把`handler`去掉的话可以供`java`后台使用

# **接入以及使用**
## 1. 引入

```
implementation 'com.ripple.component:task:0.0.6'
```
## 2. 使用
### 2.1 定义taskmodel
首先需要定义`taskmodel`，定义完成后便可以使用了，下面定义了三个不同的任务

```
data class Task1 @JvmOverloads constructor(
    private val sourcePath: String,
    private var targetPath: String? = null
) : ProcessModel<String, String> {
    override fun getSource(): String {
        return sourcePath
    }

    override fun getTarget(): String? {
        return targetPath
    }

    override fun setTarget(target: String) {
        this.targetPath = target
    }

    override fun parse(source: String, target: String?): String {
        return source.toUpperCase()
    }
}

data class Task2 @JvmOverloads constructor(
    private val sourcePath: String,
    private var targetPath: String = "任务2目标路径"
) : ProcessModel<String, String> {
    override fun getSource(): String {
        return sourcePath
    }

    override fun getTarget(): String? {
        return targetPath
    }

    override fun setTarget(target: String) {
        this.targetPath = target
    }

    override fun parse(source: String, target: String?): String {
        Thread.sleep(2000)
        return "我是任务2$target"
    }

}

data class Task3 @JvmOverloads constructor(
    private val sourcePath: String,
    private var targetPath: String = "任务三目标路径"
) : ProcessModel<String, String> {
    override fun getSource(): String {
        return sourcePath
    }

    override fun getTarget(): String? {
        return targetPath
    }

    override fun setTarget(target: String) {
        this.targetPath = target
    }

    override fun parse(source: String, target: String?): String {
        Thread.sleep(3000)
        return source.toUpperCase() + "在来个任务3一起走"
    }

}
```

### 2.2 开始使用
定义完成后交给批量任务处理器即可
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

### 2.3 新增周期任务

```
val eng = ScheduledProcessEngine.SINGLE_THREAD_EXECUTOR

val scheduleTask = ScheduledProcessTaskImpl<String,String>(eng)

btn2.setOnClickListener {
    scheduleTask.scheduleAtFixedRate({
        handleTaskList(
            listOf(
                Task1("abdaafda"),
                Task2("不会输出"),
                Task3("abdaafda又变大写了")
            )
        ) {

            onFinish { finishResult, unFinishResult ->
                println("结果回调" + finishResult.toString())

                println(unFinishResult.toString())
            }
        }
    }, 0L, 1L, TimeUnit.SECONDS)

}

btn4.setOnClickListener {
    eng.shutdown()
}
```



-------

#### 版本更新：

##### 0.0.4
1.将任务进行接口化，完成抽离
2.支持单任务，多任务执行，但是暂不支持延迟任务，周期任务（不过可以通过java的ScheduledExectorService完成此功能）
##### 0.0.5
1.通过添加ScheduledProcessEngine接口进行支持延迟，周期任务

##### 0.0.6
1.重构代码（之前固定使用的源和目标都是String，现在修改为泛型，由使用者去指定类型）
2.优化内置，内部线程调用，添加线程name，方便定位问题
3.优化lambda回调，减少对象的创建

