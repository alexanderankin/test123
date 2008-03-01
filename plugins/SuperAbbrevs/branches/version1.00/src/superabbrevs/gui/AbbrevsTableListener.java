package superabbrevs.gui;

import java.util.EventListener;
import superabbrevs.model.Abbrev;

interface AbbrevsTableListener extends EventListener {
  void tableChanged(Abbrev abbrev);
}
