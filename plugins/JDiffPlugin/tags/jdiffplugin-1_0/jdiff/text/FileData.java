package jdiff.text;

public class FileData
{
    public FileData(String fileName, FileLine[] lines)
    {
        this.fileName = fileName;
        this.lines = lines;
    }

    public String getName()
    {
        return fileName;
    }

    public FileLine[] getLines()
    {
        return getLines(0, lines.length);
    }

    public FileLine[] getLines(int offset, int count)
    {
        FileLine[] lines = new FileLine[count];
        System.arraycopy(this.lines, offset, lines, 0, lines.length);
        return lines;
    }

    public int getLineCount()
    {
        return lines.length;
    }

    private String fileName;
    private FileLine[] lines;
}
