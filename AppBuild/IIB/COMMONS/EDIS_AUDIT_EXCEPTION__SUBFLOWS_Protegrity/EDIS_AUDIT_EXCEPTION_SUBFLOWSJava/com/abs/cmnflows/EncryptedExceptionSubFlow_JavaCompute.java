package com.abs.cmnflows;

import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbNamespaceBindings;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXPath;
import com.abs.cmnflows.AES;

public class EncryptedExceptionSubFlow_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		String encryptionKey = "EDISEncryptionKey";
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			 MbElement inputRoot = outMessage.getRootElement();

			 MbElement docRoot = (MbElement) inputRoot.getLastChild().getFirstChild();
			 // System.out.println("*********docRoot = " + docRoot.getName());

			// path to get /CommonBaseEvent/extendedDataElements[2]/ActualMsg
			MbXPath xp = new MbXPath("/CommonBaseEvent/extendedDataElements[@name='ErrorDetails']/ActualMsg");
			// List<MbElement> nodeset = (List<MbElement>) outMessage
			//		.evaluateXPath(xp);
			
			List<MbElement> actualMsg = (List<MbElement>) docRoot.evaluateXPath(xp);
			//List<MbElement> actualMsg = (List<MbElement>) inputRoot.evaluateXPath("/CommonBaseEvent/extendedDataElements[*]/ActualMsg");
			// MbElement element = actualMsg.get(0).getFirstChild();
			MbElement element = actualMsg.get(0).getLastChild();
			if (actualMsg != null && actualMsg.size()>0) {
				// Convert actual message to string for encryption
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				StringWriter sw = new StringWriter();
				t.transform(new DOMSource(element.getDOMNode()), new StreamResult(sw));
				String sourceString = sw.toString();
				//System.out.println("*********DOM Message = " + sourceString);
				
				// Encrypt the string
				String protectedString = AES.encrypt(sourceString, encryptionKey);
				// System.out.println("*********protectedString = " + protectedString);
				// System.out.println("*********element.getParent() = " + element.getParent().getName());
				
				// Put theEncrypted value back to Actual Message 
				actualMsg.get(0).setValue(protectedString);
				
				// Remove Original Actual Message
				element.delete();
				// String unencryptedString = AES.decrypt(protectedString, encryptionKey);
				// System.out.println("*********unencryptedString = " + unencryptedString);
			} else 
				System.out.println("********* actualMsg is null or size 0!!!!!!!!!!");		
			// End of user code
			// ----------------------------------------------------------

		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			System.out.println("*********MbException = " + e.toString());
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			System.out.println("*********RuntimeException = " + e.toString());
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			System.out.println("*********Exception = " + e.toString());
			// Comment out exception throwing since this is the exception handling code.
			//throw new MbUserException(this, "evaluate()", "", "", e.toString(),	null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
