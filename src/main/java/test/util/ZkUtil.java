package test.util;

import com.taobao.garuda.tools.ZkClient;

import java.util.*;

public class ZkUtil {
	
	/**
	 * 清除table的partitionTable
	 * @param client
	 * @param db
	 * @param table
	 * @throws Exception
	 */
	public static void clearPartitionTable(ZkClient client, String db, String table) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/metainfo/db/" + db + "/tables/" + table, HashMap.class);
		tableInfo.put("partitionTableInfo", new ArrayList<HashMap<String, Object>>());
//		ArrayList<HashMap<String, Object>> partitionTable = (ArrayList<HashMap<String, Object>>) tableInfo.get("partitionTableInfo");
//		int idx = 0;
//		for(HashMap<String, Object> partition : partitionTable) {
//			if(((Long) partition.get("start")) == 20121220) {
//				partitionTable.remove(idx);
//			}
//			idx ++;
//		}
		client.put("/global/metainfo/db/" + db + "/tables/" + table, tableInfo);
	}
	
	private static void generateAssignProcess(ZkClient client) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> assignProcesses = new ArrayList<HashMap<String, Object>>();
		map.put("assignProcesses", assignProcesses);

		HashMap<String, Object> process = new HashMap<String, Object>();
		process.put("name", "machineGroupProcess");
		HashMap<String, Object> configedGroupStrategy = new HashMap<String, Object>(1);
		configedGroupStrategy.put("name", "configedGroupStrategy");
		process.put("localnodeGroupStrategy", configedGroupStrategy);
		HashMap<String, Object> averageResourceStrategy = new HashMap<String, Object>(1);
		averageResourceStrategy.put("name", "averageResourceStrategy");
		process.put("resourceBlockStrategy", averageResourceStrategy);
		HashMap<String, Object> averageAssignStrategy = new HashMap<String, Object>(1);
		averageAssignStrategy.put("name", "averageAssignStrategy");
		process.put("assignStrategy", averageAssignStrategy);
		assignProcesses.add(process);

		process = new HashMap<String, Object>();
		process.put("name", "resourceIsolateProcess");
		HashMap<String, Object> simple = new HashMap<String, Object>(1);
		simple.put("name", "simpleGroupStrategy");
		simple.put("mode", "single");
		process.put("localnodeGroupStrategy", simple);
		HashMap<String, Object> configedDbResourceStrategy = new HashMap<String, Object>(1);
		configedDbResourceStrategy.put("name", "configedDbResourceStrategy");
		configedDbResourceStrategy.put("dbConfigPath", "/monitor/dbConfig");
		process.put("resourceBlockStrategy", configedDbResourceStrategy);
		HashMap<String, Object> averageNodeStrategy = new HashMap<String, Object>(1);
		averageNodeStrategy.put("name", "averageLocalnodesStrategy");
		process.put("assignStrategy", averageNodeStrategy);
		assignProcesses.add(process);
		
		process = new HashMap<String, Object>();
		process.put("name", "resourceBalanceProcess");
		HashMap<String, Object> simpleGroupStrategy = new HashMap<String, Object>(1);
		simpleGroupStrategy.put("name", "simpleGroupStrategy");
		simpleGroupStrategy.put("mode", "single");
		process.put("localnodeGroupStrategy", simpleGroupStrategy);
		HashMap<String, Object> simpleResourceStrategy = new HashMap<String, Object>(1);
		simpleResourceStrategy.put("name", "simpleResourceStrategy");
		simpleResourceStrategy.put("mode", "single");
		process.put("resourceBlockStrategy", simpleResourceStrategy);
		HashMap<String, Object> averageLeftMemStrategy = new HashMap<String, Object>(1);
		averageLeftMemStrategy.put("name", "averageLeftMemStrategy");
		process.put("assignStrategy", averageLeftMemStrategy);
		assignProcesses.add(process);

		client.put("/monitor/assignConfig", map);
	}

	/**
	 * 清除导入任务
	 * @param client
	 * @throws Exception
	 */
	public static void clearLoaderJob(ZkClient client) throws Exception {
		List<String> childrens = client.getChildrenKeys("/global/loader/job");
		Collections.sort(childrens);
		for (String s : childrens) {
//			System.out.println(s);
			try {
				HashMap<String, Object> job = client.get("/global/loader/job/" + s, HashMap.class);
//				System.out.println(job.get("options"));
//				System.out.println(job.get("loaderServer"));
				client.delete("/global/loader/job/" + s);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 所有导入任务置为1
	 * @param client
	 * @param loader
	 * @throws Exception
	 */
	public static void resetLoaderJob(ZkClient client, String loader) throws Exception {
		HashMap<String, Object> job = client.get("/global/loader/job/" + loader, HashMap.class);
		job.put("state", (short) 1);
		client.put("/global/loader/job/" + loader, job);
	}

	/**
	 * 清除导入任务队列（monitor用）
	 * @param client
	 * @throws Exception
	 */
	public static void clearLoadRequestQueue(ZkClient client) throws Exception {
		List<String> childrens = client.getChildrenKeys("/monitor/loadquestqueue");
		for (String s : childrens) {
			System.out.println(s);
			try {
				System.out.println(client.get("/monitor/loadquestqueue/" + s, HashMap.class));
				client.delete("/monitor/loadquestqueue/" + s);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 更改table  meta的第一层属性
	 * @param client
	 * @param db
	 * @param table
	 * @param property
	 * @param value
	 * @throws Exception
	 */
	public static void changeFirstLevelTableInfo(ZkClient client, String db, String table, String property, Object value) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/metainfo/db/" + db + "/tables/" + table, HashMap.class);
//		tableInfo.remove("currentPartitionNum");
		tableInfo.put(property, value);
		client.put("/global/metainfo/db/" + db + "/tables/" + table, tableInfo);
	}

	/**
	 * 更改table  meta的第二层属性（慎用，不确保正确性）
	 * @param client
	 * @param db
	 * @param table
	 * @param firstLevel
	 * @param property
	 * @param value
	 * @throws Exception
	 */
	public static void changeSecondLevelTableInfo(ZkClient client, String db, String table, String firstLevel, String property, Object value) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/metainfo/db/" + db + "/tables/" + table, HashMap.class);
		Object secondLevel = tableInfo.get(firstLevel);
		if (!(secondLevel instanceof HashMap)) {
			System.out.println("can't process arraylist");
			return;
		}
		HashMap<String, Object> second = (HashMap<String, Object>) secondLevel;
		second.put(property, value);
		// tableInfo.put(property, value);
		client.put("/global/metainfo/db/" + db + "/tables/" + table, tableInfo);
	}

	/**
	 * 删除ZK配置及其子节点
	 * @param client
	 * @param parent
	 * @throws Exception
	 */
	public static void deleteConfig(ZkClient client, String parent) throws Exception {
		System.out.println(parent);
		List<String> childrens = client.getChildrenKeys(parent);
		for (String children : childrens) {
			deleteConfig(client, parent + "/" + children);
		}
		client.delete(parent);
	}

	/**
	 * 拷贝一个节点的数据至另一个节点（未判断节点属性，比如临时节点会被copy为永久节点）
	 * @param client
	 * @param parentSrc
	 * @param parentDest
	 * @throws Exception
	 */
	public static void copyConfig(ZkClient client, String parentSrc, String parentDest) throws Exception {
		try {
			client.put(parentDest, client.get(parentSrc, HashMap.class));
		} catch (Exception e) {
			try {
				client.put(parentDest, client.get(parentSrc, ArrayList.class));
			} catch (Exception e1) {
				client.put(parentDest, client.get(parentSrc, Object.class));
			}
		}
		List<String> childrens = client.getChildrenKeys(parentSrc);
		for (String children : childrens) {
			copyConfig(client, parentSrc + "/" + children, parentDest + "/" + children);
		}
	}

	/**
	 * 增加列（与addLoaderColumn一起使用）
	 * @param client
	 * @param db
	 * @param table
	 * @param columnName
	 * @param type
	 * @param comment
	 * @param indexType
	 * @throws Exception
	 */
	public static void addColumn(ZkClient client, String db, String table, String columnName, ColumnType type, String comment, String indexType) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/metainfo/db/" + db + "/tables/" + table, HashMap.class);
		List<HashMap<String, Object>> columnInfo = (List<HashMap<String, Object>>) tableInfo.get("columnInfo");
		HashMap<String, Object> column = new HashMap<String, Object>();
		column.put("id", columnInfo.size());
		column.put("name", columnName);
		column.put("type", type.getValue());
		column.put("primaryKey", false);
		column.put("nullable", false);
		column.put("defaultExpression", "");
		column.put("autoIncrement", false);
		column.put("start", (long) 0);
		column.put("increment", 1);
		column.put("comment", "");
		columnInfo.add(column);
		if (indexType != null) {
			List<HashMap<String, Object>> indexesInfo = (List<HashMap<String, Object>>) tableInfo.get("indexesInfo");
			indexesInfo.add(new IndexesInfo(columnName).setType(Enum.valueOf(IndexType.class, indexType).getValue()).setDirection((short) 0).setColumnName(columnName).build());
		}
		client.put("/global/metainfo/db/" + db + "/tables/" + table, tableInfo);
	}

	/**
	 * 删除列
	 * @param client
	 * @param db
	 * @param table
	 * @param columnName
	 * @throws Exception
	 */
	public static void deleteColumn(ZkClient client, String db, String table, String columnName) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/metainfo/db/" + db + "/tables/" + table, HashMap.class);
		List<HashMap<String, Object>> columnInfo = (List<HashMap<String, Object>>) tableInfo.get("columnInfo");
		for (int i = 0; i < columnInfo.size(); i++) {
			HashMap<String, Object> column = columnInfo.get(i);
			if (((String) column.get("name")).equalsIgnoreCase(columnName)) {
				columnInfo.remove(i);
				break;
			}
		}
		List<HashMap<String, Object>> indexesInfo = (List<HashMap<String, Object>>) tableInfo.get("indexesInfo");
		for (int i = 0; i < indexesInfo.size(); i++) {
			HashMap<String, Object> index = indexesInfo.get(i);
			if (((String) index.get("columnName")).equalsIgnoreCase(columnName)) {
				indexesInfo.remove(i);
				break;
			}
		}
		client.put("/global/metainfo/db/" + db + "/tables/" + table, tableInfo);
	}

	/**
	 * 增加导入配置中的列（与addColumn一起使用）
	 * @param client
	 * @param originalTable
	 * @param originalName
	 * @param columnName
	 * @throws Exception
	 */
	public static void addLoaderColumn(ZkClient client, String originalTable, String originalName, String columnName) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/loader/schema/" + originalTable, HashMap.class);
		HashMap<String, Object> columnMap = (HashMap<String, Object>) tableInfo.get("newColumnMap");
		if (columnMap != null) {
			List<HashMap<String, String>> tmp = new ArrayList<HashMap<String, String>>(1);
			HashMap<String, String> row = new HashMap<String, String>(1);
			row.put(originalName, columnName);
			tmp.add(row);
			columnMap.put(originalName, tmp);
		} else {
			columnMap = (HashMap<String, Object>) tableInfo.get("columnMap");
			HashMap<String, String> tmp = new HashMap<String, String>(1);
			tmp.put(originalName, columnName);
			columnMap.put(originalName, tmp);
		}
		client.put("/global/loader/schema/" + originalTable, tableInfo);
	}

	public static void deleteLoaderColumn(ZkClient client, String originalTable, String columnName) throws Exception {
		HashMap<String, Object> tableInfo = client.get("/global/loader/schema/" + originalTable, HashMap.class);
		HashMap<String, Object> columnMap = (HashMap<String, Object>) tableInfo.get("columnMap");
		// System.out.println(columnMap);
		columnMap.remove(columnName);
		client.put("/global/loader/schema/" + originalTable, tableInfo);
	}

	/**
	 * 表组配置中增加表
	 * @param client
	 * @param dbName
	 * @param groupName
	 * @param tableName
	 * @throws Exception
	 */
	public static void addTableGroup(ZkClient client, String dbName, String groupName, String tableName) throws Exception {
		Map<String, List<String>> tablegroups = (Map<String, List<String>>) client.get("/global/metainfo/db/" + dbName + "/tablegroupsInfo", HashMap.class);
		if (tablegroups == null) {
			tablegroups = new HashMap<String, List<String>>();
		}
		List<String> group = tablegroups.get(groupName);
		if (group == null) {
			group = new ArrayList<String>();
			tablegroups.put(groupName, group);
		}
		if (group.contains(tableName)) {
			return;
		}
		group.add(tableName);
		client.put("/global/metainfo/db/" + dbName + "/tablegroupsInfo", tablegroups);
	}

	/**
	 * 删除表组
	 * @param client
	 * @param dbName
	 * @param groupName
	 * @throws Exception
	 */
	public static void deleteTableGroup(ZkClient client, String dbName, String groupName) throws Exception {
		Map<String, List<String>> tablegroups = (Map<String, List<String>>) client.get("/global/metainfo/db/" + dbName + "/tablegroupsInfo", HashMap.class);
		if (tablegroups == null) {
			System.out.println("tablegroups is null");
			return;
		}
		tablegroups.remove(groupName);
		client.put("/global/metainfo/db/" + dbName + "/tablegroupsInfo", tablegroups);
	}
	
	public static String getActiveMonitor(ZkClient client) throws Exception {
		String monitorService = "/monitor/services";
		List<String> monitors = client.getChildrenKeys(monitorService);
		for(String monitor : monitors) {
			HashMap<String, Object> monitorMap = client.get(monitorService + "/" + monitor, HashMap.class);
			boolean master = (Boolean) monitorMap.get("master");
			if( ! master) {
				continue;
			}
			String ip = (String) monitorMap.get("ip");
			int port = (Integer) monitorMap.get("port");
			return ip + ":" + port;
		}
		return null;
	}

    /**
     * 获取当前的Monitor IP地址 字符串
     * @param client
     * @return
     * @throws Exception
     */
	public static String getActiveMonitorIp(ZkClient client) throws Exception {
		String monitorService = "/monitor/services";
		List<String> monitors = client.getChildrenKeys(monitorService);
		for(String monitor : monitors) {
			HashMap<String, Object> monitorMap = client.get(monitorService + "/" + monitor, HashMap.class);
			boolean master = (Boolean) monitorMap.get("master");
			if( ! master) {
				continue;
			}
			String ip = (String) monitorMap.get("ip");
			int port = (Integer) monitorMap.get("port");
			return ip;
		}
		return null;
	}

	public static List<HashMap<String, Object>> getAllLoaders(ZkClient client) throws Exception {
		String loaderPathPrefix = "/global/loader/job";
		List<HashMap<String, Object>> loaders = new ArrayList<HashMap<String, Object>>();
		List<String> loaderPaths = client.getChildrenKeys(loaderPathPrefix);
		for(String loaderPath : loaderPaths) {
			HashMap map = client.get(loaderPathPrefix + "/" + loaderPath, HashMap.class);
			loaders.add(map);
		}
		return loaders;
	}

    /**
     * 传入ZK客户端获取获取所有Monitor节点的IP:Port 字符串信息
     * @param client
     * @return
     * @throws Exception
     */
	public static String[] getAllMonitors(ZkClient client) throws Exception {
		String monitorService = "/monitor/services";
		List<String> monitorList = client.getChildrenKeys(monitorService);
		String[] monitors = new String[monitorList.size()];
		for(int i = 0;i < monitorList.size();i ++) {
			HashMap<String, Object> monitorMap = client.get(monitorService + "/" + monitorList.get(i), HashMap.class);
			String ip = (String) monitorMap.get("ip");
			monitors[i] = ip;
		}
		return monitors;
	}
	
	public static List<HashMap<String, Object>> getInitPlans(ZkClient client) throws Exception {
		String planPrefix = "/monitor/plan";
		List<String> planList = client.getChildrenKeys(planPrefix);
		List<HashMap<String, Object>> plans = new ArrayList<HashMap<String, Object>>();
		for(String str : planList) {
			HashMap<String, Object> map = client.get(planPrefix + "/" + str + "/plan", HashMap.class);
			Short state = (Short) map.get("state");
			if(state != null && state == 0) {
				plans.add(map);
			}
		}
		return plans;
	}

	public static HashMap<String, List<String>> getGroupMap(ZkClient zkClient, String[] localnodes) throws Exception {
		HashMap<String, String> localnodeGroup = zkClient.get("/global/monitor/localnodegroup", HashMap.class);
		HashMap<String, List<String>> groupMap = new HashMap<String, List<String>>();
		for(String localnode : localnodes) {
			String ip = ClusterUtil.extractIpFromNodeName(localnode);
			String group = localnodeGroup.get(ip);
			if(group == null) {
				throw new Exception("localnode: " + localnode + "  does not exist in /global/monitor/localnodegroup");
			}
			List<String> list = groupMap.get(group);
			if(list == null) {
				list = new ArrayList<String>();
				groupMap.put(group, list);
			}
			list.add(localnode);
		}
		return groupMap;
	}

	public static HashMap<String, Object> getLnmg(ZkClient client, String localnode) throws Exception {
		String lnmgPrefix = "/lnmg/service";
		return client.get(lnmgPrefix + "/" + localnode, HashMap.class);
	}

	public static long getCurrentPartition(ZkClient client, String db, String table) throws Exception {
		List<String> groups = client.getChildrenKeys("/global/metainfo/db/" + db + "/tablegroups");
		for(String group : groups) {
			HashMap<String, Object> currents = client.get("/global/metainfo/db/" + db + "/tablegroups/" + group, HashMap.class);
			HashMap<String, Object> partitions = (HashMap<String, Object>) currents.get("currentPartitionNum");
			Long partition = (Long) partitions.get(table);
			if(partition != null) {
				return partition;
			}
		}
		return -1;
	}
	
	public static long getPrevPartition(ZkClient client, String db, String table) throws Exception {
		List<String> groups = client.getChildrenKeys("/global/metainfo/db/" + db + "/tablegroups");
		for(String group : groups) {
			HashMap<String, Object> currents = client.get("/global/metainfo/db/" + db + "/tablegroups/" + group, HashMap.class);
			HashMap<String, Object> partitions = (HashMap<String, Object>) currents.get("prevPartitionNum");
			Long partition = (Long) partitions.get(table);
			if(partition != null) {
				return partition;
			}
		}
		return -1;
	}

	public static void setCurrentPartition(ZkClient client, String db, String table, long onlinePartition) throws Exception {
		List<String> groups = client.getChildrenKeys("/global/metainfo/db/" + db + "/tablegroups");
		for(String group : groups) {
			HashMap<String, Object> currents = client.get("/global/metainfo/db/" + db + "/tablegroups/" + group, HashMap.class);
			HashMap<String, Object> partitions = (HashMap<String, Object>) currents.get("currentPartitionNum");
			Long partition = (Long) partitions.get(table);
			if(partition != null) {
				partitions.put(table, onlinePartition);
				client.put("/global/metainfo/db/" + db + "/tablegroups/" + group, currents);
				return;
			}
		}
	}
	
	public static void setPrevPartition(ZkClient client, String db, String table, long onlinePartition) throws Exception {
		List<String> groups = client.getChildrenKeys("/global/metainfo/db/" + db + "/tablegroups");
		for(String group : groups) {
			HashMap<String, Object> currents = client.get("/global/metainfo/db/" + db + "/tablegroups/" + group, HashMap.class);
			HashMap<String, Object> partitions = (HashMap<String, Object>) currents.get("prevPartitionNum");
			Long partition = (Long) partitions.get(table);
			if(partition != null) {
				partitions.put(table, onlinePartition);
				client.put("/global/metainfo/db/" + db + "/tablegroups/" + group, currents);
				return;
			}
		}
	}

	public static void addToLocalnodeGroup(ZkClient client, String group, String failoverNode) throws Exception {
		String groupPath = "/global/monitor/localnodegroup";
		HashMap<String, String> localnodeGroup = client.get(groupPath, HashMap.class);
		localnodeGroup.put(ClusterUtil.extractIpFromNodeName(failoverNode), group);
		client.put(groupPath, localnodeGroup);
	}
	
	public static void removeFromLocalnodeGroup(ZkClient client, String group, String failoverNode) throws Exception {
		String groupPath = "/global/monitor/localnodegroup";
		HashMap<String, String> localnodeGroup = client.get(groupPath, HashMap.class);
		localnodeGroup.remove(ClusterUtil.extractIpFromNodeName(failoverNode));
		client.put(groupPath, localnodeGroup);
	}

	public static void setConfigSharding(ZkClient client, String column, int i) throws Exception {
		String configShardingPath = "/global/config/sharding";
		HashMap<String, Object> configSharding = client.get(configShardingPath, HashMap.class);
		configSharding.put(column, i);
		client.put(configShardingPath, configSharding);
	}
	
	public static void setEntirelyOnlineTable(ZkClient client, String[] onlineTables) throws Exception {
		String entirelyOnlinePath = "/monitor/entirelyOnline";
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<String> list = new ArrayList<String>();
		for(String s : onlineTables) {
			list.add(s);
		}
		map.put("onlinetables", list);
		client.put(entirelyOnlinePath, map);
	}
	
	public static void main(String[] args) throws Exception {
		System.setProperty("zkURL", "10.232.36.107:12181,10.232.36.107:12182,10.232.36.107:12183/garuda2");
		ZkClient zkClient = new ZkClient();
		ZkUtil.setCurrentPartition(zkClient, "golden", "tcif_hjc_cat1_d", 20130407);
		ZkUtil.setCurrentPartition(zkClient, "golden", "tcif_hjc_jhs_d", 20130407);
		ZkUtil.setPrevPartition(zkClient, "golden", "tcif_hjc_cat1_d", 20130407);
		ZkUtil.setPrevPartition(zkClient, "golden", "tcif_hjc_jhs_d", 20130407);
		zkClient.close();
	}
}
