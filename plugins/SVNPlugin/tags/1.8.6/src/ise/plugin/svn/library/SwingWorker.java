package ise.plugin.svn.library;

public abstract class SwingWorker<T, V> extends javax.swing.SwingWorker<T, V> {
    public boolean doCancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);   
    }
}