package test.mock;

import com.taobao.garuda.tools.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoaderJobMock {

	public void mockJob(ZkClient zkClient, String db, String table, String partition) throws Exception {
		HashMap<String, Object> mockjob = new HashMap<String, Object>();
		mockjob.put("startTime", System.currentTimeMillis());
		mockjob.put("loaderType", (short)1);
		mockjob.put("loaderType", (short)1);
		mockjob.put("loaderServer", "0_0_0_0_14000");
		HashMap<String, Object> pkList = new HashMap<String, Object>();
		pkList.put("0", "");
		mockjob.put("pkList", pkList);
		HashMap<String, Object> jobKey = new HashMap<String, Object>();
		String[] ss = {table + "__" + partition + "__0"};
		jobKey.put(db, ss);
		mockjob.put("jobKey", jobKey);
		mockjob.put("state", (short) 3);
		mockjob.put("parentSequenceId", 278704842992521216L);
		HashMap<String, Object> loadedInfo = new HashMap<String, Object>();
		String[] sss = {table + "__0"};
		loadedInfo.put(db, sss);
		mockjob.put("loadedInfo", loadedInfo);
		mockjob.put("sequenceId", 278705011637096448L);
		HashMap<String, Object> options = new HashMap<String, Object>();
		mockjob.put("options", options);
		zkClient.put("/global/loader/job/loader", mockjob, CreateMode.PERSISTENT_SEQUENTIAL);
		
		mockPartition(zkClient, db, table, "0", partition);
	}

	private void mockPartition(ZkClient zkClient, String db, String table, String partition, String partitionNum) throws Exception {
		HashMap<String, Object> tableMap = zkClient.get("/global/metainfo/db/" + db + "/tables/" + table, HashMap.class);
		List<HashMap<String, Object>> partitionInfo = (List<HashMap<String, Object>>) tableMap.get("partitionTableInfo");
		if(partitionInfo == null) {
			partitionInfo = new ArrayList<HashMap<String, Object>>();
			tableMap.put("partitionTableInfo", partitionInfo);
		}
		for(HashMap<String, Object> map : partitionInfo) {
			if(map.get("tableName").toString().equals(table + "__" + partition) && map.get("partitionNum").toString().equals(partitionNum)) {
				return;
			}
		}
		HashMap<String, Object> mockPartition = new HashMap<String, Object>();
		mockPartition.put("tableName", table + "__" + partition);
		mockPartition.put("partitionNum", partitionNum);
		mockPartition.put("start", Long.parseLong(partition));
		mockPartition.put("end", Long.parseLong(partition));
		mockPartition.put("dataSize", 100L);
		mockPartition.put("memSize", 100L);
		mockPartition.put("recordLength", 100L);
		mockPartition.put("status", 10000000);
		mockPartition.put("createTime", System.currentTimeMillis());
		mockPartition.put("updateTime", System.currentTimeMillis());
		mockPartition.put("version", 1);
		partitionInfo.add(mockPartition);
		zkClient.put("/global/metainfo/db/" + db + "/tables/" + table, tableMap);
	}
}
