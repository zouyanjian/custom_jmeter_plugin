package test.util;

import com.taobao.garuda.tools.ZkClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusterUtil {
	public static String entirelyOnline(String activeMonitor) {
		return entirelyOnline(activeMonitor, false);
	}
	
	public static String entirelyOnline(String activeMonitor, boolean dryRun) {
		HttpClient client = new HttpClient();
		String retVal = client.post("http://" + activeMonitor + "/monitor", "method=entirelyOnline&dryRun=" + dryRun);
		return retVal;
	}
	
	public static String reset(String activeMonitor) {
		HttpClient client = new HttpClient();
		String retVal = client.post("http://" + activeMonitor + "/monitor", "method=reset");
		return retVal;
	}
	
	public static String submitSql(String cluster, String sql,String consoleIPAndPort) {
		HttpClient client = new HttpClient();
        String retVal = client.post("http://" + consoleIPAndPort + "/garuda-console/controller/query", "sql=" + sql + "&__zk_cluster_name__=" + cluster);
		return retVal;
	}

    public static String submitSql(String cluster,String sql){
        String consoleIPAndPort = "10.235.168.153:8080";
        return submitSql(cluster,sql,consoleIPAndPort);
    }


	
    public static String[] decodeNodeName(String nodeName) {
    	if ( nodeName.length() > 0 ) {
			String[] nodeInfo = nodeName.split( "\\_" );
			if ( nodeInfo.length == 5 ) {
				String ip = nodeInfo[0].trim() + "." + nodeInfo[1].trim() + "." + nodeInfo[2].trim()
						+ "." + nodeInfo[3].trim();
				return new String[] { ip, nodeInfo[4].trim() };
			}
		}
		return null;
    }

    public static String extractIpFromNodeName(String name) {
        String[] strs = decodeNodeName(name);
        if (strs != null && strs.length > 0) {
            return strs[0];
        }
        else {
            return name;
        }
    }

	public static List<Object> getColumnFromSqlResult(String value, String column) throws Exception {
		List<Object> colVals = new ArrayList<Object>();
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<HashMap<String, Object>> resultList = mapper.readValue(value, ArrayList.class);
		for (HashMap<String, Object> map : resultList) {
			System.out.println(map);
			Object tmp = map.get(column);
			if(tmp != null) {
				colVals.add(tmp);
			}
		}
		return colVals;
	}
	
	public static boolean waitForState(ZkClient zkClient, String[] group, short target) throws Exception {
		List<String> ret = new ArrayList<String>();
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 3600000) {
			ret.clear();
			for (String localnode : group) {
				HashMap<String, Object> map = ZkUtil.getLnmg(zkClient, localnode);
				Short state = (Short) map.get("state");
				if (state != null && state == target) {
					ret.add(localnode);
				} else {
					System.out.println("localnode: " + localnode + " state is" + state);
				}
			}
			if (ret.size() == group.length) {
				return true;
			}
			Thread.currentThread().sleep(1000);
		}
		return false;
	}

	public static boolean reloadData(ZkClient zkClient, String activeMonitor, String[] dbTables, long onlinePartition) throws Exception {
		for(String dbTable : dbTables) {
			String[] tmp = dbTable.split("\\.");
			ZkUtil.setCurrentPartition(zkClient, tmp[0], tmp[1], onlinePartition);
			ZkUtil.setPrevPartition(zkClient, tmp[0], tmp[1], onlinePartition);
			ZkUtil.changeFirstLevelTableInfo(zkClient, tmp[0], tmp[1], "currentPartitionNum", onlinePartition);
		}
		
		String retVal = reset(activeMonitor);
		System.out.println("reloadData: retVal = " + retVal);
		if(retVal.indexOf("true") >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String cleanCache(String cluster) {
        String consoleIpAndPort = "10.235.168.153:8080";
		return cleanCache(cluster,consoleIpAndPort);
	}

    public static String cleanCache(String cluster,String consoleIpAndPort){
        HttpClient client = new HttpClient();
        String retVal = client.get("http://" + consoleIpAndPort + "/garuda-console/controller/clearcache", "__zk_cluster_name__=" + cluster);
        return retVal;
    }

	public static void main(String[] args) {
		System.out.println(submitSql("garuda2", "select count(user_id) as count from golden.tcif_hjc_cat1_d where 1=1"));
	}

}
