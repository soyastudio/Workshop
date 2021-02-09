package soya.framework.tao;

import org.apache.xmlbeans.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;

public class XsdTreeBaseFlow extends TreeBaseFlow<SchemaTypeSystem, KnowledgeTree<XsdTreeBaseFlow.XsNode>, XsdTreeBaseFlow> {

    //
    public static XsdTreeBaseFlow newInstance() {
        return new XsdTreeBaseFlow();
    }

    public static XsdTreeBaseFlow fromYaml(String yaml) {
        return null;
    }

    public static XsdTreeBaseFlow fromJson(String json) {
        return null;
    }

    public static XsdTreeBaseFlow fromXml(String xml) {
        return null;
    }

    //
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements Barflow.BaselineBuilder<SchemaTypeSystem, KnowledgeTree<XsNode>> {

        private Barflow.Extractor<SchemaTypeSystem> extractor = new XmlSchemaExtractor();
        private Barflow.Digester<SchemaTypeSystem, KnowledgeTree<XsdTreeBaseFlow.XsNode>> digester = new XsTreeBaseDigester();

        private Builder() {
        }


        @Override
        public Barflow.BaselineBuilder<SchemaTypeSystem, KnowledgeTree<XsNode>> extractor(Barflow.Extractor<SchemaTypeSystem> extractor) {
            this.extractor = extractor;
            return this;
        }

        @Override
        public Barflow.BaselineBuilder<SchemaTypeSystem, KnowledgeTree<XsNode>> digester(Barflow.Digester<SchemaTypeSystem, KnowledgeTree<XsNode>> digester) {
            this.digester = digester;
            return this;
        }

        @Override
        public XsdTreeBase create() throws Barflow.FlowBuilderException {
            SchemaTypeSystem sts = extractor.extract();
            return new XsdTreeBase(sts, digester.digest(sts));
        }
    }

    public static class XmlSchemaExtractor implements Barflow.Extractor<SchemaTypeSystem> {

        private Object source;
        private SchemaTypeLoader schemaTypeLoader;
        private XmlOptions xmlOptions = new XmlOptions()
                .setErrorListener(null).setCompileDownloadUrls()
                .setCompileNoPvrRule();


        public XmlSchemaExtractor schemaTypeLoader(SchemaTypeLoader schemaTypeLoader) {
            this.schemaTypeLoader = schemaTypeLoader;
            return this;
        }

        public XmlSchemaExtractor xmlOptions(XmlOptions xmlOptions) {
            if (xmlOptions != null) {
                this.xmlOptions = xmlOptions;
            }
            return this;
        }

        public XmlSchemaExtractor string(String xmlString) {
            this.source = xmlString;
            return this;
        }

        public XmlSchemaExtractor file(File file) {
            this.source = file;
            return this;
        }

        public XmlSchemaExtractor url(URL url) {
            this.source = url;
            return this;
        }

        public XmlSchemaExtractor inputStream(InputStream inputStream) {
            this.source = inputStream;
            return this;
        }

        public XmlSchemaExtractor reader(Reader reader) {
            this.source = reader;
            return this;
        }

        public XmlSchemaExtractor xmlStreamReader(XMLStreamReader xmlStreamReader) {
            this.source = xmlStreamReader;
            return this;
        }

        @Override
        public SchemaTypeSystem extract() throws Barflow.FlowBuilderException {
            try {
                // Parse the XML Schema object first to get XML object
                XmlObject parsedSchema = parse(source, xmlOptions);

                // We may have more than schemas to validate with
                XmlObject[] schemas = new XmlObject[]{parsedSchema};

                // Compile the XML Schema to create a schema type system
                return XmlBeans.compileXsd(schemas, schemaTypeLoader, xmlOptions);

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
    }

    public static class XsTreeBaseDigester implements Barflow.Digester<SchemaTypeSystem, KnowledgeTree<XsNode>> {

        @Override
        public KnowledgeTree digest(SchemaTypeSystem source) {
            MoneyTree tree = null;
            SchemaType sType = source.documentTypes()[0];
            if (SchemaType.ELEMENT_CONTENT == sType.getContentType()) {

                SchemaLocalElement element = (SchemaLocalElement) sType.getContentModel();
                QName qName = element.getName();

                tree = MoneyTree.newInstance(qName.getLocalPart(), new XsNode(element));
                processParticle(element.getType().getContentModel(), true, tree.root(), tree);
            }

            return tree;
        }

        private void processParticle(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            switch (sp.getParticleType()) {
                case (SchemaParticle.ELEMENT):
                    processElement(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.SEQUENCE):
                    processSequence(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.CHOICE):
                    processChoice(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.ALL):
                    processAll(sp, mixed, parent, tree);
                    break;

                case (SchemaParticle.WILDCARD):
                    processWildCard(sp, mixed, parent, tree);
                    break;

                default:
                    // throw new Exception("No Match on Schema Particle Type: " + String.valueOf(sp.getParticleType()));
            }
        }

        private void processElement(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            SchemaLocalElement element = (SchemaLocalElement) sp;
            TreeNode treeNode = tree.create(parent, element.getName().getLocalPart(), new XsNode(element));
            SchemaProperty[] attrProps = sp.getType().getAttributeProperties();
            if (attrProps != null) {
                for (SchemaProperty property : attrProps) {
                    tree.create(treeNode, "@" + property.getName().getLocalPart(), new XsNode(property));
                }
            }

            if (element.getType().isSimpleType()) {
                // end
                if (!element.getType().isBuiltinType())
                    System.out.println("===== simple " + element.getName() + ": " + element.getType());

            } else if (element.getType().getContentModel() != null) {
                // next
                processParticle(element.getType().getContentModel(), mixed, treeNode, tree);

            } else {
                if (element.getType().getBaseType() != null) {
                    System.out.println("================== ??? " + element.getName() + ": " + element.getType().getBaseType().isSimpleType());

                }
            }
        }

        private void processSequence(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            SchemaParticle[] spc = sp.getParticleChildren();
            for (int i = 0; i < spc.length; i++) {
                processParticle(spc[i], mixed, parent, tree);
            }
        }

        private void processChoice(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            System.out.println(sp.getName());

        }

        private void processAll(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            System.out.println(sp.getName());
        }

        private void processWildCard(SchemaParticle sp, boolean mixed, TreeNode parent, Tree tree) {
            System.out.println(sp.getName());
        }
    }

    public static class XsdTreeBase implements KnowledgeTreeBase<SchemaTypeSystem, KnowledgeTree<XsNode>> {

        private SchemaTypeSystem origin;
        private KnowledgeTree<XsNode> knowledgeBase;

        protected XsdTreeBase(SchemaTypeSystem origin, KnowledgeTree<XsNode> knowledgeBase) {
            this.origin = origin;
            this.knowledgeBase = knowledgeBase;
        }

        @Override
        public SchemaTypeSystem origin() {
            return origin;
        }

        @Override
        public KnowledgeTree<XsNode> knowledgeBase() {
            return knowledgeBase;
        }

    }

    public static enum XsNodeType {
        Folder, Field, Attribute
    }

    public static class XsNode {
        private transient SchemaType schemaType;

        private QName name;
        private XsNodeType nodeType;

        private BigInteger minOccurs;
        private BigInteger maxOccurs;

        XsNode(SchemaField schemaField) {
            this.schemaType = schemaField.getType();

            this.name = schemaField.getName();
            this.minOccurs = schemaField.getMinOccurs();
            this.minOccurs = schemaField.getMaxOccurs();

            if (schemaField.getType().isSimpleType()) {
                nodeType = XsNodeType.Field;

            } else {
                nodeType = XsNodeType.Folder;

            }

        }

        XsNode(SchemaProperty schemaProperty) {
            this.schemaType = schemaProperty.getType();
            this.nodeType = XsNodeType.Attribute;

            this.name = schemaProperty.getName();
            this.minOccurs = schemaProperty.getMinOccurs();
            this.minOccurs = schemaProperty.getMaxOccurs();
        }

        public SchemaType getSchemaType() {
            return schemaType;
        }

        public QName getName() {
            return name;
        }

        public XsNodeType getNodeType() {
            return nodeType;
        }

        public BigInteger getMinOccurs() {
            return minOccurs;
        }

        public BigInteger getMaxOccurs() {
            return maxOccurs;
        }
    }

}
