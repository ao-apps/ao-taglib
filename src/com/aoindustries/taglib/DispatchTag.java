/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.util.WildcardPatternMatcher;
import com.aoindustries.io.NullWriter;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.servlet.LocalizedServletException;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
 * TODO: Use "javax.servlet.include.servlet_path" and "javax.servlet.forward.servlet_path" for correct interaction with standard tags.
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
	 * The name of the request-scope Map that will contain the arguments for the current page.
	 */
	protected static final String ARG_MAP_REQUEST_ATTRIBUTE_NAME = "arg";

	/**
	 * Tracks the first servlet path seen, before any include/forward.
	 */
	private static final ThreadLocal<String> originalPage = new ThreadLocal<String>();

	/**
	 * Gets the original page path corresponding to the original request before any forward/include.
	 * Assumes all forward/include done with ao taglib.
	 */
	public static String getOriginalPagePath(HttpServletRequest request) {
		String original = originalPage.get();
		return original!=null ? original : request.getServletPath();
	}

	/**
	 * Tracks the current dispatch page for correct page-relative paths.
	 */
	private static final ThreadLocal<String> dispatchedPage = new ThreadLocal<String>();

	/**
	 * Gets the current page path, including any effects from include/forward.
	 * This will be the path of the current page on forward or include.
	 * Assumes all forward/include done with ao taglib.
	 * This may be used as a substitute for HttpServletRequest.getServletPath() when the current page is needed instead of the originally requested servlet.
	 */
	public static String getCurrentPagePath(HttpServletRequest request) {
		String dispatched = dispatchedPage.get();
		return dispatched!=null ? dispatched : request.getServletPath();
	}

	/**
	 * Tracks if the request has been forwarded.
	 */
	protected static final ThreadLocal<Boolean> requestForwarded = new ThreadLocal<Boolean>();

	/**
	 * Checks if the request has been forwarded.
	 */
	public static boolean isForwarded() {
		Boolean forwarded = requestForwarded.get();
		return forwarded!=null && forwarded.booleanValue();
	}

	/**
	 * Performs a forward, allowing page-relative paths and setting all values
	 * compatible with &lt;ao:forward&gt; tag.
	 */
	public static void forward(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response
	) throws ServletException, IOException {
		// Resolve the dispatcher
		String contextRelativePath = ServletUtil.getAbsolutePath(getCurrentPagePath(request), page);
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(contextRelativePath);
		if(dispatcher==null) throw new LocalizedServletException(accessor, "DispatchTag.dispatcherNotFound", contextRelativePath);
		// Track original page when first accessed
		final String oldOriginal = originalPage.get();
		try {
			// Set original request path if not already set
			if(oldOriginal==null) originalPage.set(request.getServletPath());
			// Keep old dispatch page to restore
			final String oldDispatchPage = dispatchedPage.get();
			try {
				// Store as new relative path source
				dispatchedPage.set(contextRelativePath);
				// Perform dispatch
				dispatcher.forward(request, response);
			} finally {
				dispatchedPage.set(oldDispatchPage);
			}
		} finally {
			if(oldOriginal==null) {
				originalPage.set(null);
			}
		}
	}

	/**
	 * Performs an include, allowing page-relative paths and setting all values
	 * compatible with &lt;ao:include&gt; tag.
	 */
	public static void include(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response
	) throws ServletException, IOException {
		// Resolve the dispatcher
		String contextRelativePath = ServletUtil.getAbsolutePath(getCurrentPagePath(request), page);
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(contextRelativePath);
		if(dispatcher==null) throw new LocalizedServletException(accessor, "DispatchTag.dispatcherNotFound", contextRelativePath);
		// Track original page when first accessed
		final String oldOriginal = originalPage.get();
		try {
			// Set original request path if not already set
			if(oldOriginal==null) originalPage.set(request.getServletPath());
			// Keep old dispatch page to restore
			final String oldDispatchPage = dispatchedPage.get();
			try {
				// Store as new relative path source
				dispatchedPage.set(contextRelativePath);
				// Perform dispatch
				dispatcher.include(request, response);
			} finally {
				dispatchedPage.set(oldDispatchPage);
			}
		} finally {
			if(oldOriginal==null) {
				originalPage.set(null);
			}
		}
	}

	protected String page;
    protected HttpParametersMap params;

    @Override
    public String getPage() {
        return page;
    }

    @Override
    public void setPage(String page) {
        this.page = page;
    }

	abstract protected WildcardPatternMatcher getClearParamsMatcher();

    @Override
    public HttpParameters getParams() {
        return params==null ? EmptyParameters.getInstance() : params;
    }

    @Override
    public void addParam(String name, String value) {
        if(params==null) params = new HttpParametersMap();
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
	abstract protected Map<String, Object> getArgs();

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
		// Track original page when first accessed
		final String oldOriginal = originalPage.get();
		try {
			// Set original request path if not already set
			final PageContext pageContext = (PageContext)getJspContext();
			final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			if(oldOriginal==null) originalPage.set(request.getServletPath());

			// Invoke body first to all nested tags to set attributes
			final JspFragment body = getJspBody();
			if(body!=null) {
				// Discard all nested output, since this will not use the output and this
				// output could possibly fill the response buffer and prevent the dispatch
				// from functioning.
				body.invoke(NullWriter.getInstance());
			}
			// Keep old dispatch page to restore
			final String oldDispatchPage = dispatchedPage.get();
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
					contextRelativePath = ServletUtil.getAbsolutePath(
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
				dispatchedPage.set(contextRelativePath);

				// Keep old arguments to restore
				final Object oldArgs = request.getAttribute(ARG_MAP_REQUEST_ATTRIBUTE_NAME);
				try {
					final JspWriter out = pageContext.getOut();
					final HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

					// Set new arguments
					request.setAttribute(
						ARG_MAP_REQUEST_ATTRIBUTE_NAME,
						getArgs()
					);

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
					final Map<String,List<String>> newMap;
					if(params==null) {
						newMap = Collections.emptyMap();
					} else {
						newMap = params.getParameterMap();
					}
					if(oldMap==null) oldMap = request.getParameterMap();
					Map<String,String[]> newParameters = new LinkedHashMap<String,String[]>(
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
					dispatch(
						dispatcher,
						out,
						new HttpServletRequestWrapper(request) {
							@Override
							public String getParameter(String name) {
								String[] values = parameters.get(name);
								return values==null || values.length==0 ? null : values[0];
							}

							@Override
							public Map getParameterMap() {
								return parameters;
							}

							@Override
							public Enumeration getParameterNames() {
								return Collections.enumeration(parameters.keySet());
							}

							@Override
							public String[] getParameterValues(String name) {
								return parameters.get(name);
							}
						},
						response
					);
				} finally {
					// Restore any previous args
					request.setAttribute(ARG_MAP_REQUEST_ATTRIBUTE_NAME, oldArgs);
				}
			} finally {
				dispatchedPage.set(oldDispatchPage);
			}
		} finally {
			if(oldOriginal==null) {
				originalPage.set(null);
			}
		}
    }

    abstract void dispatch(RequestDispatcher dispatcher, JspWriter out, HttpServletRequest request, HttpServletResponse response) throws JspException, IOException;
}
