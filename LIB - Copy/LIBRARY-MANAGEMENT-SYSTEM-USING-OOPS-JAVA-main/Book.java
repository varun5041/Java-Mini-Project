class Book
{	 
	//book attributes
	int BookId= 1;
	String Title;
	String Author;
	double Price;
	boolean Issued = false;
	Integer IssuedToMemberId = null; // null when available

	//constructor
	Book(int BookId,String Title,String Author,double Price)
	{
		this.BookId=BookId;
		this.Title=Title;
		this.Author=Author;
		this.Price=Price;
	}

	boolean isAvailable()
	{
		return !Issued;
	}

	void markIssuedTo(int memberId)
	{
		this.Issued = true;
		this.IssuedToMemberId = memberId;
	}

	void markReturned()
	{
		this.Issued = false;
		this.IssuedToMemberId = null;
	}

	@Override
	public String toString()
	{
		String status = Issued ? ("Issued to member " + IssuedToMemberId) : "Available";
		return "Book ID: " + BookId + ", Title: " + Title + ", Author: " + Author + ", Price: " + Price + ", Status: " + status;
	}
}