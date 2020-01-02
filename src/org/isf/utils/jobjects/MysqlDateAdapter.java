package org.isf.utils.jobjects;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MysqlDateAdapter  extends XmlAdapter<String, Date>{

//	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date unmarshal(String string) throws Exception {
        return  dateFormat.parse(string);
                
    }

	@Override
	public String marshal(Date date) throws Exception {
		return dateFormat.format(date);
	}
    
}
