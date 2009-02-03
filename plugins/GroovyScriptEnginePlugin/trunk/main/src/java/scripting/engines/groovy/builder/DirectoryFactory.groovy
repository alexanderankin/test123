/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scripting.engines.groovy.builder

/**
 *
 * @author eberry
 */
class DirectoryFactory extends AbstractFactory {

    private file

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        //println("DirectoryFactory - builder: ${builder} | name: ${name} | value: ${value} | attributes: ${attributes} | parent: ${builder.getCurrent()}")
        def parentFile = builder.getCurrent()
        def directory = new File(value)
        if(parentFile != null) {
            directory = new File(parentFile, value)
        }
        def directoryNode = new DirectoryFactory()
        directoryNode.file = directory
        return directory
    }

    public boolean isLeaf() {
        return false
    }

    void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ) {
        if(!node.exists()) {
            //node.mkdir()
            println("DirectoryFactory - creating directory: ${node.path}")
        }
    }
}

