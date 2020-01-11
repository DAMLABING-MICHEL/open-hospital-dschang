package org.isf.parameters.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Map.Entry;

import org.isf.parameters.model.Parameter;

public class Param {
	public static ArrayList<Parameter> paramDataBase = new ArrayList<Parameter>();
	public static ArrayList<Parameter> paramLocal = new ArrayList<Parameter>();
	private final String FILE_PROPERTIES = "generalData.properties";
	public Param(){
		System.out.println("parameters loaded");
		ParametersManager manager = new ParametersManager();
		
		paramDataBase = manager.getParameters();
		Parameter param = null;
		try {
			Properties p = new Properties();
			FileInputStream in = new FileInputStream("rsc" + File.separator + FILE_PROPERTIES);
			p.load(in);
			for(Entry<Object, Object> e : p.entrySet()) {
	            param = new Parameter();
	            param.setCode(e.getKey().toString());
	            param.setValue(e.getValue().toString());
	            paramLocal.add(param);
	        }
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static Parameter getParameter(String code){
		for (Parameter parameter : paramDataBase) {
			if(parameter!=null){
				if(parameter.getCode().equals(code))
					return parameter;
			}
		}
		return null;		
	}
	
	private static String getParameterLocal(String code){
		for (Parameter parameter : paramLocal) {
			if(parameter!=null){
				if(parameter.getCode().equals(code))
					return parameter.getValue();
			}
		}
		return null;		
	}

	public static  boolean bool(String code){		
			//get first in local
			String val = getParameterLocal(code);
		    if(val!=null && val.length()>0 ){
		    	if(val.equalsIgnoreCase("YES"))
		    		return true;
		    	if(val.equalsIgnoreCase("NO"))
		    		return false;
		    }
		    //
		    Parameter parameter = getParameter(code);
		    if(parameter == null) 
		    	return false;
		    String value = parameter.getValue();
	    	String default_value = parameter.getDefault_value();
		    if(parameter.getDeleted() != null && parameter.getDeleted().equals("D")){
		    	if(default_value!=null && default_value.length()>0){
		    		if(default_value.contains("@") && default_value.equals("@true")){
			    		return true;
			    	}
			    	if(default_value.contains("@") && default_value.equals("@false")){
			    		return false;
			    	}			    	
		    	}
		    }
		    if(value!=null && value.length()>0){
		    	if(value.contains("@") && value.equals("@true")){
			    	return true;
			    }
			    if(value.contains("@") && value.equals("@false")){
			    	return false;
			    }			    	
		    }
		    if(default_value!=null && default_value.length()>0){
		    	if(default_value.contains("@") && default_value.equals("@true")){
			    	return true;
			    }
			    if(default_value.contains("@") && default_value.equals("@false")){
			    	return false;
			    }			    	
		    }	    		    			    	   
			return false;		
	}
	
	public static  String string(String code){
		//get first in local
		String val = getParameterLocal(code);
	    if(val!=null && val.length()>0 ){
	    	return val;
	    }
	    //
	    
	    Parameter parameter = getParameter(code);
	    if(parameter == null)
	    	return null;
	    String value = parameter.getValue();
    	String default_value = parameter.getDefault_value();
	    if(parameter.getDeleted() != null && parameter.getDeleted().equals("D")){
	    	if(default_value!=null && default_value.length()>0){
	    		return  default_value;
	    	}
	    }
	    if(value!=null && value.length()>0){
	    	return value;
	    }
	    if(default_value!=null && default_value.length()>0){
	    	return default_value;
	    }	    		    		    	  
		return "";
	}

}
