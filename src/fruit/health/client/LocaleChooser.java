package fruit.health.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import fruit.health.client.util.URLCreator;
import fruit.health.shared.util.SharedConstants;

@Singleton
public class LocaleChooser {
	private final URLCreator urlCreator;
	
	@Inject
	public LocaleChooser(URLCreator urlCreator) {
		this.urlCreator = urlCreator;
	}
	
	public void newLanguageChosen(String lang) {
		HashMap<String, List<String>> params = new HashMap<String, List<String>>(Window.Location.getParameterMap());
		params.remove("locale"); // Remove the current locale specification, if any
		
		Cookies.setCookie(SharedConstants.LOCALE_COOKIE_NAME, lang, new Date(Long.MAX_VALUE));
		String url = urlCreator.makeURL(Window.Location.getProtocol(),Window.Location.getHost(),Window.Location.getPath(),Window.Location.getHash(),params);
		Window.Location.assign(url);
	}
}
