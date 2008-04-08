/* =================================================================
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# ================================================================= */
package org.sgodden.query;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.query.service.QueryService;

/**
 * Encapsulates the results of running a query.
 * <p/>
 * TODO - consider factoring out page fetching as a strategy.
 * Then we can have a strategy which never caches pages (for very large
 * result sets), or has a fill-in approach (for small result sets), rather
 * than the current hard-coded strategy of fetching whichever page is trying
 * to display and caching it.
 * <p/>
 * FIXME - this class currently combines the public interface required by both consumers and producers of result set objects.  This needs to be refactored.
 * 
 * @author goddens
 *
 */
public class ResultSet implements Serializable {
	
	private static final transient Log log = LogFactory.getLog(ResultSet.class);
	
	private QueryService queryService;
	
	private List<ResultSetRow>[] pages;
	
	private int rowCount = -1;
	private boolean queryBailedOut = false;
	
	private Query query;
	
	public ResultSet(){}

	/**
	 * Returns the query that was used to produce this
	 * result set.
	 * 
	 * @return
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Sets the query that was used to produce this
	 * result set.
	 * @param query
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * Returns the number of rows in the result set. 
	 * 
	 * @return
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Sets the number of rows in the result set.
	 * <p/>
	 * Must be called before calling {@link #setRows(List)}.
	 * @param rowCount
	 */
	public void setRowCount(int rowCount) {
		
		if (pages != null){
			throw new IllegalStateException("setRows(List<ResultSetRow>) has already been called");
		}
		
		this.rowCount = rowCount;
		
		/*
		 * If the query has a fetch size, then determine the
		 * number of pages we will need.
		 */
		int numberOfPages = 1;
		if (query.getFetchSize() > 0){
			numberOfPages = rowCount / query.getFetchSize();
			if (rowCount % query.getFetchSize() > 0){
				numberOfPages++;
			}
		}
		pages = new List[numberOfPages];
		log.debug("There are " + numberOfPages + " pages in the result set");
		
	}

	/**
	 * Sets the rows in the results set.
	 * @param rows the rows.
	 */
	public void setRows(List<ResultSetRow> rows) {
		/*
		 * If they are calling this without having called
		 * setRowCount, then there can only be one page.
		 */
		if (pages == null){
			pages = new List[1];
		}
		this.pages[0] = rows; // can only be setting the first page (or entire result set) from the public method
	}
	
	/**
	 * Returns all the rows at once - this bypasses all intelligent paging processing, and only works in the case
	 * where the query was run in an 'all at once' mode.
	 * @return all of the rows in the result set.
	 */
	public List<ResultSetRow> getRows(){
		return pages[0]; // when using this method, it's the entire result set (stored in the first page)
	}
	
	/**
	 * Returns the row at the specified index, causing a fetch of the appropriate page if necessary.
	 * @param rowIndex the index of the required row.
	 * @return the required row.
	 * @throws IllegalArgumentException if the row index is out of range.
	 */
	public ResultSetRow getRow(int rowIndex){
		
		if (rowIndex >= rowCount){
			throw new IllegalArgumentException("Row index out of range: " + rowIndex);
		}
		
		List<ResultSetRow> page = getPage(rowIndex);
		
		int pageRowIndex = rowIndex;
		if (query.getFetchSize() > 0){
			pageRowIndex = rowIndex % query.getFetchSize();	
		}
		
		return page.get(pageRowIndex);
	}
	
	/**
	 * Returns the correct page of rows for the given row index.
	 * @param rowIndex the row index.
	 * @return the page of results containing that index.
	 */
	private List<ResultSetRow> getPage(int rowIndex){
		
		int pageIndex = getPageIndex(rowIndex);
		
		List<ResultSetRow> page = pages[pageIndex];
		
		if (page == null){
			log.debug("Page " + (pageIndex + 1) + " has not yet been retrieved - retrieving it..");
			// We need to re-run a copy of the query to fetch the next block
			Query nextFetch = query.makeClone();
			nextFetch.setBailOutSize(0); // make sure no bail out specified, which would cause an extra query on the server
			/*
			 * The offset is the page index (zero-indexed) multiplied by the fetch size.
			 * i.e. if we wanted page 2 (the third page), and the fetch size was 100,
			 * then the offset would be 200.
			 */
			nextFetch.setRowOffset(pageIndex * query.getFetchSize());
			
			ResultSet nextFetchResults = queryService.executeQuery(nextFetch);
			page = nextFetchResults.getPage(0); // there can only be one page
			pages[pageIndex] = page;
			log.debug("Retrieved page " + (pageIndex + 1));
			
		}
		
		return page;
	}
	
	/**
	 * Returns the (zero-indexed) index of the page containing the requested row.
	 * @param rowIndex the requested row index.
	 * @return the index of the page containing that row.
	 */
	private int getPageIndex(int rowIndex){
		int ret=0;
		if (query.getFetchSize() > 0){
			ret = rowIndex / query.getFetchSize();
			/*
			 * Because we are zero-indexing, we don't need to
			 * check for remainder and increment.
			 */
		}
		return ret;
	}

	/**
	 * Returns whether the query bailed out and did not return
	 * any rows due to there being too many rows in the result set.
	 * @return whether the query bailed out.
	 */
	public boolean getQueryBailedOut() {
		return queryBailedOut;
	}

	/**
	 * Sets whether the query bailed out and did not return
	 * any rows due to there being too many rows in the result set.
	 * <p/>
	 * FIXME - this should not be on the public interface.
	 * @param queryBailedOut whether the query bailed out.
	 */
	public void setQueryBailedOut(boolean queryBailedOut) {
		this.queryBailedOut = queryBailedOut;
	}
	
	/**
	 * Sets the query service that will be used to retrieve further pages
	 * of this result set.
	 * 
	 * @param queryService
	 */
	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

}
