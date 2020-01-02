package org.isf.utils.jobjects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter  extends XmlAdapter<String, GregorianCalendar>{

//	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public GregorianCalendar unmarshal(String string) throws Exception {
        Date date= dateFormat.parse(string);
        GregorianCalendar cal=new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }

	@Override
	public String marshal(GregorianCalendar date) throws Exception {
		// TODO Auto-generated method stub
		return dateFormat.format(date.getTime());
	}
    
}
