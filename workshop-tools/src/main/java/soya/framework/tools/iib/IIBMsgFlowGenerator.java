package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;

public class IIBMsgFlowGenerator {
    private IIBMsgFlowGenerator() {
    }

    public static String generateMsgFlow(Flow flow) {

        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", builder);
        StringBuilderUtils.println("<ecore:EPackage xmi:version=\"2.0\"", builder);
        StringBuilderUtils.println("xmlns:xmi=\"http://www.omg.org/XMI\"", builder, 1);
        StringBuilderUtils.println("xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\"", builder, 1);
        StringBuilderUtils.println("xmlns:eflow=\"http://www.ibm.com/wbi/2005/eflow\"", builder, 1);

        // ===== Input/Output:
        StringBuilderUtils.println("xmlns:ComIbmWSInput.msgnode=\"ComIbmWSInput.msgnode\"", builder, 1);
        StringBuilderUtils.println("xmlns:ComIbmWSReply.msgnode=\"ComIbmWSReply.msgnode\"", builder, 1);

        // ===== Subflows:
        StringBuilderUtils.println("xmlns:com_abs_cmnflows_Audit_Validate_Input.subflow=\"com/abs/cmnflows/Audit_Validate_Input.subflow\"", builder, 1);
        StringBuilderUtils.println("xmlns:com_abs_cmnflows_Audit_Validate_Output.subflow=\"com/abs/cmnflows/Audit_Validate_Output.subflow\"", builder, 1);
        StringBuilderUtils.println("xmlns:com_abs_cmnflows_ExceptionSubFlow.subflow=\"com/abs/cmnflows/ExceptionSubFlow.subflow\"", builder, 1);
        StringBuilderUtils.println("xmlns:template_broker_schema_TEMPLATE_SUB_FLOW.subflow=\""
                + flow.getBundleName() + ".subflow\"",
                builder, 1);

        // ===== main:
        StringBuilderUtils.println("xmlns:utility=\"http://www.ibm.com/wbi/2005/eflow_utility\" nsURI=\""
                + flow.getBundleName() + ".msgflow\" nsPrefix=\"" + flow.getBundleName().replaceAll("/", "_") + ".msgflow\">", builder, 1);


        StringBuilderUtils.println("<eClassifiers xmi:type=\"eflow:FCMComposite\" name=\"FCMComposite_1\">", builder, 1);
        StringBuilderUtils.println("<eSuperTypes href=\"http://www.ibm.com/wbi/2005/eflow#//FCMBlock\"/>", builder, 2);

        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\""
                        + flow.getName() + "\" bundleName=\"" + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 2);

        StringBuilderUtils.println("<colorGraphic16 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + flow.getApplication() + "/icons/full/obj16/" + flow.getName() + ".gif\"/>",
                builder, 2);
        StringBuilderUtils.println("<colorGraphic32 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + flow.getApplication() + "/icons/full/obj32/" + flow.getName() + ".gif\"/>",
                builder, 2);

        StringBuilderUtils.println("<composition>", builder, 3);

        // ==================== Nodes:
        // Input Node
        StringBuilderUtils.println("<nodes xmi:type=\"eflow:FCMSource\" xmi:id=\"InTerminal.Input\" location=\"200,240\">", builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\"InTerminal.Input\" bundleName=\""
                        + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // Output Node
        StringBuilderUtils.println("<nodes xmi:type=\"eflow:FCMSink\" xmi:id=\"OutTerminal.Output\" location=\"1000,240\">", builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\"OutTerminal.Output\" bundleName=\""
                        + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // Transformer Node
        StringBuilderUtils.println("<nodes xmi:type=\"ComIbmCompute.msgnode:FCMComposite_1\" xmi:id=\"FCMComposite_1_1\" location=\"600,240\" computeExpression=\""
                        + flow.getDefaultEsqlLink() + "\">",
                builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:ConstantString\" string=\""
                        + flow.getDefaultEsqlModule() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // ==================== connections:
        StringBuilderUtils.println("<connections xmi:type=\"eflow:FCMConnection\" xmi:id=\"FCMConnection_1\" targetNode=\"FCMComposite_1_1\" sourceNode=\"InTerminal.Input\" sourceTerminalName=\"OutTerminal.out\" targetTerminalName=\"InTerminal.in\"/>",
                builder, 4);
        StringBuilderUtils.println("<connections xmi:type=\"eflow:FCMConnection\" xmi:id=\"FCMConnection_2\" targetNode=\"OutTerminal.Output\" sourceNode=\"FCMComposite_1_1\" sourceTerminalName=\"OutTerminal.out\" targetTerminalName=\"InTerminal.in\"/>",
                builder, 4);

        StringBuilderUtils.println("</composition>", builder, 3);

        StringBuilderUtils.println("<propertyOrganizer>", builder, 3);
        StringBuilderUtils.println("</propertyOrganizer>", builder, 3);

        StringBuilderUtils.println("<stickyBoard/>", builder, 3);
        StringBuilderUtils.println("</eClassifiers>", builder, 1);
        StringBuilderUtils.println("</ecore:EPackage>", builder);

        return builder.toString();
    }

    public static String generateTestFlow(Flow flow) {

        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", builder);
        StringBuilderUtils.println("<ecore:EPackage xmi:version=\"2.0\"", builder);
        StringBuilderUtils.println("xmlns:xmi=\"http://www.omg.org/XMI\"", builder, 1);
        StringBuilderUtils.println("xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\"", builder, 1);
        StringBuilderUtils.println("xmlns:eflow=\"http://www.ibm.com/wbi/2005/eflow\"", builder, 1);

        // ===== Input/Output:
        StringBuilderUtils.println("xmlns:ComIbmWSInput.msgnode=\"ComIbmWSInput.msgnode\"", builder, 1);
        StringBuilderUtils.println("xmlns:ComIbmWSReply.msgnode=\"ComIbmWSReply.msgnode\"", builder, 1);

        // ===== Subflows:
        StringBuilderUtils.println("xmlns:com_abs_cmnflows_ExceptionSubFlow.subflow=\"com/abs/cmnflows/ExceptionSubFlow.subflow\"", builder, 1);
        StringBuilderUtils.println("xmlns:template_broker_schema_TEMPLATE_SUB_FLOW.subflow=\""
                        + flow.getBundleName() + ".subflow\"",
                builder, 1);

        // ===== main:
        StringBuilderUtils.println("xmlns:utility=\"http://www.ibm.com/wbi/2005/eflow_utility\" nsURI=\""
                + flow.getBundleName() + ".msgflow\" nsPrefix=\"" + flow.getBundleName().replaceAll("/", "_") + ".msgflow\">", builder, 1);

        StringBuilderUtils.println("<eClassifiers xmi:type=\"eflow:FCMComposite\" name=\"FCMComposite_1\">", builder, 1);
        StringBuilderUtils.println("<eSuperTypes href=\"http://www.ibm.com/wbi/2005/eflow#//FCMBlock\"/>", builder, 2);

        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\""
                        + flow.getName() + "\" bundleName=\"" + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 2);

        StringBuilderUtils.println("<colorGraphic16 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + flow.getApplication() + "/icons/full/obj16/" + flow.getName() + ".gif\"/>",
                builder, 2);
        StringBuilderUtils.println("<colorGraphic32 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + flow.getApplication() + "/icons/full/obj32/" + flow.getName() + ".gif\"/>",
                builder, 2);

        StringBuilderUtils.println("<composition>", builder, 3);

        // ==================== Nodes:
        // Input Node
        StringBuilderUtils.println("<nodes xmi:type=\"eflow:FCMSource\" xmi:id=\"InTerminal.Input\" location=\"200,240\">", builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\"InTerminal.Input\" bundleName=\""
                        + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // Output Node
        StringBuilderUtils.println("<nodes xmi:type=\"eflow:FCMSink\" xmi:id=\"OutTerminal.Output\" location=\"1000,240\">", builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\"OutTerminal.Output\" bundleName=\""
                        + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // Transformer Node
        StringBuilderUtils.println("<nodes xmi:type=\"ComIbmCompute.msgnode:FCMComposite_1\" xmi:id=\"FCMComposite_1_1\" location=\"600,240\" computeExpression=\""
                        + flow.getDefaultEsqlLink() + "\">",
                builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:ConstantString\" string=\""
                        + flow.getDefaultEsqlModule() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // ==================== connections:
        StringBuilderUtils.println("<connections xmi:type=\"eflow:FCMConnection\" xmi:id=\"FCMConnection_1\" targetNode=\"FCMComposite_1_1\" sourceNode=\"InTerminal.Input\" sourceTerminalName=\"OutTerminal.out\" targetTerminalName=\"InTerminal.in\"/>",
                builder, 4);
        StringBuilderUtils.println("<connections xmi:type=\"eflow:FCMConnection\" xmi:id=\"FCMConnection_2\" targetNode=\"OutTerminal.Output\" sourceNode=\"FCMComposite_1_1\" sourceTerminalName=\"OutTerminal.out\" targetTerminalName=\"InTerminal.in\"/>",
                builder, 4);

        StringBuilderUtils.println("</composition>", builder, 3);

        StringBuilderUtils.println("<propertyOrganizer>", builder, 3);
        StringBuilderUtils.println("</propertyOrganizer>", builder, 3);

        StringBuilderUtils.println("<stickyBoard/>", builder, 3);
        StringBuilderUtils.println("</eClassifiers>", builder, 1);
        StringBuilderUtils.println("</ecore:EPackage>", builder);

        return builder.toString();
    }

    public static String generateSubFlow(Flow flow) {

        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", builder);
        StringBuilderUtils.println("<ecore:EPackage xmi:version=\"2.0\"", builder);
        StringBuilderUtils.println("xmlns:xmi=\"http://www.omg.org/XMI\"", builder, 1);
        StringBuilderUtils.println("xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\"", builder, 1);
        StringBuilderUtils.println("xmlns:eflow=\"http://www.ibm.com/wbi/2005/eflow\"", builder, 1);
        StringBuilderUtils.println("xmlns:ComIbmCompute.msgnode=\"ComIbmCompute.msgnode\"", builder, 1);

        StringBuilderUtils.println("xmlns:utility=\"http://www.ibm.com/wbi/2005/eflow_utility\" nsURI=\""
                + flow.getBundleName() + ".subflow\" nsPrefix=\"" + flow.getBundleName().replaceAll("/", "_") + ".subflow\">", builder, 1);


        StringBuilderUtils.println("<eClassifiers xmi:type=\"eflow:FCMComposite\" name=\"FCMComposite_1\">", builder, 1);
        StringBuilderUtils.println("<eSuperTypes href=\"http://www.ibm.com/wbi/2005/eflow#//FCMBlock\"/>", builder, 2);

        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\""
                        + flow.getName() + "\" bundleName=\"" + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 2);

        StringBuilderUtils.println("<colorGraphic16 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + flow.getApplication() + "/icons/full/obj16/" + flow.getName() + ".gif\"/>",
                builder, 2);
        StringBuilderUtils.println("<colorGraphic32 xmi:type=\"utility:GIFFileGraphic\" resourceName=\"platform:/plugin/"
                        + flow.getApplication() + "/icons/full/obj32/" + flow.getName() + ".gif\"/>",
                builder, 2);

        StringBuilderUtils.println("<composition>", builder, 3);

        // ==================== Nodes:
        // Input Node
        StringBuilderUtils.println("<nodes xmi:type=\"eflow:FCMSource\" xmi:id=\"InTerminal.Input\" location=\"200,240\">", builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\"InTerminal.Input\" bundleName=\""
                        + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // Output Node
        StringBuilderUtils.println("<nodes xmi:type=\"eflow:FCMSink\" xmi:id=\"OutTerminal.Output\" location=\"1000,240\">", builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:TranslatableString\" key=\"OutTerminal.Output\" bundleName=\""
                        + flow.getBundleName() + "\" pluginId=\"" + flow.getApplication() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // Transformer Node
        StringBuilderUtils.println("<nodes xmi:type=\"ComIbmCompute.msgnode:FCMComposite_1\" xmi:id=\"FCMComposite_1_1\" location=\"600,240\" computeExpression=\""
                        + flow.getDefaultEsqlLink() + "\">",
                builder, 4);
        StringBuilderUtils.println("<translation xmi:type=\"utility:ConstantString\" string=\""
                        + flow.getDefaultEsqlModule() + "\"/>",
                builder, 5);
        StringBuilderUtils.println("</nodes>", builder, 4);

        // ==================== connections:
        StringBuilderUtils.println("<connections xmi:type=\"eflow:FCMConnection\" xmi:id=\"FCMConnection_1\" targetNode=\"FCMComposite_1_1\" sourceNode=\"InTerminal.Input\" sourceTerminalName=\"OutTerminal.out\" targetTerminalName=\"InTerminal.in\"/>",
                builder, 4);
        StringBuilderUtils.println("<connections xmi:type=\"eflow:FCMConnection\" xmi:id=\"FCMConnection_2\" targetNode=\"OutTerminal.Output\" sourceNode=\"FCMComposite_1_1\" sourceTerminalName=\"OutTerminal.out\" targetTerminalName=\"InTerminal.in\"/>",
                builder, 4);

        StringBuilderUtils.println("</composition>", builder, 3);

        StringBuilderUtils.println("<propertyOrganizer>", builder, 3);
        StringBuilderUtils.println("</propertyOrganizer>", builder, 3);

        StringBuilderUtils.println("<stickyBoard/>", builder, 3);
        StringBuilderUtils.println("</eClassifiers>", builder, 1);
        StringBuilderUtils.println("</ecore:EPackage>", builder);

        return builder.toString();
    }
}
