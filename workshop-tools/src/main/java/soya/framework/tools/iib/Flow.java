package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;

public class Flow {
    private String application;
    private String brokerSchema;
    private String name;

    private String bundleName;

    public Flow(String application, String brokerSchema, String name) {
        this.application = application;
        this.brokerSchema = brokerSchema;
        this.name = name;

        this.bundleName = brokerSchema.replaceAll("\\.", "/") + "/" + name;
    }

    public String getApplication() {
        return application;
    }

    public String getBrokerSchema() {
        return brokerSchema;
    }

    public String getName() {
        return name;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getDefaultEsqlModule() {
        return this.name + "_COMPUTE";
    }

    public String getDefaultEsqlLink() {
        return "esql://routine/" + brokerSchema.replaceAll("\\.", "/") + "#" + getDefaultEsqlModule() + ".Main";
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", builder);
        StringBuilderUtils.println("<ecore:EPackage xmi:version=\"2.0\"", builder);
        StringBuilderUtils.println("xmlns:xmi=\"http://www.omg.org/XMI\"", builder, 1);
        StringBuilderUtils.println("xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\"", builder, 1);
        StringBuilderUtils.println("xmlns:eflow=\"http://www.ibm.com/wbi/2005/eflow\"", builder, 1);

        StringBuilderUtils.println("xmlns:utility=\"http://www.ibm.com/wbi/2005/eflow_utility\" nsURI=\""
                + bundleName + ".msgflow\" nsPrefix=\"" + bundleName.replaceAll("/", "_") + ".msgflow\">", builder, 1);


        StringBuilderUtils.println("<eClassifiers xmi:type=\"eflow:FCMComposite\" name=\"FCMComposite_1\">", builder, 1);
        StringBuilderUtils.println("<eSuperTypes href=\"http://www.ibm.com/wbi/2005/eflow#//FCMBlock\"/>", builder, 2);

        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\""
                        + name + "\" bundleName=\"" + bundleName + "\" pluginId=\"" + application + "\"/>",
                builder, 2);

        StringBuilderUtils.println("<colorGraphic16 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + application + "/icons/full/obj16/" + name + ".gif\"/>",
                builder, 2);
        StringBuilderUtils.println("<colorGraphic32 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + application + "/icons/full/obj32/" + name + ".gif\"/>",
                builder, 2);

        StringBuilderUtils.println("<composition>", builder, 3);


        StringBuilderUtils.println("</composition>", builder, 3);

        StringBuilderUtils.println("<propertyOrganizer>", builder, 3);
        StringBuilderUtils.println("</propertyOrganizer>", builder, 3);

        StringBuilderUtils.println("<stickyBoard/>", builder, 3);
        StringBuilderUtils.println("</eClassifiers>", builder, 1);
        StringBuilderUtils.println("</ecore:EPackage>", builder);

        return builder.toString();
    }

    public static Flow createBasicSubflow(String application, String brokerSchema, String name) {
        Flow flow = new Flow(application, brokerSchema, name);

        return flow;
    }

}
