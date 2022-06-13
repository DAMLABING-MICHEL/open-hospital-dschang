package org.isf.serviceprinting.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;
import net.sf.jasperreports.engine.util.JRTextAttribute;

import org.isf.generaldata.TxtPrinter;

/**
 * 
 * @author Mwithi
 * 
 *         This class will read generic/text printer parameters and compile and
 *         print given jasper report. A copy will be at given file path
 * 
 */
public class PrintReceipt {

	/**
	 * 
	 * @param jasperPrint
	 * @param TXTfile
	 */
	public PrintReceipt(JasperPrint jasperPrint, String TXTfile) {
				   
		try {
			TxtPrinter.getTxtPrinter();
			
			JRTextExporter exporter = new JRTextExporter();
			exporter.setParameter(JRTextExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRTextExporterParameter.OUTPUT_FILE_NAME, TXTfile);
			exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, TxtPrinter.CHARACTER_HEIGHT);
			exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, TxtPrinter.CHARACTER_WIDTH);
			exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, TxtPrinter.PAGE_WIDTH);
			exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, TxtPrinter.PAGE_HEIGHT);
			
			exporter.exportReport();

			//Remove empty line
			
			removeEmptyLines(TXTfile);
			
			try {
				PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

				if (printService != null) {
					getPrinter(printService);
					if (!TxtPrinter.ZPL) {
						printFileTxt(TXTfile, printService);
						printerCutOff(printService);
					} else {
						printFileZPL(TXTfile, printService);
					}
				} else {
					System.out.println("printer was not found.");
					System.out.println(printService);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeEmptyLines(String TXTfile) throws FileNotFoundException, IOException {
		File f=new File(TXTfile);
		StringBuffer output=new StringBuffer();
		if(f.exists()){
			InputStreamReader reader=new InputStreamReader(new FileInputStream(f));
			Scanner sc=new Scanner(reader);
			while(sc.hasNextLine()){
				String line=sc.nextLine();
				if(!line.trim().equals("")){
					output.append(line+"\r\n");
				}
			}
			
			output.append("\n");
			output.append("\n");
			output.append("\n");
			output.append("\n");
			output.append("\n");
			
			sc.close();
			reader.close();
			
		
			BufferedWriter bw = null;
			FileWriter fw = null;

			try {
				fw = new FileWriter(TXTfile);
				bw = new BufferedWriter(fw);
				bw.write(output.toString());

				System.out.println("Done");

			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				try {

					if (bw != null)
						bw.close();

					if (fw != null)
						fw.close();

				} catch (IOException ex) {

					ex.printStackTrace();

				}

			}
			
			
		}
	}

	/**
	 * 
	 * @param file
	 * @param printService
	 */
	private void printFileTxt(String file, PrintService printService) {
			try {
				System.out.println("Using: " + printService.getName());
				DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
				PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
				DocAttributeSet das = new HashDocAttributeSet();
				FileInputStream fis = new FileInputStream(file);
				Doc doc = new SimpleDoc(fis, flavor, das);
				
				DocPrintJob job = printService.createPrintJob();
				job.print(doc, pras);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (PrintException e) {
				e.printStackTrace();
			}
	}
	
	private void printerCutOff(PrintService printService) {
		DocPrintJob job = printService.createPrintJob();  
		byte[] bytes = {0x1d,0x56,0x00};
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(bytes, flavor, null);
		try {
			job.print(doc, null);
		} catch (PrintException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param file
	 * @param printService
	 */
	private void printFileZPL(String file, PrintService printService) {
		try {
			System.out.println("Using: " + printService.getName());
			DocPrintJob job = printService.createPrintJob();
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			DocAttributeSet das = new HashDocAttributeSet();
			
			FileReader frStream = new FileReader(file);
			BufferedReader brStream = new BufferedReader(frStream);

			//int width = 447;
			//int height = 782;
			//int charW = width / TxtPrinter.PAGE_WIDTH;
			//int charH = height / TxtPrinter.PAGE_HEIGHT;
			int charH = TxtPrinter.ZPL_ROW_HEIGHT;
			
			String font = "^A" + TxtPrinter.ZPL_FONT_TYPE;
			String aLine = brStream.readLine();
			String zpl = "^XA^LH0,30" + aLine;//starting point
			int i = 0;
			while (aLine!=null && !aLine.equals("")) {
				//System.out.println(aLine);
				zpl+="^FO0," + (i * charH);//line position
				zpl+=font + "," + charH;//font size
				zpl+="^FD" + aLine + "^FS";//line field
				aLine = brStream.readLine();
				i++;
			}
			zpl+="^XZ";//end
			System.out.println(zpl);
			byte[] by = zpl.getBytes();
			Doc doc = new SimpleDoc(by, flavor, das);
			job.print(doc, pras);
			brStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PrintException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param printService
	 */
	private void getPrinter(PrintService printService) {
		System.out.println("Printer: " + printService.getName());
		System.out.println("Supported flavors:");
		DocFlavor[] flavors = printService.getSupportedDocFlavors();
		if (flavors != null) {
			for (DocFlavor flavor : flavors) {
				System.out.println(flavor);
			}
		}
		System.out.println("Attributes:");
		Attribute[] attributes = printService.getAttributes().toArray();
		if (attributes != null) {
			for (Attribute attr : attributes) {
				System.out.println(attr.getName() + ": " + (attr.getClass()).toString());
			}
		}
	}
}
