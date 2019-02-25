package simpledb;

import java.util.*;

public class myIterator implements Iterator<Tuple>
{
	private HeapPage page;
	private int currentTupleNum;	
	private int totalTuples;
	
	public myIterator(HeapPage pid)
	{
		page = pid;
		currentTupleNum = 0;
		totalTuples = pid.entryCount();
	}
	
	public boolean hasNext()
	{
		if(currentTupleNum < totalTuples )
			return true;
		else 
			return false;
	}
	
	public Tuple next()
	{
		Tuple t = page.getTuple(currentTupleNum);
		currentTupleNum++;
		return t;
	}
}
