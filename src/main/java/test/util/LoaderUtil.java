package test.util;

public class LoaderUtil {
	private static HttpClient httpClient = new HttpClient();
	
	public static boolean submitLoaderJob(String monitor, String filelist, String meta) {
		String params = "force=true&username=root&password=123456" +
				"&filelist_path=" + filelist + 
				"&meta_file_path=" + meta;
		System.out.println(params);
		String retVal = httpClient.post("http://" + monitor + "/api/garuda/loader/task", params);
		System.out.println(retVal);
		if(retVal != null && retVal.indexOf("SC_OK") >= 0) {
			return true;
		} else {
			System.out.println("submit loader job error! message :" + retVal);
			return false;
		}
	}
}
