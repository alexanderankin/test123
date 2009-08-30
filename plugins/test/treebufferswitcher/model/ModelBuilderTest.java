package treebufferswitcher.model;

import org.gjt.sp.jedit.BufferMock;
import org.gjt.sp.jedit.jEdit;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import treebufferswitcher.model.PathItem;
import treebufferswitcher.model.BufferItem;
import treebufferswitcher.model.FlatModelBuilder;
import treebufferswitcher.model.OneLevelGroupedModelBuilder;
import treebufferswitcher.model.MultiLevelGroupedModelBuilder;

public class ModelBuilderTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        // mock jedit to allow creation of new buffers
        Class propertyManagerClass = Class.forName("org.gjt.sp.jedit.PropertyManager");
        Constructor propertyManagerConstructor = propertyManagerClass.getDeclaredConstructor();
        propertyManagerConstructor.setAccessible(true);
        Object propertyManager = propertyManagerConstructor.newInstance();
        Field propertyManagerField = jEdit.class.getDeclaredField("propMgr");
        propertyManagerField.setAccessible(true);
        propertyManagerField.set(null, propertyManager);
    }

    @Test
    public void testFlatModelBuilder() {
        BufferMock[] src = mockBuffers("1/2/a.txt", "1/2/b.txt", "3/c.txt");
        assertModel(new FlatModelBuilder().createModel(src),
                "1/2/a.txt", "1/2/b.txt", "3/c.txt");
    }

    @Test
    public void testOneLevelModelBuilder() {
        BufferMock[] src = mockBuffers("1/2/a.txt", "1/3/b.txt", "1/2/c.txt", "1/d.txt");
        assertModel(new OneLevelGroupedModelBuilder().createModel(src),
                "1", "> d.txt", "1/2", "> a.txt", "> c.txt", "1/3", "> b.txt");
    }

    @Test
    public void testMultiLevelModelBuilder() {
        BufferMock[] src = mockBuffers("1/2/a.txt", "1/3/b.txt", "1/2/c.txt", "1/d.txt");
        assertModel(new treebufferswitcher.model.MultiLevelGroupedModelBuilder(false).createModel(src),
                "1", "> 2", "> > a.txt", "> > c.txt", "> 3", "> > b.txt", "> d.txt");
    }

    @Test
    public void testCompactMultiLevelModelBuilder() {
        BufferMock[] src = mockBuffers("1/2/a.txt", "1/3/b.txt", "1/2/c.txt", "1/4/5/e.txt", "1/d.txt");
        assertModel(new MultiLevelGroupedModelBuilder(true).createModel(src),
                "1", "> 2", "> > a.txt", "> > c.txt", "> 3", "> > b.txt", "> 4/5", "> > e.txt", "> d.txt");
    }

    private static BufferMock[] mockBuffers(String... paths) {
        BufferMock[] r = new BufferMock[paths.length];
        for (int i=0; i<paths.length; ++i) {
            r[i] = new BufferMock(paths[i]);
        }
        return r;
    }

    private static void assertModel(ComboBoxModel model, String... expected) {
        DefaultComboBoxModel m = (DefaultComboBoxModel)model;
        try {
            assertEquals(expected.length, m.getSize());
            for (int i=0; i<expected.length; ++i) {
                Object item = model.getElementAt(i);
                assertEquals(expected[i], itemToPath(item));
            }
        } catch (AssertionError e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Model doesn't equal to expected:\nExpected:\n");
            for (String item : expected) {
                msg.append("\t").append(item).append("\n");
            }
            msg.append("Actual:\n");
            for (int i=0; i<model.getSize(); ++i) {
                msg.append("\t").append(itemToPath(model.getElementAt(i))).append("\n");
            }
            throw new AssertionError(msg.toString());
        }
        int keyIndex = -1;
        for (int i=0; i<model.getSize(); ++i) {
            Object item = model.getElementAt(i);
            if (item instanceof BufferItem) {
                assertEquals(++keyIndex, ((BufferItem)item).keyIndex);
            }
        }
    }

    private static String itemToPath(Object item) {
        if (item instanceof PathItem) {
            PathItem pathItem = (PathItem)item;
            return repeat("> ", pathItem.level) + pathItem.title;
        } else if (item instanceof BufferItem) {
            BufferItem bufferItem = (BufferItem)item;
            return repeat("> ", bufferItem.level) + bufferItem.title;
        } else {
            throw new UnsupportedOperationException("Bad item class: " + item.getClass().getName());
        }
    }

    private static String repeat(String s, int n) {
        StringBuilder r = new StringBuilder();
        for (int i=0; i<n; ++i) {
            r.append(s);
        }
        return r.toString();
    }

}