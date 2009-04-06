/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.model.Permission;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portlet.PortletResourceBundles;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.util.UniqueList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.PageContext;

/**
 * <a href="ResourceActionsUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class ResourceActionsUtil {

	public static final String ACTION_NAME_PREFIX = "action.";

	public static final String MODEL_RESOURCE_NAME_PREFIX = "model.resource.";

	public static final String[] ORGANIZATION_MODEL_RESOURCES = {
		Organization.class.getName(), PasswordPolicy.class.getName(),
		User.class.getName()
	};

	public static final String[] PORTAL_MODEL_RESOURCES = {
		ExpandoColumn.class.getName(), Organization.class.getName(),
		PasswordPolicy.class.getName(), Role.class.getName(),
		User.class.getName(), UserGroup.class.getName()
	};

	public static String getAction(
		long companyId, Locale locale, String action) {

		String key = ACTION_NAME_PREFIX + action;

		String value = LanguageUtil.get(companyId, locale, key, null);

		if ((value == null) || (value.equals(key))) {
			value = PortletResourceBundles.getString(locale, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	public static String getAction(PageContext pageContext, String action) {
		String key = ACTION_NAME_PREFIX + action;

		String value = LanguageUtil.get(pageContext, key, null);

		if ((value == null) || (value.equals(key))) {
			value = PortletResourceBundles.getString(pageContext, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	public static List<String> getActions(List<Permission> permissions) {
		List<String> actions = new UniqueList<String>();

		for (Permission permission : permissions) {
			actions.add(permission.getActionId());
		}

		return actions;
	}

	public static List<String> getActionsNames(
		PageContext pageContext, List<String> actions) {

		List<String> uniqueList = new UniqueList<String>();

		for (String action : actions) {
			uniqueList.add(getAction(pageContext, action));
		}

		List<String> list = new ArrayList<String>();

		list.addAll(uniqueList);

		return list;
	}

	public static List<String> getModelNames() {
		return _instance._getModelNames();
	}

	public static List<String> getModelPortletResources(String name) {
		return _instance._getModelPortletResources(name);
	}

	public static String getModelResource(
		long companyId, Locale locale, String name) {

		String key = MODEL_RESOURCE_NAME_PREFIX + name;

		String value = LanguageUtil.get(companyId, locale, key, null);

		if ((value == null) || (value.equals(key))) {
			value = PortletResourceBundles.getString(locale, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	public static String getModelResource(
		PageContext pageContext, String name) {

		String key = MODEL_RESOURCE_NAME_PREFIX + name;

		String value = LanguageUtil.get(pageContext, key, null);

		if ((value == null) || (value.equals(key))) {
			value = PortletResourceBundles.getString(pageContext, key);
		}

		if (value == null) {
			value = key;
		}

		return value;
	}

	public static List<String> getModelResourceActions(String name) {
		return _instance._getModelResourceActions(name);
	}

	public static List<String> getModelResourceCommunityDefaultActions(
		String name) {

		return _instance._getModelResourceCommunityDefaultActions(name);
	}

	public static List<String> getModelResourceGuestDefaultActions(
		String name) {

		return _instance._getModelResourceGuestDefaultActions(name);
	}

	public static List<String> getModelResourceGuestUnsupportedActions(
		String name) {

		return _instance._getModelResourceGuestUnsupportedActions(name);
	}

	public static List<String> getPortletModelResources(String portletName) {
		return _instance._getPortletModelResources(portletName);
	}

	public static List<String> getPortletNames() {
		return _instance._getPortletNames();
	}

	public static List<String> getPortletResourceActions(String name)
		throws SystemException {

		return _instance._getPortletResourceActions(name);
	}

	public static List<String> getPortletResourceCommunityDefaultActions(
		String name) {

		return _instance._getPortletResourceCommunityDefaultActions(name);
	}

	public static List<String> getPortletResourceGuestDefaultActions(
		String name) {

		return _instance._getPortletResourceGuestDefaultActions(name);
	}

	public static List<String> getPortletResourceGuestUnsupportedActions(
		String name) {

		return _instance._getPortletResourceGuestUnsupportedActions(name);
	}

	public static List<String> getPortletResourceLayoutManagerActions(
		String name) {

		return _instance._getPortletResourceLayoutManagerActions(name);
	}

	public static List<String> getResourceActions(
			String portletResource, String modelResource)
		throws SystemException {

		List<String> actions = null;

		if (Validator.isNull(modelResource)) {
			actions = getPortletResourceActions(portletResource);
		}
		else {
			actions = getModelResourceActions(modelResource);
		}

		return actions;
	}

	public static List<String> getResourceGuestUnsupportedActions(
		String portletResource, String modelResource) {

		List<String> actions = null;

		if (Validator.isNull(modelResource)) {
			actions =
				getPortletResourceGuestUnsupportedActions(portletResource);
		}
		else {
			actions = getModelResourceGuestUnsupportedActions(modelResource);
		}

		return actions;
	}

	public static List<Role> getRoles(Group group, String modelResource)
		throws SystemException {

		List<Role> allRoles = RoleLocalServiceUtil.getRoles(
			group.getCompanyId());

		int[] types = new int[] {
			RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_COMMUNITY
		};

		if (isPortalModelResource(modelResource)) {
			if (modelResource.equals(Organization.class.getName()) ||
				modelResource.equals(User.class.getName())) {

				types = new int[] {
					RoleConstants.TYPE_REGULAR,
					RoleConstants.TYPE_ORGANIZATION
				};
			}
			else {
				types = new int[] {RoleConstants.TYPE_REGULAR};
			}
		}
		else {
			if (group.isOrganization()) {
				types = new int[] {
					RoleConstants.TYPE_REGULAR,
					RoleConstants.TYPE_ORGANIZATION
				};
			}
			else if (group.isUser()) {
				types = new int[] {RoleConstants.TYPE_REGULAR};
			}
		}

		List<Role> roles = new ArrayList<Role>();

		for (int type : types) {
			for (Role role : allRoles) {
				if (role.getType() == type) {
					roles.add(role);
				}
			}
		}

		return roles;
	}

	public static void init() {
		_instance._init();
	}

	public static boolean isOrganizationModelResource(String modelResource) {
		return _instance._isOrganizationModelResource(modelResource);
	}

	public static boolean isPortalModelResource(String modelResource) {
		return _instance._isPortalModelResource(modelResource);
	}

	public static void read(
			String servletContextName, ClassLoader classLoader, String source)
		throws Exception {

		_instance._read(servletContextName, classLoader, source);
	}

	private ResourceActionsUtil() {
		_organizationModelResources = new HashSet<String>();

		for (int i = 0; i < ORGANIZATION_MODEL_RESOURCES.length; i++) {
			_organizationModelResources.add(ORGANIZATION_MODEL_RESOURCES[i]);
		}

		_portalModelResources = new HashSet<String>();

		for (int i = 0; i < PORTAL_MODEL_RESOURCES.length; i++) {
			_portalModelResources.add(PORTAL_MODEL_RESOURCES[i]);
		}

		_portletModelResources = new HashMap<String, Set<String>>();
		_portletResourceActions = new HashMap<String, List<String>>();
		_portletResourceCommunityDefaultActions =
			new HashMap<String, List<String>>();
		_portletResourceGuestDefaultActions =
			new HashMap<String, List<String>>();
		_portletResourceGuestUnsupportedActions =
			new HashMap<String, List<String>>();
		_portletResourceLayoutManagerActions =
			new HashMap<String, List<String>>();
		_modelPortletResources = new HashMap<String, Set<String>>();
		_modelResourceActions = new HashMap<String, List<String>>();
		_modelResourceCommunityDefaultActions =
			new HashMap<String, List<String>>();
		_modelResourceGuestDefaultActions =
			new HashMap<String, List<String>>();
		_modelResourceGuestUnsupportedActions =
			new HashMap<String, List<String>>();

		try {
			ClassLoader classLoader = getClass().getClassLoader();

			String[] configs = StringUtil.split(
				PropsUtil.get(PropsKeys.RESOURCE_ACTIONS_CONFIGS));

			for (int i = 0; i < configs.length; i++) {
				_read(null, classLoader, configs[i]);
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private void _checkGuestUnsupportedActions(
		List<String> guestUnsupportedActions,
		List<String> guestDefaultActions) {

		// Guest default actions cannot reference guest unsupported actions

		Iterator<String> itr = guestDefaultActions.iterator();

		while (itr.hasNext()) {
			String actionId = itr.next();

			if (guestUnsupportedActions.contains(actionId)) {
				itr.remove();
			}
		}
	}

	private void _checkPortletActions(List<String> actions) {
		if (!actions.contains("CONFIGURATION")) {
			actions.add("CONFIGURATION");
		}

		if (!actions.contains("VIEW")) {
			actions.add("VIEW");
		}
	}

	private void _checkPortletCommunityDefaultActions(List<String> actions) {
		if (actions.size() == 0) {
			actions.add("VIEW");
		}
	}

	private void _checkPortletGuestDefaultActions(List<String> actions) {
		if (actions.size() == 0) {
			actions.add("VIEW");
		}
	}

	private void _checkPortletLayoutManagerActions(List<String> actions) {
		if (!actions.contains("CONFIGURATION")) {
			actions.add("CONFIGURATION");
		}

		if (!actions.contains("PREFERENCES")) {
			actions.add("PREFERENCES");
		}

		if (!actions.contains("VIEW")) {
			actions.add("VIEW");
		}
	}

	private List<String> _getActions(
		Map<String, List<String>> map, String name) {

		List<String> actions = map.get(name);

		if (actions == null) {
			actions = new UniqueList<String>();

			map.put(name, actions);
		}

		return actions;
	}

	private List<String> _getModelNames() {
		return ListUtil.fromCollection(_modelPortletResources.keySet());
	}

	private List<String> _getModelPortletResources(String name) {
		Set<String> resources = _modelPortletResources.get(name);

		if (resources == null) {
			return new UniqueList<String>();
		}
		else {
			return Collections.list(Collections.enumeration(resources));
		}
	}

	private List<String> _getModelResourceActions(String name) {
		return _getActions(_modelResourceActions, name);
	}

	private List<String> _getModelResourceCommunityDefaultActions(
		String name) {

		return _getActions(_modelResourceCommunityDefaultActions, name);
	}

	private List<String> _getModelResourceGuestDefaultActions(String name) {
		return _getActions(_modelResourceGuestDefaultActions, name);
	}

	private List<String> _getModelResourceGuestUnsupportedActions(String name) {
		return _getActions(_modelResourceGuestUnsupportedActions, name);
	}

	private List<String> _getPortletModelResources(String portletName) {
		portletName = PortletConstants.getRootPortletId(portletName);

		Set<String> resources = _portletModelResources.get(portletName);

		if (resources == null) {
			return new UniqueList<String>();
		}
		else {
			return Collections.list(Collections.enumeration(resources));
		}
	}

	private List<String> _getPortletNames() {
		return ListUtil.fromCollection(_portletModelResources.keySet());
	}

	private List<String> _getPortletResourceActions(String name)
		throws SystemException {

		name = PortletConstants.getRootPortletId(name);

		List<String> actions = _getActions(_portletResourceActions, name);

		if (actions.size() == 0) {
			synchronized (this) {
				Portlet portlet = PortletLocalServiceUtil.getPortletById(name);

				if (portlet != null) {
					Map<String, Set<String>> portletModes =
						portlet.getPortletModes();

					Set<String> mimeTypeModes = portletModes.get("text/html");

					if (mimeTypeModes != null) {
						for (String actionId : mimeTypeModes) {
							if (actionId.equalsIgnoreCase("edit")) {
								actions.add(ActionKeys.PREFERENCES);
							}
							else if (actionId.equalsIgnoreCase("edit_guest")) {
								actions.add(ActionKeys.GUEST_PREFERENCES);
							}
							else {
								actions.add(actionId.toUpperCase());
							}
						}
					}
				}
				else {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to obtain resource actions for unknown " +
								"portlet " + name);
					}
				}

				_checkPortletActions(actions);

				List<String> communityDefaultActions =
					_portletResourceCommunityDefaultActions.get(name);

				if (communityDefaultActions == null) {
					communityDefaultActions = new UniqueList<String>();

					_portletResourceCommunityDefaultActions.put(
						name, communityDefaultActions);

					_checkPortletCommunityDefaultActions(
						communityDefaultActions);
				}

				List<String> guestDefaultActions =
					_portletResourceGuestDefaultActions.get(name);

				if (guestDefaultActions == null) {
					guestDefaultActions = new UniqueList<String>();

					_portletResourceGuestDefaultActions.put(
						name, guestDefaultActions);

					_checkPortletGuestDefaultActions(guestDefaultActions);
				}

				List<String> layoutManagerActions =
					_portletResourceLayoutManagerActions.get(name);

				if (layoutManagerActions == null) {
					layoutManagerActions = new UniqueList<String>();

					_portletResourceLayoutManagerActions.put(
						name, layoutManagerActions);

					_checkPortletLayoutManagerActions(layoutManagerActions);
				}
			}
		}

		return actions;
	}

	private List<String> _getPortletResourceCommunityDefaultActions(
		String name) {

		// This method should always be called only after
		// _getPortletResourceActions has been called at least once to
		// populate the default community actions. Check to make sure this is
		// the case. However, if it is not, that means the methods
		// _getPortletResourceGuestDefaultActions and
		// _getPortletResourceGuestDefaultActions may not work either.

		name = PortletConstants.getRootPortletId(name);

		return _getActions(_portletResourceCommunityDefaultActions, name);
	}

	private List<String> _getPortletResourceGuestDefaultActions(String name) {
		name = PortletConstants.getRootPortletId(name);

		return _getActions(_portletResourceGuestDefaultActions, name);
	}

	private List<String> _getPortletResourceGuestUnsupportedActions(
		String name) {

		name = PortletConstants.getRootPortletId(name);

		return _getActions(_portletResourceGuestUnsupportedActions, name);
	}

	private List<String> _getPortletResourceLayoutManagerActions(String name) {
		name = PortletConstants.getRootPortletId(name);

		List<String> actions = _getActions(
			_portletResourceLayoutManagerActions, name);

		// This check can never return an empty list. If the list is empty, it
		// means that the portlet does not have an explicit resource-actions
		// configuration file and should therefore be handled as if it has
		// defaults of CONFIGURATION, PREFERENCES, and VIEW.

		if (actions.size() < 1) {
			actions.add("CONFIGURATION");
			actions.add("PREFERENCES");
			actions.add("VIEW");
		}

		return actions;
	}

	private void _init() {
	}

	private boolean _isOrganizationModelResource(String modelResource) {
		if (_organizationModelResources.contains(modelResource)) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean _isPortalModelResource(String modelResource) {
		if (_portalModelResources.contains(modelResource)) {
			return true;
		}
		else {
			return false;
		}
	}

	private void _read(
			String servletContextName, ClassLoader classLoader, String source)
		throws Exception {

		String xml = null;

		try {
			xml = StringUtil.read(classLoader, source);
		}
		catch (Exception e) {
			_log.warn("Cannot load " + source);
		}

		if (xml == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Loading " + source);
		}

		Document doc = SAXReaderUtil.read(xml);

		Element root = doc.getRootElement();

		Iterator<Element> itr1 = root.elements("resource").iterator();

		while (itr1.hasNext()) {
			Element resource = itr1.next();

			String file = resource.attributeValue("file");

			_read(servletContextName, classLoader, file);
		}

		itr1 = root.elements("portlet-resource").iterator();

		while (itr1.hasNext()) {
			Element resource = itr1.next();

			String name = resource.elementText("portlet-name");

			if (servletContextName != null) {
				name =
					name + PortletConstants.WAR_SEPARATOR + servletContextName;
			}

			name = PortalUtil.getJsSafePortletId(name);

			// Actions

			List<String> actions = _getActions(_portletResourceActions, name);

			Element supports = resource.element("supports");

			Iterator<Element> itr2 = supports.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					actions.add(actionKeyText);
				}
			}

			if (!name.equals(PortletKeys.PORTAL)) {
				_checkPortletActions(actions);
			}

			// Community default actions

			List<String> communityDefaultActions =
				_getActions(_portletResourceCommunityDefaultActions, name);

			Element communityDefaults = resource.element("community-defaults");

			itr2 = communityDefaults.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					communityDefaultActions.add(actionKeyText);
				}
			}

			// Guest default actions

			List<String> guestDefaultActions =
				_getActions(_portletResourceGuestDefaultActions, name);

			Element guestDefaults = resource.element("guest-defaults");

			itr2 = guestDefaults.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					guestDefaultActions.add(actionKeyText);
				}
			}

			// Guest unsupported actions

			List<String> guestUnsupportedActions =
				_getActions(_portletResourceGuestUnsupportedActions, name);

			Element guestUnsupported = resource.element("guest-unsupported");

			itr2 = guestUnsupported.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					guestUnsupportedActions.add(actionKeyText);
				}
			}

			_checkGuestUnsupportedActions(
				guestUnsupportedActions, guestDefaultActions);

			// Layout manager actions

			List<String> layoutManagerActions = _getActions(
				_portletResourceLayoutManagerActions, name);

			Element layoutManager = resource.element("layout-manager");

			if (layoutManager != null) {
				itr2 = layoutManager.elements("action-key").iterator();

				while (itr2.hasNext()) {
					Element actionKey = itr2.next();

					String actionKeyText = actionKey.getText();

					if (Validator.isNotNull(actionKeyText)) {
						layoutManagerActions.add(actionKeyText);
					}
				}
			}
			else {

				// Set the layout manager actions to contain all the portlet
				// resource actions if the element is not specified

				layoutManagerActions.addAll(actions);
			}
		}

		itr1 = root.elements("model-resource").iterator();

		while (itr1.hasNext()) {
			Element resource = itr1.next();

			String name = resource.elementText("model-name");

			Element portletRef = resource.element("portlet-ref");

			Iterator<Element> itr2 = portletRef.elements(
				"portlet-name").iterator();

			while (itr2.hasNext()) {
				Element portletName = itr2.next();

				String portletNameString = portletName.getText();

				if (servletContextName != null) {
					portletNameString =
						portletNameString + PortletConstants.WAR_SEPARATOR +
							servletContextName;
				}

				portletNameString = PortalUtil.getJsSafePortletId(
					portletNameString);

				// Reference for a portlet to child models

				Set<String> modelResources = _portletModelResources.get(
					portletNameString);

				if (modelResources == null) {
					modelResources = new HashSet<String>();

					_portletModelResources.put(
						portletNameString, modelResources);
				}

				modelResources.add(name);

				// Reference for a model to parent portlets

				Set<String> portletResources = _modelPortletResources.get(name);

				if (portletResources == null) {
					portletResources = new HashSet<String>();

					_modelPortletResources.put(name, portletResources);
				}

				portletResources.add(portletNameString);
			}

			// Actions

			List<String> actions = _getActions(_modelResourceActions, name);

			Element supports = resource.element("supports");

			itr2 = supports.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					actions.add(actionKeyText);
				}
			}

			// Community default actions

			List<String> communityDefaultActions =
				_getActions(_modelResourceCommunityDefaultActions, name);

			Element communityDefaults = resource.element("community-defaults");

			itr2 = communityDefaults.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					communityDefaultActions.add(actionKeyText);
				}
			}

			// Guest default actions

			List<String> guestDefaultActions =
				_getActions(_modelResourceGuestDefaultActions, name);

			Element guestDefaults = resource.element("guest-defaults");

			itr2 = guestDefaults.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					guestDefaultActions.add(actionKeyText);
				}
			}

			// Guest unsupported actions

			List<String> guestUnsupportedActions =
				_getActions(_modelResourceGuestUnsupportedActions, name);

			Element guestUnsupported = resource.element("guest-unsupported");

			itr2 = guestUnsupported.elements("action-key").iterator();

			while (itr2.hasNext()) {
				Element actionKey = itr2.next();

				String actionKeyText = actionKey.getText();

				if (Validator.isNotNull(actionKeyText)) {
					guestUnsupportedActions.add(actionKeyText);
				}
			}

			_checkGuestUnsupportedActions(
				guestUnsupportedActions, guestDefaultActions);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(ResourceActionsUtil.class);

	private static ResourceActionsUtil _instance = new ResourceActionsUtil();

	private Set<String> _organizationModelResources;
	private Set<String> _portalModelResources;
	private Map<String, Set<String>> _portletModelResources;
	private Map<String, List<String>> _portletResourceActions;
	private Map<String, List<String>> _portletResourceCommunityDefaultActions;
	private Map<String, List<String>> _portletResourceGuestDefaultActions;
	private Map<String, List<String>> _portletResourceGuestUnsupportedActions;
	private Map<String, List<String>> _portletResourceLayoutManagerActions;
	private Map<String, Set<String>> _modelPortletResources;
	private Map<String, List<String>> _modelResourceActions;
	private Map<String, List<String>> _modelResourceCommunityDefaultActions;
	private Map<String, List<String>> _modelResourceGuestDefaultActions;
	private Map<String, List<String>> _modelResourceGuestUnsupportedActions;

}