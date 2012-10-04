package jdiff.util.patch.normal;

/**
 * This Library implements a simple patch algorithm which is able to process
 * the output of diff in normal format.
 *
 * This class implements an immutable data structure that is used to store the modification chunks.
 *
 * A Chunk is a consecutive amount of changes. See the diff documentation for more
 * information.
 *
 * see <a href="http://www.gnu.org/software/diffutils/manual/html_mono/diff.html#Normal">http://www.gnu.org/software/diffutils/manual/html_mono/diff.html#Normal</a>
 *
 * <pre>
 *          Copyright (c) 2007 Dominik Schulz
 *
 *          This file is part of jPatchLib.
 *
 *          jPatchLib is free software; you can redistribute it and/or modify
 *          it under the terms of the GNU General Public License as published by
 *          the Free Software Foundation; either version 2 of the License, or
 *          (at your option) any later version.
 *
 *          jPatchLib is distributed in the hope that it will be useful,
 *          but WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *          GNU General Public License for more details.
 *
 *          You should have received a copy of the GNU General Public License
 *          along with jPatchLib; if not, write to the Free Software
 *          Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * </pre>
 *
 * @author Dominik
 */
public class Chunk {
    String getName() {
        return this.from1 + "," + this.from2 + this.op + this.to1 + "," + this.to2;
    }

    /**
     * What does this chunk do?
     * One of:
     * a (add), c (change), or d (delete)
     */
    private final char		op;

    /**
     * Beginning of this change in the first file.
     */
    private final int		from1;

    /**
     * End of this change in the first file.
     */
    private final int		from2;

    /**
     * Beginning of this change in the second file.
     */
    private final int		to1;

    /**
     * End of this change in the second file.
     */
    private final int		to2;

    /**
     * The old text.
     */
    private final String[]	patch;

    /**
     * The new text.
     */
    private final String[]	target;

    /**
     * Create a new chunk.
     * @param op a (add), d (delete) or c (change).
     * @param from1
     * @param from2
     * @param to1
     * @param to2
     * @param patch
     * @param target
     */
    public Chunk(char op, int from1, int from2, int to1, int to2, String[] patch, String[] target) {
        this.op = op;
        this.from1 = from1;
        this.from2 = from2;
        this.to1 = to1;
        this.to2 = to2;
        this.patch = patch;
        this.target = target;
    }

    /**
     * @return the from1
     */
    public int getFrom1() {
        return from1;
    }

    /**
     * @return the from2
     */
    public int getFrom2() {
        return from2;
    }

    /**
     * @return the op
     */
    public char getOp() {
        return op;
    }

    /**
     * @return the patch
     */
    public String[] getPatch() {
        return patch;
    }

    /**
     * @return the target
     */
    public String[] getTarget() {
        return target;
    }

    /**
     * @return the to1
     */
    public int getTo1() {
        return to1;
    }

    /**
     * @return the to2
     */
    public int getTo2() {
        return to2;
    }
}
