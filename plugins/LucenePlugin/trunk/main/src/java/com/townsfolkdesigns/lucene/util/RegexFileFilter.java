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
package com.townsfolkdesigns.lucene.util;

import java.io.File;
import java.io.FileFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Filters based on a regular expression. If ignoreDirectories is off then directory names will be matched against the regular
 * expression also.
 * @author eberry
 */
public class RegexFileFilter implements FileFilter {

   private boolean ignoreDirectoriesOn;
   private Pattern pattern;
   private String regularExpression;

   public RegexFileFilter() {
      setRegularExpression(".*");
   }

   public boolean accept(File pathname) {
      boolean accept = false;

      if (!pathname.isDirectory() || (!isIgnoreDirectoriesOn() && pathname.isDirectory())) {
         String fileName = pathname.getName();
         Matcher matcher = getPattern().matcher(fileName);

         if (matcher.matches()) {
            accept = true;
         }
      }

      return accept;
   }

   public String getRegularExpression() {
      return regularExpression;
   }

   public boolean isIgnoreDirectoriesOn() {
      return ignoreDirectoriesOn;
   }

   public void setIgnoreDirectoriesOn(boolean ignoreDirectoriesOn) {
      this.ignoreDirectoriesOn = ignoreDirectoriesOn;
   }

   public void setRegularExpression(String regularExpression) {
      this.regularExpression = regularExpression;
      setPattern(Pattern.compile(regularExpression));
   }

   private Pattern getPattern() {
      return pattern;
   }

   private void setPattern(Pattern pattern) {
      this.pattern = pattern;
   }
}
