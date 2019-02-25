package simpledb;

/**
 * The {@code Delete} operator. {@code Delete} reads tuples from its child operator and removes them from the table they
 * belong to.
 */
public class Delete extends AbstractDbIterator {

	/**
	 * The transaction this {@code Delete} runs in.
	 */
	TransactionId tid;

	/**
	 * The child operator.
	 */
	DbIterator child;

	/**
	 * The {@code TupleDesc} associated with this {@code Delete}.
	 */
	TupleDesc td;

	/**
	 * A tuple representing the result of deletion.
	 */
	Tuple result = null;

	/**
	 * Constructs a {@code Delete} operator.
	 * 
	 * @param t
	 *            The transaction this delete runs in
	 * @param child
	 *            The child operator from which to read tuples for deletion
	 */
	public Delete(TransactionId t, DbIterator child) {
		tid = t;
		this.child = child;
		td = new TupleDesc(new Type[] { Type.INT_TYPE });
	}

	@Override
	public TupleDesc getTupleDesc() {
		return td;
	}

	@Override
	public void open() throws DbException, TransactionAbortedException 
	{
		// some code goes here
		child.open();
	}

	@Override
	public void close() 
	{
		// some code goes here
		child.close();
	}

	@Override
	public void rewind() throws DbException, TransactionAbortedException 
	{
		// some code goes here
		child.rewind();
	}

	/**
	 * Deletes tuples as they are read from the child operator. Deletes are processed via the buffer pool (which can be
	 * accessed via the Database.getBufferPool() method.
	 * 
	 * @return A 1-field tuple containing the number of deleted records.
	 * @see Database#getBufferPool
	 * @see BufferPool#deleteTuple
	 */
	@Override
	protected Tuple readNext() throws TransactionAbortedException, DbException 
	{
		// Initializing count variable to zero.
		int count = 0;
		
		// Iterating over each tuple and deleting it.
		while(child.hasNext())
		{
			Tuple t = child.next();
			Database.getBufferPool().deleteTuple(tid, t);
			count++;		// incrementing count each time we delete a tuple.
		}	
		
		//Creating a new tuple to store result of count.
		result = new Tuple(td);
		
		//creating 1 integer field to store the value of count variable.
		result.setField(0, new IntField(count));
		return result;
		
	}
}
