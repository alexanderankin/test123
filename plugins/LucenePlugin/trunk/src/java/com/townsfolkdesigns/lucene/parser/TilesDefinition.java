/*
 * Copyright (c) 2008 Eric Berry <elberry@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.townsfolkdesigns.lucene.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author eberry
 */
public class TilesDefinition {

    private List<String> jspReferences;
    private List<String> variableReferences;
    private Map<String, String> properties;
    private String controllerClass;
    private String controllerUrl;
    private String id;
    private String name;
    private String parent;
    private String resourcePath;
    private String role;

    public TilesDefinition() {

        setProperties(new LinkedHashMap<String, String>());
        setJspReferences(new ArrayList<String>());
        setVariableReferences(new ArrayList<String>());

    }

    public String getControllerClass() {

        return controllerClass;

    }

    public String getControllerUrl() {

        return controllerUrl;

    }

    public String getId() {

        return id;

    }

    public List<String> getJspReferences() {

        return jspReferences;

    }

    public String getName() {

        return name;

    }

    public String getParent() {

        return parent;

    }

    public Map<String, String> getProperties() {

        return properties;

    }

    public String getResourcePath() {

        return resourcePath;

    }

    public String getRole() {

        return role;

    }

    public List<String> getVariableReferences() {

        return variableReferences;

    }

    public void setControllerClass(String controllerClass) {

        this.controllerClass = controllerClass;

    }

    public void setControllerUrl(String controllerUrl) {

        this.controllerUrl = controllerUrl;

    }

    public void setId(String id) {

        this.id = id;

    }

    public void setJspReferences(List<String> jspReferences) {

        this.jspReferences = jspReferences;

    }

    public void setName(String name) {

        this.name = name;

    }

    public void setParent(String parent) {

        this.parent = parent;

    }

    public void setProperties(Map<String, String> properties) {

        this.properties = properties;

    }

    public void setResourcePath(String resourcePath) {

        this.resourcePath = resourcePath;

    }

    public void setRole(String role) {

        this.role = role;

    }

    public void setVariableReferences(List<String> variableReferences) {

        this.variableReferences = variableReferences;

    }

}
