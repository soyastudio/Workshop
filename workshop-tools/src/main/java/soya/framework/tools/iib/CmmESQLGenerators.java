package soya.framework.tools.iib;

public final class CmmESQLGenerators {

    private CmmESQLGenerators() {
    }

    public static CmmESQLGenerator createCmmESQLGenerator(Node node, String inputMessageType) {
        MessageType type = MessageType.valueOf(inputMessageType.toUpperCase());
        switch (type) {
            case JSON:
                return new JsonToCmmESQL(node);

            case XML:
                return new XmlToCmmESQL(node);

            default:
                throw new IllegalArgumentException("Can not create CmmESQLGenerator for input message type: " + inputMessageType);
        }

    }

    public static JsonToCmmESQL jsonToCmmESQLGenerator(Node node) {
        return new JsonToCmmESQL(node);
    }
}
