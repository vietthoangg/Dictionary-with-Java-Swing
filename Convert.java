import java.util.Enumeration;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

//Convert text from JEditorPane to UTF-8 string
//Source: StackOverFlow

public class Convert {
	
	public static String toUnicode(JEditorPane editorPane) {
		Document doc = (HTMLDocument) editorPane.getDocument();
	    StringBuilder sb = new StringBuilder();
	    javax.swing.text.Element[] styles = doc.getRootElements();
	    for (int i = 0; i < styles.length; i++) {
	        int size = styles[i].getElementCount();
	        if (!styles[i].getName().contains("bidi root")) {	        	
	        	sb.append("<" + styles[i].getName() + ">");
	        }
	        for (int j = 0; j < size; j++) {
	            String element = styles[i].getElement(j).getName();
	            if (element.equals("body")) {
	                int subsize = styles[i].getElement(j).getElementCount();
	                for (int k = 0; k < subsize; k++) {
	                    element = styles[i].getElement(j).getElement(k).getName();
	                    if (element.equals("p-implied")) {
	                        int subsubsize = styles[i].getElement(j).getElement(k).getElementCount();
	                        String cond = "fail", bold = "</b>", italic = "</i>";
	                        for (int l = 0; l < subsubsize; l++) {
	                            javax.swing.text.Element elem = styles[i].getElement(j).getElement(k).getElement(l);
	                            element = elem.getName();
	                            if (!element.contains("content")) {			                            	
	                                sb.append("<" + element + ">");
	                            }
	                            if (element.equals("content")) {
	                                AttributeSet attributes = elem.getAttributes();
	                                Enumeration<?> attrs = attributes.getAttributeNames();
	                                while (attrs.hasMoreElements()) {
	                                    String rft = attrs.nextElement().toString();
	                                    if (rft.equals("b")) {				                                    	
	                                        sb.append("<" + rft + ">");
	                                        cond = "passb";
	                                    } else if (rft.equals("i")) {
	                                        sb.append("i<" + rft + ">");
	                                        cond = "passi";
	                                    }
	                                }
	                            }
	                            try {				   
	                                sb.append(elem.getDocument().getText(elem.getStartOffset(), (elem.getEndOffset() - elem.getStartOffset())));
	                            } catch (BadLocationException e) {}
	                            if (cond.equals("passi")) {				                                
	                            	sb.append(italic);
	                            }
	                            if (cond.equals("passb")) {                            	
	                                sb.append(bold);
	                            }
	                            cond = "fail";
	                        }
	                    }
	                }
	            }
	        }
	    }
	    return sb.toString();
	}

}
