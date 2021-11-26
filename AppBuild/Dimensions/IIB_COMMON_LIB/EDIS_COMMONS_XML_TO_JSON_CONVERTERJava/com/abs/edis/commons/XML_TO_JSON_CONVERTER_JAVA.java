package com.abs.edis.commons;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class XML_TO_JSON_CONVERTER_JAVA extends MbJavaComputeNode {

	private static String XPATH_JSONTYPE_MAPPINGS = "XPATH_JSONTYPE_MAPPINGS";

	private XmlToJsonConverter converter;

	public void onInitialize() throws MbException {
		super.onInitialize();

		Map<String, String> properties = new LinkedHashMap<String, String>();
		String attr = (String) this
				.getUserDefinedAttribute(XPATH_JSONTYPE_MAPPINGS);
		if (attr != null) {
			String[] arr = attr.split(";");
			for (String exp : arr) {
				int begin = exp.indexOf("(");
				int end = exp.indexOf(")");
				String xpath = exp.substring(begin + 1, end);
				String mapping = exp.substring(0, begin);

				properties.put(xpath, mapping);
			}
		}

		converter = new XmlToJsonConverter(properties);

	}

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
			outMessage.getRootElement()
				.createElementAsLastChild(MbBLOB.PARSER_NAME)
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", 
						converter.convert(inMessage
								.getRootElement().getLastChild().getDOMNode()).getBytes());

			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be
			// handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
