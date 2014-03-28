/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2011, 2012  
# US Government Users Restricted Rights - Use, duplication or
# disclosure restricted by GSA ADP Schedule Contract with
# IBM Corp.
*/
package com.ibm.streamsx.inet.rest.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingOutput;

/**
 * Inject a tuple into an output port from a HTTP GET or POST.
 *
 */
public class InjectTuple extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1938530466506624733L;

	private final StreamingOutput<OutputTuple> port;
	
	public InjectTuple(StreamingOutput<OutputTuple> port) {
		this.port = port;
	}
	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
	
    @Override
    public void doPost(HttpServletRequest request,
		       HttpServletResponse response)
	throws ServletException, IOException
 {
	OutputTuple tuple = port.newTuple();
	populateTupleFromRequest(request, tuple);

	try {
		port.submit(tuple);
	} catch (Exception e) {
		throw new IOException(e);
	}
	
	response.setStatus(HttpServletResponse.SC_NO_CONTENT);
 }

    public static void populateTupleFromRequest(HttpServletRequest request,
            OutputTuple tuple) {
        for (Attribute attr : tuple.getStreamSchema()) {
              final String value = request.getParameter(attr.getName());
              if (value == null)
                  continue;
              
              final int ai = attr.getIndex();
              switch (attr.getType().getMetaType()) {
              case INT8:
            	  tuple.setByte(ai, Byte.parseByte(value));
            	  break;
              case INT16:
            	  tuple.setShort(ai, Short.parseShort(value));
            	  break;
              case INT32:
            	  tuple.setInt(ai, Integer.parseInt(value));
            	  break;
              case INT64:
            	  tuple.setLong(ai, Long.parseLong(value));
            	  break;
              case FLOAT32:
            	  tuple.setFloat(ai, Float.parseFloat(value));
            	  break;
              case FLOAT64:
            	  tuple.setDouble(ai, Double.parseDouble(value));
            	  break;
              case BOOLEAN:
                  tuple.setBoolean(ai, Boolean.parseBoolean(value));
              default:
            	  tuple.setString(ai, value);
            	  break;
              }
        }
    }
}
