#!/bin/bash

# ==== 参数配置 ====
kinit_user="xquant"
conf_path="/home/appadmin/transfer/distconf"
home_path="/home/liqianding"
base_path="$home_path/hdfs_infos/tradingDay/diff"
history_path="$base_path/history"
current_path="$base_path/current"
retention_days=30

# ==== 日期 ====
current_date=$(date "+%Y%m%d")
last_date=$(date -d "1 day ago" "+%Y%m%d")
cut_date=$(date -d "$retention_days days ago" "+%Y%m%d")

# ==== 初始化目录 ====
mkdir -p "$current_path"
mkdir -p "$history_path/$current_date"

# ==== 函数：Kerberos 登录 ====
kinit_auth() {
    echo "正在进行 kinit 验证：$kinit_user"
    echo "" | kinit "$kinit_user" >/dev/null 2>&1
    if [ $? -ne 0 ]; then
        echo "kinit 验证失败，脚本退出"
        exit 1
    fi
}

# ==== 函数：获取动态表名并写入 arr ====
get_day_tables() {
    echo "从 HDFS 中提取 _Day 表名并筛选差集生成最终表列表"

    t_file="$home_path/t.txt"
    t1_file="$home_path/t-1.txt"

    # 获取所有 *_Day 表（去掉 _Day 后缀）
    hdfs --config "$conf_path" dfs -ls /htdata/mdc/MDCProvider/ | \
        grep -E '_Day$' | awk -F/ '{print $NF}' | sed 's/_Day$//' | sort -u > "$t_file"

    # 拉取 t-1.txt 作为过滤条件（人工维护，排除不监控的）
    hdfs --config "$conf_path" dfs -get -f /htdata/mdc/t-1.txt "$t1_file"

    # 差集处理：排除 t-1.txt 中的表名
    mapfile -t arr < <(grep -vFf "$t1_file" "$t_file")

    echo "最终对比表数：${#arr[@]}"
}

# ==== 函数：拉取当前文件列表 ====
pull_current_date_hdfs_day() {
    echo "拉取当前 HDFS Day 表文件列表（含时间戳、大小）"
    > "$current_path/$current_date.txt"
    for tbl in "${arr[@]}"; do
        hdfs --config "$conf_path" dfs -ls -R "/htdata/mdc/MDCProvider/${tbl}_Day" 2>/dev/null | \
            awk '{print $6, $7, $5, $NF}' >> "$current_path/$current_date.txt"
    done
}

# ==== 函数：从 HDFS 拉取上一天的数据文件 ====
pull_last_date_file() {
    echo "尝试拉取上一日文件清单：$last_date.txt"
    hdfs --config "$conf_path" dfs -get "/htdata/mdc/day_file_list/${last_date}.txt" "$history_path/$last_date.txt"
    if [ $? -ne 0 ]; then
        echo "未找到 HDFS 上的 $last_date.txt，可能为首次执行"
        touch "$history_path/$last_date.txt"
    fi
}

# ==== 函数：上传当前文件清单到 HDFS ====
upload_current_file() {
    echo "上传当前文件清单到 HDFS"
    hdfs --config "$conf_path" dfs -mkdir -p "/htdata/mdc/day_file_list"
    hdfs --config "$conf_path" dfs -put -f "$current_path/$current_date.txt" "/htdata/mdc/day_file_list/${current_date}.txt"
}

# ==== 函数：比较文件差异 ====
compare_file() {
    echo "比较 $current_date 与 $last_date 的文件差异"
    sort "$current_path/$current_date.txt" > "$current_path/sorted_current.txt"
    sort "$history_path/$last_date.txt" > "$current_path/sorted_last.txt"

    diff -u "$current_path/sorted_last.txt" "$current_path/sorted_current.txt" > "$current_path/diff.txt"

    if [ -s "$current_path/diff.txt" ]; then
        echo "检测到变更，上传差异文件到 HDFS"
        hdfs --config "$conf_path" dfs -mkdir -p "/htdata/mdc/day_diff"
        hdfs --config "$conf_path" dfs -put -f "$current_path/diff.txt" "/htdata/mdc/day_diff/${current_date}_diff.txt"
    else
        echo "未检测到差异"
    fi
}

# ==== 函数：清理 N 天前的旧文件 ====
clean_old_file() {
    echo "清理 HDFS 和本地 $retention_days 天前的文件：$cut_date"

    hdfs --config "$conf_path" dfs -rm -f "/htdata/mdc/day_file_list/${cut_date}.txt"
    hdfs --config "$conf_path" dfs -rm -f "/htdata/mdc/day_diff/${cut_date}_diff.txt"

    rm -f "$history_path/${cut_date}.txt"
}

# ==== 主流程 ====
main() {
    kinit_auth
    get_day_tables
    pull_current_date_hdfs_day
    pull_last_date_file
    compare_file
    upload_current_file
    clean_old_file
    echo "完成：$current_date"
}

main