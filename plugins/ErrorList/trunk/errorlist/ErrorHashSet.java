
package errorlist;


import errorlist.ErrorSource.Error;

import java.util.HashSet;
import java.util.Iterator;

import org.gjt.sp.jedit.Buffer;


// represents a set of errors.
public class ErrorHashSet extends HashSet <ErrorSource.Error>
{

    // overriding 'add' since Error is an interface and doesn't
    // necessarily implement a good 'equals'
    @Override
    public boolean add(Error error )
    {
        if ( !contains( error ) )
        {
            return super.add( error );
        }

        return false;
    }

    // checks if the given error already exists in this set. Since Error is an
    // interface, 'equals' is likely inadequate to determine if this error is
    // already in the set. This method compares each field in the given error 
    // with the existing errors to check for sameness.
    private boolean contains( Error newError )
    {
        Iterator<Error> iter = iterator();
        while ( iter.hasNext() ) {
            Error error = iter.next();
            if ( error.getErrorType() != newError.getErrorType() )
            {
                continue;
            }

            if ( error.getLineNumber() != newError.getLineNumber() )
            {
                continue;
            }

            if ( error.getStartOffset() != newError.getStartOffset() )
            {
                continue;
            }

            if ( error.getEndOffset() != newError.getEndOffset() )
            {
                continue;
            }

            if ( !equals( error.getErrorMessage(), newError.getErrorMessage() ) )
            {
                continue;
            }

            if ( !equals( error.getFilePath(), newError.getFilePath() ) )
            {
                continue;
            }

            if ( !equals( error.getFileName(), newError.getFileName() ) )
            {
                continue;
            }

            if ( !equals( error.getErrorMessage(), newError.getErrorMessage() ) )
            {
                continue;
            }

            if ( !equals( error.getBuffer(), newError.getBuffer() ) )
            {
                continue;
            }

            if (!error.getErrorSource().equals(newError.getErrorSource())) {
                continue;
            }

            return true;
        }
        return false;
    }

    // check if two strings are equal ignoring case
    private boolean equals( String a, String b )
    {
        if ( a == null && b == null )
        {
            return true;
        }

        if ( a != null && b == null )
        {
            return false;
        }

        if ( a == null && b != null )
        {
            return false;
        }

        return a.equalsIgnoreCase( b );
    }

    // checks if two Buffers are equal based on buffer paths using a case-
    // insensitive check
    private boolean equals( Buffer a, Buffer b )
    {
        if ( a == null && b == null )
        {
            return true;
        }

        if ( a != null && b == null )
        {
            return false;
        }

        if ( a == null && b != null )
        {
            return false;
        }

        String pathA = a.getPath();
        String pathB = b.getPath();
        if ( pathA == null && pathB == null )
        {
            return true;
        }

        if ( pathA != null && pathB == null )
        {
            return false;
        }

        return pathA.equalsIgnoreCase( pathB );
    }

}
