package org.sgodden.entities.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.entities.EntityInstance;
import org.sgodden.entities.ToManyChildEntry;

public class PathUtils {
	
	private static final transient Log log = LogFactory.getLog(PathUtils.class);
	
	/**
	 * Returns whether the specified attribute path points to a to-many child entry.
	 * 
	 * @param instance the entity instance.
	 * @param path the path.
	 * @return <code>true</code> if the path points to a to-many child entry, <code>false</code> if not.
	 */
	public static boolean isPathPointsToToManyChildInstance(EntityInstance instance, String path) {
		boolean ret = false;

		// if there's an index on the end, strip it
		Object o = EntityInstanceUtils.getAttribute(instance, stripIndexSuffix(path));
		
		if (o instanceof ToManyChildEntry) {
			ret = true;
		}
		
		return ret;
	}
	
	static String stripIndexSuffix(String path) {
		String ret = path;
		
		if (ret.endsWith("]")){
			ret = ret.substring(0, ret.lastIndexOf('['));
		}
		
		return ret;
	}

	public static String goBack(String editPath) {
		String ret = null;
	
		log.debug("Calculating go back path for input path: " + editPath);
	
		if (editPath.length() == 0) {
			ret = editPath;
		} else {
			// if it ends in an index, remove the index and that's it
			if (editPath.substring(editPath.length() - 1, editPath.length()).equals("]")) {
				ret = editPath.substring(0, editPath.lastIndexOf("["));
			} else {
				// strip the last portion up to but not including the last dot
				ret = editPath.substring(0, editPath.lastIndexOf(".") + 1);
			}
		}
	
		log.debug("Calculated path is: " + ret);
	
		return ret;
	
	}

	public static String goForward(String sourcePath, String deepestPath) {
		String ret = null;
	
		log.debug("Calculating go forward path: " + sourcePath + ", "
				+ deepestPath);
	
		if (deepestPath.length() > sourcePath.length()) {
			String _RemainingPath = deepestPath.substring(sourcePath.length());
			log.debug("Remaining path is: " + _RemainingPath);
			// if it starts with a '[', then we go up to and including the next
			// ']'
			if (_RemainingPath.startsWith("[")) {
				ret = deepestPath.substring(0, deepestPath.indexOf(']',
						sourcePath.length() + 1) + 1);
			}
			// if it starts with a '.', then we go up to and including the next
			// '[', '.' or end of string in that preference
			else {
				int bracketIndex = _RemainingPath.indexOf('[');
				int dotIndex = _RemainingPath.indexOf('.', 1);
	
				int nextIndex = -1;
				if (bracketIndex > -1) {
					nextIndex = bracketIndex;
				}
				if (dotIndex > -1
						&& (dotIndex < nextIndex || nextIndex == -1)) {
					nextIndex = dotIndex;
				}
	
				log.debug("Next index is: " + nextIndex);
	
				if (nextIndex > -1) {
					ret = deepestPath.substring(0, sourcePath.length()
							+ nextIndex);
				} else {
					ret = deepestPath;
				}
			}
		}
	
		log.debug("Go forward path is: " + ret);
	
		return ret;
	}
	
	public static String appendTrailingDot(String path) {
		String ret = path;
		if (!(path.endsWith("."))) {
			ret += ".";
		}
		return ret;
	}
	
	public static boolean isCompoundPath(String attributePath) {
		return ( attributePath.indexOf('.') > 0 );
	}

}
