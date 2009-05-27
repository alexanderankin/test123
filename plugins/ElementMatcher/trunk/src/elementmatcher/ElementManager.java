package elementmatcher;

import org.apache.commons.collections15.IteratorUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.ResettableIterator;
import org.apache.commons.collections15.iterators.FilterIterator;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import javax.swing.text.Segment;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ElementManager {

    private final JEditBuffer buffer;
    private final BufferChangeListenerImpl bufferChangeListener = new BufferChangeListenerImpl();
    private final ResettableIterator<ElementProvider<?>> providers;
    private final SortedMap<Integer, List<Element<?>>> elements = new TreeMap<Integer, List<Element<?>>>();
    private final BitSet parsedLines = new BitSet();
    private final ElementIterator elementIterator = new ElementIterator();

    public ElementManager(ElementMatcherPlugin elementMatcherPlugin, JEditBuffer buffer) {
        this.buffer = buffer;
        providers = elementMatcherPlugin.getProviderManager().getProviders();
        buffer.addBufferListener(bufferChangeListener);
    }

    public void close() {
        buffer.removeBufferListener(bufferChangeListener);
    }

    /**
     *
     * @param startLine start line, inclusive
     * @param endLine end line, exclusive
     * @return
     */
    Iterator<Element<?>> getElements(int startLine, int endLine) {
        final Segment segment = new Segment();
        for (int line=startLine; line<endLine; ++line) {
            if (parsedLines.get(line)) {
                continue;
            }
            buffer.getLineText(line, segment);
            List<Element<?>> lineElements = elements.get(line);
            if (lineElements != null) {
                lineElements.clear();
            }
            for (elementIterator.reset(line, segment); elementIterator.hasNext(); ) {
                final Element element = elementIterator.next();
                if (lineElements == null) {
                    lineElements = new LinkedList<Element<?>>();
                }
                lineElements.add(element);
            }
            if (lineElements != null && lineElements.isEmpty()) {
                lineElements = null;
            }
            if (lineElements != null) {
                elements.put(line, lineElements);
            } else {
                elements.remove(line);
            }
        }
        parsedLines.set(startLine, endLine);
        return new MultilineElementIterator(elements.subMap(startLine, endLine));
    }

    /**
     *
     * @param startLine start line, inclusive
     * @param endLine end line, exclusive
     */
    private void invalidateLines(int startLine, int endLine) {
        if (startLine >= parsedLines.size()) {
            startLine = parsedLines.size();
        }
        parsedLines.clear(startLine, endLine);
    }

    private void invalidateLines(int startLine) {
        parsedLines.clear(startLine, parsedLines.size());
    }

    public void invalidateLines() {
        parsedLines.clear();
    }

    public Iterator<Element<?>> findElement(int line, final int lineOffset) {
        final List<Element<?>> lineElements = elements.get(line);
        if (lineElements == null) {
            //noinspection unchecked
            return IteratorUtils.EMPTY_ITERATOR;
        }
        return new FilterIterator<Element<?>>(lineElements.iterator(), new Predicate<Element<?>>() {
            public boolean evaluate(Element<?> object) {
                return object.lineOffset0 <= lineOffset && lineOffset < object.lineOffset1;
            }
        });
    }

    public Iterator<Element<?>> findElement(int offset) {
        final int line = buffer.getLineOfOffset(offset);
        return findElement(line, offset - buffer.getLineStartOffset(line));
    }

    private class BufferChangeListenerImpl extends BufferAdapter {

        @Override
        public void contentInserted(JEditBuffer buffer, int startLine, int offset, int numLines, int length) {
            invalidateLines(startLine);
        }

        @Override
        public void contentRemoved(JEditBuffer buffer, int startLine, int offset, int numLines, int length) {
            invalidateLines(startLine);
        }

        @Override
        public void bufferLoaded(JEditBuffer buffer) {
            invalidateLines();
        }

    }

    protected static class MultilineElementIterator implements Iterator<Element<?>> {

        private final Iterator<List<Element<?>>> lineIterator;
        private Iterator<Element<?>> elementIterator;

        protected MultilineElementIterator(SortedMap<Integer, List<Element<?>>> map) {
            lineIterator = map.values().iterator();
            if (lineIterator.hasNext()) {
                elementIterator = lineIterator.next().iterator();
            }
        }

        public boolean hasNext() {
            if (elementIterator == null) {
                return false;
            }
            if (elementIterator.hasNext()) {
                return true;
            }
            if (lineIterator.hasNext()) {
                elementIterator = lineIterator.next().iterator();
                return hasNext();
            }
            return false;
        }

        public Element<?> next() {
            return elementIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class ElementIterator implements Iterator<Element<?>> {

        private int line;
        private Segment segment;
        private Iterator<? extends Element<?>> elementIterator;

        private void reset(int line, Segment segment) {
            this.line = line;
            this.segment = segment;
            providers.reset();
            if (providers.hasNext()) {
                final ElementProvider<?> nextProvider = providers.next();
                if (nextProvider.isEnabled()) {
                    elementIterator = nextProvider.getElements(line, segment);
                }
            }
        }

        public boolean hasNext() {
            if (elementIterator == null) {
                return false;
            }
            if (elementIterator.hasNext()) {
                return true;
            }
            if (!providers.hasNext()) {
                return false;
            }
            final ElementProvider<?> nextProvider = providers.next();
            if (nextProvider.isEnabled()) {
                elementIterator = nextProvider.getElements(line, segment);
            }
            return hasNext();
        }

        public Element<?> next() {
            return elementIterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}