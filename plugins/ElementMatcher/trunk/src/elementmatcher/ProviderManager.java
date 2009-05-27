package elementmatcher;

import org.apache.commons.collections15.ResettableIterator;
import org.apache.commons.collections15.iterators.ObjectArrayIterator;
import org.gjt.sp.jedit.ServiceManager;
import java.util.ArrayList;
import java.util.List;

public class ProviderManager {

    private List<ElementProvider<?>> providers = new ArrayList<ElementProvider<?>>();

    protected ProviderManager() {
        reload();
    }

    synchronized void reload() {
        providers = new ArrayList<ElementProvider<?>>();
        for (String serviceName : ServiceManager.getServiceNames(ElementProvider.class.getName())) {
            final ElementProvider<?> provider = (ElementProvider<?>)ServiceManager.getService(ElementProvider.class.getName(), serviceName);
            providers.add(provider);
        }
    }

    ResettableIterator<ElementProvider<?>> getProviders() {
        return new ObjectArrayIterator<ElementProvider<?>>(providers.toArray(new ElementProvider<?>[providers.size()]));
    }

}