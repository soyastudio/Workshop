package com.abs.erums.groceryorder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.SessionObject;

public class ESED_GroceryOrder_Input_Normalizer_JavaCompute extends
		MbJavaComputeNode {

	private static String PROTEGRITY_USER_ATTRIBUTE = "ProtegityAppID";
	private static String PROTEGRITY_RULE_ATTRIBUTE = "PROTEGRITY_RULE";

	private String user = "es00ed";
	private Set<ProtegrityMapping> mappings = new LinkedHashSet<>();

	@Override
	public void onInitialize() throws MbException {
		super.onInitialize();
		
		
		if(getUserDefinedAttribute(PROTEGRITY_USER_ATTRIBUTE) != null) {
			user = (String)getUserDefinedAttribute(PROTEGRITY_USER_ATTRIBUTE);
		}
			
		if(getUserDefinedAttribute(PROTEGRITY_RULE_ATTRIBUTE) != null) {
			String rules = (String)getUserDefinedAttribute(PROTEGRITY_RULE_ATTRIBUTE);
			String[] arr = rules.split(";");
			for(String rule: arr) {
				addProtegrityMapping(rule);
			}
		}
	}
	
	private void addProtegrityMapping(String rule) {
		String token = rule.trim();
		int index = token.indexOf('(');
		if(index > 0 && token.endsWith(")")){
			String codeBook = token.substring(0, index);
			String xpath = token.substring(index+ 1, token.length() - 1); 	
			mappings.add(new ProtegrityMapping(xpath, codeBook, null));
		}
	}

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			
			// ----------------------------------------------------------
			// Add user code below
			
			// go through the
			MbElement root = outMessage.getRootElement().getLastChild()
					.getFirstChild();
			
			Object cards = root.evaluateXPath("paymentDetails/Item[*]");
			if (cards instanceof List) {
				List<?> cardList = (List<?>)cards;
				for(Object c: cardList) {
					MbElement card = (MbElement)c;
					MbElement expMonth = card.getFirstElementByPath("cardExpiryMonth");
					MbElement expYear = card.getFirstElementByPath("cardExpiryYear");
					if(expMonth != null && expYear != null) {
						String my = expMonth.getDOMNode().getTextContent() + expYear.getDOMNode().getTextContent();
						expMonth.getDOMNode().setTextContent(my);
					}
				}
			}
			
			for (ProtegrityMapping pm : mappings) {
				String xpath = pm.getXpath();			
				Object result = root.evaluateXPath(xpath);
				if (result instanceof List) {
					List<?> list = (List<?>) result;
					for (Object o : list) {
						MbElement e = (MbElement) o;
						protect(e, pm.converter, pm.getCodeBook(), user);
					}
				}
			}			

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
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new ProtegrityException(e);
		}
		
		
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

	private void protect(MbElement e, Converter converter, String codeBook,
			String user) throws ProtegrityException, MbException {
		Node node = e.getDOMNode();
		
		String text = node.getTextContent();
		if (converter != null) {
			text = converter.convert(text);
		}

		if (text != null && codeBook != null) {
			
			String token;			
			if("LOCAL".equalsIgnoreCase(user)) {
				token = _tokenize(text, codeBook, user);
			} else {
				token = tokenize(text, codeBook, user);
			}
			
			node.setTextContent(token);
		}
	}
	
	private String tokenize(String value, String codeBook, String user) throws ProtegrityException {
		
		if (value == null || value.trim().length() == 0) {
			return value;
		}
		
		String result = value;
		
		String[] in = new String[] { value };
		String[] out = new String[1];

		Protector protector;
		SessionObject session;
		
		try{
			protector = Protector.getProtector();
			session = protector.createSession(user);
			
			if(session == null){
				throw new ProtegrityException(new IllegalArgumentException("Cannot find user: " + user));
			}
			
		} catch(Exception e) {
			throw new ProtegrityException(e);
			
		}
		
		
		try{
			boolean boo = protector.protect(session, codeBook, in, out);
			if(boo && out != null && out.length > 0) {
				result = out[0];
				
			} else {
				throw new ProtegrityException("Tokenize failed for user: " + user + " on Codebook: " + codeBook + " with text: " + value);
			}
			
		} catch(Exception e) {
			throw new ProtegrityException(e);
			
		}
		
		return result;
		
	}
	
	private String _tokenize(String value, String codeBook, String user) throws ProtegrityException{
		return codeBook + "(" + value + ")";
	}
	
	static class ProtegrityException extends RuntimeException{
		private static final long serialVersionUID = 1L;
		
		public ProtegrityException(String msg) {
			super(msg);
		}

		public ProtegrityException(Throwable t) {
			super(t);
		}		
	}

	class ProtegrityMapping {
		private String xpath;
		private String codeBook;
		private Converter converter;

		public ProtegrityMapping(String xpath, String codeBook) {
			super();
			this.xpath = xpath;
			this.codeBook = codeBook;
		}

		public ProtegrityMapping(String xpath, String codeBook,
				Converter converter) {
			super();
			this.xpath = xpath;
			this.codeBook = codeBook;
			this.converter = converter;
		}

		public String getXpath() {
			return xpath;
		}

		public String getCodeBook() {
			return codeBook;
		}

		public Converter getConverter() {
			return converter;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ProtegrityMapping) {
				ProtegrityMapping other = (ProtegrityMapping) o;
				return this.xpath.equals(other.getXpath());
			}

			return false;
		}

	}

	interface Converter {
		String convert(String s);
	}
	
	class DoubleFormatConverter implements Converter {
		@Override
		public String convert(String input) {
			return Double.toString(Double.parseDouble(input));
		}
	}

	class DateConverter implements Converter {

		@Override
		public String convert(String input) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(
						"EEE MMM d, yyyy");
				SimpleDateFormat destFormat = new SimpleDateFormat("MM/dd/yyyy");

				Date min = format.parse("Fri Jan 1, 0600");
				Date max = format.parse("Wed Nov 27, 3337");

				Date date = format.parse(input);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				if (date.getTime() <= min.getTime()
						|| date.getTime() >= max.getTime()) {
					return "01/01/1900";
				}

				return destFormat.format(calendar.getTime());

			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

	}


}
