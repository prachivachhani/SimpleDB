package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples in no particular order. Tuples are
 * stored on pages, each of which is a fixed size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 */
public class HeapFile implements DbFile {

	/**
	 * The File associated with this HeapFile.
	 */
	protected File file;

	/**
	 * The TupleDesc associated with this HeapFile.
	 */
	protected TupleDesc td;

	/**
	 * Constructs a heap file backed by the specified file.
	 * 
	 * @param f
	 *            the file that stores the on-disk backing store for this heap file.
	 */
	public HeapFile(File f, TupleDesc td) {
		this.file = f;
		this.td = td;
	}

	/**
	 * Returns the File backing this HeapFile on disk.
	 * 
	 * @return the File backing this HeapFile on disk.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns an ID uniquely identifying this HeapFile. Implementation note: you will need to generate this tableid
	 * somewhere ensure that each HeapFile has a "unique id," and that you always return the same value for a particular
	 * HeapFile. We suggest hashing the absolute file name of the file underlying the heapfile, i.e.
	 * f.getAbsoluteFile().hashCode().
	 * 
	 * @return an ID uniquely identifying this HeapFile.
	 */
	public int getId() {
		return file.getAbsoluteFile().hashCode();
	}

	/**
	 * Returns the TupleDesc of the table stored in this DbFile.
	 * 
	 * @return TupleDesc of this DbFile.
	 */
	public TupleDesc getTupleDesc() {
		return td;
	}

	// see DbFile.java for javadocs
	public Page readPage(PageId pid) 
	{
		//check the validity of the pid.
		if(pid.pageno()<0 || pid.pageno() >= this.numPages())
		{
			return null;
		}
		try
		{
			RandomAccessFile raf = new RandomAccessFile(this.file,"r");
			byte[] b = new byte[BufferPool.PAGE_SIZE];
			raf.seek(BufferPool.PAGE_SIZE * pid.pageno());
			raf.readFully(b);
			raf.close();
			return new HeapPage((HeapPageId)pid, b);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// see DbFile.java for javadocs
	public void writePage(Page page) throws IOException {
		// some code goes here
		// not necessary for assignment1
	}

	/**
	 * Returns the number of pages in this HeapFile.
	 */
	public int numPages() 
	{
		return (int) (file.length() / BufferPool.PAGE_SIZE);
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> addTuple(TransactionId tid, Tuple t)
			throws DbException, IOException, TransactionAbortedException {
		// some code goes here
		// not necessary for assignment1
		return null;
	}

	// see DbFile.java for javadocs
	public Page deleteTuple(TransactionId tid, Tuple t) throws DbException, TransactionAbortedException 
	{
		// getting the pageId of the page from which the tuple is to be deleted.
		PageId pid = t.getRecordId().getPageId();
		
		// calling the getPage function of BufferPool to get the HeapPage.
		HeapPage p = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
		
		//deleting the tuple from the page.
		p.deleteTuple(t);
		return p;
	}

	// see DbFile.java for javadocs
	public DbFileIterator iterator(TransactionId tid) {

		return new DbFileIterator() {

			/**
			 * The ID of the page to read next.
			 */
			int nextPageID = 0;

			/**
			 * An iterator over all tuples from the page read most recently.
			 */
			Iterator<Tuple> iterator = null;

			@Override
			public void open() throws DbException, TransactionAbortedException {
				nextPageID = 0;
				// obtains an iterator over all tuples from page 0
				iterator = ((HeapPage) Database.getBufferPool().getPage(tid, new HeapPageId(getId(), nextPageID++),
						Permissions.READ_WRITE)).iterator();
			}

			// code referenced Black board solution.
			@Override
			public boolean hasNext() throws DbException, TransactionAbortedException 
			{
				if (iterator == null)
					return false;
				else if (iterator.hasNext())
					return true;
				else {
					do {
						if (nextPageID >= numPages()) {
							iterator = null;
							return false;
						}
						iterator = ((HeapPage) Database.getBufferPool().getPage(tid,
								new HeapPageId(getId(), nextPageID++), Permissions.READ_WRITE)).iterator();
					} while (!iterator.hasNext());
					return true;
				}
			}

			// code referenced black board solution.
			@Override
			public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException 
			{
				if (hasNext())
					return iterator.next();
				else
					throw new NoSuchElementException("There is no tuple available!");
			}

			@Override
			public void rewind() throws DbException, TransactionAbortedException {
				close();
				open();
			}

			@Override
			public void close() {
				iterator = null;
			}

		};
	}

}
