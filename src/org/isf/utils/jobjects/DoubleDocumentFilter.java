package org.isf.utils.jobjects;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class DoubleDocumentFilter extends DocumentFilter {

	@Override
	   public void insertString(FilterBypass fb, int offset, String string,
	         AttributeSet attr) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.insert(offset, string);

	      if (isDoubleorEmpty(sb.toString())) {
	         super.insertString(fb, offset, string, attr);
	      } else {
	         // warn the user and don't allow the insert
	      }
	   }

	   private boolean isDoubleorEmpty(String text) {
	      try {
	    	  text=text.trim();
	         Double.parseDouble(text);
	         return true;
	      } catch (NumberFormatException e) {
	    	  if(text.trim().equals("")){
	    		  return true;
	    	  }
	         return false;
	      }
	   }

	   @Override
	   public void replace(FilterBypass fb, int offset, int length, String text,
	         AttributeSet attrs) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.replace(offset, offset + length, text);

	      if (isDoubleorEmpty(sb.toString())) {
	         super.replace(fb, offset, length, text, attrs);
	      } else {
	         // warn the user and don't allow the insert
	      }

	   }

	   @Override
	   public void remove(FilterBypass fb, int offset, int length)
	         throws BadLocationException {
	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.delete(offset, offset + length);

	      if (isDoubleorEmpty(sb.toString())) {
	         super.remove(fb, offset, length);
	      } else {
	         // warn the user and don't allow the insert
	      }

	   }
}
