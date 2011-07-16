/*
 * XmlInsert.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.List;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.Log;
import sidekick.*;
import xml.completion.*;
import xml.parser.TagParser;
//}}}

public class XmlInsert extends JPanel implements EBComponent
{
	//{{{ XmlInsert constructor
	public XmlInsert(View view, boolean sideBySide)
	{
		this.view = view;

		setLayout(new GridLayout(sideBySide ? 1 : 3,
			sideBySide ? 3 : 1,3,3));

		JPanel elementPanel = new JPanel(new BorderLayout());
		elementPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.elements")));

		elementList = new JList();
		elementList.setName("elements");
		elementList.setCellRenderer(new XmlListCellRenderer());
		elementList.addMouseListener(new MouseHandler());
		elementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		elementPanel.add(BorderLayout.CENTER,
			new JScrollPane(elementList));

		add(elementPanel);

		JPanel entityPanel = new JPanel(new BorderLayout());
		entityPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.entities")));

		entityList = new JList();
		entityList.setName("entities");
		entityList.setCellRenderer(new XmlListCellRenderer());
		entityList.addMouseListener(new MouseHandler());
		entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entityPanel.add(BorderLayout.CENTER,
			new JScrollPane(entityList));

		add(entityPanel);

		JPanel idPanel = new JPanel(new BorderLayout());
		idPanel.add(BorderLayout.NORTH,
			new JLabel(jEdit.getProperty("xml-insert.ids")));

		idList = new JList();
		idList.setName("ids");
		idList.setCellRenderer(new XmlListCellRenderer());
		idList.addMouseListener(new MouseHandler());
		idList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		idPanel.add(BorderLayout.CENTER,new JScrollPane(idList));

		add(idPanel);

		caretHandler = new CaretHandler();

		update();

		updateTimer = new Timer(0,new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateTagList();
			}
		});
	} //}}}

	//{{{ addNotify() method
	public void addNotify()
	{
		super.addNotify();
		EditBus.addToBus(this);

		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			JEditTextArea textArea = editPanes[i].getTextArea();
			textArea.addCaretListener(caretHandler);
		}
	} //}}}

	//{{{ removeNotify() method
	public void removeNotify()
	{
		super.removeNotify();
		EditBus.removeFromBus(this);

		EditPane[] editPanes = view.getEditPanes();
		for(int i = 0; i < editPanes.length; i++)
		{
			JEditTextArea textArea = editPanes[i].getTextArea();
			textArea.removeCaretListener(caretHandler);
		}

		updateTimer.stop();
	} //}}}

	//{{{ handleMessage() method
	public void handleMessage(EBMessage msg)
	{
		//{{{ EditPaneUpdate
		if(msg instanceof EditPaneUpdate)
		{
			EditPaneUpdate emsg = (EditPaneUpdate)msg;
			EditPane editPane = emsg.getEditPane();
			if(emsg.getWhat() == EditPaneUpdate.BUFFER_CHANGED
				&& editPane == view.getEditPane())
			{
				update();
			}
			else if(emsg.getWhat() == EditPaneUpdate.CREATED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.addCaretListener(caretHandler);
			}
			else if(emsg.getWhat() == EditPaneUpdate.DESTROYED
				&& editPane.getView() == view)
			{
				JEditTextArea textArea = editPane.getTextArea();
				textArea.removeCaretListener(caretHandler);
			}
			else if(emsg.getWhat() == EditPaneUpdate.BUFFER_CHANGED)
				update();
		} //}}}
		//{{{ BufferUpdate
		else if(msg instanceof BufferUpdate)
		{
			BufferUpdate bmsg = (BufferUpdate)msg;
			if((bmsg.getWhat() == BufferUpdate.PROPERTIES_CHANGED
				|| bmsg.getWhat() == BufferUpdate.LOADED)
				&& bmsg.getBuffer() == view.getBuffer())
			{
				update();
			}
		} //}}}
		//{{{ SideKickUpdate
		else if(msg instanceof SideKickUpdate)
		{
			if(((SideKickUpdate)msg).getView() == view)
				update();
		} //}}}
	} //}}}
	
	
	//{{{ openAndGo method
	public static void openAndGo(View view, TextArea textArea, String uri, int gotoLine, int col)
	{
		Buffer buffer = jEdit.openFile(view,uri);
		if(buffer == null)
			return;
		// waiting for complete loading of buffer seems
		// to prevent the NullPointerException
		VFSManager.waitForRequests();
		int line = Math.min(buffer.getLineCount() - 1,gotoLine);
		int column = Math.min(buffer.getLineLength(line),col);
		int offset = buffer.getLineStartOffset(line) + column;
		// TODO: follow best practice from Navigator plugin
		try{
			textArea.setCaretPosition(offset);
		}catch(NullPointerException npe){
			Log.log(Log.ERROR,XmlInsert.class,"FIXME : setCaretPosition("+offset+")");
			Log.log(Log.ERROR,XmlInsert.class,npe);
		}
	}//}}}

	//{{{ Private members

	//{{{ Instance variables
	private View view;
	private CaretHandler caretHandler;
	private List elements;
	private JList elementList;
	private JList entityList;
	private JList idList;
	private boolean html;

	private int delay;
	private Timer updateTimer;
	//}}}

	//{{{ update() method
	private void update()
	{
		SideKickParsedData _data = SideKickParsedData.getParsedData(view);
		if(!(_data instanceof XmlParsedData))
		{
			setDeclaredEntities(null);
			setDeclaredIDs(null);
			html = false;
		}
		else
		{
			XmlParsedData data = (XmlParsedData)_data;
			setDeclaredEntities(data.entities);
			setDeclaredIDs(data.ids);
			html = data.html;
		}

		updateTagList();
	} //}}}

	//{{{ showNotParsedMessage() method
	private void showNotParsedMessage()
	{
		setDeclaredElements(null);
		setDeclaredEntities(null);
		setDeclaredIDs(null);
	} //}}}

	//{{{ setDeclaredElements() method
	private void setDeclaredElements(List elements)
	{
		this.elements = elements;

		if(elements == null)
		{
			DefaultListModel model = new DefaultListModel();
			model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
			elementList.setModel(model);
		}
		else
		{
			ArrayListModel model = new ArrayListModel(elements);
			elementList.setModel(model);

			if(model.getSize() != 0)
			{
				Rectangle cellBounds = elementList
					.getCellBounds(0,0);
				if(cellBounds != null)
				{
					elementList.setFixedCellHeight(cellBounds.height);
				}
			}
		}
	} //}}}

	//{{{ setDeclaredEntities() method
	private void setDeclaredEntities(List entities)
	{
		if(entities == null)
		{
			DefaultListModel model = new DefaultListModel();
			model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
			entityList.setModel(model);
		}
		else
		{
			ArrayListModel model = new ArrayListModel(entities);
			entityList.setModel(model);

			if(model.getSize() != 0)
			{
				Rectangle cellBounds = entityList
					.getCellBounds(0,0);
				if(cellBounds != null)
				{
					entityList.setFixedCellHeight(cellBounds.height);
				}
			}
		}

	} //}}}

	//{{{ setDeclaredIDs() method
	private void setDeclaredIDs(List ids)
	{
		if(ids == null)
		{
			DefaultListModel model = new DefaultListModel();
			model.addElement(jEdit.getProperty("xml-insert.not-parsed"));
			idList.setModel(model);
		}
		else
		{
			ArrayListModel model = new ArrayListModel(ids);
			idList.setModel(model);

			if(model.getSize() != 0)
			{
				Rectangle cellBounds = idList.getCellBounds(0,0);
				if(cellBounds != null)
				{
					idList.setFixedCellHeight(cellBounds.height);
				}
			}
		}

	} //}}}

	//{{{ updateTagList() method
	private void updateTagList()
	{
		Buffer buffer = view.getBuffer();

		SideKickParsedData _data = SideKickParsedData.getParsedData(view);

		if(_data instanceof XmlParsedData)
		{
			XmlParsedData data = (XmlParsedData)_data;

			Selection[] selection = view.getTextArea().getSelection();

			if(selection.length > 0) {
				setDeclaredElements(data.getAllowedElements(
					buffer,
					selection[0].getStart(),
					selection[0].getEnd()));
			}
			else {
				setDeclaredElements(data.getAllowedElements(
					buffer,view.getTextArea().getCaretPosition()));
			}
		}
		else
		{
			setDeclaredElements(null);
		}
	} //}}}

	//{{{ updateTagListWithDelay() method
	private void updateTagListWithDelay()
	{
		if(updateTimer.isRunning())
			updateTimer.stop();

		updateTimer.setInitialDelay(200);
		updateTimer.setRepeats(false);
		updateTimer.start();
	} //}}}

	//}}}
	
	

	//{{{ ArrayListModel class
	static class ArrayListModel implements ListModel
	{
		List list;

		ArrayListModel(List list)
		{
			this.list = list;
		}

		public int getSize()
		{
			return list.size();
		}
		public Object getElementAt(int index)
		{
			return list.get(index);
		}

		public void addListDataListener(ListDataListener l) {}
		public void removeListDataListener(ListDataListener l) {}
	} //}}}

	//{{{ CaretHandler class
	class CaretHandler implements CaretListener
	{
		public void caretUpdate(CaretEvent evt)
		{
			if(evt.getSource() == view.getTextArea())
				updateTagListWithDelay();
		}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			Selection insideTag = null;
			//{{{ Handle clicks in element list
			if(evt.getSource() == elementList)
			{
				
				int index = elementList.locationToIndex(
					evt.getPoint());
				if(index == -1)
					return;
				EditPane editPane = view.getEditPane();
				JEditTextArea textArea = editPane.getTextArea();
				Buffer buffer = editPane.getBuffer();
				int pos = textArea.getCaretPosition();
				String t = buffer.getText(0, pos);
				/* Check if we are inside a tag, and if so, wipe it out before
				   inserting the one we just created */
				if (TagParser.isInsideTag(t, pos)) {
					int openAngle = t.lastIndexOf('<');
					insideTag = new Selection.Range(openAngle, pos);
				}
				
				idList.setSelectedIndex(index);
				Object obj = elementList.getModel().getElementAt(index);
				if(!(obj instanceof ElementDecl))
					return;

				ElementDecl element = (ElementDecl)obj;

				
				//{{{ Handle right mouse button click
				if(GUIUtilities.isPopupTrigger(evt))
				{
					String openingTag = "<" + element.name
						+ element.getRequiredAttributesString()
						+ (element.empty && !html
						? XmlActions.getStandaloneEnd() : ">");
					String closingTag;
					if(element.empty)
						closingTag = "";
					else
						closingTag = "</" + element.name + ">";

					Selection[] selection = textArea.getSelection();

					if(selection.length > 0)
					{
						try
						{
							buffer.beginCompoundEdit();

							for(int i = 0; i < selection.length; i++)
							{
								buffer.insert(selection[i].getStart(),
									openingTag);
								buffer.insert(selection[i].getEnd(),
									closingTag);
							}
						}
						finally
						{
							buffer.endCompoundEdit();
						}

						textArea.selectNone();
					}
					else
					{
						textArea.setSelectedText(openingTag);
						int caret = textArea.getCaretPosition();
						textArea.setSelectedText(closingTag);
						textArea.setCaretPosition(caret);
					}

					textArea.selectNone();
					textArea.requestFocus();
				} //}}}
				else
				{
					// show edit tag dialog box
					XmlActions.showEditTagDialog(view,element,insideTag);
				}
			} //}}}
			//{{{ Handle clicks in entity list
			else if(evt.getSource() == entityList)
			{
				int index = entityList.locationToIndex(
					evt.getPoint());
				if(index == -1)
					return;

				Object obj = entityList.getModel().getElementAt(index);
				if(!(obj instanceof EntityDecl))
					return;

				EntityDecl entity = (EntityDecl)obj;

				JEditTextArea textArea = view.getTextArea();

				textArea.setSelectedText("&" + entity.name + ";");
				textArea.requestFocus();
			} //}}}
			//{{{ Handle clicks in ID list
			else if(evt.getSource() == idList)
			{
				int index = idList.locationToIndex(
					evt.getPoint());
				if(index == -1)
					return;

				idList.setSelectedIndex(index);
				Object obj = idList.getModel().getElementAt(index);
				if(!(obj instanceof IDDecl))
					return;

				IDDecl id = (IDDecl)obj;

				JEditTextArea textArea = view.getTextArea();

				if(GUIUtilities.isPopupTrigger(evt))
				{
					openAndGo(view, textArea, id.uri, id.line, id.column);
				}
				else
				{
					textArea.setSelectedText(id.id);
				}

				textArea.requestFocus();
			} //}}}
		}
	} //}}}
}
