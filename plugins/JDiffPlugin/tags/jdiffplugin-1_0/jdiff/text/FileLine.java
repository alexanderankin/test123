package jdiff.text;

public class FileLine
{
    public FileLine(String source, boolean ignoreCase)
    {
        if (source == null) throw new NullPointerException();
        this.source = source;
        this.ignoreCase = ignoreCase;
    }

    public FileLine(String source)
    {
        if (source == null) throw new NullPointerException();
        this.source = source;
    }

    public String getSourceString()
    {
        return source;
    }

    public boolean isCaseIgnored()
    {
        return ignoreCase;
    }

    public void setCaseIgnored(boolean ignoreCase)
    {
        if (this.ignoreCase == ignoreCase) return;

        this.ignoreCase = ignoreCase;

        cachedHashCode = 0;
    }

    public int hashCode()
    {
        if (cachedHashCode == 0)
        {
            if (ignoreCase)
            {
                cachedHashCode = source.toUpperCase().hashCode();
            }
            else
            {
                cachedHashCode = source.hashCode();
            }
        }

        return cachedHashCode;
    }

    // this implementation is based on the equals implementation in Classpath's
    // java.lang.String
    public boolean equals(Object o)
    {
        if (o == null) return false;
        if (!(o instanceof FileLine)) return false;
        return (
              (ignoreCase)
            ? source.equalsIgnoreCase(((FileLine)o).source)
            : source.equals(((FileLine)o).source)
        );
    }

    private int cachedHashCode;

    private String source;

    private boolean ignoreCase;
}
