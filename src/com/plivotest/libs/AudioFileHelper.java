package com.plivotest.libs;

import org.apache.commons.io.FileUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by bm3 on 2/27/16.
 */
public class AudioFileHelper
{
	/**
	 * Returns duration of the audio file
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static double getDurationOfTheAudioFile(String filePath) throws IOException, UnsupportedAudioFileException
	{
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
		AudioFormat format = audioInputStream.getFormat();
		long frames = audioInputStream.getFrameLength();
		return (frames+0.0) / format.getFrameRate();
	}

	/**
	 *
	 * @param recordUrl
	 * @param fileName
	 * @throws IOException
	 */
	public static void downloadFile(String recordUrl, String fileName) throws IOException
	{
		URL website = new URL(recordUrl);
		FileUtils.copyURLToFile(website, new File(fileName));
//		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
//		FileOutputStream fos = new FileOutputStream(fileName);
//		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}
}
