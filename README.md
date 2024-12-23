# JVM常见启动参数

## 1、设置堆内存大小

```bash
-Xms512m # 设置初始堆大小为 512M
-Xmx1024m # 设置最大堆大小为1024m
```

## 2、选择垃圾回收器

```bash
-XX:+UseG1GC # 使用G1垃圾回收器
-XX:+UseConcMarkSweepGC # 使用CMS垃圾回收器
```

> 注意：使用CMS垃圾收集器时，需要注意一些限制。例如，CMS不会对永久代进行垃圾回收，因此如果应用程序有大量的Class对象，可能需要考虑使用其他垃圾收集器。同时，由于CMS需要占用一定的CPU资源，因此在CPU资源非常有限的环境中，可能需要考虑使用其他垃圾收集器。

### 3、Metaspace大小设置

动态调整元空间，防止内存溢出

```bash
-XX:MetaspaceSize=128m # 设置Metaspace初始大小
-XX:MaxMetaspaceSize=512m # 设置Metaspace最大大小
```

## 4、启用类数据共享（Class Data Sharing）

减少内存占用，加快启动速度

```bash
-XX:+UseAppCDS # 默认CDS(只共享系统类)
-XX:SharedArchiveFile=app-cds.jsa # 参考：https://blog.csdn.net/qq_38766930/article/details/131851530
```

> Java 10引入了应用程序类[数据共享](https://so.csdn.net/so/search?q=数据共享&spm=1001.2101.3001.7020)（Application Class-Data Sharing）功能，简称CDS。CDS允许将常用类的元数据存储在共享的归档文件中，以便多个Java进程在启动时共享这些元数据，从而加快应用程序的启动速度和降低内存占用。
>
> CDS允许在应用程序启动时，将常用的类的[元数据存储](https://so.csdn.net/so/search?q=元数据存储&spm=1001.2101.3001.7020)在共享的归档文件中，以便其他Java进程可以在启动时使用这些元数据，而无需重新解析和加载类。这样可以显著缩短Java应用程序的启动时间，并降低内存消耗。

## 5、输出JVM信息

监控JVM运行状态、优化调优

```bash
-XX:+PrintGCDetails # 用于打印输出详细的GC收集日志的信息，参考：https://blog.csdn.net/qq_33229669/article/details/106035861
-XX:+PrintGCDateStamps # 打印GC发生时的时间戳，搭配 -XX:+PrintGCDetails 使用，不可以独立使用，参考：https://blog.csdn.net/chengqiuming/article/details/120126319
-XX:+PrintCompilation # 打印编译信息，参考：https://blog.csdn.net/weixin_41228362/article/details/106388843
```

## 6、启用并行垃圾回收

充分利用多核，提高GC效率

```bash
-XX:ParallelGCThreads=4 # 设置并行GC线程数为4，参考：https://blog.csdn.net/YouthStrive/article/details/105940792
```

## 7、配置安全策略

增强应用安全，保护数据安全，参考：https://blog.csdn.net/DongShanAAA/article/details/133901991

```bash
-Djava.security.manager
-Djava.security.policy=security.policy
```

## 8、启用远程监控与管理

轻松监控JVM，实时掌握应用状态，参考：https://blog.csdn.net/qq_41768644/article/details/136633874

```bash
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

