/*
 * 19:40:24 10/09/99
 *
 * XTreeItem.java - 
 * Copyright (C) 1999 Romain Guy - powerteam@chez.com
 * Portions Copyright (C) 2000 Dominic Stolerman - dominic@sspd.org.uk
 * www.chez.com/powerteam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

public class XTreeItem {
  public static final int UNKNOWN_TYPE = -1;
  public static final int TEXT_TYPE =0;
  public static final int MACRO_TYPE = 1;
  public static final int XINSERT_SCRIPT_TYPE = 2;
  public static final int NAMED_MACRO_TYPE = 3;
  public static final int REFERENCE_TYPE = 4;
  public static final int ACTION_TYPE = 5;

  private int type;
  private String content;

  public XTreeItem(String content) {
    this(content, TEXT_TYPE);
  }

  public XTreeItem(String content, int type) {
    this.content = content;
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}

// End of XTreeItem.java

