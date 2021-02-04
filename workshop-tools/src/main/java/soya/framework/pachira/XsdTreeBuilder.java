package soya.framework.pachira;

import org.apache.xmlbeans.*;

import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public class XsdTreeBuilder implements Barflow.BaselineBuilder<MoneyTree> {

    private Object source;
    private SchemaTypeLoader schemaTypeLoader;
    private XmlOptions xmlOptions = new XmlOptions()
            .setErrorListener(null).setCompileDownloadUrls()
            .setCompileNoPvrRule();

    public XsdTreeBuilder schemaTypeLoader(SchemaTypeLoader schemaTypeLoader) {
        this.schemaTypeLoader = schemaTypeLoader;
        return this;
    }

    public XsdTreeBuilder xmlOptions(XmlOptions xmlOptions) {
        if (xmlOptions != null) {
            this.xmlOptions = xmlOptions;
        }
        return this;
    }

    public XsdTreeBuilder string(String xmlString) {
        this.source = xmlString;
        return this;
    }

    public XsdTreeBuilder file(File file) {
        this.source = file;
        return this;
    }

    public XsdTreeBuilder url(URL url) {
        this.source = url;
        return this;
    }

    public XsdTreeBuilder inputStream(InputStream inputStream) {
        this.source = inputStream;
        return this;
    }

    public XsdTreeBuilder reader(Reader reader) {
        this.source = reader;
        return this;
    }

    public XsdTreeBuilder xmlStreamReader(XMLStreamReader xmlStreamReader) {
        this.source = xmlStreamReader;
        return this;
    }

    @Override
    public MoneyTree create() throws Barflow.FlowBuilderException {
        try {
            // Parse the XML Schema object first to get XML object
            XmlObject parsedSchema = parse(source, xmlOptions);

            // We may have more than schemas to validate with
            XmlObject[] schemas = new XmlObject[]{parsedSchema};

            // Compile the XML Schema to create a schema type system
            SchemaTypeSystem schemaTypeSystem = XmlBeans.compileXsd(schemas, schemaTypeLoader, xmlOptions);

            SchemaType sType = schemaTypeSystem.documentTypes()[0];

            process(sType, null);


            MoneyTree tree = MoneyTree.newInstance("");

            return tree;

        } catch (XmlException | IOException e) {
            throw new Barflow.FlowBuilderException(e);

        }
    }

    private XmlObject parse(Object source, XmlOptions xmlOptions) throws XmlException, IOException {
        if (source == null) {
            throw new IllegalStateException("Source is not set.");

        } else if (source instanceof String) {
            return XmlObject.Factory.parse((String) source, xmlOptions);

        } else if (source instanceof File) {
            return XmlObject.Factory.parse((File) source, xmlOptions);

        } else if (source instanceof URL) {
            return XmlObject.Factory.parse((URL) source, xmlOptions);

        } else if (source instanceof XMLStreamReader) {
            return XmlObject.Factory.parse((XMLStreamReader) source, xmlOptions);

        } else if (source instanceof InputStream) {
            return XmlObject.Factory.parse((InputStream) source, xmlOptions);

        } else if (source instanceof Reader) {
            return XmlObject.Factory.parse((Reader) source, xmlOptions);

        } else {
            throw new IllegalArgumentException("Source type is not supported.");

        }
    }

    private void process(SchemaType stype, MoneyTree.DefaultTreeNode parent) {
        if (stype.isSimpleType()) {
            processSimpleType(stype, parent);

        } else {
            switch (stype.getContentType()) {
                case SchemaType.NOT_COMPLEX_TYPE:

                case SchemaType.EMPTY_CONTENT:
                    // noop
                    break;

                case SchemaType.SIMPLE_CONTENT: {
                    processSimpleType(stype, parent);
                }
                break;

                case SchemaType.MIXED_CONTENT:
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), parent, true);
                    }
                    break;

                case SchemaType.ELEMENT_CONTENT:
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), parent, false);
                    }
                    break;
            }
        }

    }

    private void processSimpleType(SchemaType stype, MoneyTree.DefaultTreeNode parent) {
        if(stype.isBuiltinType()) {
            System.out.println("======== Buildin: " + stype.getName());

        } else {
            System.out.println("======== SimpeleType: " + stype.getName());

        }

    }

    private void processParticle(SchemaParticle sp, MoneyTree.DefaultTreeNode parent, boolean mixed) {

        switch (sp.getParticleType()) {
            case (SchemaParticle.ELEMENT):
                processElement(sp, parent, mixed);
                break;
            case (SchemaParticle.SEQUENCE):
                processSequence(sp, parent, mixed);
                break;
            case (SchemaParticle.CHOICE):
                processChoice(sp, parent, mixed);
                break;
            case (SchemaParticle.ALL):
                processAll(sp, parent, mixed);
                break;
            case (SchemaParticle.WILDCARD):
                processWildCard(sp, parent, mixed);
                break;
            default:
                // throw new Exception("No Match on Schema Particle Type: " + String.valueOf(sp.getParticleType()));
        }
    }

    private void processElement(SchemaParticle sp, MoneyTree.DefaultTreeNode parent, boolean mixed) {
        SchemaLocalElement element = (SchemaLocalElement) sp;
        System.out.println("element: " + sp.getName() + ": " + element.getType().isSimpleType() );
        process(element.getType(), parent);

    }

    private void processSequence(SchemaParticle sp, MoneyTree.DefaultTreeNode parent, boolean mixed) {
        SchemaParticle[] spc = sp.getParticleChildren();
        for (int i = 0; i < spc.length; i++) {
            processParticle(spc[i], parent, mixed);
        }

    }

    private void processChoice(SchemaParticle sp, MoneyTree.DefaultTreeNode parent, boolean mixed) {
        System.out.println(sp.getName());

    }

    private void processAll(SchemaParticle sp, MoneyTree.DefaultTreeNode parent, boolean mixed) {
        System.out.println(sp.getName());

    }

    private void processWildCard(SchemaParticle sp, MoneyTree.DefaultTreeNode parent, boolean mixed) {
        System.out.println(sp.getName());

    }

    public static XsdTreeBuilder newInstance() {
        return new XsdTreeBuilder();
    }

    public static void main(String[] args) {
        MoneyTree moneyTree = XsdTreeBuilder.newInstance()
                .file(new File("C:/github/Workshop/Repository/CMM/BOD/GetCustomerPreferences.xsd"))
                .create();
    }
}
