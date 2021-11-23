package com.abs.cmnflows;

import java.util.List;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbXPath;

public class EncryptedExceptionSubFlow_JavaCompute extends
		com.ibm.broker.javacompute.MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		String encryptionKey = "EDISEncryptionKey";
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);

			// ----------------------------------------------------------
			// Add user code below
			MbElement root = outMessage.getRootElement().getLastChild()
					.getFirstChild();
			MbXPath xp = new MbXPath(
					"/CommonBaseEvent/extendedDataElements[@name='ErrorDetails']/ActualMsg");

			@SuppressWarnings("unchecked")
			List<MbElement> nodes = (List<MbElement>) root.evaluateXPath(xp);
			if (nodes != null && nodes.size() > 0) {
				MbElement element = nodes.get(0).getLastChild();

				String actualMsg = element.getDOMNode().getTextContent();
				if (actualMsg != null) {
					actualMsg = AES.encrypt(actualMsg, encryptionKey);
					element.getDOMNode().setTextContent(actualMsg);
				}
			}

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
			throw new RuntimeException(e);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
