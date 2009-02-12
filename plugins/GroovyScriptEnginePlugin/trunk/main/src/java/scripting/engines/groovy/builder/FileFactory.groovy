/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scripting.engines.groovy.builder
import groovy.text.GStringTemplateEngine

/**
 *
 * @author eberry
 */
class FileFactory extends AbstractFactory {

    private append
    private content
    private file
    private template
    private templateData
    
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        //println("FileFactory - builder: ${builder} | name: ${name} | value: ${value} | attributes: ${attributes} | parent: ${builder.getCurrent()}")
        def parentFile = builder.getCurrent()
        def file = new File(parentFile, value)
        def fileNode = new FileFactory()
        fileNode.file = file
        return fileNode
    }

    void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
        // parent can only be directories.
        if(!parent.exists()) {
            parent.mkdirs()
            println("FileFactory - creating parent: ${parent.path}")
        }
        if(!node.file.exists()) {
            node.file.createNewFile()
            println("FileFactory - creating file: ${node.path}")
        }

        // now that the file has been created, let's deal with the templates
        // and content.
        if(node.content != null) {
            node.writeContent()
        } else if (node.template != null) {
            node.writeTemplate()
        }
    }

    boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ) {
        if(attributes["content"] != null && attributes["template"] != null) {
            // user has supplied both content and a template - pick one.
            throw new IllegalStateException("Content and a Template are specified for file: ${node.path} - only one is allowed.")
        }
        return true;
    }

    public boolean isLeaf() {
        return true
    }

    public getPath() {
        return file.path
    }

    private void writeContent() {
        println("writing content: ${content} | file: ${file.path}")
        def FileWriter writer = new FileWriter(file, append)
        writer.println(content)
        writer.close()
    }

    private void writeTemplate() {
        println("writing template: ${template} | file: ${file.path}")
        def FileWriter writer = new FileWriter(file, append)
        def templateFile = new File(template)
        def templateUrl = getClass().getResource(template)
        def engine = new GStringTemplateEngine()
        def templateContent
        if(templateFile.exists()) {
            templateContent = engine.createTemplate(templateFile).make(templateData)
        } else if (templateUrl != null) {
            templateContent = engine.createTemplate(templateUrl).make(templateData)
        } else {
            throw new RuntimeException("Couldn't find template file: ${template}")
        }
        writer.println(templateContent)
        writer.close()
    }
}