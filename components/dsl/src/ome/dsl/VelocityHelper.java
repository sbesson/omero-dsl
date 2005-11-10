/*
 * ome.dsl.VelocityHelper
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2005 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package ome.dsl;

//Java imports
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

// Third-party libraries
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

// Application-internal dependencies

/** utility class to setup a VelocityEngine and VelocityContext (with macros).
 * Makes methods availeble to parse a template to a String or a File (using Writer)
 */
public class VelocityHelper {

	private static Log log = LogFactory.getLog(VelocityHelper.class);
	
	private String macros;
	private VelocityEngine ve = new VelocityEngine();
	private VelocityContext vc = new VelocityContext();
	
	/** setups up a VelocityEngine with no macros file */
	public VelocityHelper(){
		this(null);	
	}
	
	/** setups up a VelocityEngine with the given macros file. Error thrown if the macros file is not available */
	public VelocityHelper(String base) { 
		ve.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.SimpleLog4JLogSystem" );
		ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
    	ve.setProperty("resource.loader","file, class");
    	ve.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    	if (null!=macros)
    		ve.setProperty( RuntimeConstants.VM_LIBRARY,macros);
    	try {
			ve.init();
		} catch (Exception e) {
			throw new RuntimeException("Velocity initialization exception.",e);
		}
	}
	
	/** adds an item to the VelocityContext */
	public void put(String key, Object value){
		vc.put(key,value);
	}
	
	/** parses the given template and returns the results as a String */
	public String invoke(String template){
        StringWriter sw = new StringWriter();
        invoke(template,sw);
        return sw.toString();
	}
	
	/** parses the given template and writes the results to Writer */
	public void invoke(String template, Writer w){
		try {
			InputStream in = VelocityHelper.class.getClassLoader().getResourceAsStream(template);
			
			if (null==in){
					throw new FileNotFoundException(template);
			}
			
        	InputStreamReader r = new InputStreamReader(in);
        	log.debug(Arrays.asList(vc.getKeys()));
        	ve.evaluate(vc,w,"Running template: "+template,r);
        } catch (Exception e){
        	throw new RuntimeException("Error invoking Velocity template:"+template,e);
		}
	}
	
}
