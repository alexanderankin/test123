package ise.plugin.svn.data;

import java.io.*;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;

public class CheckoutData extends SVNData {
    private String url = "";

    public CheckoutData(){}

    public CheckoutData( String url,
            String username,
            String password ) {
        super();
        this.url = url;
        setUsername(username);
        setPassword(password);
    }

    /**
     * Returns the value of url.
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of url.
     * @param commitMessage The value to assign url.
     */
    public void setURL( String url ) {
        this.url = url;
    }

}
