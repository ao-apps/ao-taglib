/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2012, 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-taglib.
 *
 * ao-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.io.NullWriter;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.net.URIResolver;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.WildcardPatternMatcher;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * The base class for any tag that may call a request dispatcher.
 *
 * @author  AO Industries, Inc.
 */
abstract public class DispatchTag
	extends SimpleTagSupport
	implements
		DynamicAttributes,
		PageAttribute,
		ParamsAttribute
{

	/**
	 * Tracks if the request has been forwarded.
	 *
	 * @deprecated  Please use {@link RequestDispatcher#FORWARD_SERVLET_PATH} directly.
	 */
	@Deprecated
	protected static final String FORWARDED_REQUEST_ATTRIBUTE = DispatchTag.class.getName() + ".requestForwarded";

	/**
	 * Checks if the request has been forwarded.
	 *
	 * @deprecated  Please use {@link RequestDispatcher#FORWARD_SERVLET_PATH} directly.
	 */
	@Deprecated
	public static boolean isForwarded(ServletRequest req) {
		return
			req.getAttribute(FORWARDED_REQUEST_ATTRIBUTE) != null
			|| req.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH) != null;
	}

	/**
	 * @deprecated  Please use {@link RequestDispatcher#FORWARD_SERVLET_PATH} directly.
	 */
	@Deprecated
	protected static void setForwarded(ServletRequest req, boolean requestForwarded) {
		req.setAttribute(FORWARDED_REQUEST_ATTRIBUTE,
			requestForwarded ? Boolean.TRUE : null
		);
	}

	@SuppressWarnings("unchecked")
	public static HttpServletRequest getParameterAlteredRequest(
		HttpServletRequest request,
		URIParameters params,
		Map<String,String[]> oldMap,
		WildcardPatternMatcher clearParamsMatcher
	) {
		final Map<String,List<String>> newMap;
		if(params==null) {
			newMap = Collections.emptyMap();
		} else {
			newMap = params.getParameterMap();
		}
		if(oldMap==null) oldMap = request.getParameterMap();
		Map<String,String[]> newParameters = new LinkedHashMap<>(
			(
				newMap.size()
				+ oldMap.size()
			)*4/3+1
		);
		for(Map.Entry<String,List<String>> entry : newMap.entrySet()) {
			String name = entry.getKey();
			List<String> newValues = entry.getValue();
			String[] oldValues = clearParamsMatcher.isMatch(name) ? null : oldMap.get(name);
			String[] merged;
			if(oldValues==null) {
				// No need to merge values
				merged = newValues.toArray(new String[newValues.size()]);
			} else {
				// Merge values into single String[]
				merged = new String[newValues.size() + oldValues.length];
				String[] result = newValues.toArray(merged);
				assert merged==result;
				System.arraycopy(oldValues, 0, merged, newValues.size(), oldValues.length);
			}
			newParameters.put(name, merged);
		}
		// Add any old parameters that were not merged
		for(Map.Entry<String,String[]> entry : oldMap.entrySet()) {
			String name = entry.getKey();
			if(
				!newMap.containsKey(name)
				&& !clearParamsMatcher.isMatch(name)
			) newParameters.put(name, entry.getValue());
		}
		final Map<String,String[]> parameters = Collections.unmodifiableMap(newParameters);
		return new HttpServletRequestWrapper(request) {
			@Override
			public String getParameter(String name) {
				String[] values = parameters.get(name);
				return values==null || values.length==0 ? null : values[0];
			}

			@Override
			public Map<String,String[]> getParameterMap() {
				return parameters;
			}

			@Override
			public Enumeration<String> getParameterNames() {
				return Collections.enumeration(parameters.keySet());
			}

			@Override
			public String[] getParameterValues(String name) {
				return parameters.get(name);
			}
		};
	}

	protected String page;
	protected MutableURIParameters params;

	@Override
	public void setPage(String page) {
		this.page = page;
	}

	abstract protected WildcardPatternMatcher getClearParamsMatcher();

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new URIParametersMap();
		params.addParameter(name, value);
	}

	abstract protected String getDynamicAttributeExceptionKey();

	abstract protected Serializable[] getDynamicAttributeExceptionArgs(String localName);

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(
			uri==null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			ParamUtils.setDynamicAttribute(this, uri, localName, value);
		} else {
			throw new LocalizedJspTagException(
				accessor,
				getDynamicAttributeExceptionKey(),
				getDynamicAttributeExceptionArgs(localName)
			);
		}
	}

	/**
	 * Gets the arguments that will be passed on dispatch.  For no arguments, return null.
	 */
	abstract protected Map<String,?> getArgs();

	/**
	 * Subclass hook to intercept request after servlet paths have been determined
	 * and before dispatch is called.
	 * 
	 * This default implementation does nothing.
	 * 
	 * @exception  SkipPageException  If the implementation has handled the request,
	 *                                must throw SkipPageException.
	 */
	protected void doTag(String servletPath) throws IOException, JspTagException, SkipPageException {
		// Do nothing
	}

	@Override
	@SuppressWarnings("unchecked")
	final public void doTag() throws JspException, IOException {
		final PageContext pageContext = (PageContext)getJspContext();
		final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		// Track original page when first accessed
		final String oldOriginal = Dispatcher.getOriginalPage(request);
		try {
			// Set original request path if not already set
			if(oldOriginal == null) {
				Dispatcher.setOriginalPage(request, request.getServletPath());
			}

			// Invoke body first to all nested tags to set attributes
			JspFragment body = getJspBody();
			if(body != null) invoke(body);
			// Keep old dispatch page to restore
			final String oldDispatchPage = Dispatcher.getDispatchedPage(request);
			try {
				// Determine the path of the current page based on previous dispatch or original request
				final String servletPath =
					oldDispatchPage==null
						? request.getServletPath()
						: oldDispatchPage
				;

				// Find any dispatcher before allowing subclass to intercept dispatch.  This ensures page is a correct
				// value, even when not used for a particular request.
				final String contextRelativePath;
				final RequestDispatcher dispatcher;
				if(page==null) {
					contextRelativePath = null;
					dispatcher = null;
				} else {
					// Make relative to current JSP page
					contextRelativePath = URIResolver.getAbsolutePath(
						servletPath,
						page
					);
					// Find dispatcher
					dispatcher = pageContext.getServletContext().getRequestDispatcher(contextRelativePath);
					if(dispatcher==null) throw new LocalizedJspTagException(accessor, "DispatchTag.dispatcherNotFound", contextRelativePath);
				}

				// Call any subclass hook to handle the request before being dispatched.  If the request should not
				// be dispatched, subclass will throw SkipPageException.
				doTag(servletPath);

				// Page is required when not already dispatched by subclass
				if(page==null) throw new AttributeRequiredException("page");
				assert contextRelativePath!=null : "Will have been set above when page non-null";
				assert dispatcher!=null : "Will have been set above when page non-null";

				// Store as new relative path source
				Dispatcher.setDispatchedPage(request, contextRelativePath);

				// Keep old arguments to restore
				final Object oldArgs = request.getAttribute(Dispatcher.ARG_REQUEST_ATTRIBUTE);
				try {
					final JspWriter out = pageContext.getOut();
					final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

					// Set new arguments
					request.setAttribute(Dispatcher.ARG_REQUEST_ATTRIBUTE, getArgs());

					final WildcardPatternMatcher clearParamsMatcher = getClearParamsMatcher();
					Map<String,String[]> oldMap = null; // Obtained when first needed

					// If no parameters have been added
					if(params==null) {
						// And if there is no clearParamNames, then no need to wrap
						if(clearParamsMatcher.isEmpty()) {
							// No need to wrap, use old request
							dispatch(dispatcher, out, request, response);
							return;
						}

						// And if there are no parameters on the request, then no need to wrap
						if(oldMap==null) oldMap = request.getParameterMap();
						if(oldMap.isEmpty()) {
							// No need to wrap, use old request
							dispatch(dispatcher, out, request, response);
							return;
						}
					}

					// Filter and merge all parameters
					dispatch(
						dispatcher,
						out,
						getParameterAlteredRequest(request, params, oldMap, clearParamsMatcher),
						response
					);
				} finally {
					// Restore any previous args
					request.setAttribute(Dispatcher.ARG_REQUEST_ATTRIBUTE, oldArgs);
				}
			} finally {
				Dispatcher.setDispatchedPage(request, oldDispatchPage);
			}
		} finally {
			if(oldOriginal == null) {
				Dispatcher.setOriginalPage(request, null);
			}
		}
	}

	/**
	 * Invokes the body.  This is only called when a body exists.  Subclasses may override this to perform
	 * actions before and/or after invoking the body.  Any overriding implementation should call
	 * super.invoke(JspFragment) to invoke the body.
	 * <p>
	 * Discards all nested output, since this will not use the output and this
	 * output could possibly fill the response buffer and prevent the dispatch
	 * from functioning.
	 * </p>
	 */
	protected void invoke(JspFragment body) throws JspException, IOException {
		body.invoke(NullWriter.getInstance());
	}

	abstract void dispatch(RequestDispatcher dispatcher, JspWriter out, HttpServletRequest request, HttpServletResponse response) throws JspException, IOException;
}
