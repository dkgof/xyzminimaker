package dk.fambagge.xyzminimaker;

import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jetty.webapp.WebAppContext;

public class AliasEnhancedWebAppContext
        extends WebAppContext {

    @Override
    public String getResourceAlias(String paramString) {
        Map<String, String> localMap = getResourceAliases();

        if (localMap == null) {
            return null;
        }

        for (Entry localEntry : localMap.entrySet()) {
            if (paramString.startsWith((String) localEntry.getKey())) {
                return paramString.replace((CharSequence) localEntry.getKey(), (CharSequence) localEntry.getValue());
            }
        }

        return null;
    }
}
