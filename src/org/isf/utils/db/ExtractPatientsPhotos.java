package org.isf.utils.db;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.parameters.manager.Param;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.utils.exception.OHException;
import org.isf.video.manager.VideoManager;

public class ExtractPatientsPhotos {

	public static void main(String[] args) {
		System.out
				.println("----------------------Image extraction from database");
		try {
			extractPatientsPhotos();
			System.out
					.println("------------------extraction ended");
		} catch (OHException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is an utilities method that extract patient images from the database and store them in the file system. 
	 * The method also drop the PAT_PHOTO column from patient table.
	 * The path to the images folder can be setted in the generalData.properties file throw the key "PHOTOSDIR"	
	 * @throws OHException
	 * If an error occurs
	 */
	public static void extractPatientsPhotos() throws OHException {
		ResultSet resultSet = null;
		String query = "";
		DbQueryLogger dbQuery = new DbQueryLogger();

		GeneralData.getGeneralData();
		String photoDir = Param.string("PHOTOSDIR");

		try {
			// We save all the blob photos from the database to the patients
			// folder in photo directory
			query = "SELECT PAT_PHOTO,PAT_ID FROM PATIENT";
			resultSet = dbQuery.getData(query, true);
			
			File theDir = new File(photoDir);
			if (!theDir.exists()) theDir.mkdir();
			
			while (resultSet.next()) {
				Blob photoBlob = resultSet.getBlob("PAT_PHOTO");
				int id = resultSet.getInt("PAT_ID");

				if (photoBlob != null) {
					BufferedInputStream is = new BufferedInputStream(
							photoBlob.getBinaryStream());
					Image image = ImageIO.read(is);
					if (image == null) break;
					String ext=VideoManager.shotPhotosExtension.substring(1);
					String fileName=PatientBrowserManager.getPatientPhotoPath(id);
					String photoFilenameToSave = photoDir
							+ File.separator+fileName;
					ImageIO.write(VideoManager.imageToBufferedImage(image),
							ext, new File(photoFilenameToSave));
				}

			}
			resultSet.close();
			// We drop the column "PAT_PHOTO" from Patient table
			query = "ALTER TABLE PATIENT DROP COLUMN PAT_PHOTO";
			dbQuery.setData(query, true);

		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} catch (IOException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwithserverconnection"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
	}
	
	
}
