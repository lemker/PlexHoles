import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;

class XML {
    static ArrayList<String> parseStringAsNodesValue(String input, String expression) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(input)));
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Find all media types of "show" and get their keys
            XPathExpression expr = xpath.compile(expression);
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            // Initialize ArrayList for output items
            ArrayList<String> output = new ArrayList<String>();

            // Output NodeList
            for (int i = 0; i < nl.getLength(); i++) {
                output.add(nl.item(i).getNodeValue());
            }

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
