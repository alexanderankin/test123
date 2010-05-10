package ctagsinterface.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import ctagsinterface.main.CtagsInterfacePlugin;
import ctagsinterface.main.Tag;

public class TagDB {
	static public final String OPTION = CtagsInterfacePlugin.OPTION;
	static public final String CUSTOM_DB = "Custom";
	static public final String DB_SELECTED_PRESET = OPTION + "dbSelectedPreset";
	static public final String DB_PRESETS = OPTION + "dbPresets";
	static public final String DB_CLASS = OPTION + "dbClass";
	static public final String DB_CONNECTION = OPTION + "dbConnection";
	static public final String DB_USER = OPTION + "dbUser";
	static public final String DB_PASSWORD = OPTION + "dbPassword";
	static public final String DB_MAPPINGS_FILE = OPTION + "dbMappingsFile";
	static public final String DB_ON_EXIT = OPTION + "dbOnExit";
	private static final String DEFAULT_DB_FILE_SPEC = "<default>";
	private Connection conn;
	private Set<String> columns;
	Statement st = null;
	String onExit = null;
	// Column types
	public static final String IDENTITY_TYPE = "IDENTITY";
	public static final String VARCHAR_TYPE = "VARCHAR";
	public static final String INTEGER_TYPE = "INTEGER";
	private String identityType;
	private String varcharType;
	private String integerType;
	// Tags table
	public static final String TAGS_TABLE = "TAGS";
	public static final String TAGS_NAME = "NAME";
	public static final String TAGS_FILE_ID = "FILE_ID";
	public static final String TAGS_PATTERN = "PATTERN";
	public static final String TAGS_EXTENSION_PREFIX = "A_";
	public static final String TAGS_LINE = "A_LINE";
	// Files table
	public static final String FILES_TABLE = "FILES";
	public static final String FILES_ID = "ID";
	public static final String FILES_NAME = "FILE";
	// Origins table
	public static final String ORIGINS_TABLE = "ORIGINS";
	public static final String ORIGINS_ID = "ID";
	public static final String ORIGINS_NAME = "NAME";
	public static final String ORIGINS_TYPE = "TYPE";
	// Files/Origin map table
	public static final String MAP_TABLE = "MAP";
	public static final String MAP_FILE_ID = "FILE_ID";
	public static final String MAP_ORIGIN_ID = "ORIGIN_ID";
	// Origin types
	public static final int TEMP_ORIGIN_INDEX = 0;
	public static final String TEMP_ORIGIN_NAME = "Temp";
	public static final String TEMP_ORIGIN = "Temp";
	public static final String PROJECT_ORIGIN = "Project";
	public static final String DIR_ORIGIN = "Dir";
	public static final String ARCHIVE_ORIGIN = "Archive";
	// Characters to escape (precede with '\') in string values
	private static String charsToEscape = null;
	
	public TagDB() {
		removeStaleLock();
        try {
			Class.forName(TagDB.getDbClass());
			String connectionString = TagDB.getDbConnection();
			conn = DriverManager.getConnection(
				connectionString.replace(DEFAULT_DB_FILE_SPEC, getDBFilePath()),
				TagDB.getDbUser(),
				TagDB.getDbPassword());
			st = conn.createStatement();
			onExit = TagDB.getDbOnExit();
        } catch (final Exception e) {
			e.printStackTrace();
			return;
		}
        initDbSettings();
		createTables();
		columns = getColumns();
	}

	private void initDbSettings() {
		identityType = IDENTITY_TYPE;
		varcharType = VARCHAR_TYPE;
		integerType = INTEGER_TYPE;
		String mapFile = getDbMappingsFile();
		if (mapFile == null || mapFile.length() == 0)
			return;
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(mapFile);
			props.load(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		identityType = props.getProperty("identityType", identityType);
		varcharType = props.getProperty("varcharType", varcharType);
		integerType = props.getProperty("integerType", integerType);
		charsToEscape = props.getProperty("stringValueCharsToEscape");
		if (charsToEscape != null) {
			// Prepare a pattern for the characters to escape
			// The pattern uses a character class ('[]'), so escape
			// any meta-characters inside a character class.
			StringBuffer escaped = new StringBuffer();
			for (int i = 0; i < charsToEscape.length(); i++) {
				char c = charsToEscape.charAt(i);
				if (c == ']' || c == '^' || c == '\\' || c == '-')
					escaped.append('\\');
				escaped.append(c);
			}
			if (escaped.length() > 0)
				charsToEscape = "([" + escaped + "])";
			else
				charsToEscape = null;
		}
	}
	
	public boolean isFailed() {
		return (st == null);
	}
	
	// Check if a source file is in the DB
	public boolean hasSourceFile(String file) {
		return tableColumnContainsValue(FILES_TABLE, FILES_NAME, file);
	}
	
	// Returns the ID of a source file, or (-1) if source file not in DB
	public int getSourceFileID(String file) {
		return queryInteger(FILES_ID, "SELECT " + FILES_ID + " FROM " + FILES_TABLE +
			" WHERE " + FILES_NAME + "=" + quote(file), -1);
	}
	
	// Inserts a source file to the DB
	public void insertSourceFile(String file) {
		try {
			update("INSERT INTO " + FILES_TABLE + " (" + FILES_NAME +
				") VALUES (" + quote(file) + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Inserts a source file -> origin mapping to the DB
	public void insertSourceFileOrigin(int fileId, int originId) {
		try {
			Query q = new Query("*", MAP_TABLE, MAP_FILE_ID + "=" + quote(fileId));
			q.addCondition(MAP_ORIGIN_ID + "=" + quote(originId));
			ResultSet rs = query(q);
			if ((rs == null) || rs.next())
				return;
			update("INSERT INTO " + MAP_TABLE + " (" + MAP_FILE_ID + "," + MAP_ORIGIN_ID +
				") VALUES (" + quote(fileId) + "," + quote(originId) + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Delete tags from source file
	public void deleteTagsFromSourceFile(int fileId) {
		if (fileId < 0)
			return;
		deleteRowsWithValue(TAGS_TABLE, TAGS_FILE_ID, Integer.valueOf(fileId));
	}
	
	/*
	 * Insert a tag into the DB with a given file ID.
	 * If file ID is negative, the ID is looked up in the FILES table. If the file
	 * is not in the table, it is inserted.
	 */
	public void insertTag(Tag t, int fileId) {
		if (fileId < 0) {
			System.err.println("insertTag called with fileId=-1");
			return;
		}
		// Find missing columns and build the inserted value string
		StringBuffer valueStr = new StringBuffer();
		StringBuffer columnStr = new StringBuffer();
		Iterator<String> it = t.getExtensions().iterator();
		while (it.hasNext()) {
			String extension = it.next();
			String col = extension2column(extension.toUpperCase());
			String val = t.getExtension(extension);
			if (! columns.contains(col)) {
				try {
					update("ALTER TABLE " + TAGS_TABLE + " ADD " + col + " " +
						varcharType + ";");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				columns.add(col);
			}
			if (columnStr.length() > 0)
				columnStr.append(",");
			columnStr.append(col);
			if (valueStr.length() > 0)
				valueStr.append(",");
			valueStr.append(quote(val));
		}
		// Insert the record
		try {
			update("INSERT INTO " + TAGS_TABLE + " (" + TAGS_NAME + "," +
				TAGS_PATTERN + "," + TAGS_FILE_ID + "," + 
				columnStr.toString() + ") VALUES (" +
				quote(t.getName()) + "," +
				quote(t.getPattern()) + "," +
				fileId + "," + valueStr.toString() + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Returns a basic tag query, to fill in conditions
	public Query getBasicTagQuery() {
		Query q = new Query();
		q.addColumn(field(TAGS_TABLE, "*"));
		q.addColumn(field(FILES_TABLE, FILES_NAME));
		q.addTable(TAGS_TABLE);
		q.addTable(FILES_TABLE);
		q.addCondition(field(TAGS_TABLE, TAGS_FILE_ID) + "=" + field(FILES_TABLE, FILES_ID));
		return q;
	}

	// Returns a query for a tag name
	public Query getTagNameQuery(String tag) {
		Query q = getBasicTagQuery();
		q.addCondition(field(TAGS_TABLE, TAGS_NAME) + "=" + quote(tag));
		return q;
	}
	
	// Returns a query for a tag prefix
	public Query getTagPrefixQuery(String prefix) {
		Query q = getBasicTagQuery();
		q.addCondition(field(TAGS_TABLE, TAGS_NAME) + " LIKE " + quote(prefix + '%'));
		return q;
	}

	// Makes the given tagQuery be scoped to the given project
	public void makeProjectScopedQuery(Query tagQuery, String project) {
		Query projectQuery = new Query(ORIGINS_ID, ORIGINS_TABLE,
			ORIGINS_NAME + "=" + quote(project));
		projectQuery.addCondition(ORIGINS_TYPE + "=" + quote(PROJECT_ORIGIN));
		Query projectFilesQuery = new Query();
		projectFilesQuery.setColumn(MAP_FILE_ID);
		projectFilesQuery.setTable(MAP_TABLE);
		projectFilesQuery.addCondition(field(MAP_TABLE, MAP_FILE_ID) + "=" +
			field(FILES_TABLE, FILES_ID));
		projectFilesQuery.addCondition(field(MAP_TABLE, MAP_ORIGIN_ID) + "=(" +
			projectQuery.toString() + ")");
		tagQuery.addCondition("EXISTS (" + projectFilesQuery.toString() + ")");
	}
	// Returns a query for a tag name in a list of specified origins
	// origins: A hash of origin type -> vector of origin names
	private Query getTagInOriginsQuery(String tag,
		HashMap<String, Vector<String>> origins) 
	{
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Vector<String>> origin: origins.entrySet())
		{
			String type = origin.getKey();
			Vector<String> names = origin.getValue();
			if (names == null || names.size() == 0)
				continue;
			if (sb.length() > 0)
				sb.append(" OR ");
			sb.append("(" + ORIGINS_TYPE + "=" + quote(type) + " AND " +
				ORIGINS_NAME + " IN (");
			StringBuffer nameList = new StringBuffer();
			for (String name: names) {
				if (nameList.length() > 0)
					nameList.append(", ");
				nameList.append(quote(name));
			}
			sb.append(nameList + "))");
		}
		Query projectQuery = new Query(ORIGINS_ID, ORIGINS_TABLE, sb);

		Query projectFilesQuery = new Query();
		projectFilesQuery.setColumn(MAP_FILE_ID);
		projectFilesQuery.setTable(MAP_TABLE);
		projectFilesQuery.addCondition(field(
				MAP_TABLE, MAP_FILE_ID) + "=" + field(FILES_TABLE, FILES_ID));
		projectFilesQuery.addCondition(field(
				MAP_TABLE, MAP_ORIGIN_ID) + " IN (" + projectQuery.toString() + ")");

		Query q = new Query();
		q.addColumn("*");
		q.addTable(TAGS_TABLE);
		q.addTable(FILES_TABLE);
		q.addCondition(field(TAGS_TABLE, TAGS_NAME) + "=" + quote(tag));
		q.addCondition(field(TAGS_TABLE, TAGS_FILE_ID) + "=" + field(FILES_TABLE, FILES_ID));
		q.addCondition("EXISTS (" + projectFilesQuery.toString() + ")");
		return q;
	}
	// Returns a query for a tag name in a specified project
	private Query getTagInProjectQuery(String tag, String project) {
		Query projectQuery = new Query(ORIGINS_ID, ORIGINS_TABLE, ORIGINS_NAME + "=" +
			quote(project));
		projectQuery.addCondition(ORIGINS_TYPE + "=" + quote(PROJECT_ORIGIN));
		
		Query projectFilesQuery = new Query();
		projectFilesQuery.setColumn(MAP_FILE_ID);
		projectFilesQuery.setTable(MAP_TABLE);
		projectFilesQuery.addCondition(field(
			MAP_TABLE, MAP_FILE_ID) + "=" + field(FILES_TABLE, FILES_ID));
		projectFilesQuery.addCondition(field(
			MAP_TABLE, MAP_ORIGIN_ID) + "=(" + projectQuery.toString() + ")");
		
		Query q = new Query();
		q.addColumn("*");
		q.addTable(TAGS_TABLE);
		q.addTable(FILES_TABLE);
		q.addCondition(field(TAGS_TABLE, TAGS_NAME) + "=" + quote(tag));
		q.addCondition(field(TAGS_TABLE, TAGS_FILE_ID) + "=" + field(FILES_TABLE, FILES_ID));
		q.addCondition("EXISTS (" + projectFilesQuery.toString() + ")");
		return q;
	}
	// Runs a query for the specified tag name
	public ResultSet queryTag(String tag) throws SQLException {
		return query(getTagNameQuery(tag));
	}
	// Runs a query for the specified tag name in the specified origins
	public ResultSet queryTagInOrigins(String tag, HashMap<String, Vector<String>> origins)
	throws SQLException
	{
		Query q = getTagInOriginsQuery(tag, origins);
		return query(q);
	}
	// Runs a query for the specified tag name in the specified project
	public ResultSet queryTagInProject(String tag, String project) throws SQLException {
		Query q = getTagInProjectQuery(tag, project);
		return query(q);
	}
	// Returns the ID of an origin
	public int getOriginID(String type, String name) {
		return queryInteger(ORIGINS_ID, "SELECT " + ORIGINS_ID + " FROM " + ORIGINS_TABLE +
			" WHERE " + ORIGINS_TYPE + "=" + quote(type) + " AND " + ORIGINS_NAME + "=" +
			quote(name), -1);
	}
	
	// Inserts a new origin to the DB
	public void insertOrigin(String type, String name) throws SQLException {
		// Ensure the origin is not inserted twice...
		if (getOriginID(type, name) >= 0)
			return;
		update("INSERT INTO " + ORIGINS_TABLE + " (" +
			ORIGINS_TYPE + "," + ORIGINS_NAME + ") VALUES (" +
			quote(type) + "," + quote(name) + ")");
	}

	// Delete all data associated with the specified origin
	public void deleteOriginAssociatedData(String type, String name) throws SQLException {
		int originId = getOriginID(type, name); 
		if (originId < 0)
			return;
		// Remove all mappings to the origin
		update("DELETE FROM " + MAP_TABLE + " WHERE " + MAP_ORIGIN_ID + "=" +
			quote(originId));
		// Remove all orphaned (with no origin) files
		update("DELETE FROM " + FILES_TABLE + " WHERE NOT EXISTS " +
			"(SELECT " + MAP_FILE_ID + " FROM " + MAP_TABLE + " WHERE " +
			MAP_FILE_ID + "=" + FILES_ID + ")");
		// Remove all orphaned (with no file) tags
		update("DELETE FROM " + TAGS_TABLE + " WHERE NOT EXISTS " +
			"(SELECT " + FILES_ID + " FROM " + FILES_TABLE + " WHERE " +
			FILES_ID + "=" + TAGS_FILE_ID + ")");
	}
	
	// Deletes an origin and all its associated data from the DB
	public void deleteOrigin(String type, String name) throws SQLException {
		deleteOriginAssociatedData(type, name); 
		update("DELETE FROM " + ORIGINS_TABLE + " WHERE " +
			ORIGINS_TYPE + "=" + quote(type) + " AND " +
			ORIGINS_NAME + "=" + quote(name));
	}
	
	public String field(String table, String column) {
		return table + "." + column;
	}

	/*
	 * Check if the table contains a row with the specified value in the specified column.
	 * Used for checking if a buffer is in the DB. 
	 */
	private boolean tableColumnContainsValue(String table, String column, Object value) {
		try {
			Query q = new Query(column, table, column + "=" + quote(value));
			q.setLimit(1);
			ResultSet rs = query(q);
			if (rs == null)
				return false;
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Runs a query and returns an integer value from the specified column of the first
	 * row in the query result, or defaultValue if the query result is empty.
	 */
	public int queryInteger(String column, String query, int defaultValue) {
		try {
			ResultSet rs = query(query);
			if ((rs == null) || (! rs.next()))
				return defaultValue;
			return rs.getInt(column);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	/*
	 * Converts query results to tags.
	 */
	public Vector<Tag> getResultSetTags(ResultSet rs) {
		Vector<Tag> tags = new Vector<Tag>();
		try {
			ResultSetMetaData meta;
			meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			String [] cols = new String[columnCount];
			int [] types = new int[columnCount];
			boolean [] include = new boolean[columnCount];
			int nameIndex = 0;
			int patternIndex = 0;
			int fileIndex = 0;
			for (int i = 0; i < cols.length; i++) {
				cols[i] = meta.getColumnName(i + 1);
				types[i] = meta.getColumnType(i + 1);
				include[i] = false;
				String table = meta.getTableName(i + 1);
				if (table.equals(TAGS_TABLE)) {
					include[i] = true;
					if (cols[i].equals(TAGS_NAME))
						nameIndex = i + 1;
					else if (cols[i].equals(TAGS_PATTERN))
						patternIndex = i + 1;
				}
				else if (table.equals(FILES_TABLE)) {
					if (cols[i].equals(FILES_NAME))
						fileIndex = i + 1;
				}
			}
			while (rs.next()) {
				String name = ((nameIndex == 0) ? "" : rs.getString(nameIndex));
				String file = ((fileIndex == 0) ? "" : rs.getString(fileIndex));
				String pattern = ((patternIndex == 0) ? "" : rs.getString(patternIndex));
				Tag t = new Tag(name, file, pattern);
				Hashtable<String, String> extensions = new Hashtable<String, String>();
				Hashtable<String, String> attachments = new Hashtable<String, String>();
				for (int i = 0; i < cols.length; i++) {
					if ((types[i] != Types.VARCHAR) || (! include[i]))
						continue;
					String value = rs.getString(i + 1); 
					if (value != null) {
						if (isExtensionColumn(cols[i]))
							extensions.put(column2extension(cols[i]), value);
						else
							attachments.put(cols[i], value);
					}
				}
				t.setExtensions(extensions);
				t.setAttachments(attachments);
				tags.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tags;
	}
	
	public Vector<String> getOrigins(String type) {
		return queryStringList(ORIGINS_NAME,
			"SELECT * FROM " + ORIGINS_TABLE + " WHERE " +
			ORIGINS_TYPE + "=" + quote(type));
	}
	
	public synchronized void update(String expression) throws SQLException {
		try {
			if (st.executeUpdate(expression) == -1)
	            System.err.println("db error : " + expression);
		} catch (SQLException e) {
			System.err.println("SQL update: " + expression);
			throw e;
		}
    }
	
	public synchronized ResultSet query(Query query) throws SQLException {
		return query(query.toString());
	}
	public synchronized ResultSet query(String expression) throws SQLException {
		if (conn == null) {
			JOptionPane.showMessageDialog(null, "Tag database not started, " +
				"probably due to an error in the database configuration.\n" +
				"Use Plugin -> CtagsInterface -> Change database settings... " +
				"to check and update the configuration.");
			return null;
		}
		try {
			Log.log(Log.MESSAGE, TagDB.class, "Query '" + expression + "' started");
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(expression);
			Log.log(Log.MESSAGE, TagDB.class, "Query '" + expression + "' completed");
			return rs;
		}
		catch (SQLException e) {
			System.err.println("Failed query: " + expression);
			throw e;
		}
    }

	public Query getIdenticalTagQuery(Tag tag) {
		Query q = getBasicTagQuery();
		q.addCondition(field(TAGS_TABLE, TAGS_NAME) + "=" + quote(tag.getName()));
		q.addCondition(field(FILES_TABLE, FILES_NAME) + "=" + quote(tag.getFile()));
		Set<String> extensions = tag.getExtensions();
		if (extensions != null) {
			for (String s: extensions) {
				String col = extension2column(s);
				if (col.equals(TAGS_LINE))
					continue;
				q.addCondition(field(TAGS_TABLE, col) + "=" + quote(tag.getExtension(s)));
			}
		}
		Set<String> attachments = tag.getAttachments();
		if (attachments != null) {
			for (String s: attachments)
				q.addCondition(field(TAGS_TABLE, s) + "=" + quote(tag.getAttachment(s)));
		}
		return q;
	}

	public Vector<String> queryStringList(String column, String query) {
		Vector<String> values = new Vector<String>();
		try {
			ResultSet rs = query(query);
			if (rs == null)
				return values;
			while (rs.next())
				values.add(rs.getString(column));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return values;
	}
	
	public void shutdown()
	{
		try {
			if (st != null) {
				if ((onExit != null) && (onExit.length() > 0))
					st.execute(onExit);
		        st.close();
			}
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static public String quote(Object value) {
		if (value instanceof String) {
			if (charsToEscape != null)
				value = ((String) value).replaceAll(charsToEscape, "\\\\$1");
			return "'" + ((String)value).replaceAll("'", "''") + "'";
		}
		return value.toString();
	}
	
	public void deleteRowsWithValue(String table, String column, Object value) {
		try {
			update("DELETE FROM " + table + " WHERE " + column + "=" + quote(value));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public String extension2column(String extensionName) {
		return TAGS_EXTENSION_PREFIX + extensionName.toUpperCase();
	}
	static public String column2extension(String columnName) {
		return columnName.substring(TAGS_EXTENSION_PREFIX.length()).toLowerCase();
	}
	static public boolean isExtensionColumn(String columnName) {
		return columnName.startsWith(TAGS_EXTENSION_PREFIX);
	}
	
	public HashSet<String> getColumns() {
		HashSet<String> columnNames = new HashSet<String>();
		try {
			Query q = new Query("*", TAGS_TABLE, TAGS_NAME + "=''");
			ResultSet rs = query(q);
			if (rs == null)
				return null;
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			for (int i = 0; i < cols; i++)
				columnNames.add(meta.getColumnName(i + 1));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return columnNames;
	}
	
	private String getDBFilePath() {
		return jEdit.getSettingsDirectory() + "/CtagsInterface/tagdb";
	}

	private void removeStaleLock() {
		File lock = new File(getDBFilePath() + ".lck");
		if (lock.exists())
			lock.delete();
	}

	// Create an index on a tag extension field
	public void createIndex(String index, String extension)
	throws SQLException {
		createIndex(index, TAGS_TABLE, extension2column(extension));
	}
	
	// Create an index on a table column
	public void createIndex(String index, String table, String column)
	throws SQLException {
		boolean exists = false;
		ResultSet indices = conn.getMetaData().getIndexInfo(null, null, table, false, false);
		while (indices.next()) {
			if (indices.getString("INDEX_NAME").equals(index)) {
				exists = true;
				break;
			}
		}
		if (exists) {
			indices.close();
			return;
		}
		update("CREATE INDEX " + index + " ON " + table + "(" + column + ")");
	}
	
	private void createTable(String table, String [] columns)
	throws SQLException {
		ResultSet tables = conn.getMetaData().getTables(null, null, table, null);
		if (tables.first()) {
			tables.close();
			return;	// Table already exists
		}
		StringBuffer st = new StringBuffer("CREATE TABLE ");
		st.append(table);
		st.append("(");
		for (int i = 0; i < columns.length; i += 2) {
			if (i > 0)
				st.append(", ");
			st.append(columns[i]);
			st.append(" ");
			st.append(columns[i + 1]);
		}
		st.append(")");
		update(st.toString());
	}

	private void createTables() {
		try {
			// Create Tags table
			createTable(TAGS_TABLE, new String [] {
				TAGS_NAME, varcharType,
				TAGS_FILE_ID, integerType,
				TAGS_PATTERN, varcharType
			});
			createIndex("TAGS_NAME", TAGS_TABLE, TAGS_NAME);
			createIndex("TAGS_FILE", TAGS_TABLE, TAGS_FILE_ID);
			// Create Files table
			createTable(FILES_TABLE, new String [] {
				FILES_ID, identityType,
				FILES_NAME, varcharType
			});
			createIndex("FILES_NAME", FILES_TABLE, FILES_NAME);
			// Create Origins table
			createTable(ORIGINS_TABLE, new String [] {
				ORIGINS_ID, identityType,
				ORIGINS_NAME, varcharType,
				ORIGINS_TYPE, varcharType
			});
			// Insert the TEMP origin if doesn't exist
			if (getOrigins(TEMP_ORIGIN).isEmpty())
				update("INSERT INTO " + ORIGINS_TABLE + " (" + ORIGINS_ID + "," +
					ORIGINS_NAME + "," + ORIGINS_TYPE + ") VALUES (" + TEMP_ORIGIN_INDEX +
					"," + quote(TEMP_ORIGIN_NAME) + ", " + quote(TEMP_ORIGIN) + ")");
			// Create Map table
			createTable(MAP_TABLE, new String [] {
				MAP_FILE_ID, integerType,
				MAP_ORIGIN_ID, integerType
			});
			createIndex("MAP_FILE_ID", MAP_TABLE, MAP_FILE_ID);
			createIndex("MAP_ORIGIN_ID", MAP_TABLE, MAP_ORIGIN_ID);
		} catch (SQLException e) {
			// The methods createTable/createIndex do not execute an SQL statement
			// if the table/index already exists. Hence, an exception here means
			// there is a real problem.
			e.printStackTrace();
		}
	}
	
	public static String getDbSelectedPreset() {
		return jEdit.getProperty(DB_SELECTED_PRESET);
	}
	private static String getDbPropertyPresetSuffix(String preset) {
		if (preset.equals(CUSTOM_DB))
			preset = "";
		else
			preset = "." + preset;
		return preset;
	}
	public static String getDbPropertyByPreset(String propBase, String preset) {
		return jEdit.getProperty(propBase + getDbPropertyPresetSuffix(preset));
	}
	public static void setDbPropertyByPreset(String propBase, String preset, String value) {
		preset = TagDB.getDbPropertyPresetSuffix(preset);
		jEdit.setProperty(propBase + preset, value);
	}
	public static String getDbPropertyOfSelectedPreset(String propBase) {
		String preset = getDbSelectedPreset();
		return getDbPropertyByPreset(propBase, preset);
	}
	public static String getDbClass() {
		return getDbPropertyOfSelectedPreset(DB_CLASS);
	}
	public static String getDbConnection() {
		return getDbPropertyOfSelectedPreset(DB_CONNECTION);
	}
	public static String getDbUser() {
		return getDbPropertyOfSelectedPreset(DB_USER);
	}
	public static String getDbPassword() {
		return getDbPropertyOfSelectedPreset(DB_PASSWORD);
	}
	public static String getDbMappingsFile() {
		return getDbPropertyOfSelectedPreset(DB_MAPPINGS_FILE);
	}
	public static String getDbOnExit() {
		return getDbPropertyOfSelectedPreset(DB_ON_EXIT);
	}
}
