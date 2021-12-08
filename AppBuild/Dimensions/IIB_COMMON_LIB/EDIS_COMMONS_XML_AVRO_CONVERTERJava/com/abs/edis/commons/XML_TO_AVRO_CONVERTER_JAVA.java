package com.abs.edis.commons;

import org.apache.avro.Schema;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class XML_TO_AVRO_CONVERTER_JAVA extends MbJavaComputeNode {
	private static final String AVRO_SCHEMA = "AVRO_SCHEMA";
	private static final String AVRO_SCHEMA_ID = "AVRO_SCHEMA_ID";
	private static final String AVRO_ENCODER = "AVRO_ENCODER";
	
	private Schema schema;
	private int id;
	private String format = XmlToAvroConverter.PLAIN_JSON_FORMAT;

	@Override
	public void onInitialize() throws MbException {
		super.onInitialize();
		String attr = (String) this.getUserDefinedAttribute(AVRO_SCHEMA);
		
		this.schema = XmlToAvroConverter.parse(attr.getBytes());
		
		if(this.getUserDefinedAttribute(AVRO_SCHEMA_ID) != null){
			this.id = (Integer)this.getUserDefinedAttribute(AVRO_SCHEMA_ID);
		}
		
		if(this.getUserDefinedAttribute(AVRO_ENCODER) != null) {
			this.format = (String) getUserDefinedAttribute(AVRO_ENCODER);
		}
	}

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below

			outMessage
					.getRootElement()
					.createElementAsLastChild(MbBLOB.PARSER_NAME)
					.createElementAsLastChild(
							MbElement.TYPE_NAME_VALUE,
							"BLOB",
							XmlToAvroConverter.convert(inMessage
									.getRootElement().getLastChild()
									.getLastChild().getDOMNode(), schema, id, format));

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
