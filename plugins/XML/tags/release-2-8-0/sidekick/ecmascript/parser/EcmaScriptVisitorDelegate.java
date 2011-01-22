package sidekick.ecmascript.parser;

public interface EcmaScriptVisitorDelegate {

    public void willVisit(SimpleNode node, Object data);

    public void didVisit(SimpleNode node, Object data);

}
