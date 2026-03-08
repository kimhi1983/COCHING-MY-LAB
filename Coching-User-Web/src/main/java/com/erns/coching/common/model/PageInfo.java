package com.erns.coching.common.model;

/**
 * 
 * <p>페이징 처리용</p> 
 *
 * @author Hunwoo Park 
 *
 */
public class PageInfo {
	private int totalItem; //총 아이템수
	private final int pageItemSize; //페이지당 아이템수
	private final int groupSize; //페이지 그룹수
	
	private int currentPage; //현재 페이지 번호
	private int[] currentPageGroup; //현재 페이지 그룹
	private int totalPage; //총 페이지 수
	
	private boolean havePrev; 
	private boolean haveNext;
	
	public PageInfo(int totalItem, int pageItemSize)
	{
		this(totalItem, pageItemSize, 10, 1);
	}
	
	public PageInfo(int totalItem, int pageItemSize, int groupSize)
	{
		this(totalItem, pageItemSize, groupSize, 1);
	}
	
	public PageInfo(int totalItem, int pageItemSize, int groupSize, int currentPage)
	{
		this.totalItem = totalItem;
		this.pageItemSize = pageItemSize > 0 ? pageItemSize : totalItem;
		this.groupSize = groupSize;
		havePrev = false;
		haveNext = false;
		setCurrentPage(currentPage);
	}
	
	public PageInfo(int totalItem, BaseSearchDTO bsDto) {
		this(totalItem, bsDto.getRowSize(), bsDto.getPageSize(), bsDto.getPage());
	}
	
	public void setCurrentPage(int page)
	{
		if(page < 1)
			this.currentPage = 1;
		else
			this.currentPage = page;
		
		calculate();
	}
	
	public void setCurrentPage(int totalItem, int page)
	{
		this.totalItem = totalItem;
		setCurrentPage(page);
	}
	
	public int getTotalItem() {
		return totalItem;
	}

	public int getPageItemSize() {
		return pageItemSize;
	}

	public int getGroupSize() {
		return groupSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int[] getCurrentPageGroup() {
		return currentPageGroup == null ? null : currentPageGroup.clone();
	}

	public int getTotalPage() {
		return totalPage;
	}

	public boolean isHavePrev() {
		return havePrev;
	}

	public boolean isHaveNext() {
		return haveNext;
	}
	
	/**
	 * 페이징 계산
	 */
	private void calculate()
	{
		int mod = pageItemSize == 0 ? 0 : totalItem % pageItemSize;
		int quotient = pageItemSize == 0 ? 0 : (totalItem - mod) / pageItemSize;

		this.totalPage = quotient + (mod == 0 ? 0 : 1);

		if (this.totalPage < this.currentPage)
			this.currentPage = this.totalPage;

		int sg = (int) (this.currentPage / this.groupSize);

		int sIdx = sg * groupSize + 1;
		int eIdx = sIdx + groupSize - 1;

		if (eIdx > totalPage)
			eIdx = totalPage;

		int pgCount = eIdx - sIdx + 1;

		this.currentPageGroup = new int[pgCount];
		for (int i = 0; i < this.currentPageGroup.length; ++i)
		{
			currentPageGroup[i] = sIdx + i;
		}

		if (pgCount == 0)
		{
			havePrev = haveNext = false;
		} else
		{
			havePrev = currentPageGroup[0] > groupSize;
			haveNext = currentPageGroup[currentPageGroup.length - 1] < totalPage;
		}
	}

	public int getStartItemIndex(int pageNumber)
	{
		if (pageNumber < 0)
			pageNumber = 1;

		return ((pageNumber - 1) * pageItemSize) + 1;
	}

	public int getEndItenIndex(int pageNumber, int recCnt)
	{
		if (pageNumber <= 0)
			pageNumber = 1;

		return recCnt - ((pageNumber - 1) * pageItemSize);
	}

}
