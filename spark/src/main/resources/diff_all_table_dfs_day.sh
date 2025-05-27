#!/bin/bash
<<EOF
    该脚本功能：
    对比所有文件差异，发送告警通知，
    比对结果输出到HDFS上，每次从HDFS上拉取文件对比
EOF

# 声明表
declare -a arr

# home 目录
home_path="/home/liqianding"

# kb 认证
kinit -kt /tmp/xquant.keytab xquant

# 初始化
current_date=$(date "+%Y%m%d")
# 操作基目录
diff_path="/home/liqianding/hdfs_infos/tradingDay/diff"
# 创建相应的目录结构
if [ ! -d $diff_path ]; then
    mkdir -p $diff_path
fi

# 本次操作目录
current_path="$diff_path/$current_date"
if [ ! -d $current_path ]; then
    mkdir -p $current_path
else
    # 存在，则先删除再创建
    rm -rf $current_path
    mkdir -p $current_path
fi

# 获取上次执行日期
last_date=$(date "+%Y%m%d" -d '1 day ago')
# 上次的操作目录
last_path="$diff_path/$last_date"

if [ ! -d $last_path ]; then
    mkdir -p $last_path
else
    # 存在，则先删除再创建
    rm -rf $last_path
    mkdir -p $last_path
fi

# 将有更新记录的表放到该数组中
diff_array=()

##### 获取所有 Day 数据类型
fetch_tbls(){
    # 获取所有的 Day 数据，并将其结果输出到 txt 文件中，注意每次都要覆盖写入
    hdfs --config $conf_path dfs -ls /htdata/mdc/MDCProvider/ | grep -E *_Day$ | awk '{split($NF, parts, "/"); print parts[length(parts)]}' > ${home_path}/t.txt

    # 编辑 txt 文件，去掉 _Day 字符
    sed -i 's/_Day$//' ${home_path}/t.txt

    # 编辑 txt 文件，去掉 diff监控脚本中 隔天推送的数据：比如美股、境外期货等，该数据直接从 hdfs 上拉取，这个数据是每次有更新时直接本地手动更新编辑后再推送到 hdfs 上即可。注意每次都要覆盖写入
    hdfs --config $conf_path dfs -get -f /htdata/mdc/t-1.txt ${home_path}/t-1.txt

    # 从 txt 文件中去掉 t-1.txt 中的内容，65320461 是一个临时的随机值，无意义
    grep -vFf ${home_path}/t-1.txt ${home_path}/t.txt | awk NF > ${home_path}/65320461.txt && mv ${home_path}/65320461.txt ${home_path}/t.txt

    # 初始化 arr
    readarray -t arr < ${home_path}/t.txt
}

##### 创建 hdfs 路径，hdfs --config $conf_path dfs -mkdir /htdata/mdc/diff/$current_date.txt
mkdir_hdfs(){
    # 判断 hdfs 路径是否存在
    echo "判断 hdfs 路径是否存在：/htdata/mdc/diff/$current_date/"
    hadoop --config $conf_path fs -test -e /htdata/mdc/diff/$current_date
    if [[ "$?" -eq "0" ]]; then
        # 若存在
        echo "/htdata/mdc/diff/$current_date 路径已存在，则先删除再创建"
        hdfs --config $conf_path dfs -rm -r /htdata/mdc/diff/$current_date
        hdfs --config $conf_path dfs -mkdir -p /htdata/mdc/diff/$current_date
    else
        echo "/htdata/mdc/diff/$current_date 路径不存在，开始创建"
        hdfs --config $conf_path dfs -mkdir -p /htdata/mdc/diff/$current_date
    fi
}

#####前置：删除30天前的linux上文件
del_linux_history_file(){
    # 删除该目录下前30天的文件夹
    echo "开始删除 linux $diff_path 目录下前30天的文件夹"
    min_date=$(date "+%Y%m%d -d '30 day ago'")
    ls -l $diff_path | grep -v total | awk '{print $NF}' | while read n
    do
        # 删除30天前的数据
        if [[ "$n" -lt "$min_date" ]]; then
            echo "Starting del $diff_path/$n"
            rm -rf $diff_path/$n
        fi
    done
}

#####前置：删除30天前的hdfs文件
del_hdfs_history_file(){
    echo "准备删除 HDFS 上 /htdata/mdc/diff/ 目录下前30天的数据"
    min_del_date=$(date "+%Y%m%d" -d '30 day ago')
    hdfs --config $conf_path dfs -ls /htdata/mdc/diff | awk '{print $NF}' | grep -v items | grep -v time--- | awk -F/ '{print $NF}' | while read num
    do
        # 删除30天前的数据
        if [[ "$num" -lt "${min_del_date}" ]]; then
            echo "Starting hdfs dfs -rm -r /htdata/mdc/diff/$num"
            hdfs --config $conf_path dfs -rm -r /htdata/mdc/diff/$num
        fi
    done
}

#####拉取所有表的 xx_Day 信息到本地
pull_current_date_hdfs_day(){
    echo "开始拉取所有表的 xx_Day 信息到本地"
    for i in "${!arr[@]}"; do
        hdfs --config $conf_path dfs -ls /htdata/mdc/MDCProvider/${arr[$i]}"_"Day >>$current_path/$current_date.txt
    done
    # 删除 found
    sed -i '/Found/d' $current_path/$current_date.txt
    # Spark3.x 的集群会输出 time--- 的字样，因此需要删除
    sed -i '/time---/d' $current_path/$current_date.txt
}

#####上传本次文件到 hdfs
push_hdfs(){
    echo "开始上传本次文件到hdfs相应的目录下"
    hdfs --config $conf_path dfs -put $current_path/$current_date.txt /htdata/mdc/diff/$current_date/
}

#####拉取上次的tradingday目录
filter_current_week_day(){
    echo "第二部分：开始拉取上周的tradingday目录"
    while read line; do
        s1=$line
        # 源数据有的day文件是不规范的，需要剔除
        reg_str="tradingday"
        if [[ $s1 == *$reg_str* ]]; then
            s2=${s1#*tradingday=}
            if [[ "$s2" != "" ]]; then
                if [ $((s2)) -lt $((last_date)) ]; then
                    # 过滤掉本周新增的内容，追加到临时文件
                    echo $s1 >>$current_path/temp.txt
                fi
            fi
        fi
    done <$current_path/$current_date.txt
}

#####从HDFS上拉取上次执行的结果到本地
pull_last_from_hdfs(){
    echo "从HDFS上拉取上次执行的结果到本地"
    # 先判断 hdfs 上，上次文件是否存在
    hadoop --config $conf_path fs -test -e /htdata/mdc/diff/$last_date/$last_date.txt
    if [[ "$?" -eq "0" ]]; then
        hdfs --config $conf_path dfs -get /htdata/mdc/diff/$last_date/$last_date.txt $last_path/
    else
        echo "第一次执行脚本"
        exit 0
    fi
}

#####比较 temp.txt 和 $last_path/${arr[$i]}/$last_date.txt 的文件差异
compare_file(){
    echo "第三部分：比较本次过滤后的 temp.txt 和 $last_date.txt 的文件差异"
    # Spark3.x 集群会输出一行 time---类似的内容，需要删除掉
    sed -i '/time---/d' $last_path/$last_date.txt
    diff -b $current_path/temp.txt $last_path/$last_date.txt >$current_path/diff.txt
    # 因为 在 diff.txt 文件中，第一行是输入对象的内容，这里不需要，故删除第一行
    sed -i '/Found/d' $current_path/diff.txt
    # Spark3.x 集群会输出一行 time---类似的内容，需要删除掉
    sed -i '/time---/d' $current_path/diff.txt
    # 将该结果上传到 hdfs 用于回溯
    echo "将diff.txt上传到hdfs保存"
    hdfs --config $conf_path dfs -put $current_path/diff.txt /htdata/mdc/diff/$current_date/
}

#####判断diff.txt文件是否为空
is_empty(){
    echo "第四部分：判断diff.txt文件是否为空"
    while read line; do
        if [[ $line != "" ]]; then
            diff_array[0]=$current_path/diff.txt
        fi
    done <$current_path/diff.txt
}

#####判断数组是否为空
echo_result(){
    if [ ${#diff_array[*]} -gt 0 ]; then
        echo "有告警"
        exit 1
    else
        echo "无告警"
        exit 0
    fi
}

#####获取当前脚本的执行路径
work_dir=$(
    cd $(dirname $0)
    pwd
)

# 获取 hdfs config 配置文件的路径
conf_path="/home/appadmin/transfer/distconf"
# 删除30天前的linux文件
del_linux_history_file
# 删除30天前的hdfs文件
del_hdfs_history_file
# 创建目录
mkdir_hdfs
# 拉取数据
fetch_tbls
# 执行后续操作
pull_current_date_hdfs_day
push_hdfs
filter_current_week_day
pull_last_from_hdfs
compare_file
is_empty