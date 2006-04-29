s/import org.gjt.sp.jedit.Buffer;/import org.gjt.sp.jedit.buffer.*;/g 
s/\(\W\)BufferChangeAdapter\(\W\)/\1BufferAdapter\2/g
s/\(\W\)Buffer\(\W\)/\1JEditBuffer\2/g
s/BufferChangeListener(/BufferListener(/g

