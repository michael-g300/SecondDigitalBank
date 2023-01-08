package useraction;

import java.util.List;
import java.util.Map;

import users.Client;

public interface userAction {
	Client doAction(final Client client, final Map<String, Object> params);
	Map<String, Object> validate(final List<String> untrust_params);
}
