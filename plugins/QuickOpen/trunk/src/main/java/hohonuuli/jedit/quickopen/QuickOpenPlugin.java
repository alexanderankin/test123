/*
 * @(#)QuickOpenPlugin.java   2011.01.21 at 03:18:25 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package hohonuuli.jedit.quickopen;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.VFSPathSelected;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 * @version        Enter version here..., 2011.01.15 at 11:08:15 PST
 * @author         Brian Schlining [brian@mbari.org]
 */
public class QuickOpenPlugin extends EBPlugin {

    /**  */
    public static final String NAME = "quickopen";

    /**  */
    public static final String OPTION_PREFIX = "options.quickopen.";
    private static final ConcurrentMap<View, QuickOpenFrame> frameMap = new ConcurrentHashMap<View, QuickOpenFrame>();
    protected static final String PROP_EXCLUDE_DIR = "quickopen.exclude-directories";
    protected static final String PROP_EXCLUDE_EXT = "quickopen.exclude-extensions";
    protected static final String PROP_INCLUDE_HIDDEN = "quickopen.include-hidden";
    protected static final String PROP_MAX_FILES = "quickopen.maximum-files";
    protected static final String PROP_MATCH_FIRST = "quickopen.match-first";

    /**
     *
     * @param view
     * @return
     */
    public static QuickOpenFrame getQuickOpenFrame(View view) {
        QuickOpenFrame frame = null;

        frame = frameMap.get(view);

        if (frame == null) {
            frame = new QuickOpenFrame(view);
            frame.updateFileList();
            frame.pack();
            frameMap.put(view, frame);
            frame.setLocationRelativeTo(view);
        }

        return frame;
    }

    @Override
    public void handleMessage(EBMessage message) {
        if (message instanceof  VFSPathSelected) {
            handleVFSPathSelected((VFSPathSelected) message);
        }
        else if (message instanceof  ViewUpdate) {
            handleViewUpdate((ViewUpdate) message);
        }
        else if (message instanceof PropertiesChanged) {
            handlePropertiesChanged((PropertiesChanged) message);
        }
    }

    //@EBHandler
    public void handleVFSPathSelected(VFSPathSelected vfsPathSelected) {
        View view = vfsPathSelected.getView();
        QuickOpenFrame frame = frameMap.get(view);
        if (frame != null) {
            File newDirectory = frame.getController().getDirectory();
            File oldDirectory = frame.getFileList().getRootDirectory();

            try {
                // Only update the file list if the path is actually different
                if ((oldDirectory == null) ||
                        !newDirectory.getCanonicalPath().equals(oldDirectory.getCanonicalPath())) {
                    getQuickOpenFrame(view).updateFileList();
                }
            }
            catch (IOException e) {
                getQuickOpenFrame(view).updateFileList();
            }
        }

    }

    //@EBHandler
    public void handleViewUpdate(ViewUpdate viewUpdate) {
        View view = viewUpdate.getView();

        if (viewUpdate == ViewUpdate.CLOSED) {
            frameMap.remove(view);
        }
        else if (viewUpdate == ViewUpdate.ACTIVATED) {
            // Refresh list of files when activated
            QuickOpenFrame frame = frameMap.get(view);
            if (frame != null) {
                frame.updateFileList();
            }
        }
    }

    //@EBHandler
    public void handlePropertiesChanged(PropertiesChanged msg) {
        frameMap.clear();

    }

    /**
     *
     * @param view
     */
    public static void quickOpen(View view) {
        getQuickOpenFrame(view).setVisible(true);

    }

    /**
     * Called on jEdit startup
     */
    public void start() {}

    /**
     * Called on jEdit shutdown
     */
    public void stop() {}
}
