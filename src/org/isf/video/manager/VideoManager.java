package org.isf.video.manager;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.isf.utils.db.DbSingleConn;
import org.isf.video.gui.VideoDeviceStreamApplet;
import org.isf.video.gui.VideoDeviceStreamAppletManager;
import org.isf.video.gui.VideoFrame;
import org.isf.video.model.VideoDevice;
import org.isf.video.service.XMLDocumentManager;
import org.isf.video.service.XMLDocumentManagerFactory;

public class VideoManager {

	private static VideoDevicesManager videoDevicesManager = VideoDevicesManagerFactory
			.getVideoDevicesManagerFactory().createVideoDevicesManager();

	private static XMLDocumentManager xmlDocManager = XMLDocumentManagerFactory
			.getXMLDocumentManagerFactory().createXMLDocumentManager();

	public static VideoFrame frame = null;

	public static String shotPhotosTempDir = "";
//	public static String photosDir = "";
	public static String shotPhotosExtension = ".jpg";

	public static void init(VideoFrame vf) {
		frame = vf;
		init();
	}

	public static void init() {
		shotPhotosTempDir = System.getProperty("java.io.tmpdir");
		// aggiunge lo slash finale se manca
		if (!(shotPhotosTempDir.endsWith("/") || shotPhotosTempDir
				.endsWith("\\")))
			shotPhotosTempDir += System.getProperty("file.separator");

//		Properties props = new Properties();
//		FileInputStream in;
//		try {
//			in = new FileInputStream("rsc/generalData.properties");
//			props.load(in);
//			in.close();
//			photosDir = props.getProperty("PHOTOSDIR");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static int getVideoDevicesCount() {
		return videoDevicesManager.getVideoDevicesCount();
	}

	public static VideoDevice getVideoDevice(int id) {
		return videoDevicesManager.getVideoDevice(id);
	}

	public static VideoDevice getFirstVideoDevice() {
		return videoDevicesManager.getFirstVideoDevice();
	}

	public static VideoDevice getNextVideoDevice(int id) {
		return videoDevicesManager.getNextVideoDevice(id);
	}

	public static void addDeviceToGui(VideoDevice videoDevice) {
		if (frame != null) {
			frame.addDevice(videoDevice);

			boolean ignoreFile = false;
			frame.checkResolutions(videoDevice, ignoreFile);
		}
	}

	public static void removeDeviceFromGui(VideoDevice videoDevice) {
		if (frame != null) {
			frame.removeDevice(videoDevice);
		}
	}

	public static int updateDeviceList() {
		int videoDevicesCount = videoDevicesManager.updateDeviceList();

		System.out.println("Updated device list, found " + videoDevicesCount
				+ " devices");

		return videoDevicesCount;
	}

	public static void removeAllResolutionsFromGui() {
		if (frame != null) {
			frame.removeAllResolutions();
		}
	}

	public static void saveResolutionInFile(String deviceId,
			ArrayList<String> resolutions) {
		xmlDocManager.writeResolutions(deviceId, resolutions);
	}

	public static void removeResolutionFromFile(String deviceId) {
		xmlDocManager.removeResolutionsForCurrentOs(deviceId);
	}

	public static ArrayList<String> loadResolutionFromFile(String deviceId) {
		ArrayList<String> resolutions = xmlDocManager
				.readResolutionsForCurrentOs(deviceId);

		return resolutions;
	}

	public static boolean setCurrentResolutionAsDefault(int width, int height,
			String deviceId) {
		return xmlDocManager.setDefaultResolutionForDevice(width, height,
				deviceId);
	}

	public static void reset(boolean clearDeviceList) {
		System.out.println("reset videomanager");
		videoDevicesManager.reset(clearDeviceList);
	}

	public static String getDefaultResolutionFromFile(String deviceId) {
		String defRes = xmlDocManager.getDefaultResolutionForDevice(deviceId);

		return defRes;
	}

	/*
	 * Get the extension of a file.
	 */
	public static String getFileExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/*
	 * Convert an awt Image to a BufferedImage
	 */
	public static BufferedImage imageToBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
		// img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		// Return the buffered image
		return bimage;
	}

	public static String savePhoto(VideoDeviceStreamApplet applet,
			String filename) {
		String extension = VideoManager.shotPhotosExtension;
		String generatedFileName = "";

		String path = "";

		try {
			// crea un file temporaneo per la foto scattata
			File tempFile = File.createTempFile(filename, extension);
			tempFile.deleteOnExit();

			// il metodo createTempFile aggiunge un suffisso numerico al nome
			// del file,
			// ci interessa conoscere quindi il nome reale del file creato per
			// passarlo
			// a saveCurrentFrame
			generatedFileName = tempFile.getName().replace(extension, "");

			String tempDir = VideoManager.shotPhotosTempDir;

			path = tempDir + generatedFileName + extension;

			VideoDeviceStreamAppletManager.saveCurrentFrame(applet, path);
		} catch (IOException ioe) {

		}

		return generatedFileName;
	}
}