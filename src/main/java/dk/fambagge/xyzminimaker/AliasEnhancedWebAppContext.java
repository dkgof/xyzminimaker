package dk.fambagge.xyzminimaker;

import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jetty.webapp.WebAppContext;

public class AliasEnhancedWebAppContext
        extends WebAppContext {

    @Override
    public String getResourceAlias(String alias) {
        Map<String, String> localMap = getResourceAliases();

        if (localMap == null) {
            return null;
        }

        for (Entry localEntry : localMap.entrySet()) {
            if (alias.startsWith((String) localEntry.getKey())) {
                return alias.replace((CharSequence) localEntry.getKey(), (CharSequence) localEntry.getValue());
            }
        }

        return null;
    }
}
