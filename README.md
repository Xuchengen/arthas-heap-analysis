# arthas-heap-analysis
Arthas是阿里巴巴开源的一款Java诊断工具，本项目集成Arthas模拟极端场景进行问题排查分析，从而掌握该工具的使用。

### 前置条件

调整堆内存大小

```bash
-Xms1G -Xmx1G
```

### 内存溢出场景

#### 场景一

访问创建订单接口一，执行该接口会在一段时间内将堆内存占满，堆内存溢出后随着方法的退出堆内存释放。
该场景表达的意思是线上环境日志文件中存在OOM，各业务接口可正常访问.

```bash
curl http://localhost:8080/createOrder
```

#### 场景二

访问创建订单接口二，执行该接口会在一段时间内将堆内存占满，堆内存溢出后随着方法的退出堆内存并没有释放。
该场景表达的意思是线上环境日志文件中存在OOM，大部分接口无法访问小部分接口访问延迟非常高。

```bash
curl http://localhost:8080/createOrder2
```

#### CPU飙高场景

访问斐波那契数列接口，访问该接口会持续造成CPU飙高，造成其它业务接口延迟非常高。

```bash
curl http://localhost:8080/fibonacci
```

### 传统排查方式

#### 内存溢出

使用``top``命令找到目标进程PID
```bash
[root@xuchengen ~]# top
top - 15:36:40 up 13 days, 18:55,  0 users,  load average: 0.00, 0.01, 0.05
Tasks: 153 total,   1 running, 152 sleeping,   0 stopped,   0 zombie
%Cpu(s):  0.5 us,  0.4 sy,  0.0 ni, 99.1 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  8008632 total,   818372 free,  1740484 used,  5449776 buff/cache
KiB Swap:  3145724 total,  3088636 free,    57088 used.  5916248 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                                                                                        
 5054 root      20   0  991224  52520  21208 S   1.3  0.7  69:38.94 YDService                                                                                                                      
 1613 root      20   0 1399448  50076  13968 S   0.7  0.6  22:02.30 appnode-agent-s                                                                                                                
 5095 root      20   0  997264   7124   4408 S   0.7  0.1   1:49.95 YDLive                                                                                                                         
 7211 root      20   0  354976  98264  22940 S   0.7  1.2  33:11.20 node /ql/build/                                                                                                                
 1373 root      20   0 1058860  18928   5084 S   0.3  0.2  76:46.45 fail2ban-server                                                                                                                
 1601 zerotie+  20   0  241188  10700   2600 S   0.3  0.1  14:46.96 zerotier-one                                                                                                                   
 1608 root      20   0 1321088  32352  10260 S   0.3  0.4  14:27.44 appnode-ccenter                                                                                                                
 3478 root      20   0 1311360 100148   8968 S   0.3  1.3  22:38.24 HFish                                                                                                                          
 4665 root      20   0  313280  44876  17740 S   0.3  0.6  12:28.78 PM2 v5.1.1: God                                                                                                                
 8969 root      20   0  612684  14216   2072 S   0.3  0.2 116:05.65 barad_agent                                                                                                                                                                                                                                       
```

使用``ps``命令找到目标进程PID
```bash
[root@xuchengen ~]# ps -ef | grep 进程名 | grep -v grep | awk '{print $2}'
1521
```

使用``jps``命令找到目标进程PID，当然这个命令需要Java环境。
```bash
[root@xuchengen ~]# jps
8080 RemoteMavenServer36
18148 MavenDaemon
14488 Jps
```

以上几种方式都可以找到进程PID。

使用``jmap``命令导出堆文件
```bash
jmap ‐dump:format=b,file=/home/进程名称加上进程ID组合.hprof pid
```

最后使用``MAT``或者``JVisualVM``工具导入堆文件进行具体分析。

### CPU飙高排查

使用上述的过程中的命令找到进程PID然后通过``top``命令找到具体那个线程导致的CPU飙高
```bash
# 找到进程PID
ps -ef | grep 进程名 | grep -v grep | awk '{print $2}'

# 使用top命令查看线程ID
top -H -p {进程ID}

# 转换线程ID为16进制便于后续搜索
printf '%x' {线程ID}

# 使用jstack命令导出文件
jstack {进程ID} > {项目名称_进程ID}.txt

# 使用sz命令下载文件
sz {项目名称_进程ID}.txt

# 最后使用编辑器搜索线程ID找到具体的原因
```

### arthas排查问题

使用``arthas``比较简单，本项目直接内置通过Web console来进行调试。

```bash
# 仪表板命令
# 通过仪表板指令可以直观的看到当前应用内存占用情况以及线程执行情况等各项指标
dashboard

# 内存命令
# 通过内存指令可以查看当前应用内存占用情况
memory

# 线程命令
# 展示当前最忙的3个线程
thread -n 3

# 跟踪命令
# 定位方法内部代码块性能耗时情况
trace 包名加类名 方法名

# 导出堆命令
# 导出当前应用堆信息到指定文件
heapdump /tmp/应用名加进程号.hprof
```

