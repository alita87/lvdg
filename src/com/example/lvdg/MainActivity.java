package com.example.lvdg;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.WallpaperManager;
import android.database.CursorJoiner.Result;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void updateImage(View view){
		new BackgroundOperations().execute("");
	}
	
	private class BackgroundOperations extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			
			try{
				// Check file location
				String toDownload;
				AndroidHttpClient httpclient;
				httpclient = AndroidHttpClient.newInstance(System.getProperty( "http.agent" ));
				
				HttpGet httppost = new HttpGet("http://172.16.131.217/~bullet/lvdg.php");
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity ht = response.getEntity();
				
				BufferedHttpEntity buf = new BufferedHttpEntity(ht);
				
				InputStream is = buf.getContent();
				
				BufferedReader r  = new BufferedReader(new InputStreamReader(is));
				
				StringBuilder total = new StringBuilder();
				toDownload = r.readLine();
				System.out.println(toDownload);
				//String line;
				//while ((line = r.readLine()) != null){
				//	System.out.println("########>>>>>>>>   " + line);
				//	total.append(line + "\n");
				//}			
			
				httpclient.close();
				
				// Download file
				File root = android.os.Environment.getExternalStorageDirectory();
				System.out.println("#########>>>>>>>>>>   "+root.getAbsolutePath());
				File dir = new File(root.getAbsolutePath() + "/lvdg");
				
				if(dir.exists() == false){
					dir.mkdirs();
				}
				
				URL url = new URL(toDownload);
				String fileName = toDownload.substring( toDownload.lastIndexOf('/')+1, toDownload.length() );
				File file = new  File(dir, fileName);
				
				URLConnection ucon = url.openConnection();
				
				is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				
				ByteArrayBuffer baf =  new ByteArrayBuffer(5000);
				int current = 0;
				while ((current = bis.read()) != -1){
					baf.append((byte)current);
				}
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.flush();
				fos.close();
				
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
	            display.getSize(size);
				
				WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
				//BitmapDrawable bm = new BitmapDrawable(getResources(), root.getAbsolutePath() + "/lvdg/" + fileName);
				Bitmap bm = BitmapFactory.decodeFile(root.getAbsolutePath() + "/lvdg/" + fileName);
				


	            //Bitmap bitmapResized = Bitmap.createScaledBitmap(bm, size.x, size.y, true);
	            
	            System.out.println(">>>>>>>>>> size.y: "+ size.y);
	            System.out.println(">>>>>>>>>> size.x: "+ size.x);
	            
	            //System.out.println(">>>>>>>>>> size.y: "+ size.y);
	            //System.out.println(">>>>>>>>>> size.y: "+ size.y);
	            
	            float x = ((float)bm.getWidth() * (float)size.y) / (float)bm.getHeight() ;
	            System.out.println("ssssssss: "+x);
	            int d = new Float(x).intValue();
	            
	            myWallpaperManager.suggestDesiredDimensions(d, size.y);
				myWallpaperManager.setBitmap(bm);
				
			}catch(Exception i){
				System.out.println(i.getMessage());
			}
			
			return null;
		}
		

	}

}
