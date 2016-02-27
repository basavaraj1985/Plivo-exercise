package com.plivotest;

import com.basava.mock.backend.MockBEServer;
import com.basava.mock.backend.servlets.MockResponseServlet;
import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.application.Application;
import com.plivo.helper.api.response.call.Call;
import com.plivo.helper.exception.PlivoException;
import com.plivotest.libs.AudioFileHelper;
import org.eclipse.jetty.servlet.ServletHolder;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by bm3 on 2/26/16.
 */
public class PlivoCallTest
{
	private ArrayBlockingQueue<Map> requestParamMapQueue;
	private Properties configuration;
	private RestAPI client = null;

	public static String auth_id_key = "auth_id";
	public static String auth_token_key = "auth_token";
	public static String fromNumber_key = "fromNumber";
	public static String toNumber_key = "toNumber";
	public static String ngrokLinkKey = "ngrokLink";
	public static String serverConfigKey = "serverConfig";

	private String auth_id ;
	private String auth_token ;
	private String fromNumber ;
	private String toNumber ;
	private String ngrokLink ;
	private Thread serverThread;

	@BeforeClass(alwaysRun = true)
	@Parameters("configFile")
	public void setupEnv(String configFile) throws IOException
	{
		requestParamMapQueue = new ArrayBlockingQueue<Map>(500);
		initializeConfiguration(configFile);
		runServer();
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("app_name","interview_exercise"); // The name of your application

		try {
			client = new RestAPI(auth_id, auth_token, "v1");
			Application resp = client.createApplication(parameters);
			System.out.println((resp));
		}catch (PlivoException e){
			System.out.println(e.getLocalizedMessage());
		}
	}

	private void initializeConfiguration(String configFile) throws IOException
	{
		configuration = new Properties();
		configuration.load(new FileInputStream(new File(configFile)));
		auth_id = configuration.getProperty(auth_id_key, "MAZMY1ZMVJYZCYZWI3YM");
		auth_token = configuration.getProperty(auth_token_key, "YzE2OTMwYThiNzE3NmNjNzViZWJkYjgwM2ZkMWVk");
		fromNumber = configuration.getProperty(fromNumber_key, "18787878787");
		toNumber = configuration.getProperty(toNumber_key,"919008211884" );
		ngrokLink = configuration.getProperty(ngrokLinkKey, "http://ef323007.ngrok.io" );
	}

	@AfterMethod(alwaysRun = true)
	public void emptyTheQueue()
	{
		while ( ! requestParamMapQueue.isEmpty() )
		{
			System.out.println("Emptying the queue!");
			requestParamMapQueue.poll();
		}
	}

	@AfterClass ( alwaysRun = false )
	public void endEverything()
	{
		serverThread.interrupt();
	}


	/**
	 * Make a call to your phone using Plivo API and play a .wav file
	 * Assert that the call duration is same as the length of the file played
	 * @throws PlivoException
	 * @throws InterruptedException
	 */
	@Test ( groups = { "call", "smoke" })
	public void verifyPlacingACall() throws PlivoException, InterruptedException
	{
		Call callResponse = placeACall();
		System.out.println("Message : " + callResponse.message );
		Map answerParams = requestParamMapQueue.poll(10, TimeUnit.SECONDS);
		Assert.assertNotNull(answerParams, "Answer param map is null!");
		Map hangupParams = requestParamMapQueue.poll(65, TimeUnit.SECONDS);
		Assert.assertNotNull(hangupParams, "hangupParams param map is null!");
		requestParamMapQueue.poll(20, TimeUnit.SECONDS); // consume callback

		int playbackFileLength = 51;
		String hangupDuration = getValue(hangupParams, "Duration");
		Assert.assertNotNull(hangupDuration, "hangup duration was null!");

		int durationOfTheCall = Integer.valueOf(hangupDuration.trim());
		Assert.assertTrue( durationOfTheCall >= playbackFileLength && durationOfTheCall <= durationOfTheCall + 3,
				"Duration of the call verification failed! actual: " + durationOfTheCall +
						" expected : " + playbackFileLength);
	}

	/**
	 * Assert the recording file duration is same as length of the file played
	 * @throws InterruptedException
	 * @throws PlivoException
	 */
	@Test ( groups = { "recording", "smoke" })
	public void verifyRecordingACall() throws InterruptedException, PlivoException, IOException,
			UnsupportedAudioFileException
	{
		Call callResponse = placeACall();
		System.out.println("Message : " + callResponse.message );
		Map answerParams = requestParamMapQueue.poll(10, TimeUnit.SECONDS);
		Assert.assertNotNull(answerParams, "Answer param map is null!");
		Map hangupParams = requestParamMapQueue.poll(70, TimeUnit.SECONDS);
		Assert.assertNotNull(hangupParams, "Hangup param map is null!");
		Map callbackParams = requestParamMapQueue.poll(10, TimeUnit.SECONDS);
		Assert.assertNotNull(callbackParams, "Callback param map is null!");

		String hangupDuration = getValue(hangupParams, "Duration");
		Assert.assertNotNull(hangupDuration, "hangup duration was null!");
		int durationOfTheCall = Integer.valueOf(hangupDuration.trim());

		String recordingDuration = getValue(callbackParams, "RecordingDuration");
		Assert.assertNotNull(recordingDuration, "recording duration was null!");
		int recordingDurationOfTheCall = Integer.valueOf(recordingDuration.trim());

		String recordUrl = getValue(callbackParams, "RecordUrl");
		String downloadedRecording = "target/recordingOfTheCall.wav";
		AudioFileHelper.downloadFile(recordUrl, downloadedRecording);
		double durationOfTheAudioFile = AudioFileHelper.getDurationOfTheAudioFile(downloadedRecording);
		System.out.println("Duration of the audio file : " + durationOfTheAudioFile );

		Assert.assertTrue(durationOfTheCall <= recordingDurationOfTheCall + 3 && durationOfTheCall >=
				recordingDurationOfTheCall, "Duration of the recording and duration of the actual call " + "differ  by" +
				" more than allowed variance!");
		Assert.assertEquals(recordingDurationOfTheCall, (int)durationOfTheAudioFile, "Duration of the audio file " +
				"didnt " +
				"match with duration of the recording of the call!");
	}

	private Call placeACall() throws PlivoException
	{
		client = new RestAPI(auth_id, auth_token, "v1");
		LinkedHashMap<String, String> callParams = new LinkedHashMap<String, String>();
		callParams.put("from",fromNumber);
		callParams.put("to",toNumber);
		callParams.put("answer_method","GET");
		callParams.put("answer_url", ngrokLink + "/answer");
		callParams.put("hangup_method","GET");
		callParams.put("hangup_url", ngrokLink + "/hangup");
		return client.makeCall(callParams);
	}

	private String getValue(Map map, String key)
	{
		if ( null == map || map.size() == 0 )
		{
			System.out.println("Input map was null! key - " + key);
			return null;
		}
		Iterator iterator = map.entrySet().iterator();
		while ( iterator.hasNext() )
		{
			Map.Entry<String, String[]> next = (Map.Entry<String, String[]>) iterator.next();
			StringBuilder builder = new StringBuilder();
			String[] values = next.getValue();
			for ( String s : values )
			{
				builder.append(s);
			}
			System.out.println(" --> " + next.getKey() + " : " + builder.toString() );
		}
		String[] parts = (String[]) map.get(key);
		StringBuilder buffer = new StringBuilder();
		if ( parts == null )
		{
			System.out.println("NO value found for key - " + key);
			return null;
		}
		for ( String each : parts )
		{
			buffer.append(each);
		}
		System.out.println("Obtained - " + key + " : " + buffer.toString());
		return buffer.toString();
	}

	/*
	 * sleep seconds
	 */
	private void sleep(int i)
	{
		try
		{
			Thread.sleep(i * 1000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Runnable runnable = new Runnable()
		{
			public Queue requestParamMapQueue;

			@Override
			public void run()
			{
				String config = "config/config.properties";
				requestParamMapQueue = new ArrayBlockingQueue(500);
				MockResponseServlet servlet = new MockResponseServlet("config/config.properties", requestParamMapQueue);
				MockBEServer server = new MockBEServer(config);
				server.addServlet(new ServletHolder(servlet),"/*");
				server.start();
			}
		};

		Thread serverThread = new Thread(runnable,"serverThread");
		serverThread.start();

		try
		{
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void runServer()
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				String config = configuration.getProperty(serverConfigKey,"config/config.properties");
				MockResponseServlet servlet = new MockResponseServlet("config/config.properties", requestParamMapQueue);
				MockBEServer server = new MockBEServer(config);
				server.addServlet(new ServletHolder(servlet),"/*");
				server.start();
			}
		};

		serverThread = new Thread(runnable,"serverThread");
		serverThread.start();

		try
		{
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
