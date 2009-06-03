package elementmatcher;

import org.apache.commons.collections15.ResettableIterator;
import org.apache.commons.collections15.iterators.ObjectArrayIterator;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class ProviderManager {

    private List<ElementProvider<?>> providers = new ArrayList<ElementProvider<?>>();

    protected ProviderManager() {
        reload();
    }

    synchronized void reload() {
        providers = new ArrayList<ElementProvider<?>>();
        for (String serviceName : ServiceManager.getServiceNames(ElementProvider.class.getName())) {
            final ElementProvider<?> provider = (ElementProvider<?>)ServiceManager.getService(ElementProvider.class.getName(), serviceName);
            provider.setEnabled(jEdit.getBooleanProperty(provider.getOptionsPrefix() + ElementProvider.ENABLED_PROPERTY, true));
            provider.setColor(jEdit.getColorProperty(provider.getOptionsPrefix() + ElementProvider.COLOR_PROPERTY, Color.BLUE));
            provider.setUnderline(jEdit.getBooleanProperty(provider.getOptionsPrefix() + ElementProvider.UNDERLINE_PROPERTY, true));
            providers.add(provider);
        }
    }

    ResettableIterator<ElementProvider<?>> getProviders() {
        return new ObjectArrayIterator<ElementProvider<?>>(providers.toArray(new ElementProvider<?>[providers.size()]));
    }

}