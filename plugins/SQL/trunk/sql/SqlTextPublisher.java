/**
 * SqlUtils.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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

package sql;

import java.awt.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.util.*;

import errorlist.*;

import projectviewer.*;
import projectviewer.vpt.*;

import sql.*;
import sql.preprocessors.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class SqlTextPublisher
{
	protected final static String POPUP_SUCCESSFUL_EMPTY_UPDATE_MESSAGES =
	        "sql.publisher.popupSuccessfulEmptyUpdateMessages";

	protected final static DateFormat dFormat =
	        new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss", Locale.US);

	protected static Map preprocessors = null;

	protected static String lastRunQuery = null;
	protected static int lastStartPos = 0;

	/**
	 *Gets the hideSuccessfulUpdateMessages attribute of the SqlUtils class
	 *
	 * @return    The hideSuccessfulUpdateMessages value
	 * @since
	 */
	public static boolean getPopupSuccessfulEmptyUpdateMessages()
	{
		return Boolean.valueOf(SqlPlugin.getGlobalProperty(POPUP_SUCCESSFUL_EMPTY_UPDATE_MESSAGES)).booleanValue();
	}

	public static void setPopupSuccessfulEmptyUpdateMessages(boolean popupSuccessfulEmptyUpdateMessages)
	{
		SqlPlugin.setGlobalProperty(POPUP_SUCCESSFUL_EMPTY_UPDATE_MESSAGES,
		                            new Boolean(popupSuccessfulEmptyUpdateMessages).toString());
	}

	/**
	 *  Description of the Method
	 */
	public static void clearProperties()
	{
		SqlPlugin.unsetGlobalProperty(POPUP_SUCCESSFUL_EMPTY_UPDATE_MESSAGES);
	}

	/**
	 *Gets the Preprocessors attribute of the SqlUtils class
	 *
	 * @return    The Preprocessors value
	 * @since
	 */
	public static Map getPreprocessors()
	{
		if (preprocessors == null)
			fillPreprocessors();
		return preprocessors;
	}


	/**
	 *  Gets the Preprocessor attribute of the SqlUtils class
	 *
	 * @param  name  Description of Parameter
	 * @return       The Preprocessor value
	 */
	public static Preprocessor getPreprocessor(String name)
	{
		return (Preprocessor) getPreprocessors().get(name);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view        Description of Parameter
	 * @param  serverName  Description of Parameter
	 * @since
	 */
	public static void publishSelection(View view, String serverName)
	{
		final Buffer buffer = view.getBuffer();
		final JEditTextArea tArea = view.getTextArea();
		final Selection[] sels = tArea.getSelection();
		int start;
		int end;
		if (sels.length != 1)
		{
			start = 0;
			end = buffer.getLength();
		}
		else
		{
			start = sels[0].getStart();
			end = sels[0].getEnd();
		}
		publishText(view, buffer, start, end - start, serverName);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view        Description of Parameter
	 * @param  serverName  Description of Parameter
	 * @since
	 */
	public static void publishBuffer(View view, String serverName)
	{
		final Buffer buffer = view.getBuffer();
		publishText(view, buffer, 0, buffer.getLength(), serverName);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view        Description of Parameter
	 * @param  buffer      Description of Parameter
	 * @param  startPos    Description of Parameter
	 * @param  length      Description of Parameter
	 * @param  serverName  Description of Parameter
	 * @since
	 */
	public static void publishText(final View view,
	                               final Buffer buffer,
	                               final int startPos,
	                               final int length,
	                               final String serverName)
	{
		SqlUtils.getThreadGroup().runInGroup(
		        new Runnable()
		        {
			        public void run()
			        {
				        doPublishText(view,
				                      startPos,
				                      buffer.getText(startPos, length),
				                      serverName);
			        }
		        });
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view        Description of Parameter
	 * @param  serverName  Description of Parameter
	 * @since
	 */
	public static void repeatLastQuery(final View view,
	                                   final String serverName)
	{
		if (lastRunQuery != null)
		{
			SqlUtils.getThreadGroup().runInGroup(
			        new Runnable()
			        {
				        public void run()
				        {
					        doPublishText(view,
					                      lastStartPos,
					                      lastRunQuery,
					                      serverName);
				        }
			        });
		}
	}


	/**
	 *  Gets the Sysdate attribute of the SqlUtils class
	 *
	 * @param  conn              Description of Parameter
	 * @param  rec               Description of Parameter
	 * @return                   The Sysdate value
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	protected static Timestamp getSysdate(Connection conn, SqlServerRecord rec)
	throws SQLException
	{
		CallableStatement cstmt = null;
		try
		{
			cstmt = rec.prepareCall(conn, "getSysdate", null);
			if (cstmt == null)
				return new Timestamp(new java.util.Date().getTime());

			cstmt.registerOutParameter(1, Types.TIMESTAMP);
			cstmt.execute();
			final Timestamp ts = cstmt.getTimestamp(1);
			return ts;
		} finally
		{
			rec.releaseStatement(cstmt);
		}
	}



	/**
	 *  Description of the Method
	 *
	 * @param  view        Description of Parameter
	 * @param  startPos    Description of Parameter
	 * @param  sqlText     Description of Parameter
	 * @param  serverName  Description of Parameter
	 * @since
	 */
	protected static void doPublishText(final View view,
	                                    final int startPos,
	                                    final String sqlText,
	                                    final String serverName)
	{
		SqlUtils.getErrorSource().clear();

		final SqlServerRecord rec = SqlUtils.getServerRecord(SqlUtils.getProject(view), serverName);
		if (rec == null)
			return;

		Connection conn = null;
		// update vars for re-querying
		lastRunQuery = sqlText;
		lastStartPos = startPos;

		try
		{
			conn = rec.allocConnection();

			String delimiter = rec.getStatementDelimiterRegex();
			if (delimiter == null || "".equals(delimiter))
				delimiter = rec.getServerType().getDefaultStatementDelimiterRegex();
			final SqlParser parser = new SqlParser(delimiter);
			final java.util.List fragments = parser.getFragments(sqlText);
			final Collection preprocessors = getPreprocessors().values();
			final Iterator e = fragments.iterator();
			while (true)
			{
				final Timestamp startTimeRemote = getSysdate(conn, rec);

				final Statement stmt = conn.createStatement();
				Log.log(Log.DEBUG, SqlTextPublisher.class,
				        "stmt created: " + stmt);

				final SqlParser.SqlTextFragment fragment = (SqlParser.SqlTextFragment) e.next();
				String sqlSubtext = fragment.getFragment(sqlText);

				for (Iterator e1 = preprocessors.iterator(); e1.hasNext();)
				{
					final Preprocessor pr = (Preprocessor) e1.next();
					pr.setView(view);
					sqlSubtext = pr.process(sqlSubtext);
				}

				Log.log(Log.DEBUG, SqlTextPublisher.class, "After the variable substitution: [" + sqlSubtext + "]");

				final long startTimeLocal = System.currentTimeMillis();
				final boolean bresult = stmt.execute(sqlSubtext);
				final long endTimeLocal = System.currentTimeMillis();
				final long deltaTimeLocal = endTimeLocal - startTimeLocal;
				Log.log(Log.DEBUG, SqlTextPublisher.class,
				        "Query time: " + deltaTimeLocal + "ms");

				if (bresult)
					handleResultSet(view, stmt, rec, sqlSubtext);
				else
					handleUpdateCount(view, stmt, rec, sqlSubtext, startTimeRemote, startPos + fragment.startOffset);

				// bad but otherwise errors will mix up...
				if (e.hasNext())
					try
					{
						Thread.sleep(1000);
					} catch (Exception ex)
					{}
				else
					break;
			}
		} catch (SQLException ex)
		{
			SqlUtils.processSqlException(view, ex, sqlText, rec);
		} finally
		{
			rec.releaseConnection(conn);
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view              Description of Parameter
	 * @param  stmt              Description of Parameter
	 * @param  record            Description of Parameter
	 * @param  text              Description of Parameter
	 * @param  startTime         Description of Parameter
	 * @param  startPos          Description of Parameter
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	protected static void handleUpdateCount(final View view,
	                                        Statement stmt,
	                                        SqlServerRecord record,
	                                        String text,
	                                        Timestamp startTime,
	                                        int startPos)
	throws SQLException
	{
		final Buffer buffer = view.getBuffer();
		final Connection conn = stmt.getConnection();
		final int updateCount = stmt.getUpdateCount();
		stmt.close();

		boolean anyObj = false;

		// still not clear whether this is correct...
		final Timestamp endTime = getSysdate(conn, record);
		final String startTimeStr = dFormat.format(startTime);
		final String endTimeStr = dFormat.format(endTime);

		PreparedStatement pstmt = null;
		try
		{
			pstmt =
			        record.prepareStatement(conn,
			                                "selectLastChangedObjects",
			                                new Object[]{startTimeStr, endTimeStr});

			// some SQL servers do not have objects...
			if (pstmt != null)
			{
				final ResultSet rs = SqlUtils.executeQuery(pstmt);

				while (rs.next())
				{
					anyObj = true;
					final String objectName = rs.getString("objectName");
					final String status = rs.getString("status");
					final String objectType = rs.getString("objectType");
					final String objectId = rs.getString("objectId");

					if (text.toUpperCase().indexOf(objectName.toUpperCase()) == -1)
						continue;

					// some objects from other sessions...
					if ("VALID".equals(status))
					{
						final Object args[] = {objectType, objectName, record.getName()};
						SqlUtils.runInAWTThreadNoWait(
						        new Runnable()
						        {
							        public void run()
							        {
								        GUIUtilities.message(view, "sql.publishOK", args);
							        }
						        });
					}
					else
					{
						final SqlParser parser = new SqlParser(record.getStatementDelimiterRegex());

						final int firstCodeLineNo = buffer.getLineOfOffset(startPos);

						PreparedStatement dstmt = null;
						int cnt = 0;
						try
						{
							dstmt = record.prepareStatement(
							                conn,
							                "selectCodeObjectErrors",
							                new Object[]{objectName, objectType});
							if (dstmt == null)
								continue;

							final ResultSet drs = SqlUtils.executeQuery(dstmt);
							while (drs.next())
							{
								int errLine = drs.getInt("errRow");
								int errPosition = drs.getInt("errCol");
								final String errText = drs.getString("errorMessage");

								if (errLine == 1)
								{
									final int firstCodeLineDocOfs = buffer.getLineStartOffset(firstCodeLineNo);
									//!! TODO: Add the whitespace to offset
									// errPosition += firstNWCodeCharOfs + startPos - firstCodeLineDocOfs;
									errPosition += startPos - firstCodeLineDocOfs;
								}
								errLine += firstCodeLineNo;

								final int finErrLine = errLine;
								final int finErrPosition = errPosition;
								SqlUtils.runInAWTThreadNoWait(
								        new Runnable()
								        {
									        public void run()
									        {
										        SqlUtils.getErrorSource().addError(
										                ErrorSource.ERROR,
										                buffer.getPath(),
										                finErrLine - 1,
										                finErrPosition - 1,
										                finErrPosition,
										                errText);
									        }
								        });
								cnt++;
							}
						} finally
						{
							record.releaseStatement(dstmt);
						}
						final Object args[] = {objectType,
						                       objectName,
						                       record.getName(),
						                       new Integer(cnt)};

						SqlUtils.runInAWTThreadNoWait(
						        new Runnable()
						        {
							        public void run()
							        {
								        final DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
								        dockableWindowManager.showDockableWindow("error-list");
								        GUIUtilities.message(view, "sql.publishErr", args);
							        }
						        });
					}
				}
			}
		} finally
		{
			record.releaseStatement(pstmt);
		}

		if (!anyObj && (updateCount > 0 || getPopupSuccessfulEmptyUpdateMessages()))
			SqlUtils.runInAWTThreadNoWait(
			        new Runnable()
		        {
			        final Object args[] = {new Integer(updateCount)};

			        public void run()
			        {
				        GUIUtilities.message(view, "sql.updateOK", args);
			        }
		        });
	}


	/**
	 *  Description of the Method
	 *
	 * @param  view              Description of Parameter
	 * @param  stmt              Description of Parameter
	 * @param  record            Description of Parameter
	 * @param  text              Description of Parameter
	 * @exception  SQLException  Description of Exception
	 * @since
	 */
	protected static void handleResultSet(View view,
	                                      Statement stmt,
	                                      SqlServerRecord record,
	                                      final String text)
	throws SQLException
	{
		final ResultSet rs = stmt.getResultSet();
		final ResultSetWindow.Data data = ResultSetWindow.prepareModel(record, rs);
		stmt.close();
		final View v = view;

		if (data == null)
			return;

		SqlUtils.runInAWTThreadAndWait(
		        new Runnable()
		        {
			        public void run()
			        {
				        ResultSetWindow wnd = SqlPlugin.showResultSetWindow(v);
				        if (wnd == null)
					        return;

				        wnd.addDataSet(text, data);
			        }
		        });

	}


	/**
	 *Description of the Method
	 *
	 * @since
	 */
	protected static void fillPreprocessors()
	{
		preprocessors = new TreeMap();

		int i = 0;
		while (true)
		{
			final String className = jEdit.getProperty("sql.preprocessors." + i++);
			if (className == null || "".equals(className))
				break;
			try
			{
				preprocessors.put(className, Class.forName(className).newInstance());
			} catch (Exception ex)
			{
				Log.log(Log.ERROR, SqlTextPublisher.class, "Exception creating preprocessors");
				Log.log(Log.ERROR, SqlTextPublisher.class, ex);
			}
		}
	}

}

