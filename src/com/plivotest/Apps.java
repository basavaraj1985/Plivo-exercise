package com.plivotest;

import com.basava.mock.backend.MockBEServer;
import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.application.Application;
import com.plivo.helper.api.response.call.Call;
import com.plivo.helper.api.response.response.Record;
import com.plivo.helper.exception.PlivoException;

import java.util.LinkedHashMap;

public class Apps {

	public static void runServer()
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				String config = "config/config.properties";
				MockBEServer server = new MockBEServer(config);
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

	public static void main(String[] args) throws IllegalAccessException, PlivoException
	{
		String auth_id = "MAZMY1ZMVJYZCYZWI3YM";
		String auth_token = "YzE2OTMwYThiNzE3NmNjNzViZWJkYjgwM2ZkMWVk";

		RestAPI api = new RestAPI(auth_id, auth_token, "v1");

		runServer();
		// Create an Application
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("answer_url","https://s3.amazonaws.com/plivosamplexml/play_url.xml"); // The URL Plivo will fetch when a call executes this application
		parameters.put("app_name","interview_exercise"); // The name of your application

		try {
			Application resp = api.createApplication(parameters);
			System.out.println((resp));
		}catch (PlivoException e){
			System.out.println(e.getLocalizedMessage());
		}

		api = new RestAPI(auth_id, auth_token, "v1");
		LinkedHashMap<String, String> callParams = new LinkedHashMap<String, String>();
		callParams.put("from","18787878787");
		callParams.put("to","919008211884");
		callParams.put("answer_method","GET");
		callParams.put("answer_url","http://ef323007.ngrok.io/answer");
		callParams.put("hangup_method","GET");
		callParams.put("hangup_url","http://ef323007.ngrok.io/hangup");
		Call callResponse = api.makeCall(callParams);
		System.out.println("Message : " + callResponse.message );


//		LinkedHashMap<String, String> recordingParams = new LinkedHashMap<String, String>();
//		recordingParams.put("action","http://ef323007.ngrok.io");
//		recordingParams.put("method","POST");
//		recordingParams.put("fileFormat","mp3");
//		recordingParams.put("recordSession","true");
//		recordingParams.put("startOnDialAnswer","true");
//		recordingParams.put("callbackUrl","");
//		recordingParams.put("callbackMethod","GET");
//		Record recordResp = api.record(recordingParams);
//		System.out.println("Message : " + recordResp);





        /*
        Sample Output
        Application [
            serverCode=201,
            fallbackMethod=null,
            isDefaultApplication=null,
            applicationName=null,
            isProductionApplication=null,
            applicationID=10559320185257208,
            hangupUrl=null,
            answerUrl=null,
            messageUrl=null,
            resourceUri=null,
            answerMethod=null,
            hangupMethod=null,
            messageMethod=null,
            fallbackAnswerUrl=null,
            error=null,
            apiId=4f6b0f36-c716-11e4-9107-22000afaaa90,
            message=created
        ]
        */

		// Get details all existing applications
//		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
//		parameters.put("limit","10"); // The number of results per page
//		parameters.put("offset", "0"); // The number of value items by which the results should be offset
//
//		try {
//			ApplicationFactory resp = api.getApplications(parameters);
//			System.out.println(resp);
//			// Print the total number of apps
//			System.out.println("Total count : " + resp.meta.total_count);
//		}catch (PlivoException e){
//			System.out.println(e.getLocalizedMessage());
//		}

        /*
        Sample Output
        ApplicationFactory [
            serverCode=200,
            applicationList=[
                Application [
                    serverCode=null,
                    fallbackMethod=POST,
                    isDefaultApplication=false,
                    applicationName=Testing_App,
                    isProductionApplication=null,
                    applicationID=10559320185257208,
                    hangupUrl=http://example.com,
                    answerUrl=http://example.com,
                    messageUrl=,
                    resourceUri=/v1/Account/xxxxxxxxxxxxxxxxx/Application/10559320185257208/,
                    answerMethod=POST,
                    hangupMethod=POST,
                    messageMethod=POST,
                    fallbackAnswerUrl=,
                    error=null,
                    apiId=null,
                    message=null
                ], Application [
                    serverCode=null,
                    fallbackMethod=POST,
                    isDefaultApplication=false,
                    applicationName=Dial,
                    isProductionApplication=null,
                    applicationID=27082215185108636,
                    hangupUrl=http://plivodirectdial.herokuapp.com/response/sip/route/?DialMusic=real&CLID=1111111111,
                    answerUrl=http://morning-ocean-4669.herokuapp.com/response/sip/route/?DialMusic=real,
                    messageUrl=http://plivodirectdial.herokuapp.com/response/sip/route/?DialMusic=real&CLID=11111111111,
                    resourceUri=/v1/Account/xxxxxxxxxxxxxxxxx/Application/
                    /,
                    answerMethod=POST,
                    hangupMethod=POST,
                    messageMethod=POST,
                    fallbackAnswerUrl=http://plivodirectdial.herokuapp.com/response/sip/route/?DialMusic=real&CLID=1111111111,
                    error=null,
                    apiId=null,
                    message=null
                ],
            ]
            apiId=6fa5f0da-c717-11e4-b423-22000ac8a2f8,
            error=null
        ]
        Total count : 9
        */

		// Get details of a single application
//		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
//		parameters.put("app_id","10559320185257208"); // ID of the application for which the details have to be retrieved
//
//		try {
//			Application resp = api.getApplication(parameters);
//			System.out.println(resp);
//		}catch (PlivoException e){
//			System.out.println(e.getLocalizedMessage());
//		}

        /*
        Sample Output
        Application [
            serverCode=200,
            fallbackMethod=POST,
            isDefaultApplication=false,
            applicationName=Testing_App,
            isProductionApplication=null,
            applicationID=10559320185257208,
            hangupUrl=http://example.com,
            answerUrl=http://example.com,
            messageUrl=, resourceUri=/v1/Account/xxxxxxxxxxxxxxxxx/Application/10559320185257208/,
            answerMethod=POST,
            hangupMethod=POST,
            messageMethod=POST,
            fallbackAnswerUrl=,
            error=null,
            apiId=259ae40e-c718-11e4-b423-22000ac8a2f8,
            message=null
        ]
        */

		// Modify an application
//		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
//		parameters.put("app_id","10559320185257208"); // ID of the application for which has to be modified
//		parameters.put("answer_url", "http://exampletest.com"); // Values that have to be updated
//
//		try {
//			GenericResponse resp = api.editApplication(parameters);
//			System.out.println(getFields(resp));
//		}catch (PlivoException e){
//			System.out.println(e.getLocalizedMessage());
//		}

        /*
        Sample Output
        GenericResponse [
            serverCode=202,
            message=changed,
            error=null,
            apiId=daccceb4-c718-11e4-ac1f-22000ac51de6
        ]
        */

		// Delete an application
//		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
//		parameters.put("app_id","10559320185257208"); // ID of the application for which has to be deleted
//
//		try {
//			GenericResponse resp = api.deleteApplication(parameters);
//			System.out.println(resp);
//		}catch (PlivoException e){
//			System.out.println(e.getLocalizedMessage());
//		}

        /*
        Sample Output
        GenericResponse [
            serverCode=204,
            message=no response,
            error=null,
            apiId=unknown
        ]

        Unsuccessful Output
        GenericResponse [
            serverCode=404,
            message=null,
            error=not found,
            apiId=13a16e66-c719-11e4-b423-22000ac8a2f8
        ]
        */
	}
}