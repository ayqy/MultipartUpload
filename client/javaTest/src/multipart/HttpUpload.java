/**
 * 实现multipart表单提交
 */
package multipart;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import multipart.MyMultipartEntity.ProgressListener;

/**
 * @author ay
 *
 */
public class HttpUpload {
	private static String filePath = "C:/Users/ay/Desktop/activity.png";

	private static HttpClient client;

	private static long totalSize;

	private static final String url = "http://localhost:3000/upload";

	public HttpUpload(String filePath) {
		super();
		this.filePath = filePath;
	}

	public static void main(String[] args) {
		// --- onPreExecute
		// Set timeout parameters
		int timeout = 10000;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
		HttpConnectionParams.setSoTimeout(httpParameters, timeout);

		// We'll use the DefaultHttpClient
		client = new DefaultHttpClient(httpParameters);

		// 创建并显示进度
		System.out.println("upload start");

		// ---doInBackground
		try {
			File file = new File(filePath);

			// Create the POST object
			HttpPost post = new HttpPost(url);

			// Create the multipart entity object and add a progress listener
			// this is a our extended class so we can know the bytes that have
			// been transfered
			MultipartEntity entity = new MyMultipartEntity(new ProgressListener() {
				@Override
				public void transferred(long num) {
					// Call the onProgressUpdate method with the percent
					// completed
					// publishProgress((int) ((num / (float) totalSize) * 100));
					System.out.println(num + " - " + totalSize);
				}
			});
			// Add the file to the content's body
			ContentBody cbFile = new FileBody(file, "image/png");
			entity.addPart("source", cbFile);

			// set fields
			entity.addPart("userid", new StringBody("u30018512", Charset.forName("UTF-8")));
			entity.addPart("username", new StringBody("中文不乱码", Charset.forName("UTF-8")));

			// After adding everything we get the content's lenght
			totalSize = entity.getContentLength();

			// We add the entity to the post request
			post.setEntity(entity);

			// Execute post request
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				// If everything goes ok, we can get the response
				String fullRes = EntityUtils.toString(response.getEntity());
				System.out.println("OK: " + fullRes);

			} else {
				System.out.println("Error: " + statusCode);
			}

		} catch (ClientProtocolException e) {
			// Any error related to the Http Protocol (e.g. malformed url)
			e.printStackTrace();
		} catch (IOException e) {
			// Any IO error (e.g. File not found)
			e.printStackTrace();
		}

		// --- onProgressUpdate
		// 更新进度

		// --- onPostExecute
		// 隐藏进度
	}
}
