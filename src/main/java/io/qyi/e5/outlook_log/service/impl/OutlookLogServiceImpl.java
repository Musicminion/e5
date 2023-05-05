package io.qyi.e5.outlook_log.service.impl;


import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;

import com.influxdb.query.FluxTable;
import io.qyi.e5.outlook_log.entity.OutlookLog;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 落叶
 * @since 2020-03-03
 */
@Service
public class OutlookLogServiceImpl implements IOutlookLogService {

    @Resource
    private InfluxDBClient influxDBClient;

    @Value("${spring.influx.org}")
    private String org;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Override
    public void addLog(int githubId, int outlookId, String msg, int result, String original_msg) {
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            OutlookLog log = new OutlookLog();
            log.setCallTime(System.currentTimeMillis())
                    .setGithubId(String.valueOf(githubId))
                    .setOutlookId(String.valueOf(outlookId))
                    .setMsg(msg)
                    .setOriginalMsg(original_msg)
                    .setResult(result);
            writeApi.writeMeasurement(bucket, org, WritePrecision.NS, log);

        }

    }

    @Override
    public List<OutlookLog> findAllList(int githubId, int outlookId) {
        String flux = "from(bucket:\"" + bucket + "\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"OutlookLog\")" +
                "|> filter(fn: (r) => r[\"githubId\"] == \"" + githubId + "\")" +
                "|> filter(fn: (r) => r[\"outlookId\"] == \"" + outlookId + "\")" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<OutlookLog> tables = queryApi.query(flux, org, OutlookLog.class);
        return tables;
    }


    @Override
    public List<OutlookLog> findListByPage(int githubId, int outlookId, int page, int pageSize) {
        // 检查page参数是否合法
        if (page < 1) {
            page = 1;
        }

        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        String flux = "from(bucket:\"" + bucket + "\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"OutlookLog\")" +
                "|> filter(fn: (r) => r[\"githubId\"] == \"" + githubId + "\")" +
                "|> filter(fn: (r) => r[\"outlookId\"] == \"" + outlookId + "\")" +
                "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                "|> limit(n:" + pageSize + ", offset: " + (page - 1) * pageSize + ")";

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<OutlookLog> tables = queryApi.query(flux, org, OutlookLog.class);
        return tables;
    }



    // findListByPage
    @Override
    public int findPagesNum(int githubId, int outlookId, int pageSize){
        // 查询githubId下、outlookId下的所有数据一共有多少条
        String flux = "from(bucket:\"" + bucket + "\") |> range(start: 0)" +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"OutlookLog\")" +
                "|> filter(fn: (r) => r[\"githubId\"] == \"" + githubId + "\")" +
                "|> filter(fn: (r) => r[\"outlookId\"] == \"" + outlookId + "\")" +
                "|> count()";
        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux, org);
        long count = (long) tables.get(0).getRecords().get(0).getValueByKey("_value");
    
        // 计算总页数
        return (int) Math.ceil((double) count / pageSize);
    }
}
